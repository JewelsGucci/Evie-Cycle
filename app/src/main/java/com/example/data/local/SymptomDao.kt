package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailySymptomLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {
    @Query("SELECT * FROM daily_symptoms WHERE date = :date")
    fun getSymptomsForDate(date: Long): Flow<DailySymptomLog?>

    @Query("SELECT * FROM daily_symptoms WHERE date = :date")
    suspend fun getSymptomsForDateSync(date: Long): DailySymptomLog?

    @Query("SELECT * FROM daily_symptoms ORDER BY date DESC")
    fun getAllSymptoms(): Flow<List<DailySymptomLog>>

    @Query("SELECT * FROM daily_symptoms WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getSymptomsBetween(startDate: Long, endDate: Long): Flow<List<DailySymptomLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSymptoms(symptomLog: DailySymptomLog)

    @Query("DELETE FROM daily_symptoms WHERE date = :date")
    suspend fun deleteSymptomsForDate(date: Long)

    @Query("DELETE FROM daily_symptoms")
    suspend fun clearAll()
}
