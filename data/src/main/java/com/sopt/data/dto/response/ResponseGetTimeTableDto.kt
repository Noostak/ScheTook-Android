package com.sopt.data.dto.response

import com.sopt.data.dto.response.base.BaseTimeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetTimeTableDto(
    @SerialName("isAppointmentMemberTimeSet") val isAppointmentMemberTimeSet: Boolean,
    @SerialName("appointmentSchedule") val appointmentSchedule: ResponseAppointmentScheduleDto
)

@Serializable
data class ResponseAppointmentScheduleDto(
    @SerialName("duration") val duration: Long,
    @SerialName("appointmentHostSelectionTimes") val appointmentHostSelectionTimes: List<BaseTimeDto>,
    @SerialName("appointmentMembersInfo") val appointmentMembersInfo: List<ResponseAppointmentMembersInfoDto?>
)

@Serializable
data class ResponseAppointmentMembersInfoDto(
    @SerialName("memberId") val memberId: Int,
    @SerialName("memberName") val memberName: String,
    @SerialName("appointmentMemberAvailableTimes") val appointmentMemberAvailableTimes: List<BaseTimeDto>
)
