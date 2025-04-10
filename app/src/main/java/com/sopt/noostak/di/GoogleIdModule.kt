package com.sopt.noostak.di

import com.sopt.noostak.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleIdModule {
    @Provides
    @Singleton
    @Named("GoogleClientId")
    fun provideGoogleClientId(): String {
        return BuildConfig.GOOGLE_CLIENT_ID
    }

    @Provides
    @Singleton
    @Named("GoogleClientSecret")
    fun provideGoogleClientSecret(): String {
        return BuildConfig.GOOGLE_CLIENT_SECRET
    }
}
