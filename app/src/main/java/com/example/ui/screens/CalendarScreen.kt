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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AppDateProvider
import com.example.ui.components.EvelynScreen
import com.example.ui.theme.LilacFertile
import com.example.ui.theme.OvulationPink
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.PeriodLight
import com.example.ui.theme.PeriodRed
import com.example.ui.theme.PetalBlush
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CycleUiState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    uiState: CycleUiState,
    onSelectDate: (LocalDate) -> Unit,
    onNavigate: (EvelynScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = AppDateProvider.getToday()
    val selectedDate = uiState.selectedDate
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday

    val periods = uiState.periods
    val cycleInfo = uiState.cycleInfo
    val symptoms = uiState.symptoms

    // Precompute date mappings
    val periodDates = mutableSetOf<LocalDate>()
    periods.forEach { period ->
        val end = period.endDate ?: (period.startDate + cycleInfo.averagePeriodDuration - 1)
        var curr = period.startDate
        while (curr <= end) {
            periodDates.add(LocalDate.ofEpochDay(curr))
            curr++
        }
    }

    val estimatedPeriodDates = mutableSetOf<LocalDate>()
    if (cycleInfo.nextEstimatedPeriodStart != null && cycleInfo.nextEstimatedPeriodEnd != null) {
        var curr = cycleInfo.nextEstimatedPeriodStart.toEpochDay()
        val end = cycleInfo.nextEstimatedPeriodEnd.toEpochDay()
        while (curr <= end) {
            estimatedPeriodDates.add(LocalDate.ofEpochDay(curr))
            curr++
        }
    }

    val fertileDates = mutableSetOf<LocalDate>()
    if (cycleInfo.fertileWindowStart != null && cycleInfo.fertileWindowEnd != null) {
        var curr = cycleInfo.fertileWindowStart.toEpochDay()
        val end = cycleInfo.fertileWindowEnd.toEpochDay()
        while (curr <= end) {
            fertileDates.add(LocalDate.ofEpochDay(curr))
            curr++
        }
    }

    val symptomDates = symptoms.map { LocalDate.ofEpochDay(it.date) }.toSet()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("calendar_screen")
    ) {
        // Month Navigation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) },
                        modifier = Modifier.testTag("calendar_prev_month")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                    }

                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PeonyDark
                        )
                    )

                    IconButton(
                        onClick = { currentMonth = currentMonth.plusMonths(1) },
                        modifier = Modifier.testTag("calendar_next_month")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Days of week header
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { dayLabel ->
                        Text(
                            text = dayLabel,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Grid
                val totalCells = ((startDayOfWeek + daysInMonth + 6) / 7) * 7
                for (row in 0 until (totalCells / 7)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - startDayOfWeek + 1

                            if (dayNumber in 1..daysInMonth) {
                                val cellDate = currentMonth.atDay(dayNumber)
                                val isPeriodDay = periodDates.contains(cellDate)
                                val isEstPeriod = estimatedPeriodDates.contains(cellDate)
                                val isFertile = fertileDates.contains(cellDate)
                                val isOvulation = cycleInfo.estimatedOvulationDate == cellDate
                                val isToday = cellDate == today
                                val isSelected = cellDate == selectedDate
                                val hasSymptoms = symptomDates.contains(cellDate)

                                // Cycle day relative to closest preceding period
                                val lastStartBefore = periods
                                    .map { LocalDate.ofEpochDay(it.startDate) }
                                    .filter { !it.isAfter(cellDate) }
                                    .maxOrNull()
                                val cycleDayNumber = if (lastStartBefore != null) {
                                    (cellDate.toEpochDay() - lastStartBefore.toEpochDay() + 1).toInt()
                                } else null

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                isSelected -> PeonyDark
                                                isPeriodDay -> PeriodLight
                                                isEstPeriod -> PeonyLight
                                                isFertile -> LilacFertile.copy(alpha = 0.15f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .then(
                                            if (isToday) Modifier.border(
                                                1.5.dp,
                                                PeonyRose,
                                                RoundedCornerShape(12.dp)
                                            ) else Modifier
                                        )
                                        .clickable { onSelectDate(cellDate) }
                                        .testTag("calendar_day_$dayNumber"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isToday || isSelected || isPeriodDay) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> Color.White
                                                    isPeriodDay -> PeriodRed
                                                    isFertile -> LilacFertile
                                                    else -> TextPrimary
                                                },
                                                fontSize = 13.sp
                                            )
                                        )
                                        // Cycle Day subscript
                                        if (cycleDayNumber != null && cycleDayNumber > 0) {
                                            Text(
                                                text = "D$cycleDayNumber",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.sp,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary
                                                )
                                            )
                                        }
                                        // Symptom or Ovulation dot
                                        if (hasSymptoms || isOvulation) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isOvulation) OvulationPink
                                                        else if (isSelected) Color.White
                                                        else PeonyRose
                                                    )
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f).height(48.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = PeriodRed, label = "Period")
                    LegendItem(color = LilacFertile, label = "Fertile")
                    LegendItem(color = PeonyRose, label = "Estimated")
                    LegendItem(color = TextSecondary, label = "• Symptoms")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Date Details Card
        val selectedPeriod = periods.firstOrNull { p ->
            val end = p.endDate ?: (p.startDate + cycleInfo.averagePeriodDuration - 1)
            selectedDate.toEpochDay() in p.startDate..end
        }
        val selectedSymptoms = uiState.selectedDateSymptoms

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("calendar_selected_date_card"),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PeonyDark
                            )
                        )
                        val lastStart = periods
                            .map { LocalDate.ofEpochDay(it.startDate) }
                            .filter { !it.isAfter(selectedDate) }
                            .maxOrNull()
                        val cycleDay = if (lastStart != null) (selectedDate.toEpochDay() - lastStart.toEpochDay() + 1).toInt() else null
                        Text(
                            text = if (cycleDay != null) "Cycle Day $cycleDay" else "Cycle Day —",
                            style = MaterialTheme.typography.bodySmall.copy(color = PeonyRose, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    if (selectedPeriod != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PeriodLight)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Period (${selectedPeriod.flow})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PeriodRed
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Symptoms logged
                if (selectedSymptoms != null) {
                    Text(
                        text = "Logged for this day:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Mood: ${selectedSymptoms.mood}\n• Cramps: ${selectedSymptoms.cramps}\n• Headache: ${selectedSymptoms.headache}\n• Discharge: ${selectedSymptoms.discharge}\n• Energy: ${selectedSymptoms.energy}" +
                                (if (selectedSymptoms.tookMedication) "\n• Medication: Taken" else "") +
                                (if (selectedSymptoms.hadIntercourse) "\n• Intimacy recorded" else "") +
                                (if (selectedSymptoms.notes.isNotBlank()) "\n• Note: ${selectedSymptoms.notes}" else ""),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    )
                } else {
                    Text(
                        text = "No symptoms recorded for this day.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onNavigate(EvelynScreen.SYMPTOMS) },
                        modifier = Modifier.weight(1f).testTag("calendar_edit_symptoms_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (selectedSymptoms != null) "Edit Symptoms" else "Log Symptoms")
                    }

                    OutlinedButton(
                        onClick = { onNavigate(EvelynScreen.LOG_PERIOD) },
                        modifier = Modifier.weight(1f).testTag("calendar_log_period_btn")
                    ) {
                        Icon(Icons.Default.Opacity, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Period")
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp))
    }
}
