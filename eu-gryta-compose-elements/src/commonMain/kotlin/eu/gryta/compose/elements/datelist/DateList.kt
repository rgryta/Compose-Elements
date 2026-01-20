package eu.gryta.compose.elements.datelist

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.gryta.compose.elements.infinitelist.GenericInfiniteLazyRow
import eu.gryta.compose.elements.infinitelist.rememberInfiniteListState
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class DateCardInfo(val date: LocalDate)

@Immutable
data class DateListColors(
    val selectedContainerColor: Color,
    val unselectedContainerColor: Color,
    val selectedContentColor: Color,
    val unselectedContentColor: Color,
    val selectedBorderColor: Color,
    val unselectedBorderColor: Color,
)

object DateListDefaults {
    @Composable
    fun colors(
        selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
        unselectedContainerColor: Color = MaterialTheme.colorScheme.surface,
        selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        unselectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
        selectedBorderColor: Color = MaterialTheme.colorScheme.primary,
        unselectedBorderColor: Color = MaterialTheme.colorScheme.outline,
    ): DateListColors = DateListColors(
        selectedContainerColor = selectedContainerColor,
        unselectedContainerColor = unselectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        selectedBorderColor = selectedBorderColor,
        unselectedBorderColor = unselectedBorderColor,
    )
}

@Composable
fun DateList(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    onDateSelect: (LocalDate, LocalDate?) -> Unit = { _, _ -> },
    initialItemsCount: Int = 50,
    itemSpacing: Dp = 8.dp,
    loadMoreThreshold: Int = 5,
    cardShape: Shape = RoundedCornerShape(12.dp),
    colors: DateListColors = DateListDefaults.colors(),
    itemContent: (@Composable (
        date: LocalDate,
        selected: Boolean,
        onClick: () -> Unit,
    ) -> Unit)? = null,
) {
    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }

    val state = rememberInfiniteListState(
        initialItems = (0 until initialItemsCount).map {
            DateCardInfo(
                date = now.minus(DatePeriod(days = it))
            )
        },
        loadMore = { currentItems ->
            val lastDate = currentItems.last().date
            (1..initialItemsCount).map {
                DateCardInfo(date = lastDate.minus(DatePeriod(days = it)))
            }
        }
    )

    val selectedIndex = remember(selectedDate, state.items) {
        state.items.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }
    }

    GenericInfiniteLazyRow(
        state = state,
        modifier = modifier,
        selectedIndex = selectedIndex,
        onItemSelect = { selected, previous ->
            onDateSelect(selected.date, previous?.date)
        },
        itemSpacing = itemSpacing,
        loadMoreThreshold = loadMoreThreshold,
        keySelector = { it.date.toEpochDays() },
    ) { dateCardInfo, onItemClick ->
        val isSelected = dateCardInfo.date == selectedDate
        if (itemContent != null) {
            itemContent(dateCardInfo.date, isSelected, onItemClick)
        } else {
            DateCard(
                date = dateCardInfo.date,
                selected = isSelected,
                onClick = onItemClick,
                shape = cardShape,
                containerColor = if (isSelected) colors.selectedContainerColor else colors.unselectedContainerColor,
                contentColor = if (isSelected) colors.selectedContentColor else colors.unselectedContentColor,
                borderColor = if (isSelected) colors.selectedBorderColor else colors.unselectedBorderColor,
            )
        }
    }
}
