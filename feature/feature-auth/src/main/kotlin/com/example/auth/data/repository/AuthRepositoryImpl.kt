package com.example.auth.data.repository

import com.example.auth.domain.repository.AuthRepository
import com.example.auth.mapper.toDto
import com.example.auth.mapper.toLoginRequestDto
import com.example.auth.mapper.toSignupRequestDto
import com.example.network.api.ApiService
import com.example.network.dto.user.AuthResponseDto
import com.example.preferences.auth.AuthPreferences
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject internal constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences,
) : AuthRepository {

    override fun isAccessTokenValid(): Boolean {
        return authPreferences.isAccessTokenValid()
    }

    override fun isRefreshTokenValid(): Boolean {
        return authPreferences.isRefreshTokenValid()
    }

    override suspend fun refreshTokens(): Boolean {
        val refreshToken = authPreferences.getRefreshToken()?.takeIf {
            authPreferences.isRefreshTokenValid()
        } ?: return false
        val response = apiService.refreshToken(refreshToken.toDto())
        return processAuthResponse(response)
    }

    override suspend fun login(email: String, password: String): Boolean {
        val temp = toLoginRequestDto(email, password)
        val response = apiService.login(temp)
        return processAuthResponse(response)
    }

    override suspend fun signup(email: String, password: String, name: String): Boolean {
        val response = apiService.signup(toSignupRequestDto(email, password, name))
        return processAuthResponse(response)
    }

    private fun processAuthResponse(response: Response<AuthResponseDto>): Boolean {
        if (!response.isSuccessful) return false
        val newAccessToken = response.body()?.accessToken
        val newRefreshToken = response.body()?.refreshToken
        val accessTokenExpirationDate = response.headers()["X-Access-Expires-After"] ?: ""
        val refreshTokenExpirationDate = response.headers()["X-Refresh-Expires-After"] ?: ""

        return if (newAccessToken != null && newRefreshToken != null) {
            authPreferences.saveTokens(
                newAccessToken,
                newRefreshToken,
                accessTokenExpirationDate,
                refreshTokenExpirationDate
            )
            true
        } else {
            false
        }
    }
}