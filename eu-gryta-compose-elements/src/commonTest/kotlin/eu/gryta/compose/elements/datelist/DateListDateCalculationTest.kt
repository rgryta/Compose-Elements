package eu.gryta.compose.elements.datelist

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DateListDateCalculationTest {

    @Test
    fun `initial items generate correct date sequence`() {
        val today = LocalDate(2024, 1, 15)
        val itemCount = 10

        val items = (0 until itemCount).map {
            DateCardInfo(date = today.minus(DatePeriod(days = it)))
        }

        assertEquals(itemCount, items.size)
        assertEquals(today, items[0].date)
        assertEquals(today.minus(DatePeriod(days = 1)), items[1].date)
        assertEquals(today.minus(DatePeriod(days = 9)), items[9].date)
    }

    @Test
    fun `date sequence crosses month boundary correctly`() {
        val startDate = LocalDate(2024, 2, 3)
        val itemCount = 10

        val items = (0 until itemCount).map {
            DateCardInfo(date = startDate.minus(DatePeriod(days = it)))
        }

        assertEquals(LocalDate(2024, 2, 3), items[0].date)
        assertEquals(LocalDate(2024, 2, 2), items[1].date)
        assertEquals(LocalDate(2024, 2, 1), items[2].date)
        assertEquals(LocalDate(2024, 1, 31), items[3].date)
        assertEquals(LocalDate(2024, 1, 30), items[4].date)
    }

    @Test
    fun `date sequence crosses year boundary correctly`() {
        val startDate = LocalDate(2024, 1, 3)
        val itemCount = 10

        val items = (0 until itemCount).map {
            DateCardInfo(date = startDate.minus(DatePeriod(days = it)))
        }

        assertEquals(LocalDate(2024, 1, 3), items[0].date)
        assertEquals(LocalDate(2024, 1, 2), items[1].date)
        assertEquals(LocalDate(2024, 1, 1), items[2].date)
        assertEquals(LocalDate(2023, 12, 31), items[3].date)
        assertEquals(LocalDate(2023, 12, 30), items[4].date)
    }

    @Test
    fun `loadMore generates correct date sequence from last item`() {
        val initialItems = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )

        val lastDate = initialItems.last().date
        val loadMoreCount = 5

        val newItems = (1..loadMoreCount).map {
            DateCardInfo(date = lastDate.minus(DatePeriod(days = it)))
        }

        assertEquals(loadMoreCount, newItems.size)
        assertEquals(LocalDate(2024, 1, 12), newItems[0].date)
        assertEquals(LocalDate(2024, 1, 11), newItems[1].date)
        assertEquals(LocalDate(2024, 1, 8), newItems[4].date)
    }

    @Test
    fun `loadMore continues sequence without gaps`() {
        val initialItems = listOf(
            DateCardInfo(date = LocalDate(2024, 1, 15)),
            DateCardInfo(date = LocalDate(2024, 1, 14)),
            DateCardInfo(date = LocalDate(2024, 1, 13))
        )

        val lastDate = initialItems.last().date
        val newItems = (1..3).map {
            DateCardInfo(date = lastDate.minus(DatePeriod(days = it)))
        }

        val allItems = initialItems + newItems

        for (i in 0 until allItems.size - 1) {
            val expectedDiff = 1L
            val actualDiff = allItems[i].date.toEpochDays() - allItems[i + 1].date.toEpochDays()
            assertEquals(expectedDiff, actualDiff, "Gap found between items $i and ${i + 1}")
        }
    }

    @Test
    fun `large item count generates correct sequence`() {
        val today = LocalDate(2024, 6, 15)
        val itemCount = 100

        val items = (0 until itemCount).map {
            DateCardInfo(date = today.minus(DatePeriod(days = it)))
        }

        assertEquals(itemCount, items.size)
        assertEquals(today, items[0].date)
        assertEquals(today.minus(DatePeriod(days = 99)), items[99].date)

        for (i in 0 until items.size - 1) {
            val expectedDiff = 1L
            val actualDiff = items[i].date.toEpochDays() - items[i + 1].date.toEpochDays()
            assertEquals(expectedDiff, actualDiff, "Gap found at index $i")
        }
    }

    @Test
    fun `dates are in descending order`() {
        val today = LocalDate(2024, 3, 15)
        val itemCount = 20

        val items = (0 until itemCount).map {
            DateCardInfo(date = today.minus(DatePeriod(days = it)))
        }

        for (i in 0 until items.size - 1) {
            assertTrue(
                items[i].date > items[i + 1].date,
                "Dates should be in descending order: ${items[i].date} should be > ${items[i + 1].date}"
            )
        }
    }
}
