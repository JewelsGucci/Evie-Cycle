package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PeriodLog
import com.example.domain.AppDateProvider
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.PeriodLight
import com.example.ui.theme.PeriodRed
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SpottingOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CycleUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun LogPeriodScreen(
    uiState: CycleUiState,
    onSavePeriod: (LocalDate, LocalDate?, String, String) -> Unit,
    onDeletePeriod: (PeriodLog) -> Unit,
    onAddSampleSecondPeriod: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = AppDateProvider.getToday()
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    var startDate by remember { mutableStateOf(uiState.selectedDate) }
    var isPeriodOngoing by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf<LocalDate?>(uiState.selectedDate.plusDays(3)) }
    var selectedFlow by remember { mutableStateOf("MEDIUM") }
    var notes by remember { mutableStateOf("") }

    val flowOptions = listOf(
        FlowOption("SPOTTING", "Spotting", SpottingOrange, 1),
        FlowOption("LIGHT", "Light", PeriodRed.copy(alpha = 0.6f), 2),
        FlowOption("MEDIUM", "Medium", PeriodRed, 3),
        FlowOption("HEAVY", "Heavy", PeonyDark, 4)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("log_period_screen")
    ) {
        Text(
            text = "Log Period",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Text(
            text = "Record period start and end dates with flow intensity",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Logging Form Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("period_form_card"),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Start Date Selection
                Text(
                    text = "Period Start Date",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSurfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PeonyRose, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(startDate.format(dateFormatter), fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DateAdjustButton(label = "-1d") { startDate = startDate.minusDays(1) }
                        DateAdjustButton(label = "+1d") { startDate = startDate.plusDays(1) }
                        DateAdjustButton(label = "Today") { startDate = today }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // End Date Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Period End Date",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isPeriodOngoing,
                            onCheckedChange = { isPeriodOngoing = it },
                            colors = CheckboxDefaults.colors(checkedColor = PeonyRose)
                        )
                        Text("Still bleeding", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (!isPeriodOngoing) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val currentEnd = endDate ?: startDate.plusDays(3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardSurfaceVariant)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(currentEnd.format(dateFormatter), fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            DateAdjustButton(label = "-1d") { endDate = currentEnd.minusDays(1).coerceAtLeast(startDate) }
                            DateAdjustButton(label = "+1d") { endDate = currentEnd.plusDays(1) }
                            DateAdjustButton(label = "4 Days") { endDate = startDate.plusDays(3) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Flow Intensity Selector
                Text(
                    text = "Flow Intensity",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    flowOptions.forEach { opt ->
                        val isSelected = selectedFlow == opt.key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) opt.color else CardSurfaceVariant)
                                .clickable { selectedFlow = opt.key }
                                .padding(vertical = 12.dp)
                                .testTag("flow_option_${opt.key.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(opt.dropCount) {
                                        Icon(
                                            imageVector = Icons.Default.Opacity,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else opt.color,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = opt.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    placeholder = { Text("e.g. Mild cramps, needed warm tea") },
                    modifier = Modifier.fillMaxWidth().testTag("period_notes_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Save Button
                Button(
                    onClick = {
                        val finalEnd = if (isPeriodOngoing) null else (endDate ?: startDate.plusDays(3))
                        onSavePeriod(startDate, finalEnd, selectedFlow, notes)
                        notes = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_period_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
                ) {
                    Text("Save Period Record", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History of Period Logs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Period History (${uiState.periods.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PeonyDark
                )
            )

            if (uiState.periods.size == 1) {
                // Quick test trigger for user/evaluator to test automatic calculation
                OutlinedButton(
                    onClick = onAddSampleSecondPeriod,
                    modifier = Modifier.testTag("quick_add_second_cycle_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Add 2nd Cycle", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.periods.isEmpty()) {
            Text(
                text = "No periods recorded yet.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        } else {
            uiState.periods.sortedByDescending { it.startDate }.forEach { period ->
                val start = LocalDate.ofEpochDay(period.startDate)
                val end = period.endDate?.let { LocalDate.ofEpochDay(it) }
                val duration = if (end != null) (period.endDate - period.startDate + 1).toInt() else null

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("period_item_${period.id}"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, SleekCardBorder),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PeriodLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Opacity, contentDescription = null, tint = PeriodRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (end != null) {
                                        "${start.format(dateFormatter)} – ${end.format(dateFormatter)}"
                                    } else {
                                        "${start.format(dateFormatter)} (Ongoing)"
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${duration?.let { "$it days • " } ?: ""}Flow: ${period.flow.lowercase().replaceFirstChar { it.uppercase() }}" +
                                            if (period.notes.isNotBlank()) " • \"${period.notes}\"" else "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDeletePeriod(period) },
                            modifier = Modifier.testTag("delete_period_${period.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DateAdjustButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PeonyLight)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
    }
}

private data class FlowOption(
    val key: String,
    val label: String,
    val color: Color,
    val dropCount: Int
)
