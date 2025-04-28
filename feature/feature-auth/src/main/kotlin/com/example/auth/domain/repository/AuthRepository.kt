package com.example.auth.domain.repository

interface AuthRepository {

    /**
     * Проверяет, является ли токен доступа действительным.
     * @return Boolean, который указывает, действителен ли токен доступа.
     */
    fun isAccessTokenValid(): Boolean

    /**
     * Проверяет, является ли токен обновления действительным.
     * @return Boolean, который указывает, действителен ли токен обновления.
     */
    fun isRefreshTokenValid(): Boolean

    /**
     * Обновляет токены доступа и обновления.
     * @return Boolean, который указывает, были ли токены успешно обновлены.
     */
    suspend fun refreshTokens(): Boolean

    /**
     * Выполняет вход пользователя с указанным email и паролем.
     * @param email Email пользователя.
     * @param password Пароль пользователя.
     * @return Boolean, который указывает, был ли вход успешным.
     */
    suspend fun login(email: String, password: String): Boolean

    /**
     * Регистрирует нового пользователя с указанным email, паролем и именем.
     * @param email Email пользователя.
     * @param password Пароль пользователя.
     * @param name Имя пользователя.
     * @return Boolean, который указывает, была ли регистрация успешной.
     */
    suspend fun signup(email: String, password: String, name: String): Boolean
}