package com.sopt.presentation.calendar

import androidx.lifecycle.viewModelScope
import com.sopt.core.extension.toDateString
import com.sopt.core.state.UiState
import com.sopt.core.util.BaseViewModel
import com.sopt.domain.entity.CalendarAppointmentDayEntity
import com.sopt.domain.entity.CalendarAppointmentEntity
import com.sopt.domain.entity.CalendarSchedule
import com.sopt.domain.entity.ConfirmedDetailEntity
import com.sopt.domain.entity.GroupEntity
import com.sopt.domain.repository.GroupDetailRepository
import com.sopt.domain.usecase.GetCalendarAppointmentsUseCase
import com.sopt.domain.usecase.GetCalendarGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getCalendarAppointmentsUseCase: GetCalendarAppointmentsUseCase,
    private val groupDetailRepository: GroupDetailRepository,
    private val getCalendarGroupsUseCase: GetCalendarGroupsUseCase
) :
    BaseViewModel<CalendarSideEffect>() {
    private val _getGroupsState: MutableStateFlow<UiState<List<GroupEntity>>> =
        MutableStateFlow(UiState.Empty)
    val getGroupsState: StateFlow<UiState<List<GroupEntity>>> get() = _getGroupsState.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> get() = _selectedGroupId

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog get() = _showAddDialog

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> get() = _showBottomSheet

    private val _showDataErrorDialog = MutableStateFlow(false)
    val showDataErrorDialog: StateFlow<Boolean> get() = _showDataErrorDialog

    private val _scheduleMap = MutableStateFlow<Map<String, List<CalendarSchedule>>>(emptyMap())
    val scheduleMap: StateFlow<Map<String, List<CalendarSchedule>>> get() = _scheduleMap

    private var currentYearMonth: YearMonth = YearMonth.now()

    private val _selectedDayAppointments =
        MutableStateFlow<List<CalendarAppointmentEntity>>(emptyList())
    val selectedDayAppointments: StateFlow<List<CalendarAppointmentEntity>> get() = _selectedDayAppointments

    private var _currentMonthAppointments = emptyList<CalendarAppointmentDayEntity>()

    private val _getConfirmedDetailState =
        MutableStateFlow<UiState<ConfirmedDetailEntity>>(UiState.Empty)
    val getConfirmedDetailState: StateFlow<UiState<ConfirmedDetailEntity>> =
        _getConfirmedDetailState.asStateFlow()

    init {
        getGroups()
    }

    fun getConfirmedDetail(optionId: Long) {
        viewModelScope.launch {
            _getConfirmedDetailState.emit(UiState.Loading)
            groupDetailRepository.getConfirmedDetail(optionId)
                .fold(onSuccess = { _getConfirmedDetailState.emit(UiState.Success(it)) },
                    onFailure = {
                        triggerDataErrorDialog()
                        _getConfirmedDetailState.emit(UiState.Failure(it.message.toString()))
                    })
        }
    }

    fun showDataErrorDialog(show: Boolean) {
        _showDataErrorDialog.update { show }
    }

    fun showAddDialog(show: Boolean) {
        _showAddDialog.update { show }
    }

    fun showBottomSheet(show: Boolean) {
        _showBottomSheet.update { show }
    }

    private fun triggerDataErrorDialog() {
        emitSideEffect(CalendarSideEffect.ShowDataErrorDialog)
    }

    private fun triggerShowBottomSheet() {
        emitSideEffect(CalendarSideEffect.ShowBottomSheet)
    }

    fun navigateToGroupCreate() {
        emitSideEffect(CalendarSideEffect.NavigateToGroupCreate)
    }

    fun navigateToGroupEnter() {
        emitSideEffect(CalendarSideEffect.NavigateToGroupEnter)
    }

    fun navigateToAppointmentCreate() {
        emitSideEffect(CalendarSideEffect.NavigateToAppointmentCreate)
    }

    private fun getGroups() {
        viewModelScope.launch {
            _getGroupsState.emit(UiState.Loading)
            getCalendarGroupsUseCase().fold(
                onSuccess = {
                    _getGroupsState.emit(UiState.Success(it))
                    if (it.isNotEmpty()) {
                        selectGroup(it.first().groupId)
                    }
                },
                onFailure = {
                    _getGroupsState.emit(UiState.Failure(it.message.toString()))
                }
            )
        }
    }

    fun selectGroup(groupId: Long) {
        _selectedGroupId.value = groupId
        getCalendarAppointments(currentYearMonth.year, currentYearMonth.monthValue)
    }

    private fun getCalendarAppointments(year: Int, month: Int) {
        viewModelScope.launch {
            selectedGroupId.value?.let {
                getCalendarAppointmentsUseCase(it, year, month)
                    .fold(
                        onSuccess = { response ->
                            val newScheduleMap =
                                response.currentMonthAppointments.associate { dayAppointments ->
                                    LocalDate.of(year, month, dayAppointments.day)
                                        .toDateString() to dayAppointments.appointments.map { appointment ->
                                        CalendarSchedule(
                                            scrapId = appointment.appointmentId,
                                            title = appointment.name,
                                            categoryType = appointment.category
                                        )
                                    }
                                }
                            _scheduleMap.value = newScheduleMap
                            _currentMonthAppointments = response.currentMonthAppointments
                        },
                        onFailure = { error ->
                            Timber.e("getCalendarAppointments Failed: ${error.message}")
                        }
                    )
            }
        }
    }

    // 현재 월 변경 시 캘린더 API 재호출
    fun onMonthChanged(newYearMonth: YearMonth) {
        if (currentYearMonth != newYearMonth) {
            currentYearMonth = newYearMonth
            getCalendarAppointments(newYearMonth.year, newYearMonth.monthValue)
        }
    }

    // 날짜 클릭
    fun onDayClicked(date: LocalDate) {
        val appointments = _currentMonthAppointments
            .firstOrNull {
                it.day == date.dayOfMonth &&
                        currentYearMonth.year == date.year &&
                        currentYearMonth.monthValue == date.monthValue
            }?.appointments ?: emptyList()

        _selectedDayAppointments.value = appointments
        triggerShowBottomSheet()
    }
}
