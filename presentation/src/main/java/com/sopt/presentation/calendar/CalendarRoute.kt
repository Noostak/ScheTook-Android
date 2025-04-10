package com.sopt.presentation.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sopt.core.designsystem.component.bottomsheet.NoostakBottomSheet
import com.sopt.core.designsystem.component.calendar.WeekDaysHeader
import com.sopt.core.designsystem.component.calendar.YearMonthHeader
import com.sopt.core.designsystem.component.dialog.NoostakDialog
import com.sopt.core.designsystem.component.topappbar.NoostakLogoAppBar
import com.sopt.core.designsystem.screen.NoostakLoadingScreen
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.getYearMonthByPage
import com.sopt.core.extension.initialPage
import com.sopt.core.extension.pageCount
import com.sopt.core.state.UiState
import com.sopt.core.type.DialogType
import com.sopt.domain.entity.CalendarAppointmentEntity
import com.sopt.domain.entity.CalendarSchedule
import com.sopt.domain.entity.GroupEntity
import com.sopt.domain.entity.ScheduleEntity
import com.sopt.presentation.R
import com.sopt.presentation.calendar.component.CalendarFloatingActionDialog
import com.sopt.presentation.calendar.component.CalendarGroup
import com.sopt.presentation.calendar.component.bottomsheet.ScheduleDetailScreen
import com.sopt.presentation.calendar.component.bottomsheet.ScheduleListScreen
import java.time.LocalDate
import java.time.YearMonth

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarRoute(
    paddingValues: PaddingValues,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    navigateToGroupCreate: () -> Unit,
    navigateToGroupEnter: () -> Unit,
    navigateToAppointmentCreate: (Long) -> Unit
) {
    val navController = rememberNavController()

    val getGroupsState by calendarViewModel.getGroupsState.collectAsStateWithLifecycle()
    val getConfirmedDetailState by calendarViewModel.getConfirmedDetailState.collectAsStateWithLifecycle()

    val showAddDialog by calendarViewModel.showAddDialog.collectAsStateWithLifecycle()
    val showBottomSheet by calendarViewModel.showBottomSheet.collectAsStateWithLifecycle()
    val showDataErrorDialog by calendarViewModel.showDataErrorDialog.collectAsStateWithLifecycle()

    val scheduleMap by calendarViewModel.scheduleMap.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pageCount }
    )

    val currentYearMonth by remember { derivedStateOf { getYearMonthByPage(pagerState.currentPage) } }

    var clickDate by remember { mutableStateOf<LocalDate?>(null) }
    var clickOptionId by remember { mutableLongStateOf(-1) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                calendarViewModel.onMonthChanged(getYearMonthByPage(page))
            }
    }

    LaunchedEffect(key1 = calendarViewModel.sideEffects) {
        calendarViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is CalendarSideEffect.NavigateToGroupCreate -> navigateToGroupCreate()
                is CalendarSideEffect.NavigateToGroupEnter -> navigateToGroupEnter()
                is CalendarSideEffect.NavigateToAppointmentCreate -> navigateToAppointmentCreate(
                    calendarViewModel.selectedGroupId.value ?: -1
                )

                is CalendarSideEffect.ShowAddDialog -> calendarViewModel.showAddDialog(true)
                is CalendarSideEffect.ShowBottomSheet -> calendarViewModel.showBottomSheet(true)
                is CalendarSideEffect.ShowDataErrorDialog -> calendarViewModel.showDataErrorDialog(
                    true
                )
            }
        }
    }

    if (showDataErrorDialog) {
        NoostakDialog(
            dialogType = DialogType.DATA_FAILURE,
            onClick = {
                calendarViewModel.getConfirmedDetail(clickOptionId)
            },
            onDismissRequest = {
                calendarViewModel.showDataErrorDialog(false)
                navController.popBackStack()
            }
        )
    }

    if (showAddDialog) {
        CalendarFloatingActionDialog(
            onClick = { calendarViewModel.showAddDialog(false) },
            onDismissRequest = { calendarViewModel.showAddDialog(false) },
            onCreateGroupClick = {
                calendarViewModel.navigateToGroupCreate()
                calendarViewModel.showAddDialog(false)
            },
            onEnterGroupClick = {
                calendarViewModel.navigateToGroupEnter()
                calendarViewModel.showAddDialog(false)
            }
        )
    }

    if (showBottomSheet) {
        NoostakBottomSheet(
            onDismissRequest = {
                calendarViewModel.showBottomSheet(false)
                navController.popBackStack(SCHEDULE_LIST, inclusive = false)
            },
            content = {
                NavHost(navController, startDestination = SCHEDULE_LIST) {
                    composable(SCHEDULE_LIST) {
                        clickDate?.let { date ->
                            ScheduleListScreen(
                                data = ScheduleEntity(
                                    groupId = calendarViewModel.selectedGroupId.value ?: -1,
                                    date = date,
                                    scheduleList = calendarViewModel.selectedDayAppointments.value.map {
                                        CalendarAppointmentEntity(
                                            appointmentId = it.appointmentId,
                                            optionId = it.optionId,
                                            name = it.name,
                                            category = it.category,
                                            startTime = it.startTime,
                                            endTime = it.endTime,
                                            duration = it.duration,
                                            date = it.date
                                        )
                                    }
                                ),
                                onItemClick = { id ->
                                    clickOptionId = id
                                    calendarViewModel.getConfirmedDetail(clickOptionId)
                                    navController.navigate(SCHEDULE_DETAIL)
                                },
                                onCreateAppointmentBtnClick = {
                                    calendarViewModel.navigateToAppointmentCreate()
                                    calendarViewModel.showBottomSheet(false)
                                }
                            )
                        }
                    }
                    composable(SCHEDULE_DETAIL) {
                        when (getConfirmedDetailState) {
                            is UiState.Loading -> NoostakLoadingScreen()
                            is UiState.Success -> {
                                ScheduleDetailScreen(
                                    data = (getConfirmedDetailState as UiState.Success).data,
                                    onBackBtnClick = { navController.popBackStack() }
                                )
                            }

                            else -> Unit
                        }
                    }
                }
            }
        )
    }

    CalendarScreen(
        paddingValues = paddingValues,
        groups = (getGroupsState as? UiState.Success)?.data ?: emptyList(),
        scheduleMap = scheduleMap,
        pagerState = pagerState,
        currentYearMonth = currentYearMonth,
        showAddDialog = showAddDialog,
        onAddBtnClick = { calendarViewModel.showAddDialog(true) },
        onItemClick = { clickedDate ->
            clickDate = clickedDate
            calendarViewModel.onDayClicked(clickedDate)
        },
        onGroupClick = { groupId ->
            calendarViewModel.selectGroup(groupId)
        }
    )
}

