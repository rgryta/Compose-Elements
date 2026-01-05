package eu.gryta.compose.elements.infinitelist

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable
class InfiniteListState<E>(
    initialItems: List<E>,
    private val loadMore: suspend (currentItems: List<E>) -> List<E>
) {
    var items by mutableStateOf(initialItems)
        private set

    suspend fun loadMoreItems() {
        val newItems = loadMore(items)
        items = items + newItems
    }
}

@Composable
fun <E> rememberInfiniteListState(
    initialItems: List<E>,
    loadMore: suspend (currentItems: List<E>) -> List<E>
): InfiniteListState<E> {
    return remember(initialItems, loadMore) {
        InfiniteListState(initialItems, loadMore)
    }
}

@Composable
fun <E> GenericInfiniteLazyRow(
    state: InfiniteListState<E>,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    onItemSelect: (E, E?) -> Unit = { _, _ -> },
    itemSpacing: Dp = 8.dp,
    loadMoreThreshold: Int = 5,
    keySelector: ((E) -> Any)? = null,
    content: @Composable (E, () -> Unit) -> Unit,
) {
    val listState: LazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val selectedItem by remember(selectedIndex) {
        derivedStateOf {
            selectedIndex?.let { state.items.getOrNull(it) }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                if (lastVisibleItem == null) {
                    false
                } else {
                    lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - loadMoreThreshold
                }
            }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                state.loadMoreItems()
            }
    }

    // Animate scroll to item
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != null) {
            coroutineScope.launch {
                listState.animateScrollToItem(index = selectedIndex)
            }
        }
    }

    LazyRow(
        modifier = modifier,
        state = listState,
        reverseLayout = true,
    ) {
        itemsIndexed(
            items = state.items,
            key = { _, item -> keySelector?.invoke(item) ?: item.hashCode() }
        ) { index: Int, item: E ->
            Spacer(modifier = Modifier.width(itemSpacing))
            content(
                item,
            ) {
                onItemSelect(item, selectedItem)
                coroutineScope.launch {
                    listState.animateScrollToItem(index = index)
                }
            }
        }
    }
}