package com.sopt.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetCalendarDto(
    @SerialName("year") val year: Int,
    @SerialName("month") val month: Int,
    @SerialName("previousMonthAppointments") val previousMonthAppointments: List<CalendarAppointmentDayDto>,
    @SerialName("currentMonthAppointments") val currentMonthAppointments: List<CalendarAppointmentDayDto>
)

@Serializable
data class CalendarAppointmentDayDto(
    @SerialName("day") val day: Int,
    @SerialName("appointments") val appointments: List<CalendarAppointmentDto>
)

@Serializable
data class CalendarAppointmentDto(
    @SerialName("appointmentId") val appointmentId: Long,
    @SerialName("optionId") val optionId: Long,
    @SerialName("name") val name: String,
    @SerialName("date") val date: String,
    @SerialName("startTime") val startTime: String,
    @SerialName("endTime") val endTime: String,
    @SerialName("duration") val duration: Long,
    @SerialName("category") val category: String
)
