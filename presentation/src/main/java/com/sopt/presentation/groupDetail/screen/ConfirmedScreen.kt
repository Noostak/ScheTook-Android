package com.sopt.presentation.groupDetail.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sopt.core.designsystem.component.box.CategoryBox
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.ConfirmedEntity
import com.sopt.presentation.R
import com.sopt.presentation.groupDetail.GroupDetailViewModel

@Composable
fun ConfirmedScreen(
    groupId: Long,
    confirmedEntities: List<ConfirmedEntity>,
    onItemClicked: (Long, Long, String) -> Unit
) {
    if (confirmedEntities.isEmpty()) {
        Image(
            modifier = Modifier
                .padding(top = 103.dp, bottom = 4.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_group_detail_empty),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.tv_group_detail_no_complete),
            color = NoostakTheme.colors.gray900,
            style = NoostakTheme.typography.b2Regular
        )
    } else {
        LazyColumn(
            modifier = Modifier.padding(top = 13.dp)
        ) {
            items(confirmedEntities, key = { it.appointmentId }) {
                ConfirmedItem(
                    groupId = groupId,
                    confirmedEntity = it,
                    onItemClicked = onItemClicked
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = NoostakTheme.colors.gray200
                )
            }
        }
    }
}

@Composable
fun ConfirmedItem(
    groupId: Long,
    confirmedEntity: ConfirmedEntity,
    onItemClicked: (Long, Long, String) -> Unit
) {
    val calculateTime = CalculateTimeFromString()
    val date = calculateTime.extractDateWithKorean(confirmedEntity.date)
    val dayOfWeek = calculateTime.extractDayOfWeekWithBraces(confirmedEntity.date)
    val startHour = calculateTime.extractHourWithZero(confirmedEntity.startTime)
    val endHour = calculateTime.extractHourWithZero(confirmedEntity.endTime)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onItemClicked(
                    groupId,
                    confirmedEntity.appointmentOptionId,
                    confirmedEntity.appointmentName
                )
            }
            .padding(
                top = 15.dp,
                bottom = 16.dp,
                start = 2.dp
            )
    ) {
        Row {
            CategoryBox(text = confirmedEntity.category)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = confirmedEntity.appointmentName,
                    color = NoostakTheme.colors.gray900,
                    style = NoostakTheme.typography.b1SemiBold,
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp),
                    text = stringResource(
                        R.string.text_group_detail_confirmed_date,
                        date,
                        dayOfWeek,
                        startHour,
                        endHour
                    ),
                    color = NoostakTheme.colors.gray700,
                    style = NoostakTheme.typography.c3Regular,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompleteScreenPreview() {
    NoostakAndroidTheme {
        val groupDetailViewModel: GroupDetailViewModel = hiltViewModel()
        ConfirmedScreen(
            groupId = 1,
            confirmedEntities = groupDetailViewModel.mockGroupDetail.confirmedEntities,
            onItemClicked = { _, _, _ -> }
        )
    }
}
