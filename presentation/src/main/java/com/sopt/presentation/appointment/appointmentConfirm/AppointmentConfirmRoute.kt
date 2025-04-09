package com.sopt.presentation.appointment.appointmentConfirm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sopt.core.designsystem.component.button.NoostakBottomButton
import com.sopt.core.designsystem.component.chip.AvailableUserChips
import com.sopt.core.designsystem.component.chip.NoostakCategoryChip
import com.sopt.core.designsystem.component.chip.UnavailableUserChips
import com.sopt.core.designsystem.component.dialog.NoostakDialog
import com.sopt.core.designsystem.component.topappbar.NoostakTopAppBar
import com.sopt.core.designsystem.screen.NoostakFailureScreen
import com.sopt.core.designsystem.screen.NoostakLoadingScreen
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.showIf
import com.sopt.core.state.UiState
import com.sopt.core.util.RearrangeList
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.AppointmentDetailEntity
import com.sopt.domain.entity.IdentityEntity
import com.sopt.presentation.R
import com.sopt.presentation.groupDetail.confirmedDetail.CompleteDetailInfo
import timber.log.Timber

@Composable
fun AppointmentConfirmRoute(
    groupId: Long,
    optionId: Long,
    appointmentName: String,
    isHost: Boolean,
    navigateUp: () -> Unit,
    navigateToGroupDetail: (Long) -> Unit,
    appointmentConfirmViewModel: AppointmentConfirmViewModel = hiltViewModel()
) {
    val showErrorDialog by appointmentConfirmViewModel.showErrorDialog.collectAsStateWithLifecycle()
    val getConfirmedState by appointmentConfirmViewModel.getConfirmedState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = appointmentConfirmViewModel.sideEffects) {
        appointmentConfirmViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is AppointmentConfirmSideEffect.NavigateUp -> navigateUp()
                is AppointmentConfirmSideEffect.NavigateToGroupDetail -> {
                    navigateToGroupDetail(sideEffect.groupId)
                }

                is AppointmentConfirmSideEffect.ShowErrorDialog -> appointmentConfirmViewModel.showErrorDialog(
                    sideEffect.show,
                    sideEffect.dialogType
                )
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        appointmentConfirmViewModel.getOptionDetail(optionId)
    }

    when (getConfirmedState) {
        is UiState.Loading -> NoostakLoadingScreen()
        is UiState.Success -> {
            AppointmentConfirmScreen(
                groupId = groupId,
                appointmentName = appointmentName,
                isHost = isHost,
                onBackButtonClick = appointmentConfirmViewModel::navigateUp,
                onConfirmButtonClick = {
                    appointmentConfirmViewModel.postOptionConfirm(groupId, optionId)
                },
                data = (getConfirmedState as UiState.Success).data
            )
        }

        is UiState.Failure -> {
            Timber.e("getConfirmedState is failure $getConfirmedState")
            NoostakFailureScreen(
                onBackButtonClick = appointmentConfirmViewModel::navigateUp,
                onRetryButtonClick = {
                    appointmentConfirmViewModel.getOptionDetail(optionId)
                }
            )
        }

        else -> Unit
    }

    if (showErrorDialog.first) {
        NoostakDialog(
            dialogType = showErrorDialog.second,
            onClick = {
                appointmentConfirmViewModel.showErrorDialog(false, showErrorDialog.second)
                appointmentConfirmViewModel.postOptionConfirm(groupId, optionId)
            },
            onDismissRequest = {
                appointmentConfirmViewModel.showErrorDialog(false, showErrorDialog.second)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppointmentConfirmScreen(
    groupId: Long,
    appointmentName: String,
    isHost: Boolean = false,
    onBackButtonClick: () -> Unit,
    onConfirmButtonClick: (Long) -> Unit,
    data: AppointmentDetailEntity
) {
    val calculateTime = CalculateTimeFromString()
    val date = calculateTime.extractDateWithKorean(data.date)
    val dayOfWeek = calculateTime.extractDayOfWeekWithBraces(data.date)
    val startHour = calculateTime.extractHourWithZero(data.startTime)
    val rearrangeList = RearrangeList()
    val availableMembers = rearrangeList.rearrangeMembersBasedOnAvailability(
        data.myIdentity,
        data.availableMembers
    )
    val unavailableMembers = rearrangeList.rearrangeMembersBasedOnAvailability(
        data.myIdentity,
        data.unavailableMembers
    )
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            NoostakTopAppBar(
                title = appointmentName,
                isIconVisible = true,
                onBackButtonClick = onBackButtonClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(id = R.dimen.horizontal_padding))
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.title_appointment_confirm),
                color = NoostakTheme.colors.black,
                style = NoostakTheme.typography.h4Bold
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.subtitle_appointment_confirm),
                color = NoostakTheme.colors.black,
                style = NoostakTheme.typography.c3Regular
            )
            Text(
                modifier = Modifier.padding(top = 20.dp, start = 3.dp, bottom = 12.dp),
                text = stringResource(R.string.tv_appointment_confirm_info),
                color = NoostakTheme.colors.black,
                style = NoostakTheme.typography.t4Bold
            )
            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = NoostakTheme.colors.blue700
                    )
                    .background(
                        color = NoostakTheme.colors.blue50,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(dimensionResource(id = R.dimen.default_padding)),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CompleteDetailInfo(text = stringResource(R.string.tv_complete_detail_time)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        Text(
                            text = "$date $dayOfWeek",
                            color = NoostakTheme.colors.black,
                            style = NoostakTheme.typography.b4SemiBold
                        )
                        Text(
                            text = startHour,
                            color = NoostakTheme.colors.black,
                            style = NoostakTheme.typography.b4SemiBold
                        )
                    }
                }
                CompleteDetailInfo(text = stringResource(R.string.tv_complete_detail_category)) {
                    NoostakCategoryChip(text = data.category)
                }
                Column {
                    CompleteDetailInfo(
                        text = stringResource(
                            R.string.tv_complete_detail_available,
                            data.availableMembersCount
                        )
                    )
                    FlowRow(
                        modifier = Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AvailableUserChips(
                            members = availableMembers,
                            myIdentity = data.myIdentity
                        )
                    }
                }
                Column {
                    CompleteDetailInfo(
                        text = stringResource(
                            R.string.tv_complete_detail_unavailable,
                            data.unavailableMembersCount
                        )
                    )
                    FlowRow(
                        modifier = Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UnavailableUserChips(
                            members = unavailableMembers,
                            myIdentity = data.myIdentity
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            NoostakBottomButton(
                modifier = Modifier.showIf(isHost),
                text = stringResource(R.string.btn_appointment_confirm_complete),
                onButtonClick = { onConfirmButtonClick(groupId) },
                isEnabled = true,
                activateColor = NoostakTheme.colors.gray900
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentConfirmScreenPreview() {
    NoostakAndroidTheme {
        AppointmentConfirmScreen(
            groupId = 1,
            appointmentName = "약속 이름",
            isHost = true,
            onBackButtonClick = {},
            onConfirmButtonClick = {},
            data = AppointmentDetailEntity(
                myIdentity = IdentityEntity(
                    availability = "UNAVAILABLE",
                    position = 2,
                    name = "박영수"
                ),
                date = "2025-01-06T00:00:00",
                startTime = "2025-01-06T11:00:00",
                endTime = "2025-01-06T14:00:00",
                category = "기타",
                availableMembersCount = 22,
                availableMembers = listOf(
                    "선우정아", "대한민국만세", "최영희", "정영수",
                    "이가을", "김언지", "박유진", "임하늘", "변우석", "김혜윤", "정해인", "카리나", "닝닝",
                    "지젤", "장원영", "이채연", "김민주", "김채원", "김민주", "김채원", "김민주"
                ),
                unavailableMembersCount = 5,
                unavailableMembers = listOf("한강", "이영희", "박영수", "최영희", "정영수")
            )
        )
    }
}
