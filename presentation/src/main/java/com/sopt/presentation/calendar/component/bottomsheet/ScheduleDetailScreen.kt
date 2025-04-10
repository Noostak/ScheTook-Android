package com.sopt.presentation.calendar.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.core.designsystem.component.chip.AvailableUserChips
import com.sopt.core.designsystem.component.chip.NoostakCategoryChip
import com.sopt.core.designsystem.component.chip.UnavailableUserChips
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.util.RearrangeList
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.ConfirmedDetailEntity
import com.sopt.domain.entity.IdentityEntity
import com.sopt.presentation.R
import com.sopt.presentation.groupDetail.confirmedDetail.CompleteDetailInfo

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleDetailScreen(
    data: ConfirmedDetailEntity,
    onBackBtnClick: () -> Unit = {}
) {
    val rearrangeList = RearrangeList()
    val availableMembers = rearrangeList.rearrangeMembersBasedOnAvailability(
        data.myIdentity,
        data.availableMembers
    )
    val unavailableMembers = rearrangeList.rearrangeMembersBasedOnAvailability(
        data.myIdentity,
        data.unavailableMembers
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ScheduleDetailTopAppBar(
                title = data.appointmentName.chunked(10).joinToString("\n"),
                onBackButtonClick = onBackBtnClick
            )
        }
        HorizontalDivider(color = NoostakTheme.colors.gray100)
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CompleteDetailInfo(text = stringResource(R.string.tv_complete_detail_time)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Text(
                        text = CalculateTimeFromString().extractDateWithSlash(data.date),
                        color = NoostakTheme.colors.black,
                        style = NoostakTheme.typography.b4SemiBold
                    )
                    Text(
                        text = CalculateTimeFromString().extractHourWithZero(data.date),
                        color = NoostakTheme.colors.black,
                        style = NoostakTheme.typography.b4SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            CompleteDetailInfo(text = stringResource(R.string.tv_complete_detail_category)) {
                NoostakCategoryChip(text = data.category)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                CompleteDetailInfo(
                    text = stringResource(
                        R.string.tv_complete_detail_available,
                        data.availableMembers.size
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
            Spacer(modifier = Modifier.height(20.dp))
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
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleDetailScreenPreview() {
    NoostakAndroidTheme {
        ScheduleDetailScreen(
            ConfirmedDetailEntity(
                myIdentity = IdentityEntity(
                    availability = "available",
                    position = 0,
                    name = "김언지"
                ),
                appointmentName = "누스탁 전체회의 호이호이호이호이호이",
                date = "2024-09-07T00:00:00",
                startTime = "1/13 21:00",
                endTime = "1/13 21:00",
                category = "기타",
                availableMembersCount = 81,
                availableMembers = listOf(
                    "김언지", "하루", "야마다", "이누마키", "츠키시마",
                    "마이키", "호크스", "토도로키", "아이자와", "리바이", "이구로", "호시나", "신에이",
                    "하루", "야마다", "이누마키", "츠키시마",
                    "마이키", "호크스", "토도로키", "아이자와", "리바이", "이구로", "호시나", "신에이"
                ),
                unavailableMembersCount = 3,
                unavailableMembers = listOf("박보검", "정해인", "권지용")
            )
        )
    }
}
