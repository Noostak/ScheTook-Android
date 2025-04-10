package com.sopt.data.mapper

import com.sopt.data.dto.response.ResponseAppointmentMembersInfoDto
import com.sopt.data.dto.response.ResponseAppointmentScheduleDto
import com.sopt.data.dto.response.ResponseGetTimeTableDto
import com.sopt.data.dto.response.base.BaseTimeDto
import com.sopt.domain.entity.AppointmentMembersInfoEntity
import com.sopt.domain.entity.AppointmentScheduleEntity
import com.sopt.domain.entity.TimeEntity
import com.sopt.domain.entity.TimeTableEntity

fun ResponseGetTimeTableDto.toTimeTableEntity() = TimeTableEntity(
    isAppointMemberTimeSet = isAppointmentMemberTimeSet,
    appointmentSchedule = appointmentSchedule.toAppointmentScheduleEntity()
)

fun ResponseAppointmentScheduleDto.toAppointmentScheduleEntity() = AppointmentScheduleEntity(
    duration = duration,
    appointmentHostSelectionTimes = appointmentHostSelectionTimes.map { it.toTimeEntity() },
    appointmentMembersInfo = appointmentMembersInfo.map {
        it?.toAppointmentMembersInfoEntity() ?: AppointmentMembersInfoEntity(
            memberId = -1,
            memberName = "나",
            appointmentMemberAvailableTimes = emptyList()
        )
    }
)

fun ResponseAppointmentMembersInfoDto.toAppointmentMembersInfoEntity() =
    AppointmentMembersInfoEntity(
        memberId = memberId,
        memberName = memberName,
        appointmentMemberAvailableTimes = appointmentMemberAvailableTimes.map { it.toTimeEntity() }
    )

fun BaseTimeDto.toTimeEntity() = TimeEntity(
    date = date,
    startTime = startTime,
    endTime = endTime
)

fun TimeEntity.toBaseTimeDto() = BaseTimeDto(
    date = date,
    startTime = startTime,
    endTime = endTime
)
