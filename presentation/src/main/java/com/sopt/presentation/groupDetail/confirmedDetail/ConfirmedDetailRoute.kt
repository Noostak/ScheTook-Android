package com.sopt.presentation.groupDetail.confirmedDetail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sopt.core.designsystem.component.chip.AvailableUserChips
import com.sopt.core.designsystem.component.chip.NoostakCategoryChip
import com.sopt.core.designsystem.component.chip.UnavailableUserChips
import com.sopt.core.designsystem.component.topappbar.NoostakTopAppBar
import com.sopt.core.designsystem.screen.NoostakFailureScreen
import com.sopt.core.designsystem.screen.NoostakLoadingScreen
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.state.UiState
import com.sopt.core.util.RearrangeList
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.ConfirmedDetailEntity
import com.sopt.presentation.R

@Composable
fun ConfirmedDetailRoute(
    groupId: Long,
    confirmedId: Long,
    appointmentName: String,
    navigateUp: () -> Unit,
    confirmedDetailViewModel: ConfirmedDetailViewModel = hiltViewModel()
) {
    val confirmedDetailState by confirmedDetailViewModel.confirmedDetailState.collectAsState()

    LaunchedEffect(Unit) {
        confirmedDetailViewModel.getConfirmedDetail(confirmedId)
    }

    LaunchedEffect(confirmedDetailViewModel.sideEffects) {
        confirmedDetailViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is ConfirmedDetailSideEffect.NavigateUp -> navigateUp()
            }
        }
    }

    when (confirmedDetailState) {
        is UiState.Loading -> {
            NoostakLoadingScreen()
        }

        is UiState.Failure -> {
            NoostakFailureScreen(
                onBackButtonClick = confirmedDetailViewModel::navigateUp,
                onRetryButtonClick = { confirmedDetailViewModel.getConfirmedDetail(confirmedId) }
            )
        }

        is UiState.Success -> {
            ConfirmedDetailScreen(
                appointmentName = appointmentName,
                data = (confirmedDetailState as UiState.Success).data,
                onBackButtonClick = confirmedDetailViewModel::navigateUp
            )
        }

        else -> {
            NoostakLoadingScreen()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfirmedDetailScreen(
    appointmentName: String,
    data: ConfirmedDetailEntity,
    onBackButtonClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            NoostakTopAppBar(
                title = appointmentName,
                modifier = Modifier.fillMaxWidth(),
                isIconVisible = true,
                onBackButtonClick = { onBackButtonClick() }
            )
        }
    ) { innerPadding ->
        val calculateTime = CalculateTimeFromString()
        val date = calculateTime.extractDateWithSlash(data.date)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_padding))
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.header_complete_detail),
                color = NoostakTheme.colors.gray800,
                style = NoostakTheme.typography.t4Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = NoostakTheme.colors.gray200
                    )
                    .padding(dimensionResource(id = R.dimen.default_padding)),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CompleteDetailInfo(text = stringResource(R.string.tv_complete_detail_time)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        Text(
                            text = date,
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
        }
    }
}

@Composable
fun CompleteDetailInfo(
    text: String,
    content: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = NoostakTheme.colors.black,
            style = NoostakTheme.typography.c3Regular
        )
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCompleteDetailScreen() {
    NoostakAndroidTheme {
        val confirmedDetailViewModel: ConfirmedDetailViewModel = hiltViewModel()
        ConfirmedDetailScreen(
            appointmentName = "3차 회의",
            data = confirmedDetailViewModel.mockConfirmedDetail,
            onBackButtonClick = {}
        )
    }
}
