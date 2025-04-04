package com.example.feature.profile.presentation.owner_profile

sealed class OwnerProfileEvent {
    data class ShowError(val message: String) : OwnerProfileEvent()
}
