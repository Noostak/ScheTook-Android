package com.sopt.presentation.calendar.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.CalendarAppointmentEntity
import com.sopt.presentation.R

@Composable
fun ScheduleItem(
    data: CalendarAppointmentEntity,
    onItemClick: (Long) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onItemClick(data.optionId) }
            .background(color = NoostakTheme.colors.gray50, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Top)
        ) {
            ScheduleColorChip(data.category)
        }

        Spacer(modifier = Modifier.width(7.dp))
        Column {
            Text(
                text = data.name,
                style = NoostakTheme.typography.b1SemiBold,
                color = NoostakTheme.colors.black,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = if (data.duration.toInt() == FULL_DURATION) {
                    stringResource(R.string.text_calendar_schedule_item_all_day)
                } else {
                    "${
                    CalculateTimeFromString().extractHourWithZero(
                        data.startTime
                    )
                    }~${CalculateTimeFromString().extractHourWithZero(data.endTime)}"
                },
                style = NoostakTheme.typography.c4Regular,
                color = NoostakTheme.colors.gray700
            )
        }
    }
}

const val FULL_DURATION = 24