@Composable
fun CalendarScreen(
    paddingValues: PaddingValues = PaddingValues(),
    groups: List<GroupEntity>,
    scheduleMap: Map<String, List<CalendarSchedule>>,
    pagerState: PagerState,
    currentYearMonth: YearMonth,
    showAddDialog: Boolean = false,
    onAddBtnClick: () -> Unit = {},
    onItemClick: (LocalDate) -> Unit = {},
    onGroupClick: (Long) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            NoostakLogoAppBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = dimensionResource(id = R.dimen.vertical_padding))
        ) {
            CalendarGroup(
                groups = groups,
                showAddDialog = showAddDialog,
                onAddBtnClick = onAddBtnClick,
                onGroupClick = onGroupClick
            )
            Spacer(modifier = Modifier.height(24.dp))
            CalendarContent(
                scheduleMap = scheduleMap,
                pagerState = pagerState,
                currentYearMonth = currentYearMonth,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
private fun CalendarContent(
    scheduleMap: Map<String, List<CalendarSchedule>>,
    pagerState: PagerState,
    currentYearMonth: YearMonth,
    modifier: Modifier = Modifier,
    onItemClick: (LocalDate) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NoostakTheme.colors.white)
            .padding(bottom = 34.dp)
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        YearMonthHeader(date = currentYearMonth)
        Spacer(modifier = Modifier.height(8.dp))
        WeekDaysHeader()
        CalendarMonthScreen(
            pagerState = pagerState,
            scheduleMap = scheduleMap,
            onItemClick = onItemClick
        )
    }
}

const val SCHEDULE_LIST = "schedule_list"
const val SCHEDULE_DETAIL = "schedule_detail"

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    NoostakAndroidTheme {
        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { pageCount }
        )

        CalendarScreen(
            groups = listOf(
                GroupEntity(
                    groupId = 0,
                    groupName = "그룹 이름",
                    groupMemberCount = 1,
                    groupProfileImageUrl = ""
                )
            ),
            scheduleMap = emptyMap(),
            pagerState = pagerState,
            currentYearMonth = getYearMonthByPage(initialPage)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenEmptyPreview() {
    NoostakAndroidTheme {
        CalendarScreen(
            groups = emptyList(),
            scheduleMap = emptyMap(),
            pagerState = rememberPagerState(
                initialPage = initialPage,
                pageCount = { pageCount }
            ),
            currentYearMonth = getYearMonthByPage(initialPage)
        )
    }
}
