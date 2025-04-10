package com.sopt.domain.entity

data class CalendarEntity(
    val year: Int,
    val month: Int,
    val previousMonthAppointments: List<CalendarAppointmentDayEntity>,
    val currentMonthAppointments: List<CalendarAppointmentDayEntity>
)

data class CalendarAppointmentDayEntity(
    val day: Int,
    val appointments: List<CalendarAppointmentEntity>
)

data class CalendarAppointmentEntity(
    val appointmentId: Long,
    val optionId: Long,
    val name: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val duration: Long,
    val category: String
)
