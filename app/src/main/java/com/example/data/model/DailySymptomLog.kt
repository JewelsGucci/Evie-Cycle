package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_symptoms")
data class DailySymptomLog(
    @PrimaryKey
    val date: Long, // LocalDate.toEpochDay()
    val cramps: String = "NONE", // NONE, MILD, MODERATE, SEVERE
    val headache: String = "NONE", // NONE, MILD, MODERATE, SEVERE
    val mood: String = "CALM", // CALM, HAPPY, ENERGETIC, SENSITIVE, IRRITABLE, SAD, ANXIOUS, FATIGUED
    val discharge: String = "NONE", // NONE, DRY, STICKY, CREAMY, EGG_WHITE, WATERY
    val energy: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val flow: String = "NONE", // NONE, SPOTTING, LIGHT, MEDIUM, HEAVY
    val hadIntercourse: Boolean = false,
    val tookMedication: Boolean = false,
    val notes: String = ""
)
