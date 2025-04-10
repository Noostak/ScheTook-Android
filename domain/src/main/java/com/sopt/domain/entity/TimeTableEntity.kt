package com.sopt.domain.entity

import kotlinx.serialization.Serializable

data class TimeTableEntity(
    val isAppointMemberTimeSet: Boolean,
    val appointmentSchedule: AppointmentScheduleEntity
)

data class AppointmentScheduleEntity(
    val duration: Long,
    val appointmentHostSelectionTimes: List<TimeEntity>,
    val appointmentMembersInfo: List<AppointmentMembersInfoEntity>
)

data class AppointmentMembersInfoEntity(
    val memberId: Int,
    val memberName: String,
    val appointmentMemberAvailableTimes: List<TimeEntity>
)

@Serializable
data class TimeEntity(
    val date: String,
    val startTime: String,
    val endTime: String
)
