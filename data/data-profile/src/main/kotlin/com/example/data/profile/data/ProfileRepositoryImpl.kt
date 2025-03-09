package com.example.data.profile.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.data.profile.domain.ProfileRepository
import com.example.network.api.ApiService
import com.example.preferences.AuthPreferences
import com.example.preferences.AuthRequestWrapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import androidx.core.net.toUri

class ProfileRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val preferences: AuthPreferences,
    private val authRequestWrapper: AuthRequestWrapper,
) : ProfileRepository {

    override suspend fun getUserProfileImage(): Uri? {
        return try {
            authRequestWrapper.executeWithAuth { token ->
                val response = apiService.getUserPicture(token)
                if (response.isSuccessful) {
                    response.body()?.byteStream()?.let { inputStream ->
                        val tempFile =
                            File(context.filesDir, "profile_pic_${System.currentTimeMillis()}.jpg")
                        tempFile.outputStream().use { output ->
                            inputStream.copyTo(output)
                        }
                        val uri = Uri.fromFile(tempFile)
                        preferences.saveUserProfileImage(uri.toString())
                        uri
                    }
                } else {
                    if (response.code()!= 404) {
                        loadProfileImageFromPreferences()
                    }else{
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserProfileImage: Error fetching image: ${e.message}")
            loadProfileImageFromPreferences()
        }
    }

    private fun loadProfileImageFromPreferences(): Uri? {
        val imageUriString = preferences.getUserProfileImage()
        return if (!imageUriString.isNullOrEmpty()) {
            imageUriString.toUri()
        } else {
            null
        }
    }


    override suspend fun updateUserProfileImage(imageUri: Uri) {
        return authRequestWrapper.executeWithAuth { token ->
            val file = File(context.filesDir, "profile_pic_${System.currentTimeMillis()}.jpg")

            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating file: ${e.message}")
                    throw Exception("Error creating file")
                }
            }

            val inputStream = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (file.length() > 5 * 1024 * 1024) {
                Log.e(TAG, "updateUserProfileImage: Image size exceeds 5MB limit")
                file.delete()
                throw Exception("Image size exceeds 5MB limit")
            }

            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)

            val response = apiService.updateUserPicture(token, multipartBody)

            if (response.isSuccessful) {
                preferences.saveUserProfileImage(imageUri.toString())
            } else {
                Log.e(
                    TAG,
                    "updateUserProfileImage: Error uploading profile image: ${
                        response.errorBody()?.string()
                    }"
                )
                throw Exception("Error uploading profile image: ${response.errorBody()?.string()}")
            }
        }
    }


    override suspend fun deleteUserProfileImage() {
        return authRequestWrapper.executeWithAuth { token ->
            val response = apiService.deleteUserPicture(token)

            if (response.isSuccessful) {
                preferences.deleteUserProfileImage()
            } else {
                Log.e(
                    TAG,
                    "deleteUserProfileImage: Error deleting profile image: ${
                        response.errorBody()?.string()
                    }"
                )
                throw Exception("Error deleting profile image: ${response.errorBody()?.string()}")
            }
        }
    }

    companion object {

        private const val TAG = "ProfileRepositoryImpl"
    }
}
