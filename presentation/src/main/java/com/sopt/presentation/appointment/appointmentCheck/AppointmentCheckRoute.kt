package com.sopt.presentation.appointment.appointmentCheck

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sopt.core.designsystem.component.button.NoostakBottomButton
import com.sopt.core.designsystem.component.dialog.NoostakDialog
import com.sopt.core.designsystem.component.snackbar.NoostakSnackBar
import com.sopt.core.designsystem.component.snackbar.SNACK_BAR_DURATION
import com.sopt.core.designsystem.component.timetable.NoostakEditableTimeTable
import com.sopt.core.designsystem.component.topappbar.NoostakTopAppBar
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.domain.entity.TimeEntity
import com.sopt.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun AppointmentCheckRoute(
    groupId: Long,
    appointmentId: Long,
    appointmentName: String,
    availablePeriods: List<TimeEntity>,
    duration: Long,
    navigateUp: () -> Unit,
    navigateToAppointment: (Long, Long, String) -> Unit,
    navigateToGroupDetail: (Long) -> Unit,
    appointmentCheckViewModel: AppointmentCheckViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showErrorDialog by appointmentCheckViewModel.showErrorDialog.collectAsStateWithLifecycle()
    var selectedData by remember { mutableStateOf(emptyList<TimeEntity>()) }
    val rememberedAvailablePeriods = remember { availablePeriods }
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackBarVisible = remember { mutableStateOf(false) }

    val onShowFailureSnackBar: (message: String) -> Unit = {
        coroutineScope.launch {
            snackBarVisible.value = true
            val job = launch { snackBarHostState.showSnackbar(message = it) }
            delay(SNACK_BAR_DURATION)
            job.cancel()
            snackBarVisible.value = false
        }
    }

    LaunchedEffect(key1 = appointmentCheckViewModel.sideEffects) {
        appointmentCheckViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is AppointmentCheckSideEffect.NavigateUp -> navigateUp()
                is AppointmentCheckSideEffect.NavigateToAppointment -> {
                    navigateToAppointment(
                        sideEffect.groupId,
                        sideEffect.appointmentId,
                        sideEffect.appointmentName
                    )
                }

                is AppointmentCheckSideEffect.NavigateToGroupDetail -> {
                    navigateToGroupDetail(sideEffect.groupId)
                }

                is AppointmentCheckSideEffect.ShowErrorDialog -> appointmentCheckViewModel.showErrorDialog(
                    sideEffect.show,
                    sideEffect.dialogType
                )

                is AppointmentCheckSideEffect.ShowSnackBar -> onShowFailureSnackBar(
                    context.getString(sideEffect.message, duration / 60)
                )
            }
        }
    }

    AppointmentCheckScreen(
        groupId = groupId,
        appointmentName = appointmentName,
        availablePeriods = rememberedAvailablePeriods,
        onSelectedDataChange = { selectedData = it },
        onBackButtonClick = appointmentCheckViewModel::navigateToGroupDetail,
        onConfirmButtonClick = {
            if (appointmentCheckViewModel.isSelectedDataValid(duration/60, selectedData)) {
                appointmentCheckViewModel.postTimeTable(
                    groupId,
                    appointmentId,
                    appointmentName,
                    selectedData
                )
            }
        },
        snackBarHostState = snackBarHostState,
        snackBarVisible = snackBarVisible
    )

    if (showErrorDialog.first) {
        NoostakDialog(
            dialogType = showErrorDialog.second,
            onClick = {
                appointmentCheckViewModel.showErrorDialog(false, showErrorDialog.second)
                appointmentCheckViewModel.postTimeTable(
                    groupId,
                    appointmentId,
                    appointmentName,
                    selectedData
                )
            },
            onDismissRequest = {
                appointmentCheckViewModel.showErrorDialog(false, showErrorDialog.second)
            }
        )
    }
}

@Composable
fun AppointmentCheckScreen(
    groupId: Long,
    appointmentName: String,
    availablePeriods: List<TimeEntity>,
    onSelectedDataChange: (List<TimeEntity>) -> Unit = {},
    onBackButtonClick: (Long) -> Unit,
    onConfirmButtonClick: () -> Unit,
    snackBarHostState: SnackbarHostState,
    snackBarVisible: MutableState<Boolean>
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            NoostakTopAppBar(
                title = appointmentName,
                isIconVisible = true,
                onBackButtonClick = { onBackButtonClick(groupId) }
            )
        },
        snackbarHost = {
            AnimatedVisibility(
                visible = snackBarVisible.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                SnackbarHost(
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.bottom_padding_snack_bar_non_exist_code)),
                    hostState = snackBarHostState,
                    snackbar = { snackBarData ->
                        NoostakSnackBar(
                            message = snackBarData.visuals.message,
                            textStyle = NoostakTheme.typography.c3SemiBold,
                            textColor = NoostakTheme.colors.red01,
                            backgroundColor = NoostakTheme.colors.pink
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.default_padding))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 11.dp, start = 6.dp, bottom = 16.dp),
                    text = stringResource(R.string.title_appointment_check),
                    color = NoostakTheme.colors.black,
                    style = NoostakTheme.typography.h4Bold,
                    textAlign = TextAlign.Start
                )
                NoostakEditableTimeTable(
                    availablePeriods = availablePeriods,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onSelectedDataChange(it)
                    Timber.d("selectedData: $it")
                }
            }
            NoostakBottomButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimensionResource(id = R.dimen.vertical_padding))
                    .zIndex(1f),
                text = stringResource(R.string.btn_appointment_check),
                onButtonClick = onConfirmButtonClick,
                isEnabled = true,
                activateColor = NoostakTheme.colors.gray900
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppointmentConfirmScreen() {
    NoostakAndroidTheme {
        AppointmentCheckScreen(
            groupId = 1,
            appointmentName = "3차 회의",
            availablePeriods = listOf(
                TimeEntity(
                    date = "2024-09-05T10:00:00",
                    startTime = "2024-09-05T10:00:00",
                    endTime = "2024-09-05T23:00:00"
                ),
                TimeEntity(
                    date = "2024-09-06T10:00:00",
                    startTime = "2024-09-06T10:00:00",
                    endTime = "2024-09-06T18:00:00"
                ),
                TimeEntity(
                    date = "2024-09-07T10:00:00",
                    startTime = "2024-09-07T10:00:00",
                    endTime = "2024-09-07T18:00:00"
                )
            ),
            onBackButtonClick = {},
            onConfirmButtonClick = {},
            snackBarHostState = SnackbarHostState(),
            snackBarVisible = remember { mutableStateOf(true) }
        )
    }
}
