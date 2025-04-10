package com.sopt.data.mapper

import com.sopt.data.dto.response.CalendarAppointmentDayDto
import com.sopt.data.dto.response.CalendarAppointmentDto
import com.sopt.data.dto.response.ResponseGetCalendarDto
import com.sopt.domain.entity.CalendarAppointmentDayEntity
import com.sopt.domain.entity.CalendarAppointmentEntity
import com.sopt.domain.entity.CalendarEntity

fun ResponseGetCalendarDto.toCalendarEntity() = CalendarEntity(
    year = year,
    month = month,
    previousMonthAppointments = previousMonthAppointments.map { it.toCalendarAppointmentDayEntity() },
    currentMonthAppointments = currentMonthAppointments.map { it.toCalendarAppointmentDayEntity() }
)

fun CalendarAppointmentDayDto.toCalendarAppointmentDayEntity() = CalendarAppointmentDayEntity(
    day = day,
    appointments = appointments.map { it.toCalendarAppointmentEntity() }
)

fun CalendarAppointmentDto.toCalendarAppointmentEntity() = CalendarAppointmentEntity(
    appointmentId = appointmentId,
    optionId = optionId,
    name = name,
    date = date,
    startTime = startTime,
    endTime = endTime,
    duration = duration,
    category = category
)
