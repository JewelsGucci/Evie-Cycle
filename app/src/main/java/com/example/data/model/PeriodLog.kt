package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period_logs")
data class PeriodLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: Long, // LocalDate.toEpochDay()
    val endDate: Long? = null, // LocalDate.toEpochDay()
    val flow: String = "MEDIUM", // SPOTTING, LIGHT, MEDIUM, HEAVY
    val notes: String = ""
)
