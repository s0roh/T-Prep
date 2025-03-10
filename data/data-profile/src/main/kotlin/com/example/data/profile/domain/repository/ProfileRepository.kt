package com.example.data.profile.domain.repository

import android.net.Uri
import com.example.data.profile.domain.entity.ProfileInfo

interface ProfileRepository {

    suspend fun updateUserProfileImage(imageUri: Uri)

    suspend fun deleteUserProfileImage()

    suspend fun getUserInfo(): ProfileInfo
}