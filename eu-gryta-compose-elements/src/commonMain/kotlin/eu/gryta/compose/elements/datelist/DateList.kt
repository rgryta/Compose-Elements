package eu.gryta.compose.elements.datelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import eu.gryta.compose.elements.infinitelist.GenericInfiniteLazyRow
import eu.gryta.compose.elements.infinitelist.rememberInfiniteListState
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class DateCardInfo(val date: LocalDate)

@Composable
fun DateList(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    onDateSelect: (LocalDate, LocalDate?) -> Unit = { _, _ -> },
    initialItemsCount: Int = 50,
) {
    val state = rememberInfiniteListState(
        initialItems = (0 until initialItemsCount).map {
            DateCardInfo(
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(DatePeriod(days = it))
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
    ) { dateCardInfo, onItemClick ->
        DateCard(
            date = dateCardInfo.date,
            selected = dateCardInfo.date == selectedDate,
            onClick = onItemClick
        )
    }
}