package com.example.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.example.feature.profile.domain.ClearTokensUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val clearTokensUseCase: ClearTokensUseCase,
) : ViewModel() {

    fun logout() {
        clearTokensUseCase()
    }
}