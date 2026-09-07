package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.domain.AppDateProvider
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CycleUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SymptomsScreen(
    uiState: CycleUiState,
    onSaveSymptoms: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = AppDateProvider.getToday()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")

    var selectedDate by remember { mutableStateOf(uiState.selectedDate) }
    val existingSymptom = uiState.symptoms.firstOrNull { it.date == selectedDate.toEpochDay() }

    var cramps by remember { mutableStateOf("NONE") }
    var headache by remember { mutableStateOf("NONE") }
    var mood by remember { mutableStateOf("CALM") }
    var discharge by remember { mutableStateOf("NONE") }
    var energy by remember { mutableStateOf("MEDIUM") }
    var flow by remember { mutableStateOf("NONE") }
    var hadIntercourse by remember { mutableStateOf(false) }
    var tookMedication by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    // Update local state when existing symptom changes for the selected date
    LaunchedEffect(selectedDate, existingSymptom) {
        if (existingSymptom != null) {
            cramps = existingSymptom.cramps
            headache = existingSymptom.headache
            mood = existingSymptom.mood
            discharge = existingSymptom.discharge
            energy = existingSymptom.energy
            flow = existingSymptom.flow
            hadIntercourse = existingSymptom.hadIntercourse
            tookMedication = existingSymptom.tookMedication
            notes = existingSymptom.notes
        } else {
            cramps = "NONE"
            headache = "NONE"
            mood = "CALM"
            discharge = "NONE"
            energy = "MEDIUM"
            flow = "NONE"
            hadIntercourse = false
            tookMedication = false
            notes = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("symptoms_screen")
    ) {
        Text(
            text = "Daily Symptoms & Mood",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Text(
            text = "Track cramps, headache, discharge, moods, and notes",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
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
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = PeonyRose,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = selectedDate.format(dateFormatter),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PeonyDark
                            )
                        )
                        if (selectedDate == today) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelSmall.copy(color = PeonyRose, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PeonyLight)
                            .clickable { selectedDate = selectedDate.minusDays(1) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("-1d", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PeonyDark))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PeonyLight)
                            .clickable { selectedDate = today }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("Today", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PeonyDark))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PeonyLight)
                            .clickable { selectedDate = selectedDate.plusDays(1) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("+1d", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PeonyDark))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cramps Category
        SymptomSectionCard(title = "Cramps") {
            SymptomOptionRow(
                options = listOf("NONE", "MILD", "MODERATE", "SEVERE"),
                selected = cramps,
                onSelect = { cramps = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mood Category
        SymptomSectionCard(title = "Mood") {
            val moods = listOf(
                "CALM", "HAPPY", "ENERGETIC", "SENSITIVE",
                "IRRITABLE", "SAD", "ANXIOUS", "FATIGUED"
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.take(4).forEach { m ->
                        SymptomChip(
                            label = m.lowercase().replaceFirstChar { it.uppercase() },
                            isSelected = mood == m,
                            onSelect = { mood = m },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.drop(4).forEach { m ->
                        SymptomChip(
                            label = m.lowercase().replaceFirstChar { it.uppercase() },
                            isSelected = mood == m,
                            onSelect = { mood = m },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Headache Category
        SymptomSectionCard(title = "Headache") {
            SymptomOptionRow(
                options = listOf("NONE", "MILD", "MODERATE", "SEVERE"),
                selected = headache,
                onSelect = { headache = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cervical Discharge Category
        SymptomSectionCard(title = "Cervical Discharge") {
            val dischargeOptions = listOf(
                "NONE", "DRY", "STICKY", "CREAMY", "EGG_WHITE", "WATERY"
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dischargeOptions.take(3).forEach { d ->
                        val formatted = d.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                        SymptomChip(
                            label = formatted,
                            isSelected = discharge == d,
                            onSelect = { discharge = d },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dischargeOptions.drop(3).forEach { d ->
                        val formatted = d.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                        SymptomChip(
                            label = formatted,
                            isSelected = discharge == d,
                            onSelect = { discharge = d },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Energy Category
        SymptomSectionCard(title = "Energy Level") {
            SymptomOptionRow(
                options = listOf("LOW", "MEDIUM", "HIGH"),
                selected = energy,
                onSelect = { energy = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Toggles: Intimacy & Medication
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = PeonyRose, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Intimacy / Intercourse", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                    Switch(
                        checked = hadIntercourse,
                        onCheckedChange = { hadIntercourse = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PeonyRose)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = PeonyRose, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Took Medication / Supplements", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                    Switch(
                        checked = tookMedication,
                        onCheckedChange = { tookMedication = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PeonyRose)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Notes",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("How are you feeling today?") },
                    modifier = Modifier.fillMaxWidth().testTag("symptoms_notes_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Symptoms Button
        Button(
            onClick = {
                onSaveSymptoms(
                    selectedDate,
                    cramps,
                    headache,
                    mood,
                    discharge,
                    energy,
                    flow,
                    hadIntercourse,
                    tookMedication,
                    notes
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_symptoms_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Symptoms for ${selectedDate.format(DateTimeFormatter.ofPattern("d MMM"))}", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SymptomSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SleekCardBorder),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PeonyDark
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SymptomOptionRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { opt ->
            val isSelected = selected == opt
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) PeonyRose else CardSurfaceVariant)
                    .clickable { onSelect(opt) }
                    .padding(vertical = 10.dp)
                    .testTag("opt_${opt.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opt.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun SymptomChip(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PeonyRose else CardSurfaceVariant)
            .clickable { onSelect() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextPrimary
            )
        )
    }
}
