package com.sopt.data.mapper

import com.sopt.data.dto.response.ResponseGetGroupConfirmedDetailDto
import com.sopt.domain.entity.ConfirmedDetailEntity
import com.sopt.domain.entity.IdentityEntity

fun ResponseGetGroupConfirmedDetailDto.toConfirmedDetailEntity() = ConfirmedDetailEntity(
    myIdentity = IdentityEntity(
        availability = myInfo.availability,
        position = myInfo.position,
        name = myInfo.name
    ),
    appointmentName = appointmentName,
    date = appointmentTime.date,
    startTime = appointmentTime.startTime,
    endTime = appointmentTime.endTime,
    category = category,
    availableMembersCount = availableFriends.count,
    availableMembers = availableFriends.names,
    unavailableMembersCount = unavailableFriends.count,
    unavailableMembers = unavailableFriends.names
)
