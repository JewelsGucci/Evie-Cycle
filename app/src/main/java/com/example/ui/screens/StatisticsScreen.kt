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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EvelynScreen
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.PeriodLight
import com.example.ui.theme.PeriodRed
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CycleUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsScreen(
    uiState: CycleUiState,
    onNavigate: (EvelynScreen) -> Unit,
    onAddSampleSecondPeriod: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cycleInfo = uiState.cycleInfo
    val periods = uiState.periods.sortedBy { it.startDate }
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("statistics_screen")
    ) {
        Text(
            text = "Cycle & Period Statistics",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Text(
            text = "Personal averages derived strictly from your recorded data",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Top 2 KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Average Cycle Length Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("stat_cycle_length_card"),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, SleekCardBorder),
                colors = CardDefaults.cardColors(
                    containerColor = if (cycleInfo.averageCycleLength != null) PeonyLight else CardSurfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Loop, contentDescription = null, tint = PeonyRose, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Average Cycle Length",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (cycleInfo.averageCycleLength != null) "${cycleInfo.averageCycleLength} days" else "Not known yet",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PeonyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (cycleInfo.averageCycleLength != null) {
                            "Based on ${cycleInfo.completedCyclesCount} cycle interval"
                        } else {
                            "Requires 2+ recorded periods"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                    )
                }
            }

            // Average Period Duration Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("stat_period_length_card"),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, SleekCardBorder),
                colors = CardDefaults.cardColors(containerColor = PeriodLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Opacity, contentDescription = null, tint = PeriodRed, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Average Period Length",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${cycleInfo.averagePeriodDuration} days",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PeriodRed
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Usual duration: 4 days",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Automatic Calculation Explanation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (cycleInfo.averageCycleLength != null) Icons.Default.CheckCircle else Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = if (cycleInfo.averageCycleLength != null) PeonyDark else PeonyRose,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (cycleInfo.averageCycleLength != null) "Personal Rhythm Established" else "How Cycle Calculation Works",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PeonyDark
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = cycleInfo.cycleLengthExplanation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                )

                if (cycleInfo.averageCycleLength == null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onAddSampleSecondPeriod,
                        modifier = Modifier.fillMaxWidth().testTag("stats_add_second_cycle_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Sample 2nd Period (Sep 26, 2026)")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Cycle History
        Text(
            text = "Cycle History",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (periods.isEmpty()) {
            Text("No cycles recorded.", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        } else {
            for (i in periods.indices) {
                val p = periods[i]
                val startDate = LocalDate.ofEpochDay(p.startDate)
                val endDate = p.endDate?.let { LocalDate.ofEpochDay(it) }
                val periodDuration = if (endDate != null) (p.endDate - p.startDate + 1).toInt() else null

                // Cycle interval is distance to the NEXT period start date
                val nextPeriod = if (i < periods.size - 1) periods[i + 1] else null
                val cycleLength = if (nextPeriod != null) {
                    (nextPeriod.startDate - p.startDate).toInt()
                } else null

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(18.dp),
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
                        Column {
                            Text(
                                text = "Cycle ${i + 1} • Started ${startDate.format(dateFormatter)}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Bleeding: ${periodDuration?.let { "$it days" } ?: "Ongoing"} (${p.flow.lowercase().replaceFirstChar { it.uppercase() }} flow)",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (cycleLength != null) PeonyLight else CardSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (cycleLength != null) "$cycleLength days" else "Current Cycle",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (cycleLength != null) PeonyDark else TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
