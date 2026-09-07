package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.AppDateProvider
import com.example.ui.components.EvelynBottomBar
import com.example.ui.components.EvelynScreen
import com.example.ui.components.EvelynTopBar
import com.example.ui.components.PinLockScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.FertilityScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LogPeriodScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.SymptomsScreen
import com.example.ui.theme.EvelynCycleTheme
import com.example.ui.viewmodel.CycleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EvelynCycleTheme {
                EvelynCycleApp()
            }
        }
    }
}

@Composable
fun EvelynCycleApp(
    viewModel: CycleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(EvelynScreen.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbars whenever snackbarMessage emits
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (uiState.isAppLocked) {
        PinLockScreen(
            onUnlock = { pin -> viewModel.verifyPin(pin) }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                EvelynTopBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> currentScreen = screen }
                )
            },
            bottomBar = {
                EvelynBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> currentScreen = screen }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                    when (screen) {
                        EvelynScreen.HOME -> HomeScreen(
                            uiState = uiState,
                            onNavigate = { target -> currentScreen = target },
                            onQuickLogPeriod = {
                                viewModel.selectDate(AppDateProvider.getToday())
                                currentScreen = EvelynScreen.LOG_PERIOD
                            },
                            onQuickLogSymptoms = {
                                viewModel.selectDate(AppDateProvider.getToday())
                                currentScreen = EvelynScreen.SYMPTOMS
                            }
                        )

                        EvelynScreen.CALENDAR -> CalendarScreen(
                            uiState = uiState,
                            onSelectDate = { date -> viewModel.selectDate(date) },
                            onNavigate = { target -> currentScreen = target }
                        )

                        EvelynScreen.LOG_PERIOD -> LogPeriodScreen(
                            uiState = uiState,
                            onSavePeriod = { start, end, flow, notes ->
                                viewModel.logPeriod(start, end, flow, notes)
                            },
                            onDeletePeriod = { period ->
                                viewModel.deletePeriod(period)
                            },
                            onAddSampleSecondPeriod = {
                                viewModel.addSampleSecondPeriod()
                            }
                        )

                        EvelynScreen.FERTILITY -> FertilityScreen(
                            uiState = uiState,
                            onNavigate = { target -> currentScreen = target }
                        )

                        EvelynScreen.SYMPTOMS -> SymptomsScreen(
                            uiState = uiState,
                            onSaveSymptoms = { date, cramps, headache, mood, discharge, energy, flow, intercourse, med, notes ->
                                viewModel.saveSymptoms(
                                    date, cramps, headache, mood, discharge, energy, flow, intercourse, med, notes
                                )
                            }
                        )

                        EvelynScreen.STATISTICS -> StatisticsScreen(
                            uiState = uiState,
                            onNavigate = { target -> currentScreen = target },
                            onAddSampleSecondPeriod = {
                                viewModel.addSampleSecondPeriod()
                            }
                        )

                        EvelynScreen.NOTIFICATIONS -> NotificationsScreen(
                            uiState = uiState,
                            onUpdateSettings = { newSettings ->
                                viewModel.updateSettings(newSettings)
                            }
                        )

                        EvelynScreen.SETTINGS -> SettingsScreen(
                            uiState = uiState,
                            onNavigate = { target -> currentScreen = target },
                            onSetPin = { pin -> viewModel.setPin(pin) },
                            onDisablePin = { viewModel.disablePin() },
                            onLockApp = { viewModel.lockApp() },
                            onResetData = { viewModel.resetData() },
                            onAddSampleSecondPeriod = { viewModel.addSampleSecondPeriod() }
                        )
                    }
                }
            }
        }
    }
}
