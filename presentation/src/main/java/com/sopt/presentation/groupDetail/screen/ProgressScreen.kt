package com.sopt.presentation.groupDetail.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable
import com.sopt.core.util.time.CalculateTimeFromString
import com.sopt.domain.entity.ProgressEntity
import com.sopt.presentation.R
import com.sopt.presentation.groupDetail.GroupDetailViewModel

@Composable
fun ProgressScreen(
    groupId: Long,
    progressEntities: List<ProgressEntity>,
    onItemClicked: (Long, Long, String) -> Unit
) {
    if (progressEntities.isEmpty()) {
        Image(
            modifier = Modifier
                .padding(top = 103.dp, bottom = 4.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_group_detail_empty),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.tv_group_detail_no_progress),
            color = NoostakTheme.colors.gray900,
            style = NoostakTheme.typography.b2Regular
        )
    } else {
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            items(progressEntities, key = { it.appointmentId }) {
                ProgressItem(
                    groupId = groupId,
                    progressEntity = it,
                    onItemClicked = onItemClicked
                )
            }
        }
    }
}

@Composable
fun ProgressItem(
    groupId: Long,
    progressEntity: ProgressEntity,
    onItemClicked: (Long, Long, String) -> Unit
) {
    val calculateTime = CalculateTimeFromString()
    val startDate = calculateTime.extractDateWithKorean(progressEntity.startDate)
    val dayOfWeek = calculateTime.extractDayOfWeekWithBraces(progressEntity.startDate)
    val startHour = calculateTime.extractHourWithZero(progressEntity.startDate)
    val endHour = calculateTime.extractHourWithZero(progressEntity.endDate)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onItemClicked(
                    groupId,
                    progressEntity.appointmentId,
                    progressEntity.appointmentName
                )
            }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(15.dp),
                color = NoostakTheme.colors.gray200
            )
            .padding(
                top = 15.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 19.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = progressEntity.appointmentName,
                color = NoostakTheme.colors.gray900,
                style = NoostakTheme.typography.t4Bold,
                textAlign = TextAlign.Start
            )
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_group_detail_arrow),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(11.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.text_group_detail_progress_date,
                    startDate,
                    dayOfWeek,
                    startHour,
                    endHour
                ),
                color = NoostakTheme.colors.gray800,
                style = NoostakTheme.typography.b5Regular
            )
            Text(
                text = stringResource(
                    R.string.text_group_detail_progress_participants,
                    progressEntity.participants,
                    progressEntity.maxParticipants
                ),
                color = NoostakTheme.colors.gray700,
                style = NoostakTheme.typography.b5Regular
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progressEntity.participants.toFloat() / progressEntity.maxParticipants },
            modifier = Modifier.fillMaxWidth(),
            color = NoostakTheme.colors.blue300,
            trackColor = NoostakTheme.colors.gray200,
            gapSize = 0.dp,
            strokeCap = StrokeCap.Round,
            drawStopIndicator = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    NoostakAndroidTheme {
        val groupDetailViewModel: GroupDetailViewModel = hiltViewModel()
        ProgressScreen(
            groupId = 1,
            progressEntities = groupDetailViewModel.mockGroupDetail.progressEntities,
            onItemClicked = { _, _, _ -> }
        )
    }
}
