package com.sopt.presentation.auth.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.sopt.core.type.DialogType
import com.sopt.core.util.BaseViewModel
import com.sopt.domain.entity.AuthTypeEntity
import com.sopt.domain.repository.UserInfoRepository
import com.sopt.domain.usecase.PostSocialLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(
    @Named("GoogleClientId") private val googleClientId: String,
    @Named("GoogleClientSecret") private val googleClientSecret: String,
    private val userInfoRepository: UserInfoRepository,
    private val postSocialLoginUseCase: PostSocialLoginUseCase
) : BaseViewModel<LoginSideEffect>() {

    private val _showDialog = MutableStateFlow(Pair(DialogType.NETWORK_LOGIN_GOOGLE_FAILURE, false))
    val showDialog: StateFlow<Pair<DialogType, Boolean>> get() = _showDialog

    private val _googleSignInIntent = MutableStateFlow<Intent?>(null)
    val googleSignInIntent: StateFlow<Intent?> get() = _googleSignInIntent

    fun showDialog(dialogType: DialogType, isVisible: Boolean) {
        _showDialog.update { it.copy(first = dialogType, second = isVisible) }
    }

    fun prepareGoogleSignInIntent(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(googleClientId, true)
            .build()
        val client: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
        _googleSignInIntent.value = client.signInIntent
    }

    fun clearGoogleSignInIntent() {
        _googleSignInIntent.value = null
    }

    // Kakao Login
    fun kakaoLogin(context: Context) {
        val loginCallback: (OAuthToken?) -> Unit = this::handleKakaoLoginResult

        with(UserApiClient.instance) {
            if (isKakaoTalkLoginAvailable(context)) {
                loginWithKakaoTalk(context) { token, _ -> loginCallback(token) }
            } else {
                loginWithKakaoAccount(context) { token, _ -> loginCallback(token) }
            }
        }
    }

    private fun handleKakaoLoginResult(token: OAuthToken?) {
        viewModelScope.launch {
            token?.let {
                postSocialLogin(
                    BEARER + it.accessToken,
                    KAKAO
                )
            } ?: run {
                showDialog(DialogType.NETWORK_LOGIN_KAKAO_FAILURE, true)
            }
        }
    }

    // Get Google AccessToken
    fun exchangeAuthCodeForAccessToken(authCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add("code", authCode)
                    .add("client_id", googleClientId)
                    .add("client_secret", googleClientSecret)
                    .add("redirect_uri", "")
                    .build()

                val request = Request.Builder()
                    .url("https://oauth2.googleapis.com/token")
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                val jsonString = response.body?.string()
                val json = JSONObject(jsonString ?: "")

                val accessToken = json.optString("access_token")

                if (accessToken.isNotBlank()) {
                    onGoogleAccessTokenReceived(accessToken)
                } else {
                    showDialog(DialogType.NETWORK_LOGIN_GOOGLE_FAILURE, true)
                }
            } catch (e: Exception) {
                showDialog(DialogType.NETWORK_LOGIN_GOOGLE_FAILURE, true)
            }
        }
    }

    // Google Login
    private fun onGoogleAccessTokenReceived(accessToken: String) {
        postSocialLogin(BEARER + accessToken, GOOGLE)
    }

    // 소셜 로그인
    private fun postSocialLogin(accessToken: String, socialType: String) {
        viewModelScope.launch {
            postSocialLoginUseCase(accessToken, AuthTypeEntity(socialType)).fold(
                onSuccess = { response ->
                    saveTokens(response.accessToken, response.refreshToken)
                    userInfoRepository.saveMemberId(response.memberId)
                    userInfoRepository.saveIsAutoLogin(true)
                    emitSideEffect(LoginSideEffect.NavigateToHome)
                },
                onFailure = { error ->
                    emitSideEffect(LoginSideEffect.NavigateToOnboarding(accessToken, socialType))
                }
            )
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        viewModelScope.launch {
            userInfoRepository.saveAccessToken(BEARER + accessToken)
            userInfoRepository.saveRefreshToken(BEARER + refreshToken)
        }
    }

    companion object {
        private const val BEARER = "Bearer "
        private const val KAKAO = "KAKAO"
        private const val GOOGLE = "GOOGLE"
    }
}
