package com.stytch.testapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.StytchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(val repository: SignInRepository) : ViewModel() {

    private val _currentResponse = MutableLiveData("")
    val currentResponse: LiveData<String>
        get() = _currentResponse

    fun authenticate() {
        viewModelScope.launch {
            val result = repository.authenticate(BuildConfig.PUBLIC_TOKEN_FROM_DASHHBOARD, 60)
            _currentResponse.value = result.toString()
        }
    }

    fun loginOrCreate() {
        viewModelScope.launch {
            val result = repository.loginOrCreate(StytchClient.MagicLinks.Parameters("test@test.com"))
            _currentResponse.value = result.toString()
        }
    }

}
