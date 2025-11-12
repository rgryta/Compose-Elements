package eu.gryta.compose.elements.datelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class DateCardInfo(
    val date: LocalDate,
    val selected: MutableState<Boolean> = mutableStateOf(false)
) {
    private val dateOfWeek: StringResource = mapOf(
        DayOfWeek.MONDAY to Res.string.day_of_week_mon_short,
        DayOfWeek.TUESDAY to Res.string.day_of_week_tue_short,
        DayOfWeek.WEDNESDAY to Res.string.day_of_week_wed_short,
        DayOfWeek.THURSDAY to Res.string.day_of_week_thu_short,
        DayOfWeek.FRIDAY to Res.string.day_of_week_fri_short,
        DayOfWeek.SATURDAY to Res.string.day_of_week_sat_short,
        DayOfWeek.SUNDAY to Res.string.day_of_week_sun_short
    ).getValue(key = date.dayOfWeek)

    @Composable
    fun buttonText(): String {
        val dayString: String = stringResource(resource = dateOfWeek)
        val shortDate = "${date.day}/${date.month.number}"
        return "$dayString $shortDate"
    }
}