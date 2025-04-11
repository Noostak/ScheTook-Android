package com.sopt.presentation.appointment.appointmentCheck

import androidx.lifecycle.viewModelScope
import com.sopt.core.state.UiState
import com.sopt.core.type.DialogType
import com.sopt.core.util.BaseViewModel
import com.sopt.domain.entity.TimeEntity
import com.sopt.domain.repository.AppointmentConfirmRepository
import com.sopt.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentCheckViewModel @Inject constructor(
    private val appointmentConfirmRepository: AppointmentConfirmRepository
) : BaseViewModel<AppointmentCheckSideEffect>() {
    private val _showErrorDialog = MutableStateFlow(Pair(false, DialogType.DATA_FAILURE))
    val showErrorDialog: StateFlow<Pair<Boolean, DialogType>> get() = _showErrorDialog.asStateFlow()

    private val _postTimeTableState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Empty)

    fun postTimeTable(
        groupId: Long,
        appointmentId: Long,
        appointmentName: String,
        availableTimes: List<TimeEntity>
    ) {
        viewModelScope.launch {
            _postTimeTableState.emit(UiState.Loading)
            appointmentConfirmRepository.postTimeTable(appointmentId, availableTimes).fold(
                onSuccess = {
                    _postTimeTableState.emit(UiState.Success(it))
                    emitSideEffect(
                        AppointmentCheckSideEffect.NavigateToAppointment(
                            groupId,
                            appointmentId,
                            appointmentName
                        )
                    )
                },
                onFailure = { throwable ->
                    when (throwable) {
                        is IOException -> {
                            _postTimeTableState.emit(UiState.Failure(throwable.message.toString()))
                            emitSideEffect(
                                AppointmentCheckSideEffect.ShowErrorDialog(
                                    true,
                                    DialogType.NETWORK_FAILURE
                                )
                            )
                        }

                        else -> {
                            _postTimeTableState.emit(UiState.Failure(throwable.message.toString()))
                            emitSideEffect(
                                AppointmentCheckSideEffect.ShowErrorDialog(
                                    true,
                                    DialogType.DATA_FAILURE
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    fun isSelectedDataValid(duration: Long, selectedDate: List<TimeEntity>): Boolean {
        if (selectedDate.isEmpty()) {
            emitSideEffect(AppointmentCheckSideEffect.ShowSnackBar(R.string.sb_appointment_check_invalid))
            return false
        }

        val sorted = selectedDate.sortedBy { LocalDateTime.parse(it.startTime) }
        var currentStart = LocalDateTime.parse(sorted.first().startTime)
        var currentEnd = LocalDateTime.parse(sorted.first().endTime)

        for (i in 1 until sorted.size) {
            val nextStart = LocalDateTime.parse(sorted[i].startTime)
            val nextEnd = LocalDateTime.parse(sorted[i].endTime)

            if (nextStart == currentEnd) {
                currentEnd = nextEnd
            } else {
                val blockDurationInHours = Duration.between(currentStart, currentEnd).toHours()
                if (blockDurationInHours >= duration) return true

                currentStart = nextStart
                currentEnd = nextEnd
            }
        }

        val finalDurationInHours = Duration.between(currentStart, currentEnd).toHours()
        if (finalDurationInHours >= duration) return true

        emitSideEffect(AppointmentCheckSideEffect.ShowSnackBar(R.string.sb_appointment_check_invalid))
        return false
    }

    fun showErrorDialog(show: Boolean, dialogType: DialogType) {
        _showErrorDialog.update { it.copy(first = show, second = dialogType) }
    }

    fun navigateUp() {
        emitSideEffect(AppointmentCheckSideEffect.NavigateUp)
    }

    fun navigateToGroupDetail(groupId: Long) {
        emitSideEffect(AppointmentCheckSideEffect.NavigateToGroupDetail(groupId))
    }
}

sealed class AppointmentCheckSideEffect {
    data object NavigateUp : AppointmentCheckSideEffect()
    data class NavigateToAppointment(
        val groupId: Long,
        val appointmentId: Long,
        val appointmentName: String
    ) : AppointmentCheckSideEffect()

    data class NavigateToGroupDetail(val groupId: Long) : AppointmentCheckSideEffect()
    data class ShowErrorDialog(val show: Boolean, val dialogType: DialogType) :
        AppointmentCheckSideEffect()

    data class ShowSnackBar(val message: Int) : AppointmentCheckSideEffect()
}
