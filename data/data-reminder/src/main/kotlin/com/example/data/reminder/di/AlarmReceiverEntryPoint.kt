package com.example.data.reminder.di

import com.example.data.reminder.data.util.RouteNavigator
import com.example.data.reminder.domain.repository.ReminderScheduler
import com.example.database.TPrepDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmReceiverEntryPoint {

    fun getReminderScheduler(): ReminderScheduler

    fun getRouteNavigator(): RouteNavigator

    fun getTPrepDatabase(): TPrepDatabase
}