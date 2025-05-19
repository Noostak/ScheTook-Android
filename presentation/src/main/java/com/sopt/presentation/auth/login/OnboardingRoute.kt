package com.sopt.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.core.designsystem.component.button.NoostakBottomButton
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.presentation.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun OnboardingRoute(
    accessToken: String,
    socialType: String,
    navigateToSignUp: (String, String) -> Unit
) {
    OnboardingScreen(
        onNextButtonClick = { navigateToSignUp(accessToken, socialType) }
    )
}

@Composable
fun OnboardingScreen(
    onNextButtonClick: () -> Unit
) {
    val pages = persistentListOf(
        BoardingPage(
            imageRes = R.drawable.ic_onboarding_1,
            subtitle = stringResource(R.string.tv_onboarding_subtitle_1),
            title = stringResource(R.string.tv_onboarding_title_1)
        ),
        BoardingPage(
            imageRes = R.drawable.ic_onboarding_2,
            subtitle = stringResource(R.string.tv_onboarding_subtitle_2),
            title = stringResource(R.string.tv_onboarding_title_2)
        ),
        BoardingPage(
            imageRes = R.drawable.ic_onboarding_3,
            subtitle = stringResource(R.string.tv_onboarding_subtitle_3),
            title = stringResource(R.string.tv_onboarding_title_3)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoostakTheme.colors.white)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = pages[page].imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NoostakTheme.colors.gray100)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                Text(
                    modifier = Modifier.padding(top = 30.dp),
                    text = pages[page].subtitle,
                    color = NoostakTheme.colors.blue700,
                    style = NoostakTheme.typography.b1SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            bottom = 26.3.dp
                        ),
                    text = pages[page].title,
                    color = NoostakTheme.colors.gray900,
                    style = NoostakTheme.typography.h322SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 26.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) NoostakTheme.colors.blue600 else NoostakTheme.colors.gray200
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        val isLastPage = pagerState.currentPage == pages.size - 1
        val buttonText =
            if (isLastPage) stringResource(R.string.btn_onboarding_start) else stringResource(R.string.btn_next)

        NoostakBottomButton(
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_padding),
                vertical = dimensionResource(id = R.dimen.vertical_padding)
            ),
            text = buttonText,
            activateColor = NoostakTheme.colors.blue600,
            deactivateColor = NoostakTheme.colors.gray500,
            onButtonClick = {
                coroutineScope.launch {
                    if (isLastPage) {
                        onNextButtonClick()
                    } else {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        )
    }
}

@Immutable
data class BoardingPage(
    val imageRes: Int,
    val subtitle: String,
    val title: String
)

@Preview
@Composable
fun BoardingScreenPreview() {
    NoostakAndroidTheme {
        OnboardingScreen(
            onNextButtonClick = {}
        )
    }
}
