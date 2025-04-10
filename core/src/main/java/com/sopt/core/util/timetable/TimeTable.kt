package com.sopt.core.util.timetable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.type.AvailabilityLevel
import com.sopt.core.type.CellType
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.AppointmentMembersInfoEntity
import com.sopt.domain.entity.TimeEntity

class TimeTable {
    fun calculateTimeSlots(startTime: String, endTime: String): Int {
        val startHour = extractHour(startTime)
        val endHour = extractHour(endTime)
        return endHour - startHour
    }

    fun determineCellType(rowIndex: Int, columnIndex: Int): CellType = when {
        rowIndex == 0 && columnIndex == 0 -> CellType.Blank
        rowIndex == 0 -> CellType.DateHeader
        columnIndex == 0 -> CellType.TimeHeader
        else -> CellType.Data
    }

    @Composable
    fun getBackgroundColor(
        cellType: CellType,
        rowIndex: Int,
        columnIndex: Int,
        availablePeriods: List<TimeEntity>,
        availableTimes: List<AppointmentMembersInfoEntity>
    ): Color {
        return when (cellType) {
            CellType.Blank, CellType.DateHeader, CellType.TimeHeader -> Color.Transparent
            CellType.Data -> {
                val startHour = extractHour(availablePeriods.first().startTime)
                val currentHour = startHour + (rowIndex - 1)

                val date =
                    availablePeriods.getOrNull(columnIndex - 1)?.date ?: return Color.Transparent
                // 해당 열(columnIndex) 날짜 데이터 가져오기
                val formattedDate = extractDate(date)
                // 해당 날짜에 해당하는 사용자의 가능 시간 데이터 필터링
                val availableTimesForDate = availableTimes.flatMap { member ->
                    member.appointmentMemberAvailableTimes.filter { timeEntity ->
                        extractDate(timeEntity.date) == formattedDate
                    }
                }

                // 현재 시간에 해당하는 가능 레벨 계산
                val totalMembers = availableTimes.size
                val availableMembers = availableTimesForDate.count { timeEntity ->
                    val entityStartHour = extractHour(timeEntity.startTime)
                    val entityEndHour = extractHour(timeEntity.endTime)
                    currentHour in entityStartHour until entityEndHour
                }

                // 전체 멤버 중 몇 %가 가능 여부 반환
                val percentage =
                    if (totalMembers == 0) 0 else (availableMembers * 100 / totalMembers)
                getColorByLevel(percentage)
            }
        }
    }

    @Composable
    fun getEditableBackgroundColor(
        cellType: CellType,
        isSelected: Boolean
    ): Color = when (cellType) {
        CellType.Blank, CellType.DateHeader, CellType.TimeHeader -> Color.Transparent
        CellType.Data -> if (isSelected) NoostakTheme.colors.blue400 else Color.Transparent
    }

    @Composable
    fun getColorByLevel(level: Int): Color =
        when (AvailabilityLevel.entries.firstOrNull { level in it.range }) {
            AvailabilityLevel.NONE -> Color.Transparent
            AvailabilityLevel.FEW -> NoostakTheme.colors.blue50
            AvailabilityLevel.SOME -> NoostakTheme.colors.blue200
            AvailabilityLevel.MANY -> NoostakTheme.colors.blue400
            AvailabilityLevel.MOST -> NoostakTheme.colors.blue700
            else -> NoostakTheme.colors.blue800
        }

    fun getCellText(
        cellType: CellType,
        rowIndex: Int,
        columnIndex: Int,
        data: List<TimeEntity>
    ): String {
        val startHour = extractHour(data.first().startTime)

        return when (cellType) {
            CellType.Blank -> "\n"
            CellType.DateHeader -> {
                if (columnIndex == 0) {
                    ""
                } else {
                    formatDateTimeToCustomFormat(data[columnIndex - 1].date)
                }
            }

            CellType.TimeHeader -> "${startHour + (rowIndex - 1)}시"
            CellType.Data -> ""
        }
    }

    fun getSelectedTimes(
        selectedCells: List<Pair<Int, Int>>,
        availablePeriods: List<TimeEntity>
    ): List<TimeEntity> {
        val selectedTimes = mutableListOf<TimeEntity>()
        val selectedCellsByDate = selectedCells.groupBy { it.second }

        selectedCellsByDate.forEach { (dateColumnIndex, cells) ->
            val date = availablePeriods.getOrNull(dateColumnIndex - 1)?.date ?: return@forEach
            cells.forEach { (rowIndex, _) ->
                val startHour = extractHour(availablePeriods.first().startTime) + (rowIndex - 1)
                val endHour = startHour + 1
                selectedTimes.add(
                    TimeEntity(
                        date = "${extractDate(date)}T${String.format("%02d", startHour)}:00:00",
                        startTime = "${extractDate(date)}T${
                        String.format(
                            "%02d",
                            startHour
                        )
                        }:00:00",
                        endTime = "${extractDate(date)}T${String.format("%02d", endHour)}:00:00"
                    )
                )
            }
        }

        return selectedTimes
    }

    private fun formatDateTimeToCustomFormat(dateTime: String): String {
        val dayOfWeek = CalculateTimeFromString().extractDayOfWeek(dateTime)
        val date = CalculateTimeFromString().extractDateWithSlash(dateTime)
        return "$dayOfWeek\n$date"
    }

    private fun extractDate(dateTime: String): String = dateTime.substringBefore('T')

    private fun extractHour(dateTime: String): Int =
        dateTime.substringAfter('T').substringBefore(':').toInt()
}
