package eu.gryta.compose.elements.infinitelist

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContains

class InfiniteListStateTest {

    @Test
    fun `initial items are loaded correctly`() {
        val initialItems = listOf("A", "B", "C")
        val state = InfiniteListState(initialItems) { emptyList() }

        assertEquals(3, state.items.size)
        assertEquals(initialItems, state.items)
    }

    @Test
    fun `loadMore adds new items to existing list`() = runTest {
        val initialItems = listOf("A", "B", "C")
        val state = InfiniteListState(initialItems) { currentItems ->
            listOf("D", "E")
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
            emptyList()
        }

        state.loadMoreItems()

        assertEquals(initialItems, receivedItems)
    }

    @Test
    fun `multiple loadMore calls accumulate items`() = runTest {
        var counter = 0
        val state = InfiniteListState(listOf(0)) { currentItems ->
            counter++
            listOf(counter)
        }

        state.loadMoreItems()
        state.loadMoreItems()
        state.loadMoreItems()

        assertEquals(4, state.items.size)
        assertEquals(listOf(0, 1, 2, 3), state.items)
    }

    @Test
    fun `loadMore with empty result does not crash`() = runTest {
        val state = InfiniteListState(listOf("A")) { emptyList() }

        state.loadMoreItems()

        assertEquals(1, state.items.size)
        assertEquals(listOf("A"), state.items)
    }

    @Test
    fun `empty initial list works correctly`() = runTest {
        val state = InfiniteListState(emptyList<String>()) { listOf("A", "B") }

        assertEquals(0, state.items.size)

        state.loadMoreItems()

        assertEquals(2, state.items.size)
        assertEquals(listOf("A", "B"), state.items)
    }

    @Test
    fun `loadMore can return dynamic count based on current items`() = runTest {
        val state = InfiniteListState(listOf(1)) { currentItems ->
            val lastValue = currentItems.last()
            List(3) { lastValue + it + 1 }
        }

        state.loadMoreItems()

        assertEquals(4, state.items.size)
        assertEquals(listOf(1, 2, 3, 4), state.items)
    }

    @Test
    fun `items are observable through mutableState`() = runTest {
        val state = InfiniteListState(listOf("A")) { listOf("B") }

        val initialItems = state.items
        assertEquals(listOf("A"), initialItems)

        state.loadMoreItems()

        val updatedItems = state.items
        assertEquals(listOf("A", "B"), updatedItems)
    }

    @Test
    fun `loadMore handles duplicate items from loader`() = runTest {
        val state = InfiniteListState(listOf("A", "B")) { listOf("B", "C") }

        state.loadMoreItems()

        assertEquals(4, state.items.size)
        assertContains(state.items, "A")
        assertContains(state.items, "B")
        assertContains(state.items, "C")
    }
}
