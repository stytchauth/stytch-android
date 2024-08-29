package com.stytch.javademoapp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stytch.sdk.common.StytchResult;
import com.stytch.sdk.common.network.models.BasicData;
import com.stytch.sdk.consumer.StytchClient;
import com.stytch.sdk.consumer.network.models.SessionData;
import com.stytch.sdk.consumer.network.models.UserData;
import com.stytch.sdk.consumer.sessions.Sessions;

import kotlin.Unit;

public class MainViewModel extends ViewModel {
    private MutableLiveData<StytchState> stytchState;
    public MutableLiveData<StytchState> getStytchState() {
        if (stytchState == null) {
            stytchState = new MutableLiveData<>(StytchState.DEFAULT);
        }
        return stytchState;
    }


    Unit handleInitializationChange(Boolean isInitialized) {
        StytchState newState = new StytchState(
            isInitialized,
            StytchClient.getSessions().getSync(),
            StytchClient.getUser().getSyncUser()
        );
        getStytchState().postValue(newState);
        if (isInitialized) {
            StytchClient.getUser().onChange(this::handleUserChange);
            StytchClient.getSessions().onChange(this::handleSessionChange);
        }
        return Unit.INSTANCE;
    }

    private Unit handleUserChange(UserData userData) {
        StytchState newState = new StytchState(
            true,
            StytchClient.getSessions().getSync(),
            userData
        );
        getStytchState().postValue(newState);
        return Unit.INSTANCE;
    }

    private Unit handleSessionChange(SessionData sessionData) {
        StytchState newState = new StytchState(
            true,
            sessionData,
            StytchClient.getUser().getSyncUser()
        );
        getStytchState().postValue(newState);
        return Unit.INSTANCE;
    }

    void logout() {
        StytchClient.getSessions().revoke(new Sessions.RevokeParams(), (StytchResult<BasicData> result) -> Unit.INSTANCE);
    }
}

