package com.sopt.presentation.groupDetail

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sopt.core.designsystem.component.button.NoostakFloatingActionButton
import com.sopt.core.designsystem.component.topappbar.NoostakTopAppBar
import com.sopt.core.designsystem.screen.NoostakFailureScreen
import com.sopt.core.designsystem.screen.NoostakLoadingScreen
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable
import com.sopt.core.state.UiState
import com.sopt.core.util.NoRippleInteractionSource
import com.sopt.domain.entity.ConfirmedEntity
import com.sopt.domain.entity.ProgressEntity
import com.sopt.presentation.R
import com.sopt.presentation.groupDetail.screen.ConfirmedScreen
import com.sopt.presentation.groupDetail.screen.ProgressScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun GroupDetailRoute(
    groupId: Long,
    navigateUp: () -> Unit,
    navigateToGroup: () -> Unit,
    navigateToConfirmedDetail: (Long, Long, String) -> Unit,
    navigateToGroupMember: (Long) -> Unit,
    navigateToAppointment: (Long, Long, String) -> Unit,
    navigateToAppointmentCreate: (Long) -> Unit,
    groupDetailViewModel: GroupDetailViewModel = hiltViewModel()
) {
    val groupOngoingState by groupDetailViewModel.groupOngoingState.collectAsState()
    val groupConfirmedState by groupDetailViewModel.groupConfirmedState.collectAsState()
    BackHandler {
        groupDetailViewModel.navigateToGroup()
    }

    LaunchedEffect(Unit) {
        groupDetailViewModel.getGroupOngoingAppointments(groupId)
        groupDetailViewModel.getGroupConfirmedAppointments(groupId)
    }

    LaunchedEffect(key1 = groupDetailViewModel.sideEffects) {
        groupDetailViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is GroupDetailSideEffect.NavigateUp -> navigateUp()
                is GroupDetailSideEffect.NavigateToGroup -> navigateToGroup()
                is GroupDetailSideEffect.NavigateToConfirmedDetail -> {
                    navigateToConfirmedDetail(
                        sideEffect.groupId,
                        sideEffect.confirmedId,
                        sideEffect.appointmentName
                    )
                }
                is GroupDetailSideEffect.NavigateToGroupMember -> navigateToGroupMember(sideEffect.groupId)
                is GroupDetailSideEffect.NavigateToAppointment -> navigateToAppointment(
                    sideEffect.groupId,
                    sideEffect.appointmentsId,
                    sideEffect.appointmentName
                )
                is GroupDetailSideEffect.NavigateToAppointmentCreate -> navigateToAppointmentCreate(sideEffect.groupId)
            }
        }
    }

    when {
        groupOngoingState is UiState.Loading || groupConfirmedState is UiState.Loading -> {
            NoostakLoadingScreen()
        }

        groupOngoingState is UiState.Failure || groupConfirmedState is UiState.Failure -> {
            NoostakFailureScreen(
                onBackButtonClick = groupDetailViewModel::navigateUp,
                onRetryButtonClick = {
                    groupDetailViewModel.getGroupOngoingAppointments(groupId)
                    groupDetailViewModel.getGroupConfirmedAppointments(groupId)
                }
            )
        }

        groupOngoingState is UiState.Success && groupConfirmedState is UiState.Success -> {
            val groupOngoing = (groupOngoingState as UiState.Success).data
            val confirmedAppointments = (groupConfirmedState as UiState.Success).data

            GroupDetailScreen(
                groupId = groupId,
                tabs = groupDetailViewModel.tabs,
                groupName = groupOngoing.groupOngoingInfo.groupName,
                groupImage = groupOngoing.groupOngoingInfo.groupProfileImageUrl,
                groupInvitationCode = groupOngoing.groupOngoingInfo.groupInviteCode,
                groupMembersCount = groupOngoing.groupOngoingInfo.groupMemberCount.toInt(),
                progressEntities = groupOngoing.ongoingAppointments.map {
                    ProgressEntity(
                        appointmentId = it.appointmentId,
                        appointmentName = it.appointmentName,
                        startDate = it.appointmentTime.startTime,
                        endDate = it.appointmentTime.endTime,
                        participants = it.availableGroupMemberCount.toInt(),
                        maxParticipants = groupOngoing.groupOngoingInfo.groupMemberCount.toInt()
                    )
                },
                confirmedEntities = confirmedAppointments,
                onBackButtonClick = groupDetailViewModel::navigateUp,
                onConfirmedClick = groupDetailViewModel::navigateToConfirmedDetail,
                onGroupMemberClick = groupDetailViewModel::navigateToGroupMember,
                onProgressClick = groupDetailViewModel::navigateToAppointment,
                onAppointmentCreateClick = groupDetailViewModel::navigateToAppointmentCreate
            )
        }

        else -> {
            NoostakLoadingScreen()
        }
    }
}

