package com.example.data.profile.domain

import android.net.Uri

interface ProfileRepository {

    suspend fun getUserProfileImage(): Uri?

    suspend fun updateUserProfileImage(imageUri: Uri)

    suspend fun deleteUserProfileImage()
}