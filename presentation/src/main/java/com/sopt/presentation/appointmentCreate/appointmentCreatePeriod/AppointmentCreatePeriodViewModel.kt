package com.sopt.presentation.appointmentCreate.appointmentCreatePeriod

import android.content.Context
import com.sopt.core.extension.stringOf
import com.sopt.core.util.BaseViewModel
import com.sopt.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.internal.immutableListOf
import javax.inject.Inject

@HiltViewModel
class AppointmentCreatePeriodViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel<AppointmentCreatePeriodSideEffect>() {
    val days = immutableListOf(
        context.stringOf(R.string.text_calendar_appointment_sun),
        context.stringOf(R.string.text_calendar_appointment_mon),
        context.stringOf(R.string.text_calendar_appointment_tues),
        context.stringOf(R.string.text_calendar_appointment_wed),
        context.stringOf(R.string.text_calendar_appointment_thu),
        context.stringOf(R.string.text_calendar_appointment_fri),
        context.stringOf(R.string.text_calendar_appointment_sat)
    )

    fun navigateToAppointmentCreateTimePicker(
        groupId: Long,
        appointmentName: String,
        appointmentCategory: String,
        appointmentDuration: Int,
        isSingleDateMode: Boolean,
        appointmentDate: List<String>
    ) {
        emitSideEffect(
            AppointmentCreatePeriodSideEffect.NavigateToTimePicker(
                groupId = groupId,
                appointmentName,
                appointmentCategory,
                appointmentDuration,
                isSingleDateMode,
                appointmentDate
            )
        )
    }

    fun navigateUp() {
        emitSideEffect(AppointmentCreatePeriodSideEffect.NavigateUp)
    }

    fun showSnackBar() {
        emitSideEffect(AppointmentCreatePeriodSideEffect.ShowSnackBar(R.string.text_calendar_appointment_7days))
    }
}

sealed class AppointmentCreatePeriodSideEffect {
    data class NavigateToTimePicker(
        val groupId: Long,
        val appointmentName: String,
        val appointmentCategory: String,
        val appointmentDuration: Int,
        val isSingleDateMode: Boolean,
        val appointmentDate: List<String>
    ) : AppointmentCreatePeriodSideEffect()

    data object NavigateUp : AppointmentCreatePeriodSideEffect()
    data class ShowSnackBar(val message: Int) : AppointmentCreatePeriodSideEffect()
}
