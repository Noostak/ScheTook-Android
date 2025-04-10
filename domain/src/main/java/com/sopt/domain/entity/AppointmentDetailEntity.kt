package com.sopt.domain.entity

data class AppointmentDetailEntity(
    val myIdentity: IdentityEntity,
    val appointmentName: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val category: String = "",
    val availableMembersCount: Int = 0,
    val availableMembers: List<String>,
    val unavailableMembersCount: Int = 0,
    val unavailableMembers: List<String>
)
