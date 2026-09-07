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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.domain.AppDateProvider
import com.example.domain.CyclePhase
import com.example.ui.components.CycleRing
import com.example.ui.components.EvelynScreen
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
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    uiState: CycleUiState,
    onNavigate: (EvelynScreen) -> Unit,
    onQuickLogPeriod: () -> Unit,
    onQuickLogSymptoms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = AppDateProvider.getToday()
    val cycleInfo = uiState.cycleInfo
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    val todaySymptom = uiState.symptoms.firstOrNull { it.date == today.toEpochDay() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("home_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Today, ${today.format(DateTimeFormatter.ofPattern("EEEE, d MMMM"))}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Personal Health & Cycle",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prominent Interactive Cycle Ring
        CycleRing(
            cycleInfo = cycleInfo,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Next Estimated Period Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("next_period_card"),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(
                containerColor = if (cycleInfo.averageCycleLength != null) PeonyLight else CardSurfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PeonyRose.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = "Period",
                            tint = PeonyRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Next Estimated Period",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PeonyDark
                            )
                        )
                        Text(
                            text = if (cycleInfo.averageCycleLength != null && cycleInfo.nextEstimatedPeriodStart != null) {
                                "${cycleInfo.nextEstimatedPeriodStart.format(dateFormatter)} (${cycleInfo.daysUntilNextPeriod ?: 0} days away)"
                            } else {
                                "Cycle length: Not known yet"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (cycleInfo.averageCycleLength != null) {
                        "Estimated based on your calculated average cycle length of ${cycleInfo.averageCycleLength} days. Period duration: ${cycleInfo.averagePeriodDuration} days."
                    } else {
                        "Evelyn doesn't invent your cycle length. Once you record your next period start date, the app will automatically calculate your personal average and provide personalized predictions."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                )

                if (cycleInfo.averageCycleLength == null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { onNavigate(EvelynScreen.LOG_PERIOD) },
                        modifier = Modifier.fillMaxWidth().testTag("home_log_period_cta"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PeonyRose
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Period")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Log Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onQuickLogPeriod,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("quick_log_period_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PeonyRose,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Opacity,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Period", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onQuickLogSymptoms,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("quick_log_symptoms_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PeonyDark
                )
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Symptoms", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Today's Phase & Insights
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("phase_insight_card"),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PeonyRose,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Phase Insight • ${cycleInfo.currentPhase.displayName}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PeonyDark
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (cycleInfo.currentPhase) {
                        CyclePhase.MENSTRUATION -> "Rest and stay hydrated. Gentle movement and warmth help ease pelvic tension."
                        CyclePhase.FOLLICULAR -> "Estrogen is rising steadily. You may feel increasing physical energy, heightened focus, and an uplifted mood."
                        CyclePhase.OVULATION -> "Peak fertility window. Energy, social confidence, and libido are typically at their monthly zenith."
                        CyclePhase.FERTILE -> "High conception probability. Hormonal vitality creates peak receptivity."
                        CyclePhase.LUTEAL -> "Progesterone rises as your body winds down. Prioritize restorative sleep and balanced nutrition."
                        CyclePhase.UNKNOWN -> "Listening to your body's natural signals. Every cycle logged brings deeper personalized insight."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Today's Logged Symptoms Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(EvelynScreen.SYMPTOMS) }
                .testTag("today_symptoms_card"),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Today's Symptoms",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (todaySymptom != null) {
                        Text(
                            text = "Mood: ${todaySymptom.mood} • Cramps: ${todaySymptom.cramps} • Energy: ${todaySymptom.energy}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    } else {
                        Text(
                            text = "Nothing logged yet today. Tap to track mood, cramps & energy.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go to Symptoms",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Navigation Cards (Calendar & Fertility)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(EvelynScreen.CALENDAR) }
                    .testTag("home_card_calendar"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, SleekCardBorder),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = PeonyRose,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "History & Cycle Days",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(EvelynScreen.FERTILITY) }
                    .testTag("home_card_fertility"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, SleekCardBorder),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Fertility",
                        tint = LilacFertile,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fertility",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Ovulation Window",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
