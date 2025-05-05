package com.example.data.profile.domain.repository

import android.net.Uri
import com.example.data.profile.domain.entity.ProfileInfo

interface ProfileRepository {

    /**
     * Обновляет изображение профиля пользователя.
     *
     * @param imageUri URI нового изображения профиля.
     * @throws Exception в случае ошибки обновления изображения.
     */
    suspend fun updateUserProfileImage(imageUri: Uri)

    /**
     * Удаляет текущее изображение профиля пользователя.
     *
     * @throws Exception в случае ошибки удаления изображения.
     */
    suspend fun deleteUserProfileImage()

    /**
     * Получает информацию о текущем пользователе.
     *
     * @return [ProfileInfo] данные профиля пользователя.
     * @throws IllegalStateException в случае ошибки получения информации.
     */
    suspend fun getUserInfo(): ProfileInfo

    /**
     * Загружает изображение профиля текущего пользователя.
     *
     * @return [Uri] URI изображения профиля пользователя или `null`, если изображение отсутствует.
     */
    suspend fun getUserProfileImage(): Uri?
}