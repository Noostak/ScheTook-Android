package com.sopt.core.designsystem.component.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.core.designsystem.theme.Gray200
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable
import com.sopt.core.type.CellType
import com.sopt.core.util.timetable.TimeTable
import com.sopt.domain.entity.TimeEntity

@Composable
fun NoostakEditableTimeTable(
    availablePeriods: List<TimeEntity>,
    modifier: Modifier = Modifier,
    onSelectedTimesChanged: (List<TimeEntity>) -> Unit
) {
    val days = availablePeriods.size
    val timeSlots =
        TimeTable().calculateTimeSlots(
            availablePeriods.first().startTime,
            availablePeriods.first().endTime
        )
    val selectedCells = remember { mutableStateListOf<Pair<Int, Int>>() }

    LazyColumn(
        modifier = modifier
            .border(
                width = 1.dp,
                color = NoostakTheme.colors.gray200,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        // 행 반복
        items(timeSlots + 1) { rowIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // 열 반복
                for (columnIndex in 0..days) {
                    val cellType = TimeTable().determineCellType(rowIndex, columnIndex)
                    val text =
                        TimeTable().getCellText(cellType, rowIndex, columnIndex, availablePeriods)
                    val isSelected = selectedCells.contains(rowIndex to columnIndex)
                    val backgroundColor =
                        TimeTable().getEditableBackgroundColor(cellType, isSelected)
                    val shape = when (rowIndex to columnIndex) {
                        0 to 0 -> RoundedCornerShape(topStart = 8.dp)
                        0 to days -> RoundedCornerShape(topEnd = 8.dp)
                        timeSlots to days -> RoundedCornerShape(bottomEnd = 8.dp)
                        timeSlots to 0 -> RoundedCornerShape(bottomStart = 8.dp)
                        else -> RoundedCornerShape(0.dp)
                    }

                    Box(
                        modifier = when (cellType) {
                            CellType.Blank ->
                                Modifier
                                    .width(42.dp)
                                    .height(36.dp)

                            CellType.TimeHeader ->
                                Modifier
                                    .width(42.dp)
                                    .height(32.dp)

                            CellType.DateHeader ->
                                Modifier
                                    .weight(1f)
                                    .height(36.dp)

                            else ->
                                Modifier
                                    .weight(1f)
                                    .height(32.dp)
                        }
                            .background(
                                color = backgroundColor,
                                shape = shape
                            )
                            .drawBehind {
                                val borderWidth = 1.dp.toPx()
                                val borderColor = Gray200

                                if (rowIndex > 0) {
                                    drawLine(
                                        color = borderColor,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        strokeWidth = borderWidth
                                    )
                                }

                                if (columnIndex > 0) {
                                    drawLine(
                                        color = borderColor,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = borderWidth
                                    )
                                }
                            }
                            .noRippleClickable {
                                if (cellType == CellType.Data) {
                                    val cell = rowIndex to columnIndex
                                    if (selectedCells.contains(cell)) {
                                        selectedCells.remove(cell)
                                    } else {
                                        selectedCells.add(cell)
                                    }
                                    onSelectedTimesChanged(
                                        TimeTable().getSelectedTimes(
                                            selectedCells,
                                            availablePeriods
                                        )
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            style = NoostakTheme.typography.c4Regular,
                            color = NoostakTheme.colors.gray600,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoostakEditableTimeTablePreview() {
    NoostakAndroidTheme {
        val mockAvailablePeriods = listOf(
            TimeEntity(
                date = "2024-09-05T10:00:00",
                startTime = "2024-09-05T10:00:00",
                endTime = "2024-09-05T18:00:00"
            ),
            TimeEntity(
                date = "2024-09-06T10:00:00",
                startTime = "2024-09-06T10:00:00",
                endTime = "2024-09-06T18:00:00"
            ),
            TimeEntity(
                date = "2024-09-07T10:00:00",
                startTime = "2024-09-07T10:00:00",
                endTime = "2024-09-07T18:00:00"
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            NoostakEditableTimeTable(
                availablePeriods = mockAvailablePeriods,
                modifier = Modifier.fillMaxWidth(),
                onSelectedTimesChanged = { }
            )
        }
    }
}