@Composable
fun GroupDetailScreen(
    groupId: Long,
    tabs: List<String>,
    groupName: String,
    groupImage: String?,
    groupInvitationCode: String = "",
    groupMembersCount: Int,
    progressEntities: List<ProgressEntity>,
    confirmedEntities: List<ConfirmedEntity>,
    onBackButtonClick: () -> Unit,
    onConfirmedClick: (Long, Long, String) -> Unit,
    onGroupMemberClick: (Long) -> Unit,
    onProgressClick: (Long, Long, String) -> Unit,
    onAppointmentCreateClick: (Long) -> Unit
) {
    val pagerState = rememberPagerState { tabs.size }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            NoostakTopAppBar(
                title = stringResource(R.string.appbar_group_detail),
                modifier = Modifier,
                isIconVisible = false,
                onBackButtonClick = onBackButtonClick
            )
        },
        floatingActionButton = {
            NoostakFloatingActionButton(
                title = stringResource(R.string.fab_group_detail),
                modifier = Modifier.offset(x = 0.dp, y = (-51).dp)
            ) {
                onAppointmentCreateClick(groupId)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_padding))
        ) {
            GroupDetailHeader(
                groupInvitationCode = groupInvitationCode,
                groupImage = groupImage,
                groupName = groupName
            )
            Row(
                modifier = Modifier
                    .padding(top = 9.dp)
                    .noRippleClickable { onGroupMemberClick(groupId) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.tv_group_detail_member, groupMembersCount),
                    color = NoostakTheme.colors.gray800,
                    style = NoostakTheme.typography.b2Regular
                )
                Image(
                    modifier = Modifier.padding(start = 5.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_group_detail_arrow),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.tv_group_detail_list),
                color = NoostakTheme.colors.gray800,
                style = NoostakTheme.typography.b1SemiBold
            )
            Spacer(modifier = Modifier.height(9.dp))
            CustomTabPager(
                groupId = groupId,
                pagerState = pagerState,
                tabs = tabs,
                progressEntities = progressEntities,
                confirmedEntities = confirmedEntities,
                onProgressClick = onProgressClick,
                onConfirmedClick = onConfirmedClick
            )
        }
    }
}

@Composable
fun CustomTabPager(
    groupId: Long,
    pagerState: PagerState,
    tabs: List<String>,
    progressEntities: List<ProgressEntity>,
    confirmedEntities: List<ConfirmedEntity>,
    onProgressClick: (Long, Long, String) -> Unit,
    onConfirmedClick: (Long, Long, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = NoostakTheme.colors.gray900,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .clip(CircleShape),
                    height = 4.dp,
                    color = NoostakTheme.colors.gray900
                )
            },
            divider = {
                HorizontalDivider(
                    modifier = Modifier.clip(CircleShape),
                    thickness = 4.dp,
                    color = NoostakTheme.colors.gray200
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    selectedContentColor = NoostakTheme.colors.gray900,
                    unselectedContentColor = NoostakTheme.colors.gray500,
                    interactionSource = NoRippleInteractionSource
                ) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = title,
                        style = NoostakTheme.typography.b1SemiBold
                    )
                }
            }
        }
        HorizontalPager(state = pagerState) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (page) {
                    0 -> ProgressScreen(
                        groupId = groupId,
                        progressEntities = progressEntities,
                        onItemClicked = { groupId, appointmentsId, appointmentName ->
                            onProgressClick(groupId, appointmentsId, appointmentName)
                        }
                    )

                    1 -> ConfirmedScreen(
                        groupId = groupId,
                        confirmedEntities = confirmedEntities,
                        onItemClicked = { groupId, optionId, name ->
                            onConfirmedClick(groupId, optionId, name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupDetailHeader(
    groupInvitationCode: String,
    groupImage: String?,
    groupName: String
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(7.14.dp))
                    .size(40.dp),
                model = groupImage,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_group_profile),
                error = painterResource(id = R.drawable.ic_group_profile),
                contentScale = ContentScale.FillBounds
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = groupName,
                color = NoostakTheme.colors.gray900,
                style = NoostakTheme.typography.h1Bold
            )
        }
        Icon(
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            groupInvitationCode
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    ContextCompat.startActivity(context, shareIntent, null)
                },
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_share),
            contentDescription = null,
            tint = NoostakTheme.colors.gray700
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupDetailRoutePreview() {
    NoostakAndroidTheme {
        GroupDetailScreen(
            groupId = 0,
            tabs = persistentListOf("진행 중", "확정"),
            groupName = "누스탁",
            groupImage = "https://avatars.githubusercontent.com/u/91470334?v=4",
            groupMembersCount = 10,
            progressEntities = listOf(
                ProgressEntity(
                    appointmentId = 1,
                    appointmentName = "1차 회의",
                    startDate = "2025-01-06T14:00:00",
                    endDate = "2025-01-06T15:00:00",
                    participants = 5,
                    maxParticipants = 10
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
            ),
            onBackButtonClick = {},
            onConfirmedClick = { _, _, _ -> },
            onGroupMemberClick = {},
            onProgressClick = { _, _, _ -> },
            onAppointmentCreateClick = {}
        )
    }
}
