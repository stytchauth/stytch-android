package com.stytch.testapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.StytchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(val repository: SignInRepository) : ViewModel() {

    fun authenticate(){
        viewModelScope.launch {
            repository.authenticate("", 60)
        }
    }

    fun loginOrCreate(){
        viewModelScope.launch {
            repository.loginOrCreate(StytchClient.MagicLinks.Parameters("test@test.com"))
        }
    }

}
