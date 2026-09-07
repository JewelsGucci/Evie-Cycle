package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val usualPeriodDuration: Int = 4,
    val isPinEnabled: Boolean = false,
    val pinCode: String = "",
    val periodReminders: Boolean = true,
    val fertileReminders: Boolean = true,
    val medicationReminders: Boolean = false,
    val medicationTime: String = "09:00",
    val medicationName: String = "Daily Vitamin / Pill",
    val dailyLogReminder: Boolean = true,
    val dailyLogTime: String = "20:00"
)
