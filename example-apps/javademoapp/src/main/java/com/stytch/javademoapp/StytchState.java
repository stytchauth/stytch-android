package com.stytch.javademoapp;

import androidx.annotation.Nullable;

import com.stytch.sdk.consumer.network.models.SessionData;
import com.stytch.sdk.consumer.network.models.UserData;

public record StytchState(
    Boolean isInitialized,
    @Nullable SessionData sessionData,
    @Nullable UserData userData
){
    public static final StytchState DEFAULT = new StytchState(false, null, null);
}
