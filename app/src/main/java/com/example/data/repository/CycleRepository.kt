package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PeriodDao
import com.example.data.local.SettingsDao
import com.example.data.local.SymptomDao
import com.example.data.model.DailySymptomLog
import com.example.data.model.PeriodLog
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

class CycleRepository(
    private val database: AppDatabase,
    private val periodDao: PeriodDao = database.periodDao(),
    private val symptomDao: SymptomDao = database.symptomDao(),
    private val settingsDao: SettingsDao = database.settingsDao()
) {
    val allPeriods: Flow<List<PeriodLog>> = periodDao.getAllPeriods()
    val latestPeriod: Flow<PeriodLog?> = periodDao.getLatestPeriod()
    val allSymptoms: Flow<List<DailySymptomLog>> = symptomDao.getAllSymptoms()
    val settings: Flow<UserSettings?> = settingsDao.getSettings()

    fun getSymptomsForDate(date: Long): Flow<DailySymptomLog?> =
        symptomDao.getSymptomsForDate(date)

    suspend fun getSymptomsForDateSync(date: Long): DailySymptomLog? =
        symptomDao.getSymptomsForDateSync(date)

    suspend fun insertPeriod(period: PeriodLog): Long =
        periodDao.insertPeriod(period)

    suspend fun updatePeriod(period: PeriodLog) =
        periodDao.updatePeriod(period)

    suspend fun deletePeriod(period: PeriodLog) =
        periodDao.deletePeriod(period)

    suspend fun deletePeriodById(id: Long) =
        periodDao.deletePeriodById(id)

    suspend fun saveSymptoms(symptomLog: DailySymptomLog) =
        symptomDao.insertOrUpdateSymptoms(symptomLog)

    suspend fun deleteSymptomsForDate(date: Long) =
        symptomDao.deleteSymptomsForDate(date)

    suspend fun updateSettings(settings: UserSettings) =
        settingsDao.insertOrUpdateSettings(settings)

    suspend fun resetAllData() {
        periodDao.clearAll()
        symptomDao.clearAll()
        settingsDao.clearAll()
        AppDatabase.seedInitialData(database)
    }

    suspend fun seedInitialDataIfNeeded() {
        AppDatabase.seedInitialData(database)
    }
}
