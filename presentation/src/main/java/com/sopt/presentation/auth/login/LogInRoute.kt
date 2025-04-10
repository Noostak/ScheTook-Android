package com.sopt.presentation.auth.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.sopt.core.designsystem.component.dialog.NoostakDialog
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.toast
import com.sopt.core.type.DialogType
import com.sopt.presentation.R
import com.sopt.presentation.auth.component.LoginButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToOnboarding: (String, String) -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showDialog by loginViewModel.showDialog.collectAsStateWithLifecycle()
    val googleSignInIntent by loginViewModel.googleSignInIntent.collectAsStateWithLifecycle()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            loginViewModel.showDialog(DialogType.NETWORK_LOGIN_GOOGLE_FAILURE, true)
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val authCode = account.serverAuthCode
                if (!authCode.isNullOrBlank()) {
                    loginViewModel.exchangeAuthCodeForAccessToken(authCode)
                } else {
                    loginViewModel.showDialog(DialogType.NETWORK_LOGIN_GOOGLE_FAILURE, true)
                }
            } catch (e: Exception) {
                loginViewModel.showDialog(DialogType.NETWORK_LOGIN_GOOGLE_FAILURE, true)
            }
        }
        loginViewModel.clearGoogleSignInIntent()
    }

    LaunchedEffect(googleSignInIntent) {
        googleSignInIntent?.let { intent ->
            googleSignInLauncher.launch(intent)
        }
    }

    LaunchedEffect(loginViewModel.sideEffects) {
        loginViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is LoginSideEffect.NavigateToHome -> navigateToHome()
                is LoginSideEffect.NavigateToOnboarding -> navigateToOnboarding(sideEffect.authId, sideEffect.socialType)
                is LoginSideEffect.ShowToast -> context.toast(sideEffect.message)
                is LoginSideEffect.ShowDialog -> loginViewModel.showDialog(
                    sideEffect.dialogType,
                    true
                )
            }
        }
    }

    showDialog.let { (dialogType, isVisible) ->
        if (isVisible) {
            NoostakDialog(
                dialogType = dialogType,
                onClick = {
                    loginViewModel.showDialog(dialogType, false)
                    when (dialogType) {
                        DialogType.NETWORK_LOGIN_KAKAO_FAILURE -> loginViewModel.kakaoLogin(context)
                        DialogType.NETWORK_LOGIN_GOOGLE_FAILURE -> loginViewModel.prepareGoogleSignInIntent(
                            context
                        )

                        else -> Unit
                    }
                },
                onDismissRequest = { loginViewModel.showDialog(dialogType, false) }
            )
        }
    }

    LoginScreen(
        onKakaoLoginClick = { loginViewModel.kakaoLogin(context) },
        onGoogleLoginClick = { loginViewModel.prepareGoogleSignInIntent(context) }
    )
}

@Composable
fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit
) {
    val offsetY = remember { Animatable(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoostakTheme.colors.blue600)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(dimensionResource(R.dimen.horizontal_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.ic_login_logo),
            contentDescription = stringResource(R.string.iv_login_description),
            modifier = Modifier
                .offset(y = offsetY.value.dp)
                .padding(bottom = 32.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        SocialLoginBottom(
            onKakaoLoginClick = onKakaoLoginClick,
            onGoogleLoginClick = onGoogleLoginClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SocialLoginBottom(
    onKakaoLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.tv_login_description),
            color = NoostakTheme.colors.white,
            style = NoostakTheme.typography.c3SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LoginButton(
            text = stringResource(R.string.btn_login_kakao),
            onLoginBtnClick = onKakaoLoginClick,
            modifier = Modifier.padding(bottom = 12.dp),
            containerColor = NoostakTheme.colors.yellow,
            contentColor = NoostakTheme.colors.black
        )
        LoginButton(
            text = stringResource(R.string.btn_login_google),
            onLoginBtnClick = onGoogleLoginClick,
            containerColor = NoostakTheme.colors.black,
            contentColor = NoostakTheme.colors.white
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    NoostakAndroidTheme {
        LoginScreen(
            onKakaoLoginClick = {},
            onGoogleLoginClick = {}
        )
    }
}
