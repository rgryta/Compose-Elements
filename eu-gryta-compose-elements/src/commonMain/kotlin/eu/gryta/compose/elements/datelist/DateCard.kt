package eu.gryta.compose.elements.datelist

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun DateCard(
    date: LocalDate,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !selected,
    ) {
        Text(text = date.buttonText())
    }
}

@Composable
internal fun LocalDate.buttonText(): String {
    val dayString: String = stringResource(resource = this.dateOfWeek())
    val shortDate = "${this.day}/${this.month.number}"
    return "$dayString $shortDate"
}

internal fun LocalDate.dateOfWeek(): StringResource {
    return mapOf(
        DayOfWeek.MONDAY to Res.string.day_of_week_mon_short,
        DayOfWeek.TUESDAY to Res.string.day_of_week_tue_short,
        DayOfWeek.WEDNESDAY to Res.string.day_of_week_wed_short,
        DayOfWeek.THURSDAY to Res.string.day_of_week_thu_short,
        DayOfWeek.FRIDAY to Res.string.day_of_week_fri_short,
        DayOfWeek.SATURDAY to Res.string.day_of_week_sat_short,
        DayOfWeek.SUNDAY to Res.string.day_of_week_sun_short
    ).getValue(key = this.dayOfWeek)
}