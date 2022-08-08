package com.stytch.exampleapp.di

import android.content.Context
import com.stytch.sdk.StytchClient
import com.stytch.exampleapp.BuildConfig
import com.stytch.exampleapp.SignInRepository
import com.stytch.exampleapp.SignInRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val HOST_URL = "https://web.stytch.com/sdk/v1/"

@InstallIn(SingletonComponent::class)
@Module
class TestAppProvider {

    @Provides
    @Singleton
    fun providesMagicLinks(@ApplicationContext context: Context): StytchClient.MagicLinks {
        StytchClient.apply {
            configure(context = context, publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN, hostUrl = HOST_URL)
        }
        return StytchClient.MagicLinks
    }

    @Provides
    @Singleton
    fun provideSignInRepository(magicLinks: StytchClient.MagicLinks): SignInRepository = SignInRepositoryImpl(magicLinks = magicLinks)

}
