package com.sopt.presentation.groupDetail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.sopt.core.extension.stringOf
import com.sopt.core.state.UiState
import com.sopt.core.util.BaseViewModel
import com.sopt.domain.entity.ConfirmedEntity
import com.sopt.domain.entity.GroupDetailEntity
import com.sopt.domain.entity.GroupOngoingAppointmentsEntity
import com.sopt.domain.entity.ProgressEntity
import com.sopt.domain.repository.GroupDetailRepository
import com.sopt.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.immutableListOf
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val groupDetailRepository: GroupDetailRepository
) : BaseViewModel<GroupDetailSideEffect>() {

    private val _groupOngoingState = MutableStateFlow<UiState<GroupOngoingAppointmentsEntity>>(UiState.Empty)
    val groupOngoingState: StateFlow<UiState<GroupOngoingAppointmentsEntity>> = _groupOngoingState

    private val _groupConfirmedState = MutableStateFlow<UiState<List<ConfirmedEntity>>>(UiState.Empty)
    val groupConfirmedState: StateFlow<UiState<List<ConfirmedEntity>>> = _groupConfirmedState

    fun getGroupOngoingAppointments(groupId: Long) {
        viewModelScope.launch {
            _groupOngoingState.value = UiState.Loading
            groupDetailRepository.getGroupOngoingAppointments(groupId)
                .onSuccess { result -> _groupOngoingState.value = UiState.Success(result) }
                .onFailure {
                    Timber.e(it)
                    _groupOngoingState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    fun getGroupConfirmedAppointments(groupId: Long) {
        viewModelScope.launch {
            _groupConfirmedState.value = UiState.Loading
            groupDetailRepository.getGroupConfirmedAppointments(groupId)
                .onSuccess { result ->
                    _groupConfirmedState.value = UiState.Success(
                        result.confirmedAppointments.map {
                            ConfirmedEntity(
                                appointmentId = it.appointmentId,
                                appointmentOptionId = it.appointmentOptionId,
                                appointmentName = it.appointmentName,
                                date = it.appointmentTime.date,
                                startTime = it.appointmentTime.startTime,
                                endTime = it.appointmentTime.endTime,
                                category = it.category
                            )
                        }
                    )
                }
                .onFailure {
                    Timber.e(it)
                    _groupConfirmedState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    fun navigateUp() {
        emitSideEffect(GroupDetailSideEffect.NavigateUp)
    }

    fun navigateToGroup() {
        emitSideEffect(GroupDetailSideEffect.NavigateToGroup)
    }

    fun navigateToConfirmedDetail(groupId: Long, confirmedId: Long, appointmentName: String) {
        emitSideEffect(
            GroupDetailSideEffect.NavigateToConfirmedDetail(
                groupId,
                confirmedId,
                appointmentName
            )
        )
    }

    fun navigateToGroupMember(groupId: Long) {
        emitSideEffect(GroupDetailSideEffect.NavigateToGroupMember(groupId))
    }

    fun navigateToAppointment(groupId: Long, appointmentsId: Long, appointmentName: String) {
        emitSideEffect(
            GroupDetailSideEffect.NavigateToAppointment(
                groupId,
                appointmentsId,
                appointmentName
            )
        )
    }

    fun navigateToAppointmentCreate(groupId: Long) {
        emitSideEffect(GroupDetailSideEffect.NavigateToAppointmentCreate(groupId))
    }

    val tabs = immutableListOf(
        context.stringOf(R.string.tab_group_detail_progress),
        context.stringOf(R.string.tab_group_detail_confirmed)
    )

    val mockGroupDetail = GroupDetailEntity(
        groupName = "누스탁",
        groupImage = "https://avatars.githubusercontent.com/u/91470334?v=4",
        groupMembersCount = 10,
        progressEntities = listOf(
            ProgressEntity(
                appointmentId = 1,
                appointmentName = "1주차",
                startDate = "2025-01-06T11:00:00",
                endDate = "2025-01-06T14:00:00",
                participants = 3,
                maxParticipants = 5
            ),
            ProgressEntity(
                appointmentId = 2,
                appointmentName = "2주차",
                startDate = "2025-01-06T11:00:00",
                endDate = "2025-01-06T14:00:00",
                participants = 2,
                maxParticipants = 5
            ),
            ProgressEntity(
                appointmentId = 3,
                appointmentName = "3주차",
                startDate = "2025-01-06T11:00:00",
                endDate = "2025-01-06T14:00:00",
                participants = 5,
                maxParticipants = 5
            ),
            ProgressEntity(
                appointmentId = 4,
                appointmentName = "4주차",
                startDate = "2025-01-06T11:00:00",
                endDate = "2025-01-06T14:00:00",
                participants = 0,
                maxParticipants = 5
            )
        ),
        confirmedEntities = listOf(
            ConfirmedEntity(
                appointmentId = 1,
                appointmentName = "3차 회의",
                date = "2025-01-06T14:00:00",
                startTime = "2025-01-06T14:00:00",
                endTime = "2025-01-06T15:00:00",
                category = "기타",
                appointmentOptionId = 1
            ),
            ConfirmedEntity(
                appointmentId = 2,
                appointmentName = "회의",
                date = "2025-01-06T14:00:00",
                startTime = "2025-01-06T14:00:00",
                endTime = "2025-01-06T15:00:00",
                category = "일정",
                appointmentOptionId = 1
            )
        )
    )
}
