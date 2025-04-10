package com.sopt.presentation.appointmentCreate.appointmentCreateInfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sopt.core.designsystem.component.button.NoostakBottomButton
import com.sopt.core.designsystem.component.chip.NoostakCalendarChip
import com.sopt.core.designsystem.component.progressbar.NoostakProgressBar
import com.sopt.core.designsystem.component.snackbar.NoostakSnackBar
import com.sopt.core.designsystem.component.snackbar.SNACK_BAR_DURATION
import com.sopt.core.designsystem.component.text.NoostakHeaderText
import com.sopt.core.designsystem.component.text.NoostakSubHeaderText
import com.sopt.core.designsystem.component.textfield.NoostakTextField
import com.sopt.core.designsystem.component.topappbar.NoostakTopAppBar
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable
import com.sopt.core.type.TextFieldType
import com.sopt.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppointmentCreateInfoRoute(
    groupId: Long,
    navigateUp: () -> Unit,
    navigateToPeriod: (Long, String, String, Int) -> Unit,
    calendarInfoViewModel: AppointmentCreateInfoViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val snackBarVisible = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onShowSnackBar: (String) -> Unit = { msg ->
        coroutineScope.launch {
            snackBarVisible.value = true
            val job = launch { snackBarHostState.showSnackbar(message = msg) }
            delay(SNACK_BAR_DURATION)
            job.cancel()
            snackBarVisible.value = false
        }
    }

    LaunchedEffect(key1 = calendarInfoViewModel.sideEffects) {
        calendarInfoViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is AppointmentCreateInfoSideEffect.NavigateToPeriod -> {
                    navigateToPeriod(
                        sideEffect.groupId,
                        sideEffect.appointmentName,
                        sideEffect.appointmentCategory,
                        sideEffect.appointmentDuration
                    )
                }

                is AppointmentCreateInfoSideEffect.NavigateUp -> {
                    navigateUp()
                }

                is AppointmentCreateInfoSideEffect.ShowSnackBar -> onShowSnackBar(
                    context.getString(sideEffect.message)
                )
            }
        }
    }

    AppointmentCreateInfoScreen(
        snackBarHostState = snackBarHostState,
        snackBarVisible = snackBarVisible,
        showSnackBar = calendarInfoViewModel::showSnackBar,
        onBackButtonClick = calendarInfoViewModel::navigateUp,
        onButtonClick = calendarInfoViewModel::navigateToAppointmentCreatePeriod,
        categories = calendarInfoViewModel.categories,
        groupId = groupId
    )
}

