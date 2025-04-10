package com.sopt.noostak.di

import android.app.Application
import android.content.Intent
import android.widget.Toast
import com.sopt.data.datasource.UserDataSource
import com.sopt.data.service.AuthApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val preferenceDatasource: UserDataSource,
    private val context: Application,
    private val authService: AuthApiService
) : Interceptor {
    private val mutex = Mutex()
    private var currentToast: Toast? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var response = chain.proceed(originalRequest.newAuthBuilder())

        if (response.code == CODE_TOKEN_EXPIRE) {
            response.close()
            val tokenRefreshed = runBlocking {
                refreshTokenIfNeeded()
            }
            if (tokenRefreshed) {
                response = chain.proceed(originalRequest.newAuthBuilder())
            } else {
                handleFailedTokenReissue()
            }
        }
        return response
    }

    private suspend fun refreshTokenIfNeeded(): Boolean {
        mutex.withLock {
            val refreshToken = preferenceDatasource.refreshToken.first()

            return try {
                val tokenResult = withContext(Dispatchers.IO) {
                    authService.postReissueToken(refreshToken)
                }

                if (tokenResult.status == SUCCESS && tokenResult.result != null) {
                    tokenResult.result?.let { result ->
                        preferenceDatasource.updateAccessToken(BEARER + result.accessToken)
                        preferenceDatasource.updateRefreshToken(BEARER + result.refreshToken)
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun handleFailedTokenReissue() = CoroutineScope(Dispatchers.Main).launch {
        showToast()
        withContext(Dispatchers.IO) {
            preferenceDatasource.clear()
        }
        restartActivity()
    }

    private fun showToast() {
        currentToast?.cancel()
        currentToast = Toast.makeText(context, "다시 로그인해주세요.", Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    private suspend fun restartActivity() = with(context) {
        mutex.withLock {
            startActivity(
                Intent.makeRestartActivityTask(
                    packageManager.getLaunchIntentForPackage(packageName)?.component
                )
            )
        }
    }

    private fun Request.newAuthBuilder() = newBuilder()
        .addHeader(
            name = AUTHORIZATION,
            value = runBlocking {
                preferenceDatasource.accessToken.first()
            }
        ).build()

    companion object {
        const val SUCCESS = 201
        const val CODE_TOKEN_EXPIRE = 401
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
    }
}
