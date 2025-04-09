package com.sopt.presentation.appointmentCreate.appointmentSubmitComplete

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sopt.core.designsystem.component.button.NoostakBottomButton
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.presentation.R
import com.sopt.presentation.appointmentCreate.appointmentSubmit.AppointmentInfoRow

@Composable
fun AppointmentSubmitCompleteRoute(
    groupId: Long,
    appointmentName: String,
    isConsecutive: Boolean,
    appointmentDate: List<String>,
    appointmentTime: String? = null,
    appointmentCategory: String,
    appointmentDuration: Int,
    navigateToGroupDetail: (Long) -> Unit,
    appointmentSubmitCompleteViewModel: AppointmentSubmitCompleteViewModel = hiltViewModel()
) {
    BackHandler {
        appointmentSubmitCompleteViewModel.navigateToGroupDetail(groupId)
    }

    LaunchedEffect(key1 = appointmentSubmitCompleteViewModel.sideEffects) {
        appointmentSubmitCompleteViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is AppointmentSubmitCompleteSideEffect.NavigateToGroupDetail -> {
                    navigateToGroupDetail(sideEffect.groupId)
                }
            }
        }
    }

    AppointmentSubmitCompleteScreen(
        groupId = groupId,
        appointmentName = appointmentName,
        isConsecutive = isConsecutive,
        appointmentDate = appointmentDate,
        appointmentTime = appointmentTime,
        appointmentCategory = appointmentCategory,
        appointmentDuration = appointmentDuration,
        onConfirmButtonClick = appointmentSubmitCompleteViewModel::navigateToGroupDetail
    )
}

@Composable
fun AppointmentSubmitCompleteScreen(
    groupId: Long,
    appointmentName: String,
    isConsecutive: Boolean,
    appointmentDate: List<String>,
    appointmentTime: String? = null,
    appointmentCategory: String,
    appointmentDuration: Int,
    onConfirmButtonClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(dimensionResource(id = R.dimen.default_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 76.dp, bottom = 40.dp),
            text = stringResource(R.string.header_appointment_submit_complete),
            color = NoostakTheme.colors.gray900,
            style = NoostakTheme.typography.h2Bold
        )
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_appointment_submit_complete),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(25.26.dp))
        SubmittedAppointmentInfoBox(
            appointmentName = appointmentName,
            isConsecutive = isConsecutive,
            appointmentDate = appointmentDate,
            appointmentTime = appointmentTime,
            appointmentCategory = appointmentCategory,
            appointmentDuration = appointmentDuration
        )
        Spacer(modifier = Modifier.weight(1f))
        NoostakBottomButton(
            text = stringResource(R.string.btn_appointment_submit_complete),
            isEnabled = true,
            activateColor = NoostakTheme.colors.gray900,
            onButtonClick = { onConfirmButtonClick(groupId) }
        )
    }
}

@Composable
fun SubmittedAppointmentInfoBox(
    appointmentName: String,
    isConsecutive: Boolean,
    appointmentDate: List<String>,
    appointmentTime: String? = null,
    appointmentCategory: String,
    appointmentDuration: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = NoostakTheme.colors.blue50,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 27.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = appointmentName,
            color = NoostakTheme.colors.gray900,
            style = NoostakTheme.typography.h4Bold
        )
        AppointmentInfoRow(
            icon = R.drawable.ic_appointment_calendar,
            label = stringResource(R.string.text_appointment_submit_time),
            content = if (!isConsecutive) {
                val firstDateParts = appointmentDate.first().split("-")
                val lastDateParts = appointmentDate.last().split("-")
                "${firstDateParts[1].toInt()}/${firstDateParts[2].toInt()} ~ ${lastDateParts[1].toInt()}/${lastDateParts[2].toInt()}"
            } else {
                appointmentDate.joinToString(", ") { date ->
                    val parts = date.split("-")
                    "${parts[1].toInt()}/${parts[2].toInt()}"
                }
            },
            additionalContent = appointmentTime
        )
        AppointmentInfoRow(
            icon = R.drawable.ic_appointment_category,
            label = stringResource(R.string.text_appointment_submit_category),
            content = appointmentCategory,
            isChip = true
        )
        AppointmentInfoRow(
            icon = R.drawable.ic_appointment_clock,
            label = stringResource(R.string.text_appointment_submit_duration),
            content = stringResource(R.string.text_appointment_submit_hour, appointmentDuration)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppointmentSubmitCompleteRoute() {
    NoostakAndroidTheme {
        AppointmentSubmitCompleteScreen(
            groupId = 1,
            appointmentName = "누스탁 3차 회의",
            isConsecutive = false,
            appointmentDate = listOf("9/27", "9/28", "9/29", "9/30"),
            appointmentTime = "10:00 ~ 11:00",
            appointmentCategory = "기타",
            appointmentDuration = 2,
            onConfirmButtonClick = { }
        )
    }
}
