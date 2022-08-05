package com.stytch.testapp.di

import android.content.Context
import com.stytch.sdk.StytchClient
import com.stytch.testapp.BuildConfig
import com.stytch.testapp.SignInRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val HOST_URL = "https://web.stytch.com/sdk/v1/"

@InstallIn(SingletonComponent::class)
@Module
class TestAppProvider {

    @Provides
    fun providesStytchClient(@ApplicationContext context: Context) = StytchClient.apply {
        configure(context = context, publicToken = BuildConfig.PUBLIC_TOKEN_FROM_DASHHBOARD, hostUrl = HOST_URL)
    }

    @Provides
    fun providesMagicLinks(stytchClient: StytchClient) = StytchClient.MagicLinks

    @Provides
    @Singleton
    fun provideSignInRepository() = SignInRepository()

}
