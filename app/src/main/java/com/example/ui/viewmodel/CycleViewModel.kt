package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DailySymptomLog
import com.example.data.model.PeriodLog
import com.example.data.model.UserSettings
import com.example.data.repository.CycleRepository
import com.example.domain.AppDateProvider
import com.example.domain.CycleCalculator
import com.example.domain.CycleInfo
import com.example.domain.CyclePhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CycleUiState(
    val periods: List<PeriodLog> = emptyList(),
    val symptoms: List<DailySymptomLog> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val cycleInfo: CycleInfo = CycleInfo(
        currentCycleDay = 10,
        isPeriodActiveToday = false,
        averageCycleLength = null,
        averagePeriodDuration = 4,
        nextEstimatedPeriodStart = null,
        nextEstimatedPeriodEnd = null,
        daysUntilNextPeriod = null,
        estimatedOvulationDate = null,
        fertileWindowStart = null,
        fertileWindowEnd = null,
        currentPhase = CyclePhase.FOLLICULAR,
        completedCyclesCount = 0,
        cycleLengthExplanation = "Cycle length: Not known yet."
    ),
    val selectedDate: LocalDate = AppDateProvider.getToday(),
    val selectedDateSymptoms: DailySymptomLog? = null,
    val isAppLocked: Boolean = false,
    val isLockScreenDismissed: Boolean = false,
    val snackbarMessage: String? = null
)

class CycleViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = CycleRepository(database)

    private val _selectedDate = MutableStateFlow(AppDateProvider.getToday())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _isLockScreenDismissed = MutableStateFlow(false)
    val isLockScreenDismissed: StateFlow<Boolean> = _isLockScreenDismissed.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    val uiState: StateFlow<CycleUiState> = combine(
        repository.allPeriods,
        repository.allSymptoms,
        repository.settings,
        _selectedDate,
        _isLockScreenDismissed
    ) { periods, symptoms, settingsNullable, selectedDate, lockDismissed ->
        val settings = settingsNullable ?: UserSettings()
        val today = AppDateProvider.getToday()
        val cycleInfo = CycleCalculator.calculate(periods, settings, today)
        val selectedDateSymptoms = symptoms.firstOrNull { it.date == selectedDate.toEpochDay() }
        val isLocked = settings.isPinEnabled && settings.pinCode.isNotBlank() && !lockDismissed

        CycleUiState(
            periods = periods,
            symptoms = symptoms,
            settings = settings,
            cycleInfo = cycleInfo,
            selectedDate = selectedDate,
            selectedDateSymptoms = selectedDateSymptoms,
            isAppLocked = isLocked,
            isLockScreenDismissed = lockDismissed,
            snackbarMessage = _snackbarMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CycleUiState()
    )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun logPeriod(
        startDate: LocalDate,
        endDate: LocalDate?,
        flow: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertPeriod(
                PeriodLog(
                    startDate = startDate.toEpochDay(),
                    endDate = endDate?.toEpochDay(),
                    flow = flow,
                    notes = notes
                )
            )
            showSnackbar("Period recorded successfully")
        }
    }

    fun updatePeriod(period: PeriodLog) {
        viewModelScope.launch {
            repository.updatePeriod(period)
            showSnackbar("Period updated")
        }
    }

    fun deletePeriod(period: PeriodLog) {
        viewModelScope.launch {
            repository.deletePeriod(period)
            showSnackbar("Period deleted")
        }
    }

    fun saveSymptoms(
        date: LocalDate,
        cramps: String,
        headache: String,
        mood: String,
        discharge: String,
        energy: String,
        flow: String,
        hadIntercourse: Boolean,
        tookMedication: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            repository.saveSymptoms(
                DailySymptomLog(
                    date = date.toEpochDay(),
                    cramps = cramps,
                    headache = headache,
                    mood = mood,
                    discharge = discharge,
                    energy = energy,
                    flow = flow,
                    hadIntercourse = hadIntercourse,
                    tookMedication = tookMedication,
                    notes = notes
                )
            )
            showSnackbar("Symptoms saved for ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}")
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            showSnackbar("Settings updated")
        }
    }

    fun verifyPin(pin: String): Boolean {
        val currentSettings = uiState.value.settings
        return if (currentSettings.pinCode == pin) {
            _isLockScreenDismissed.value = true
            true
        } else {
            false
        }
    }

    fun setPin(pin: String) {
        viewModelScope.launch {
            val updated = uiState.value.settings.copy(
                isPinEnabled = true,
                pinCode = pin
            )
            repository.updateSettings(updated)
            _isLockScreenDismissed.value = true
            showSnackbar("PIN lock enabled")
        }
    }

    fun disablePin() {
        viewModelScope.launch {
            val updated = uiState.value.settings.copy(
                isPinEnabled = false,
                pinCode = ""
            )
            repository.updateSettings(updated)
            _isLockScreenDismissed.value = true
            showSnackbar("PIN lock removed")
        }
    }

    fun lockApp() {
        _isLockScreenDismissed.value = false
    }

    fun addSampleSecondPeriod() {
        // Quick convenience action for testing cycle calculation:
        // Adds period on Sep 26, 2026 (29 days after Aug 28, 2026)
        viewModelScope.launch {
            val secondPeriodStart = LocalDate.of(2026, 9, 26).toEpochDay()
            val secondPeriodEnd = LocalDate.of(2026, 9, 29).toEpochDay()
            repository.insertPeriod(
                PeriodLog(
                    startDate = secondPeriodStart,
                    endDate = secondPeriodEnd,
                    flow = "MEDIUM",
                    notes = "Second cycle recorded (29-day interval)"
                )
            )
            showSnackbar("Second period added: cycle length calculated at 29 days!")
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetAllData()
            _isLockScreenDismissed.value = true
            showSnackbar("App data reset to initial baseline (28 Aug 2026)")
        }
    }
}
