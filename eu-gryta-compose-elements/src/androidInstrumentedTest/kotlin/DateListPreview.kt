package eu.gryta.compose.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.gryta.compose.elements.datelist.DateList
import eu.gryta.compose.elements.theme.AppTheme
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock


@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    var selectedDate by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    }
    AppTheme {
        Column(
            modifier = Modifier.height(300.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                selectedDate = selectedDate.minus(DatePeriod(days = 7))
            }) {
                Text(selectedDate.toString())
            }
            DateList(
                selectedDate = selectedDate,
                onDateSelect = { newSelection, _ ->
                    selectedDate = newSelection
                }
            )
        }
    }
}
