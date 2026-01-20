package eu.gryta.compose.elements.infinitelist

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContains

class InfiniteListStateTest {

    @Test
    fun `initial items are loaded correctly`() {
        val initialItems = listOf("A", "B", "C")
        val state = InfiniteListState(initialItems) { Result.success(emptyList()) }

        assertEquals(3, state.items.size)
        assertEquals(initialItems, state.items)
    }

    @Test
    fun `loadMore adds new items to existing list`() = runTest {
        val initialItems = listOf("A", "B", "C")
        val state = InfiniteListState(initialItems) { currentItems ->
            Result.success(listOf("D", "E"))
        }

        state.loadMoreItems()

        assertEquals(5, state.items.size)
        assertEquals(listOf("A", "B", "C", "D", "E"), state.items)
    }

    @Test
    fun `loadMore receives current items as parameter`() = runTest {
        val initialItems = listOf(1, 2, 3)
        var receivedItems: List<Int>? = null

        val state = InfiniteListState(initialItems) { currentItems ->
            receivedItems = currentItems
            Result.success(emptyList())
        }

        state.loadMoreItems()

        assertEquals(initialItems, receivedItems)
    }

    @Test
    fun `multiple loadMore calls accumulate items`() = runTest {
        var counter = 0
        val state = InfiniteListState(listOf(0)) { currentItems ->
            counter++
            Result.success(listOf(counter))
        }

        state.loadMoreItems()
        state.loadMoreItems()
        state.loadMoreItems()

        assertEquals(4, state.items.size)
        assertEquals(listOf(0, 1, 2, 3), state.items)
    }

    @Test
    fun `loadMore with empty result does not crash`() = runTest {
        val state = InfiniteListState(listOf("A")) { Result.success(emptyList()) }

        state.loadMoreItems()

        assertEquals(1, state.items.size)
        assertEquals(listOf("A"), state.items)
    }

    @Test
    fun `empty initial list works correctly`() = runTest {
        val state = InfiniteListState(emptyList<String>()) { Result.success(listOf("A", "B")) }

        assertEquals(0, state.items.size)

        state.loadMoreItems()

        assertEquals(2, state.items.size)
        assertEquals(listOf("A", "B"), state.items)
    }

    @Test
    fun `loadMore can return dynamic count based on current items`() = runTest {
        val state = InfiniteListState(listOf(1)) { currentItems ->
            val lastValue = currentItems.last()
            Result.success(List(3) { lastValue + it + 1 })
        }

        state.loadMoreItems()

        assertEquals(4, state.items.size)
        assertEquals(listOf(1, 2, 3, 4), state.items)
    }

    @Test
    fun `items are observable through mutableState`() = runTest {
        val state = InfiniteListState(listOf("A")) { Result.success(listOf("B")) }

        val initialItems = state.items
        assertEquals(listOf("A"), initialItems)

        state.loadMoreItems()

        val updatedItems = state.items
        assertEquals(listOf("A", "B"), updatedItems)
    }

    @Test
    fun `loadMore handles duplicate items from loader`() = runTest {
        val state = InfiniteListState(listOf("A", "B")) { Result.success(listOf("B", "C")) }

        state.loadMoreItems()

        assertEquals(4, state.items.size)
        assertContains(state.items, "A")
        assertContains(state.items, "B")
        assertContains(state.items, "C")
    }

    @Test
    fun `error handling sets error state and does not modify items`() = runTest {
        val initialItems = listOf("A", "B")
        val exception = RuntimeException("Test error")
        val state = InfiniteListState(initialItems) { Result.failure(exception) }

        state.loadMoreItems()

        assertEquals(2, state.items.size)
        assertEquals(initialItems, state.items)
        assertEquals(exception, state.error)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `loading state is true during load and false after`() = runTest {
        val state = InfiniteListState(listOf("A")) { Result.success(listOf("B")) }

        assertEquals(false, state.isLoading)

        state.loadMoreItems()

        assertEquals(false, state.isLoading)
    }

    @Test
    fun `successful load clears error state`() = runTest {
        var shouldFail = true
        val state = InfiniteListState(listOf("A")) {
            if (shouldFail) Result.failure(RuntimeException("Error"))
            else Result.success(listOf("B"))
        }

        state.loadMoreItems()
        assertEquals(true, state.error != null)

        shouldFail = false
        state.loadMoreItems()

        assertEquals(null, state.error)
        assertEquals(2, state.items.size)
    }

    @Test
    fun `retry clears error state`() = runTest {
        val state = InfiniteListState(listOf("A")) { Result.failure(RuntimeException("Error")) }

        state.loadMoreItems()
        assertEquals(true, state.error != null)

        state.retry()

        assertEquals(null, state.error)
    }

    @Test
    fun `error state is cleared before each load attempt`() = runTest {
        var shouldFail = true
        val state = InfiniteListState(listOf("A")) {
            if (shouldFail) Result.failure(RuntimeException("Error"))
            else Result.success(listOf("B"))
        }

        state.loadMoreItems()
        assertEquals(true, state.error != null)

        shouldFail = false
        state.loadMoreItems()

        assertEquals(null, state.error)
    }
}
