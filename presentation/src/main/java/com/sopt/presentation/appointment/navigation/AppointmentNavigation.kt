package com.sopt.presentation.appointment.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sopt.core.navigation.Route
import com.sopt.domain.entity.TimeEntity
import com.sopt.presentation.appointment.AppointmentRoute
import com.sopt.presentation.appointment.appointmentCheck.AppointmentCheckRoute
import com.sopt.presentation.appointment.appointmentConfirm.AppointmentConfirmRoute
import com.sopt.presentation.groupDetail.navigation.GroupDetail
import com.sopt.presentation.groupDetail.navigation.navigateGroupDetail
import kotlinx.serialization.Serializable
import timber.log.Timber

fun NavController.navigateAppointment(
    groupId: Long,
    appointmentId: Long,
    appointmentName: String,
    navOptions: NavOptions? = null
) {
    navigate(
        route = Appointment(
            groupId = groupId,
            appointmentId = appointmentId,
            appointmentName = appointmentName
        ),
        navOptions = navOptions ?: NavOptions.Builder()
            .setPopUpTo(
                route = GroupDetail(groupId = groupId),
                inclusive = false
            )
            .build()
    )
}

fun NavController.navigateAppointmentCheck(
    groupId: Long,
    appointmentId: Long,
    appointmentName: String,
    duration: Long,
    navOptions: NavOptions? = null
) {
    navigate(
        route = AppointmentCheck(
            groupId = groupId,
            appointmentId = appointmentId,
            appointmentName = appointmentName,
            duration = duration
        ),
        navOptions = navOptions
    )
}

fun NavController.navigateAppointmentConfirm(
    groupId: Long,
    appointmentId: Long,
    appointmentName: String,
    optionId: Long,
    isHost: Boolean,
    navOptions: NavOptions? = null
) {
    navigate(
        route = AppointmentConfirm(
            groupId = groupId,
            appointmentName = appointmentName,
            optionId = optionId,
            isHost = isHost
        ),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.appointmentNavGraph(
    navHostController: NavHostController
) {
    composable<Appointment> {
        val args = it.toRoute<Appointment>()
        AppointmentRoute(
            groupId = args.groupId,
            appointmentId = args.appointmentId,
            appointmentName = args.appointmentName,
            navigateUp = navHostController::navigateUp,
            navigateToAppointmentCheck = { groupId, appointmentId, appointmentName, availablePeriods, duration ->
                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                    "availablePeriods",
                    availablePeriods
                )
                navHostController.navigateAppointmentCheck(
                    groupId = groupId,
                    appointmentId = appointmentId,
                    appointmentName = appointmentName,
                    duration = duration
                )
            },
            navigateToAppointmentConfirm = { groupId, appointmentId, optionId, appointmentName, isHost ->
                navHostController.navigateAppointmentConfirm(
                    groupId = groupId,
                    appointmentId = appointmentId,
                    optionId = optionId,
                    appointmentName = appointmentName,
                    isHost = isHost
                )
            }
        )
    }

    composable<AppointmentCheck> {
        val args = it.toRoute<AppointmentCheck>()
        val availablePeriods = navHostController.previousBackStackEntry?.savedStateHandle?.get<List<TimeEntity>>(
            "availablePeriods"
        ) ?: emptyList()
        Timber.d("availablePeriods: $availablePeriods")
        AppointmentCheckRoute(
            groupId = args.groupId,
            appointmentId = args.appointmentId,
            appointmentName = args.appointmentName,
            availablePeriods = availablePeriods,
            duration = args.duration,
            navigateUp = navHostController::navigateUp,
            navigateToAppointment = { groupId, appointmentId, appointmentName ->
                navHostController.navigateAppointment(
                    groupId = groupId,
                    appointmentId = appointmentId,
                    appointmentName = appointmentName
                )
            },
            navigateToGroupDetail = { groupId ->
                navHostController.navigateGroupDetail(groupId = groupId)
            }
        )
    }

    composable<AppointmentConfirm> {
        val args = it.toRoute<AppointmentConfirm>()
        AppointmentConfirmRoute(
            groupId = args.groupId,
            optionId = args.optionId,
            appointmentName = args.appointmentName,
            isHost = args.isHost,
            navigateUp = navHostController::navigateUp,
            navigateToGroupDetail = { groupId ->
                navHostController.navigateGroupDetail(groupId = groupId)
            }
        )
    }
}

@Serializable
data class Appointment(
    val groupId: Long,
    val appointmentId: Long,
    val appointmentName: String
) : Route

@Serializable
data class AppointmentCheck(
    val groupId: Long,
    val appointmentId: Long,
    val appointmentName: String,
    val duration: Long
) : Route

@Serializable
data class AppointmentConfirm(
    val groupId: Long,
    val optionId: Long,
    val appointmentName: String,
    val isHost: Boolean
) : Route
