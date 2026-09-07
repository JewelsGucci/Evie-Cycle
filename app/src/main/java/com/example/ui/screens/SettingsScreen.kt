package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AppDateProvider
import com.example.ui.components.EvelynScreen
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CycleUiState

@Composable
fun SettingsScreen(
    uiState: CycleUiState,
    onNavigate: (EvelynScreen) -> Unit,
    onSetPin: (String) -> Unit,
    onDisablePin: () -> Unit,
    onLockApp: () -> Unit,
    onResetData: () -> Unit,
    onAddSampleSecondPeriod: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = uiState.settings
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showExportSummaryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "Settings & Privacy",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Text(
            text = "PIN lock, notification controls, and data management",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy & PIN Lock Card
        Text(
            text = "Security & Privacy",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PeonyDark)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().testTag("pin_settings_card"),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PeonyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PeonyDark, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("PIN / Biometric Lock", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(
                                if (settings.isPinEnabled) "App is protected with a 4-digit PIN" else "Require PIN to view health records",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    Switch(
                        checked = settings.isPinEnabled,
                        onCheckedChange = { enable ->
                            if (enable) {
                                pinInput = ""
                                pinError = null
                                showPinDialog = true
                            } else {
                                onDisablePin()
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PeonyRose),
                        modifier = Modifier.testTag("toggle_pin_lock")
                    )
                }

                if (settings.isPinEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                pinInput = ""
                                pinError = null
                                showPinDialog = true
                            },
                            modifier = Modifier.weight(1f).testTag("change_pin_button")
                        ) {
                            Text("Change PIN")
                        }

                        Button(
                            onClick = onLockApp,
                            modifier = Modifier.weight(1f).testTag("lock_now_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PeonyDark)
                        ) {
                            Text("Lock App Now")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification Controls Shortcut
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PeonyDark)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("notification_controls_card"),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            onClick = { onNavigate(EvelynScreen.NOTIFICATIONS) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = PeonyRose, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Notification Controls", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Period, fertile window, and medication alerts", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Management
        Text(
            text = "Data Management",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PeonyDark)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().testTag("data_management_card"),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = PeonyRose, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Local Storage Summary",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• ${uiState.periods.size} period records stored\n• ${uiState.symptoms.size} daily symptom entries\n• On-device Room SQLite database (No cloud sync, 100% private)",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showExportSummaryDialog = true },
                        modifier = Modifier.weight(1f).testTag("export_data_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Summary")
                    }

                    OutlinedButton(
                        onClick = { showResetConfirmDialog = true },
                        modifier = Modifier.weight(1f).testTag("reset_data_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Baseline")
                    }
                }

                if (uiState.periods.size == 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onAddSampleSecondPeriod,
                        modifier = Modifier.fillMaxWidth().testTag("settings_add_sample_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add 2nd Period to Test Auto-Calculation")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About & Baseline Data Overview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Version 1 — “Evelyn Cycle”",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = PeonyDark)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Initial Baseline:\n• Last period started: 28 Aug 2026\n• Expected end: 31 Aug 2026 (4 days duration)\n• Cycle length: Not known yet (awaits personal records)",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // Set PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set 4-Digit PIN") },
            text = {
                Column {
                    Text("Enter a 4-digit code to secure your cycle and symptoms logs:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                pinInput = it
                                pinError = null
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        label = { Text("4-digit PIN") },
                        isError = pinError != null,
                        supportingText = pinError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth().testTag("pin_dialog_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length == 4) {
                            onSetPin(pinInput)
                            showPinDialog = false
                        } else {
                            pinError = "Please enter exactly 4 digits"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset to Initial Baseline?") },
            text = {
                Text("This will restore the app data to the baseline from 28 Aug 2026 (4-day duration, cycle length unknown) and clear any custom symptoms or periods.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Export Summary Dialog
    if (showExportSummaryDialog) {
        val cycle = uiState.cycleInfo
        val textSummary = buildString {
            appendLine("=== Evelyn Cycle Data Summary ===")
            appendLine("As of: ${AppDateProvider.getToday()}")
            appendLine("Current Cycle Day: ${cycle.currentCycleDay ?: "—"}")
            appendLine("Cycle Length: ${cycle.averageCycleLength?.let { "$it days" } ?: "Not known yet"}")
            appendLine("Average Period Duration: ${cycle.averagePeriodDuration} days")
            appendLine("Recorded Periods: ${uiState.periods.size}")
            appendLine("Recorded Symptoms: ${uiState.symptoms.size}")
            if (cycle.nextEstimatedPeriodStart != null) {
                appendLine("Next Estimated Period: ${cycle.nextEstimatedPeriodStart}")
            }
            if (cycle.estimatedOvulationDate != null) {
                appendLine("Estimated Ovulation: ${cycle.estimatedOvulationDate}")
            }
        }

        AlertDialog(
            onDismissRequest = { showExportSummaryDialog = false },
            title = { Text("Cycle Data Summary") },
            text = {
                Text(
                    text = textSummary,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showExportSummaryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
                ) {
                    Text("Close")
                }
            }
        )
    }
}
