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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSettings
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.LilacFertile
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.PeriodRed
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CycleUiState

@Composable
fun NotificationsScreen(
    uiState: CycleUiState,
    onUpdateSettings: (UserSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings = uiState.settings

    var periodReminders by remember(currentSettings) { mutableStateOf(currentSettings.periodReminders) }
    var fertileReminders by remember(currentSettings) { mutableStateOf(currentSettings.fertileReminders) }
    var medicationReminders by remember(currentSettings) { mutableStateOf(currentSettings.medicationReminders) }
    var medicationName by remember(currentSettings) { mutableStateOf(currentSettings.medicationName) }
    var medicationTime by remember(currentSettings) { mutableStateOf(currentSettings.medicationTime) }
    var dailyLogReminder by remember(currentSettings) { mutableStateOf(currentSettings.dailyLogReminder) }
    var dailyLogTime by remember(currentSettings) { mutableStateOf(currentSettings.dailyLogTime) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("notifications_screen")
    ) {
        Text(
            text = "Notifications & Reminders",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Text(
            text = "Period, fertile window, medication and daily reminders",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Period Reminders
        NotificationToggleCard(
            title = "Period Reminder",
            description = "Get notified 1-2 days before your next estimated period",
            icon = Icons.Default.Opacity,
            iconTint = PeriodRed,
            enabled = periodReminders,
            onToggle = {
                periodReminders = it
                onUpdateSettings(currentSettings.copy(periodReminders = it))
            },
            tag = "toggle_period_reminder"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Fertile Window Reminders
        NotificationToggleCard(
            title = "Fertile Window Alert",
            description = "Receive an alert when your estimated fertile window begins",
            icon = Icons.Default.Favorite,
            iconTint = LilacFertile,
            enabled = fertileReminders,
            onToggle = {
                fertileReminders = it
                onUpdateSettings(currentSettings.copy(fertileReminders = it))
            },
            tag = "toggle_fertile_reminder"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Medication / Pill Reminders
        Card(
            modifier = Modifier.fillMaxWidth().testTag("medication_reminder_card"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PeonyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = PeonyRose, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Medication Reminder", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Daily pill or vitamin notification", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }

                    Switch(
                        checked = medicationReminders,
                        onCheckedChange = {
                            medicationReminders = it
                            onUpdateSettings(currentSettings.copy(medicationReminders = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PeonyRose),
                        modifier = Modifier.testTag("toggle_medication_reminder")
                    )
                }

                if (medicationReminders) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = medicationName,
                        onValueChange = {
                            medicationName = it
                            onUpdateSettings(currentSettings.copy(medicationName = it))
                        },
                        label = { Text("Medication Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_medication_name"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = medicationTime,
                        onValueChange = {
                            medicationTime = it
                            onUpdateSettings(currentSettings.copy(medicationTime = it))
                        },
                        label = { Text("Reminder Time (e.g. 09:00 AM)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_medication_time"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Daily Symptom Log Reminder
        Card(
            modifier = Modifier.fillMaxWidth().testTag("daily_log_reminder_card"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CardSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = PeonyDark, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Daily Logging Reminder", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Gentle evening prompt to record symptoms", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }

                    Switch(
                        checked = dailyLogReminder,
                        onCheckedChange = {
                            dailyLogReminder = it
                            onUpdateSettings(currentSettings.copy(dailyLogReminder = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PeonyRose),
                        modifier = Modifier.testTag("toggle_daily_log_reminder")
                    )
                }

                if (dailyLogReminder) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dailyLogTime,
                        onValueChange = {
                            dailyLogTime = it
                            onUpdateSettings(currentSettings.copy(dailyLogTime = it))
                        },
                        label = { Text("Daily Log Time (e.g. 20:00 / 8:00 PM)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_daily_log_time"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // In-App Notification Previews
        Text(
            text = "Reminder Previews",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        NotificationPreviewCard(
            title = "Evelyn • Period Approaching",
            body = "Your period is estimated to arrive in 2 days. Be gentle with yourself today.",
            icon = Icons.Default.Opacity,
            iconTint = PeriodRed
        )

        Spacer(modifier = Modifier.height(8.dp))

        NotificationPreviewCard(
            title = "Evelyn • Daily Check-in",
            body = "How is your body feeling this evening? Tap to quickly log cramps, energy & mood.",
            icon = Icons.Default.EditNote,
            iconTint = PeonyRose
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun NotificationToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SleekCardBorder),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PeonyRose),
                modifier = Modifier.testTag(tag)
            )
        }
    }
}

@Composable
private fun NotificationPreviewCard(
    title: String,
    body: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SleekCardBorder),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PeonyDark))
                Spacer(modifier = Modifier.height(2.dp))
                Text(body, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 16.sp))
            }
        }
    }
}
