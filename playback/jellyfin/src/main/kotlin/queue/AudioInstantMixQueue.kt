package org.jellyfin.playback.jellyfin.queue

import org.jellyfin.playback.core.queue.PagedQueue
import org.jellyfin.playback.core.queue.item.QueueEntry
import org.jellyfin.playback.jellyfin.queue.item.BaseItemDtoUserQueueEntry
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.instantMixApi
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ItemFields

class AudioInstantMixQueue(
	private val item: BaseItemDto,
	private val api: ApiClient,
) : PagedQueue() {
	companion object {
		val instantMixableItems = arrayOf(
			BaseItemKind.MUSIC_GENRE,
			BaseItemKind.PLAYLIST,
			BaseItemKind.MUSIC_ALBUM,
			BaseItemKind.MUSIC_ARTIST,
			BaseItemKind.AUDIO,
			BaseItemKind.FOLDER,
		)
	}

	init {
		require(item.type in instantMixableItems)
	}

	override suspend fun loadPage(offset: Int, size: Int): Collection<QueueEntry> {
		// API doesn't support paging for instant mix
		if (offset > 0) return emptyList()

		val result by api.instantMixApi.getInstantMixFromItem(
			id = item.id,
			userId = api.userId,
			fields = listOf(ItemFields.MEDIA_SOURCES),
			// Pagination
			limit = size,
		)
		return result.items.orEmpty().map { BaseItemDtoUserQueueEntry.build(api, it) }
	}
}
