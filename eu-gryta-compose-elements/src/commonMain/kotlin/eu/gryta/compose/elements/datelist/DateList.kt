package eu.gryta.compose.elements.datelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import eu.gryta.compose.elements.infinitelist.GenericInfiniteLazyRow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
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
    var items by remember { mutableStateOf<List<DateCardInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        val initialItems = (0 until initialItemsCount).map {
            DateCardInfo(
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(DatePeriod(days = it))
            )
        }
        items = initialItems
    }

    val selectedIndex = remember(selectedDate, items) {
        items.indexOfFirst { it.date == selectedDate }.takeIf { it != -1 }
    }

    GenericInfiniteLazyRow(
        modifier = modifier,
        items = items,
        onLoadMore = {
            val lastDate = items.last().date
            val newItems = (1..initialItemsCount).map {
                DateCardInfo(date = lastDate.minus(DatePeriod(days = it)))
            }
            items = items + newItems
        },
        itemContent = { dateCardInfo, onItemClick ->
            DateCard(
                date = dateCardInfo.date,
                selected = dateCardInfo.date == selectedDate,
                onClick = onItemClick
            )
        },
        selectedIndex = selectedIndex,
        onItemSelect = { selected, previous ->
            onDateSelect(selected.date, previous?.date)
        },
    )
}