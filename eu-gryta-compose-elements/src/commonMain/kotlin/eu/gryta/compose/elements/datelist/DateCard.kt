package eu.gryta.compose.elements.datelist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
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

private val DAY_OF_WEEK_STRING_RESOURCES = mapOf(
    DayOfWeek.MONDAY to Res.string.day_of_week_mon_short,
    DayOfWeek.TUESDAY to Res.string.day_of_week_tue_short,
    DayOfWeek.WEDNESDAY to Res.string.day_of_week_wed_short,
    DayOfWeek.THURSDAY to Res.string.day_of_week_thu_short,
    DayOfWeek.FRIDAY to Res.string.day_of_week_fri_short,
    DayOfWeek.SATURDAY to Res.string.day_of_week_sat_short,
    DayOfWeek.SUNDAY to Res.string.day_of_week_sun_short
)

/**
 * A card displaying a single date with day-of-week and day/month.
 *
 * @param date The date to display
 * @param selected Whether this date is currently selected
 * @param onClick Callback invoked when the card is clicked
 * @param modifier Modifier to be applied to the card
 * @param enabled Whether the date can be selected
 * @param containerColor Background color of the card
 * @param contentColor Text color
 * @param borderColor Border color
 * @param shape Shape of the card
 */
@Composable
fun DateCard(
    date: LocalDate,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surface,
    contentColor: Color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurface,
    borderColor: Color = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    val dayName = stringResource(resource = date.dateOfWeek())
    val shortDate = "${date.day}/${date.month.number}"

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics {
            contentDescription = when {
                !enabled -> "Disabled date: $dayName $shortDate"
                selected -> "Selected date: $dayName $shortDate"
                else -> "Date: $dayName $shortDate"
            }
            this.selected = selected
            role = Role.Button
        },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Text(
            text = date.buttonText(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun LocalDate.buttonText(): String {
    val dayString: String = stringResource(resource = this.dateOfWeek())
    val shortDate = "${this.day}/${this.month.number}"
    return "$dayString $shortDate"
}

fun LocalDate.dateOfWeek(): StringResource {
    return DAY_OF_WEEK_STRING_RESOURCES.getValue(key = this.dayOfWeek)
}