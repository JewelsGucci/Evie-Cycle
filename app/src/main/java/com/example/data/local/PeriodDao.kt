package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PeriodLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Query("SELECT * FROM period_logs ORDER BY startDate ASC")
    fun getAllPeriods(): Flow<List<PeriodLog>>

    @Query("SELECT * FROM period_logs ORDER BY startDate DESC LIMIT 1")
    fun getLatestPeriod(): Flow<PeriodLog?>

    @Query("SELECT * FROM period_logs WHERE id = :id")
    suspend fun getPeriodById(id: Long): PeriodLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodLog): Long

    @Update
    suspend fun updatePeriod(period: PeriodLog)

    @Delete
    suspend fun deletePeriod(period: PeriodLog)

    @Query("DELETE FROM period_logs WHERE id = :id")
    suspend fun deletePeriodById(id: Long)

    @Query("SELECT COUNT(*) FROM period_logs")
    suspend fun getPeriodCount(): Int

    @Query("DELETE FROM period_logs")
    suspend fun clearAll()
}
