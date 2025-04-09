package com.sopt.presentation.groupDetail

sealed class GroupDetailSideEffect {
    data object NavigateUp : GroupDetailSideEffect()
    data object NavigateToGroup : GroupDetailSideEffect()
    data class NavigateToConfirmedDetail(
        val groupId: Long,
        val confirmedId: Long,
        val appointmentName: String
    ) :
        GroupDetailSideEffect()

    data class NavigateToGroupMember(val groupId: Long) : GroupDetailSideEffect()
    data class NavigateToAppointment(
        val groupId: Long,
        val appointmentsId: Long,
        val appointmentName: String
    ) : GroupDetailSideEffect()
    data class NavigateToAppointmentCreate(val groupId: Long) : GroupDetailSideEffect()
}
