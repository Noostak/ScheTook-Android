package com.sopt.presentation.groupDetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sopt.core.navigation.Route
import com.sopt.presentation.appointment.navigation.navigateAppointment
import com.sopt.presentation.appointmentCreate.navigation.navigateAppointmentCreateInfo
import com.sopt.presentation.group.navigation.Group
import com.sopt.presentation.group.navigation.navigateGroup
import com.sopt.presentation.groupDetail.GroupDetailRoute
import com.sopt.presentation.groupDetail.confirmedDetail.ConfirmedDetailRoute
import com.sopt.presentation.groupDetail.groupMember.GroupMemberRoute
import kotlinx.serialization.Serializable

fun NavController.navigateGroupDetail(
    groupId: Long,
    navOptions: NavOptions? = null
) {
    navigate(
        route = GroupDetail(groupId = groupId),
        navOptions = navOptions ?: NavOptions.Builder()
            .setPopUpTo(
                route = Group,
                inclusive = false
            )
            .build()
    )
}

fun NavController.navigateConfirmedDetail(
    groupId: Long,
    confirmedId: Long,
    appointmentName: String,
    navOptions: NavOptions? = null
) {
    navigate(
        route = ConfirmedDetail(
            groupId = groupId,
            confirmedId = confirmedId,
            appointmentName = appointmentName
        ),
        navOptions = navOptions
    )
}

fun NavController.navigateGroupMember(
    groupId: Long,
    navOptions: NavOptions? = null
) {
    navigate(
        route = GroupMember(groupId = groupId),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.groupDetailNavGraph(
    navHostController: NavController
) {
    composable<GroupDetail> {
        val args = it.toRoute<GroupDetail>() // 이전 화면에서 데이터 전달 받기
        GroupDetailRoute(
            groupId = args.groupId,
            navigateUp = navHostController::navigateUp,
            navigateToGroup = navHostController::navigateGroup,
            navigateToConfirmedDetail = { groupId, confirmedId, appointmentName ->
                navHostController.navigateConfirmedDetail(
                    groupId = groupId,
                    confirmedId = confirmedId,
                    appointmentName = appointmentName
                )
            },
            navigateToGroupMember = { groupId ->
                navHostController.navigateGroupMember(groupId = groupId)
            },
            navigateToAppointment = { groupId, appointmentsId, appointmentName ->
                navHostController.navigateAppointment(
                    groupId = groupId,
                    appointmentId = appointmentsId,
                    appointmentName = appointmentName
                )
            },
            navigateToAppointmentCreate = { groupId ->
                navHostController.navigateAppointmentCreateInfo(groupId = groupId)
            }
        )
    }

    composable<ConfirmedDetail> {
        val args = it.toRoute<ConfirmedDetail>() // 이전 화면에서 데이터 전달 받기
        ConfirmedDetailRoute(
            groupId = args.groupId,
            confirmedId = args.confirmedId,
            appointmentName = args.appointmentName,
            navigateUp = navHostController::navigateUp
        )
    }

    composable<GroupMember> {
        val args = it.toRoute<GroupMember>() // 이전 화면에서 데이터 전달 받기
        GroupMemberRoute(
            groupId = args.groupId,
            navigateUp = navHostController::navigateUp
        )
    }
}

@Serializable
data class GroupDetail(
    val groupId: Long
) : Route

@Serializable
data class ConfirmedDetail(
    val groupId: Long,
    val confirmedId: Long,
    val appointmentName: String
) : Route

@Serializable
data class GroupMember(
    val groupId: Long
) : Route
