package com.example.data.reminder.domain.repository

import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.Source

interface ReminderScheduler {

    /**
     * Планирует напоминание на указанное время.
     *
     * @param reminderId Уникальный идентификатор напоминания.
     * @param reminderTime Время напоминания.
     */
    fun scheduleReminder(reminderId: Long, reminderTime: Long)

    /**
     * Отменяет ранее запланированное напоминание.
     *
     * @param reminderId Уникальный идентификатор напоминания.
     */
    fun cancelReminder(reminderId: Long)

    /**
     * Получает список напоминаний для указанной колоды и источника.
     *
     * @param deckId Идентификатор колоды.
     * @param source Источник колоды для напоминаний.
     * @return Список напоминаний.
     */
    suspend fun getRemindersForDeck(deckId: String, source: Source): List<Reminder>

    /**
     * Вставляет новое напоминание в базу данных.
     *
     * @param reminder Объект напоминания для вставки.
     * @return Идентификатор созданного напоминания.
     */
    suspend fun insertReminder(reminder: Reminder): Long

    /**
     * Удаляет напоминание по идентификатору колоды, источнику и времени напоминания.
     *
     * @param deckId Идентификатор колоды.
     * @param source Источник напоминания.
     * @param reminderTime Время напоминания.
     */
    suspend fun deleteReminder(deckId: String, source: Source, reminderTime: Long)

    /**
     * Генерирует план тренировок на указанный период времени с учётом предпочтительного времени суток.
     *
     * @param startDate Дата начала периода.
     * @param finishDate Дата окончания периода.
     * @param preferredTime Предпочтительное время суток.
     * @return Список временных меток для запланированных тренировок.
     */
    suspend fun getTrainingPlan(startDate: Int, finishDate: Int, preferredTime: Int): List<Long>
}
