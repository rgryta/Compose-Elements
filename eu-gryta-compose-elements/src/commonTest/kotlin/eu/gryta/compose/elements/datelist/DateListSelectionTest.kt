package eu.gryta.compose.elements.datelist

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DateListSelectionTest {

    @Test
    fun `selectedIndex is null when selectedDate not in items`() {
        val items = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )
        val selectedDate = LocalDate(2024, 1, 10)

        val selectedIndex = items.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }

        assertNull(selectedIndex)
    }

    @Test
    fun `selectedIndex is correct when selectedDate is first item`() {
        val items = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )
        val selectedDate = LocalDate(2024, 1, 15)

        val selectedIndex = items.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }

        assertEquals(0, selectedIndex)
    }

    @Test
    fun `selectedIndex is correct when selectedDate is last item`() {
        val items = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )
        val selectedDate = LocalDate(2024, 1, 13)

        val selectedIndex = items.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }

        assertEquals(2, selectedIndex)
    }

    @Test
    fun `selectedIndex is correct when selectedDate is middle item`() {
        val items = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13)),
            DateCardInfo(date = LocalDate(2024, 1, 12)),
            DateCardInfo(date = LocalDate(2024, 1, 11))
        )
        val selectedDate = LocalDate(2024, 1, 13)

        val selectedIndex = items.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }

        assertEquals(2, selectedIndex)
    }

    @Test
    fun `selectedIndex is null when selectedDate is null`() {
        val items = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )
        val selectedDate: LocalDate? = null

        val selectedIndex = items.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }

        assertNull(selectedIndex)
    }

    @Test
    fun `selectedIndex updates when items list changes`() {
        val initialItems = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )
        val selectedDate = LocalDate(2024, 1, 13)

        val initialIndex = initialItems.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }
        assertEquals(2, initialIndex)

        val updatedItems = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 16)),
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )

        val updatedIndex = updatedItems.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }
        assertEquals(3, updatedIndex)
    }
}
