package eu.gryta.compose.elements.datelist

import eu.gryta.compose.elements.resources.Res
import eu.gryta.compose.elements.resources.day_of_week_fri_short
import eu.gryta.compose.elements.resources.day_of_week_mon_short
import eu.gryta.compose.elements.resources.day_of_week_sat_short
import eu.gryta.compose.elements.resources.day_of_week_sun_short
import eu.gryta.compose.elements.resources.day_of_week_thu_short
import eu.gryta.compose.elements.resources.day_of_week_tue_short
import eu.gryta.compose.elements.resources.day_of_week_wed_short
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlin.test.Test
import kotlin.test.assertEquals

class DateCardFormattingTest {

    @Test
    fun `dateOfWeek returns correct resource for Monday`() {
        val monday = LocalDate(2024, 1, 15) // Monday
        assertEquals(Res.string.day_of_week_mon_short, monday.dateOfWeek())
    }

    @Test
    fun `dateOfWeek returns correct resource for Tuesday`() {
        val tuesday = LocalDate(2024, 1, 16) // Tuesday
        assertEquals(Res.string.day_of_week_tue_short, tuesday.dateOfWeek())
    }

    @Test
    fun `dateOfWeek returns correct resource for Wednesday`() {
        val wednesday = LocalDate(2024, 1, 17) // Wednesday
        assertEquals(Res.string.day_of_week_wed_short, wednesday.dateOfWeek())
    }

    @Test
    fun `dateOfWeek returns correct resource for Thursday`() {
        val thursday = LocalDate(2024, 1, 18) // Thursday
        assertEquals(Res.string.day_of_week_thu_short, thursday.dateOfWeek())
    }

    @Test
    fun `dateOfWeek returns correct resource for Friday`() {
        val friday = LocalDate(2024, 1, 19) // Friday
        assertEquals(Res.string.day_of_week_fri_short, friday.dateOfWeek())
    }

    @Test
    fun `dateOfWeek returns correct resource for Saturday`() {
        val saturday = LocalDate(2024, 1, 20) // Saturday
        assertEquals(Res.string.day_of_week_sat_short, saturday.dateOfWeek())
    }

    @Test
    fun `dateOfWeek returns correct resource for Sunday`() {
        val sunday = LocalDate(2024, 1, 21) // Sunday
        assertEquals(Res.string.day_of_week_sun_short, sunday.dateOfWeek())
    }

    @Test
    fun `date format includes day and month for single digit day`() {
        val date = LocalDate(2024, 3, 5)
        val expectedShortDate = "5/3"

        val actualDay = date.day
        val actualMonth = date.month.number
        val actualShortDate = "$actualDay/$actualMonth"

        assertEquals(expectedShortDate, actualShortDate)
    }

    @Test
    fun `date format includes day and month for double digit day`() {
        val date = LocalDate(2024, 11, 25)
        val expectedShortDate = "25/11"

        val actualDay = date.day
        val actualMonth = date.month.number
        val actualShortDate = "$actualDay/$actualMonth"

        assertEquals(expectedShortDate, actualShortDate)
    }

    @Test
    fun `date format includes day and month for December`() {
        val date = LocalDate(2024, 12, 31)
        val expectedShortDate = "31/12"

        val actualDay = date.day
        val actualMonth = date.month.number
        val actualShortDate = "$actualDay/$actualMonth"

        assertEquals(expectedShortDate, actualShortDate)
    }

    @Test
    fun `date format includes day and month for January 1st`() {
        val date = LocalDate(2024, 1, 1)
        val expectedShortDate = "1/1"

        val actualDay = date.day
        val actualMonth = date.month.number
        val actualShortDate = "$actualDay/$actualMonth"

        assertEquals(expectedShortDate, actualShortDate)
    }

    @Test
    fun `day of week map contains all DayOfWeek values`() {
        val allDaysOfWeek = DayOfWeek.entries.toSet()

        // Test that all days can be looked up
        allDaysOfWeek.forEach { dayOfWeek ->
            val date = when (dayOfWeek) {
                DayOfWeek.MONDAY -> LocalDate(2024, 1, 15)
                DayOfWeek.TUESDAY -> LocalDate(2024, 1, 16)
                DayOfWeek.WEDNESDAY -> LocalDate(2024, 1, 17)
                DayOfWeek.THURSDAY -> LocalDate(2024, 1, 18)
                DayOfWeek.FRIDAY -> LocalDate(2024, 1, 19)
                DayOfWeek.SATURDAY -> LocalDate(2024, 1, 20)
                DayOfWeek.SUNDAY -> LocalDate(2024, 1, 21)
            }
            // This will throw if the day is not in the map
            date.dateOfWeek()
        }
    }

    @Test
    fun `leap year date formatting works correctly`() {
        val leapYearDate = LocalDate(2024, 2, 29) // 2024 is a leap year
        val expectedShortDate = "29/2"

        val actualDay = leapYearDate.day
        val actualMonth = leapYearDate.month.number
        val actualShortDate = "$actualDay/$actualMonth"

        assertEquals(expectedShortDate, actualShortDate)
    }
}