@Composable
fun AppointmentCreateInfoScreen(
    groupId: Long,
    onButtonClick: (Long, String, String, Int) -> Unit,
    onBackButtonClick: () -> Unit,
    categories: List<String>,
    snackBarHostState: SnackbarHostState,
    snackBarVisible: MutableState<Boolean>,
    showSnackBar: () -> Unit
) {
    var appointmentName by remember { mutableStateOf("") }
    var appointmentCategory by remember { mutableStateOf("") }
    var appointmentDuration by remember { mutableStateOf("") }
    var hasInput by remember { mutableStateOf(false) }
    val isOnlySpace = hasInput && appointmentName.isBlank()

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            NoostakTopAppBar(
                title = stringResource(R.string.text_calendar_appointment),
                isIconVisible = true,
                onBackButtonClick = { onBackButtonClick() }
            )
        },
        snackbarHost = {
            AnimatedVisibility(
                visible = snackBarVisible.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                SnackbarHost(
                    modifier = Modifier.padding(bottom = 96.dp),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(id = R.dimen.horizontal_padding))
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            NoostakProgressBar(progressBar = listOf(true, false, false))
            NoostakHeaderText(text = stringResource(R.string.text_calendar_appointment_write))
            NoostakSubHeaderText(
                stringResource(R.string.text_calendar_appointment_name),
                Modifier.padding(top = 22.dp, bottom = 10.dp)
            )
            NoostakTextField(
                textFieldType = TextFieldType.CALENDAR,
                cursorColor = NoostakTheme.colors.gray500,
                shape = RoundedCornerShape(10.dp),
                focusedBorderColor = if (isOnlySpace) NoostakTheme.colors.red02 else NoostakTheme.colors.blue600,
                unfocusedBorderColor = if (isOnlySpace) NoostakTheme.colors.red02 else NoostakTheme.colors.gray500,
                maxLength = 20,
                placeholderColor = NoostakTheme.colors.gray500,
                textStyle = NoostakTheme.typography.b1SemiBold,
                maxLengthColor = NoostakTheme.colors.gray500,
                onValueChange = {
                    appointmentName = it
                    hasInput = true
                },
                value = appointmentName
            )
            NoostakSubHeaderText(
                stringResource(R.string.text_calendar_appointment_category),
                Modifier.padding(top = 32.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = appointmentCategory == category
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .noRippleClickable { appointmentCategory = category }
                    ) {
                        NoostakCalendarChip(
                            text = category,
                            textStyle = NoostakTheme.typography.b4SemiBold,
                            textColor = if (isSelected) NoostakTheme.colors.white else NoostakTheme.colors.gray900,
                            backgroundColor = if (isSelected) NoostakTheme.colors.black else NoostakTheme.colors.white,
                            borderColor = if (isSelected) Color.Transparent else NoostakTheme.colors.gray200,
                            horizontalPaddingValues = 20.dp,
                            verticalPaddingValues = 8.dp
                        )
                    }
                }
            }
            NoostakSubHeaderText(
                stringResource(R.string.text_calendar_appointment_time),
                Modifier.padding(top = 41.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                var text by remember { mutableStateOf(TextFieldValue("")) }
                var isFocused by remember { mutableStateOf(false) }
                val isTimeExceedLimit = (text.text.toIntOrNull() ?: 0) > 10

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { newValue ->
                            if (newValue.text.length <= 2) {
                                text = newValue
                                appointmentDuration = newValue.text
                            }
                        },
                        textStyle = NoostakTheme.typography.b1SemiBold.copy(textAlign = TextAlign.Right),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 44.dp)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            }
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp),
                        thickness = 2.dp,
                        color = if (isTimeExceedLimit) {
                            NoostakTheme.colors.red02
                        } else if (isFocused) {
                            NoostakTheme.colors.blue600
                        } else {
                            NoostakTheme.colors.gray500
                        }
                    )
                    Text(
                        text = stringResource(R.string.text_calendar_appointment_time_text),
                        style = NoostakTheme.typography.c3SemiBold,
                        color = if (isTimeExceedLimit) NoostakTheme.colors.red02 else NoostakTheme.colors.gray500,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.text_calendar_appointment_duration),
                    style = NoostakTheme.typography.b1SemiBold,
                    color = NoostakTheme.colors.gray700,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 15.dp, bottom = 15.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            NoostakBottomButton(
                text = stringResource(R.string.text_calendar_appointment_next),
                onButtonClick = {
                    val trimmedName = appointmentName.trim()
                    if (trimmedName.isEmpty()) {
                        showSnackBar()
                        return@NoostakBottomButton
                    }
                    val time = appointmentDuration.toIntOrNull() ?: 0
                    onButtonClick(groupId, trimmedName, appointmentCategory, time)
                },
                isEnabled = appointmentName.isNotEmpty() && appointmentCategory.isNotBlank() &&
                    appointmentDuration.isNotBlank() &&
                    (appointmentDuration.toIntOrNull()?.let { it in 1..10 } == true),
                deactivateColor = NoostakTheme.colors.gray500,
                activateColor = NoostakTheme.colors.gray900
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentCreateInfoScreenPreview() {
    NoostakAndroidTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        val snackBarVisible = remember { mutableStateOf(false) }
        AppointmentCreateInfoScreen(
            groupId = 0,
            onButtonClick = { _, _, _, _ -> },
            onBackButtonClick = { },
            categories = listOf("중요", "일정", "취미", "기타"),
            snackBarHostState = snackBarHostState,
            snackBarVisible = snackBarVisible,
            showSnackBar = {
                snackBarVisible.value = true
            }
        )
    }
}
