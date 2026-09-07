package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailySymptomLog
import com.example.data.model.PeriodLog
import com.example.data.model.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [PeriodLog::class, DailySymptomLog::class, UserSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao
    abstract fun symptomDao(): SymptomDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "evelyn_cycle_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial data as requested
                        scope.launch(Dispatchers.IO) {
                            val database = getDatabase(context, scope)
                            seedInitialData(database)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedInitialData(database: AppDatabase) {
            val periodDao = database.periodDao()
            val settingsDao = database.settingsDao()

            if (periodDao.getPeriodCount() == 0) {
                // Initial data:
                // Last period started: 28 Aug 2026
                // Expected end: 31 Aug 2026 (Usual period duration: 4 days)
                val initialStart = LocalDate.of(2026, 8, 28).toEpochDay()
                val initialEnd = LocalDate.of(2026, 8, 31).toEpochDay()

                periodDao.insertPeriod(
                    PeriodLog(
                        startDate = initialStart,
                        endDate = initialEnd,
                        flow = "MEDIUM",
                        notes = "Initial recorded period"
                    )
                )

                settingsDao.insertOrUpdateSettings(
                    UserSettings(
                        id = 1,
                        usualPeriodDuration = 4,
                        isPinEnabled = false,
                        pinCode = "",
                        periodReminders = true,
                        fertileReminders = true,
                        medicationReminders = false,
                        medicationTime = "09:00",
                        medicationName = "Daily Multivitamin",
                        dailyLogReminder = true,
                        dailyLogTime = "20:00"
                    )
                )
            }
        }
    }
}
