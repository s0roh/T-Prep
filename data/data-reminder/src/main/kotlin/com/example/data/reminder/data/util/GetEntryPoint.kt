package com.example.data.reminder.data.util

import android.content.Context
import com.example.data.reminder.di.AlarmReceiverEntryPoint
import dagger.hilt.android.EntryPointAccessors

internal fun getEntryPoint(context: Context): AlarmReceiverEntryPoint {
    return EntryPointAccessors.fromApplication(context, AlarmReceiverEntryPoint::class.java)
}