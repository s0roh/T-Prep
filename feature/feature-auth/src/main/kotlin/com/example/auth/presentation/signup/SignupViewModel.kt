package com.example.auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SignupViewModel @Inject constructor(
    private val signupUseCase: SignupUseCase
) : ViewModel() {

    var screenState = MutableStateFlow<SignupScreenState>(SignupScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        screenState.tryEmit(SignupScreenState.Error("Ошибка авторизации: ${exception.message}"))
    }

    fun onSignupClick(email: String, password: String, name: String) {
        viewModelScope.launch(exceptionHandler) {
            if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
                screenState.emit(SignupScreenState.Loading)

                val signupSuccess = signupUseCase(email = email, password = password, name = name)
                if (signupSuccess) {
                    screenState.emit(SignupScreenState.Success("Signup Successful"))
                } else {
                    screenState.emit(SignupScreenState.Error("Ошибка регистрации"))
                }
            } else {
                screenState.emit(SignupScreenState.Error("Все поля должны быть заполнены"))
            }
        }
    }
}