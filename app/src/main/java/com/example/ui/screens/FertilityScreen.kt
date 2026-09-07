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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AppDateProvider
import com.example.domain.CyclePhase
import com.example.ui.components.EvelynScreen
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.LilacFertile
import com.example.ui.theme.OvulationPink
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CycleUiState
import java.time.format.DateTimeFormatter

@Composable
fun FertilityScreen(
    uiState: CycleUiState,
    onNavigate: (EvelynScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = AppDateProvider.getToday()
    val cycleInfo = uiState.cycleInfo
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

    val isFertilityKnown = cycleInfo.averageCycleLength != null &&
            cycleInfo.estimatedOvulationDate != null &&
            cycleInfo.fertileWindowStart != null &&
            cycleInfo.fertileWindowEnd != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("fertility_screen")
    ) {
        Text(
            text = "Fertility & Ovulation",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PeonyDark
            )
        )
        Text(
            text = "Estimated fertile window and ovulation tracking",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isFertilityKnown) {
            // Explanatory card when cycle length is not yet known
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fertility_learning_card"),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, SleekCardBorder),
                colors = CardDefaults.cardColors(containerColor = PeonyLight)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = LilacFertile,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Fertility Estimation Waiting for Data",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PeonyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Evelyn does not invent or assume your cycle length. Because every body's rhythm is unique, the app requires at least two recorded period start dates before calculating your personal cycle length.\n\nOnce you record your next period start date, Evelyn will automatically determine your ovulation day and pin-point your high-chance fertile window.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onNavigate(EvelynScreen.LOG_PERIOD) },
                        modifier = Modifier.fillMaxWidth().testTag("fertility_log_period_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PeonyRose)
                    ) {
                        Text("Log Next Period Start")
                    }
                }
            }
        } else {
            // Fertility is calculated!
            val ovDate = cycleInfo.estimatedOvulationDate!!
            val fStart = cycleInfo.fertileWindowStart!!
            val fEnd = cycleInfo.fertileWindowEnd!!

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fertility_active_card"),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, SleekCardBorder),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Ovulation",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            )
                            Text(
                                text = ovDate.format(dateFormatter),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OvulationPink
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(OvulationPink.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = OvulationPink,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fertile Window
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(LilacFertile.copy(alpha = 0.12f), PeonyLight)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Fertile Window",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LilacFertile
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${fStart.format(DateTimeFormatter.ofPattern("d MMM"))} – ${fEnd.format(DateTimeFormatter.ofPattern("d MMM yyyy"))}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PeonyDark
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Higher probability of conception during these 6 days.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Current status today
                    val conceptionChance = when {
                        today == ovDate -> "Peak"
                        !today.isBefore(fStart) && !today.isAfter(fEnd) -> "High"
                        today.isAfter(fEnd) -> "Low (Luteal Phase)"
                        else -> "Low (Early Follicular)"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Conception Probability:",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (conceptionChance == "Peak" || conceptionChance == "High") PeonyLight else CardSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = conceptionChance,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (conceptionChance == "Peak") OvulationPink else if (conceptionChance == "High") LilacFertile else TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Cervical Mucus & Signs of Fertility Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, SleekCardBorder),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = LilacFertile)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fertility Biomarkers & Discharge",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PeonyDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FertilitySignItem(
                    phase = "Egg White / Clear & Stretchy",
                    meaning = "Peak fertility. Resembles raw egg white; facilitates sperm motility.",
                    isPeak = true
                )
                FertilitySignItem(
                    phase = "Creamy / Lotion-like",
                    meaning = "Approaching fertile window. Estrogen is actively rising.",
                    isPeak = false
                )
                FertilitySignItem(
                    phase = "Dry or Sticky",
                    meaning = "Lower conception chance. Standard post-menstrual or luteal state.",
                    isPeak = false
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Educational Note
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PeonyRose,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Medical note: Cycle and ovulation estimates are algorithmic tools based on typical luteal phases (~14 days). They should not be used as a primary method of birth control.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FertilitySignItem(
    phase: String,
    meaning: String,
    isPeak: Boolean
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = phase,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isPeak) OvulationPink else TextPrimary
                )
            )
            if (isPeak) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OvulationPink.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Most Fertile",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OvulationPink
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = meaning,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
    }
}
