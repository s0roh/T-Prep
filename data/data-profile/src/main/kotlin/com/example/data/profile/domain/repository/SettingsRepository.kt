package com.example.data.profile.domain.repository

interface SettingsRepository {

    /**
     * Проверяет, включена ли вибрация в настройках.
     *
     * @return true, если вибрация включена, иначе false.
     */
    fun isVibrationEnabled(): Boolean

    /**
     * Проверяет, включен ли звук в настройках.
     *
     * @return true, если звук включен, иначе false.
     */
    fun isSoundEnabled(): Boolean

    /**
     * Переключает текущее состояние вибрации (включает или выключает).
     */
    fun toggleVibration()

    /**
     * Переключает текущее состояние звука (включает или выключает).
     */
    fun toggleSound()
}