package com.sopt.domain.entity

import java.time.LocalDate

data class ScheduleEntity(
    val groupId: Long = -1,
    val date: LocalDate,
    val scheduleList: List<CalendarAppointmentEntity>
)
