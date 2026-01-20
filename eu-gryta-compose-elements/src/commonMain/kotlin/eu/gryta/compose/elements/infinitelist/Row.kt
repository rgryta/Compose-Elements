package eu.gryta.compose.elements.infinitelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.gryta.compose.elements.generics.Loading
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable
class InfiniteListState<E>(
    initialItems: List<E>,
    private val loadMore: suspend (currentItems: List<E>) -> Result<List<E>>
) {
    var items by mutableStateOf(initialItems)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<Throwable?>(null)
        private set

    suspend fun loadMoreItems() {
        isLoading = true
        error = null
        loadMore(items)
            .onSuccess { newItems -> items = items + newItems }
            .onFailure { throwable -> error = throwable }
        isLoading = false
    }

    fun retry() {
        error = null
    }
}

@Composable
fun <E> rememberInfiniteListState(
    initialItems: List<E>,
    loadMore: suspend (currentItems: List<E>) -> Result<List<E>>
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
    loadingContent: (@Composable () -> Unit)? = null,
    errorContent: (@Composable (error: Throwable, retry: () -> Unit) -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,
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

    Box(modifier = modifier) {
        when {
            state.items.isEmpty() && !state.isLoading && state.error == null -> {
                emptyContent?.invoke() ?: Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No items", style = MaterialTheme.typography.bodyLarge)
                }
            }
            state.error != null -> {
                errorContent?.invoke(state.error!!) {
                    state.retry()
                    coroutineScope.launch { state.loadMoreItems() }
                } ?: Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Error: ${state.error!!.message ?: "Unknown error"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = {
                        state.retry()
                        coroutineScope.launch { state.loadMoreItems() }
                    }) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                LazyRow(
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

                    if (state.isLoading) {
                        item {
                            Spacer(modifier = Modifier.width(itemSpacing))
                            loadingContent?.invoke() ?: Loading()
                        }
                    }
                }
            }
        }
    }
}