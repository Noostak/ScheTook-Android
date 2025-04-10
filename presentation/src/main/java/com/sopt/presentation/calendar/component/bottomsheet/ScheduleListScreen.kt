package com.sopt.presentation.calendar.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.core.designsystem.component.button.NoostakBottomButton
import com.sopt.core.designsystem.screen.NoostakEmptyScreen
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.util.time.CalculateTimeFromLocalDate
import com.sopt.domain.entity.CalendarAppointmentEntity
import com.sopt.domain.entity.ScheduleEntity
import com.sopt.presentation.R
import java.time.LocalDate

@Composable
fun ScheduleListScreen(
    data: ScheduleEntity,
    onItemClick: (Long) -> Unit = {},
    onCreateAppointmentBtnClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(63.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = CalculateTimeFromLocalDate().formatLocalDateWithDay(data.date),
                    color = NoostakTheme.colors.black,
                    style = NoostakTheme.typography.b1SemiBold
                )
            }
            HorizontalDivider(color = NoostakTheme.colors.gray100)
            if (data.scheduleList.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 30.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
                ) {
                    itemsIndexed(
                        items = data.scheduleList,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        ScheduleItem(data = item, onItemClick = onItemClick)
                    }
                }
            } else {
                NoostakEmptyScreen(
                    emptyText = R.string.text_calendar_schedule_list_empty_content,
                    color = NoostakTheme.colors.gray700,
                    style = NoostakTheme.typography.b4Regular
                )
            }
        }
        NoostakBottomButton(
            modifier = Modifier.padding(
                bottom = dimensionResource(id = R.dimen.vertical_padding),
                start = dimensionResource(id = R.dimen.vertical_padding),
                end = dimensionResource(id = R.dimen.vertical_padding)
            ),
            text = stringResource(R.string.btn_calendar_schedule_list_create_appointment),
            activateColor = NoostakTheme.colors.blue600,
            isEnabled = true,
            onButtonClick = {
                onCreateAppointmentBtnClick()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleListScreenPreview() {
    NoostakAndroidTheme {
        ScheduleListScreen(
            data = ScheduleEntity(
                groupId = 1,
                date = LocalDate.of(2025, 4, 4),
                scheduleList = listOf(
                    CalendarAppointmentEntity(
                        id = 1,
                        name = "누스탁 회의dasfdsafsafdafdafsdfsafdsafdsadsdafsadfsdfsdafadafdsafsdafsaf",
                        category = "중요",
                        startTime = "2024-09-07T00:00:00",
                        endTime = "2024-09-07T00:20:00",
                        duration = 24,
                        date = ""
                    ),
                    CalendarAppointmentEntity(
                        id = 2,
                        name = "누스탁 모각작",
                        category = "일정",
                        startTime = "2024-09-07T06:00:00",
                        endTime = "2024-09-07T08:00:00",
                        duration = 5,
                        date = ""
                    ),
                    CalendarAppointmentEntity(
                        id = 3,
                        name = "누스탁 회식",
                        category = "취미",
                        startTime = "2024-09-07T12:00:00",
                        endTime = "2024-09-07T13:00:00",
                        duration = 2,
                        date = ""
                    )
                )
            )
        )
    }
}
