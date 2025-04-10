package com.sopt.presentation.appointment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sopt.core.designsystem.component.chip.AvailableUserChips
import com.sopt.core.designsystem.component.chip.UnavailableUserChips
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable
import com.sopt.core.extension.showIf
import com.sopt.core.util.RearrangeList
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.IdentityEntity
import com.sopt.domain.entity.OptionEntity
import com.sopt.domain.entity.RecommendationPriorityEntity
import com.sopt.presentation.R

@Composable
fun RecommendationScreen(
    isHost: Boolean,
    selectedItemIndex: Int,
    data: List<RecommendationPriorityEntity>,
    onConfirmButtonClick: (Long) -> Unit,
    onLikeClick: (Long, Boolean) -> Unit = { _, _ -> }
) {
    val filteredData = data[selectedItemIndex].options
    var selectedItemId by remember { mutableStateOf<Long?>(null) }

    // filteredData가 변경될 때마다 초기화
    LaunchedEffect(filteredData) {
        selectedItemId = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_padding))
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(filteredData, key = { it.id }) { recommendation ->
                RecommendationItem(
                    data = recommendation,
                    isSelected = if (isHost) selectedItemId == recommendation.id else false,
                    onItemClick = {
                        selectedItemId =
                            if (selectedItemId == recommendation.id) null else recommendation.id
                    },
                    onLikeClick = { isLiked ->
                        onLikeClick(recommendation.id, isLiked)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        NoostakBottomButton(
            modifier = Modifier.showIf(isHost),
            text = stringResource(R.string.btn_appointment_confirm),
            onButtonClick = { selectedItemId?.let { onConfirmButtonClick(it) } },
            isEnabled = selectedItemId != null,
            deactivateColor = NoostakTheme.colors.gray500,
            activateColor = NoostakTheme.colors.gray900
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_padding)))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecommendationItem(
    data: OptionEntity,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onLikeClick: (Boolean) -> Unit,
    recommendationViewModel: RecommendationViewModel = hiltViewModel()
) {
    val likeState by remember {
        derivedStateOf {
            recommendationViewModel.likeStates[data.id] ?: (data.liked to data.likes)
        }
    }
    val (isLiked, likes) = likeState
    val calculateTime = CalculateTimeFromString()
    val date = calculateTime.extractDateWithKorean(data.date)
    val dayOfWeek = calculateTime.extractDayOfWeekWithBraces(data.date)
    val startHour = calculateTime.extractHourWithZero(data.startTime)
    val endHour = calculateTime.extractHourWithZero(data.endTime)
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
            .background(
                color = if (isSelected) NoostakTheme.colors.blue50 else NoostakTheme.colors.white,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) NoostakTheme.colors.blue700 else NoostakTheme.colors.gray100,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(dimensionResource(id = R.dimen.default_padding))
            .noRippleClickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$date $dayOfWeek",
                color = NoostakTheme.colors.black,
                style = NoostakTheme.typography.t4Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.text_brace_between_hours, startHour, endHour),
                color = NoostakTheme.colors.black,
                style = NoostakTheme.typography.t4Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .background(
                        color = NoostakTheme.colors.white,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = NoostakTheme.colors.gray100,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 13.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.noRippleClickable {
                        recommendationViewModel.toggleLike(data.id, data.liked, data.likes)
                        onLikeClick(!isLiked)
                    },
                    imageVector = if (isLiked) {
                        ImageVector.vectorResource(id = R.drawable.ic_heart_on)
                    } else {
                        ImageVector.vectorResource(id = R.drawable.ic_heart_off)
                    },
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = likes.toString(),
                    color = if (isLiked) NoostakTheme.colors.black else NoostakTheme.colors.gray600,
                    style = NoostakTheme.typography.c3SemiBold
                )
            }
        }
        Text(
            text = stringResource(
                R.string.header_appointment_available,
                data.availableMemberCount
            ),
            color = NoostakTheme.colors.black,
            style = NoostakTheme.typography.c3SemiBold
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
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(
                R.string.header_appointment_unavailable,
                data.unavailableMemberCount
            ),
            color = NoostakTheme.colors.black,
            style = NoostakTheme.typography.c3SemiBold
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

@Preview(showBackground = true)
@Composable
fun RecommendationScreenPreview() {
    NoostakAndroidTheme {
        RecommendationScreen(
            isHost = true,
            selectedItemIndex = 0,
            data = listOf(
                RecommendationPriorityEntity(
                    priority = 1,
                    options = listOf(
                        OptionEntity(
                            id = 1,
                            totalMemberCount = 20,
                            myIdentity = IdentityEntity(
                                availability = "available",
                                position = 0,
                                name = "이가을"
                            ),
                            date = "2024-09-27T00:00:00",
                            startTime = "2024-09-27T11:00:00",
                            endTime = "2024-09-27T14:00:00",
                            likes = 15,
                            liked = true,
                            availableMemberCount = 10,
                            availableMembers = listOf(
                                "이가을", "선우정아", "대한민국만세", "최영희", "정영수",
                                "이가을", "김언지", "박유진", "임하늘", "변우석"
                            ),
                            unavailableMemberCount = 5,
                            unavailableMembers = listOf("한강", "이영희", "박영수", "최영희", "정영수")
                        ),
                        OptionEntity(
                            id = 2,
                            totalMemberCount = 20,
                            myIdentity = IdentityEntity(
                                availability = "available",
                                position = 0,
                                name = "이가을"
                            ),
                            date = "2024-09-27T00:00:00",
                            startTime = "2024-09-27T11:00:00",
                            endTime = "2024-09-27T14:00:00",
                            likes = 15,
                            liked = false,
                            availableMemberCount = 10,
                            availableMembers = listOf(
                                "이가을", "선우정아", "대한민국만세", "최영희", "정영수",
                                "이가을", "김언지", "박유진", "임하늘", "변우석"
                            ),
                            unavailableMemberCount = 5,
                            unavailableMembers = listOf("한강", "이영희", "박영수", "최영희", "정영수")
                        )
                    )
                )
            ),
            onConfirmButtonClick = {}
        )
    }
}
