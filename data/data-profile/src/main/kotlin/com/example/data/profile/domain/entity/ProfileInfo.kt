package com.example.data.profile.domain.entity

import android.net.Uri

data class ProfileInfo(
    val profileName: String,
    val profileEmail: String,
    val profileImage: Uri?,
)
