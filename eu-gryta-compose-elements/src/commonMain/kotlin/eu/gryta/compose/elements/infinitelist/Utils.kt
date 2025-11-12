package eu.gryta.compose.elements.infinitelist

import androidx.compose.foundation.lazy.LazyListState


internal fun LazyListState.reachedBottom(buffer: Int = 10): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem != null && (lastVisibleItem.index >= this.layoutInfo.totalItemsCount - buffer)
}