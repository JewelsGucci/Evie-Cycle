package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CycleInfo
import com.example.domain.CyclePhase
import com.example.ui.theme.LilacFertile
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.PeriodRed
import com.example.ui.theme.PetalBlush

@Composable
fun CycleRing(
    cycleInfo: CycleInfo,
    modifier: Modifier = Modifier
) {
    val day = cycleInfo.currentCycleDay ?: 1
    val totalDays = cycleInfo.averageCycleLength ?: 28 // visual default for track scale if not known
    val progress = (day.toFloat() / totalDays.toFloat()).coerceIn(0.05f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "cycleProgress"
    )

    val phaseColor = when (cycleInfo.currentPhase) {
        CyclePhase.MENSTRUATION -> PeriodRed
        CyclePhase.FERTILE, CyclePhase.OVULATION -> LilacFertile
        CyclePhase.FOLLICULAR -> PeonyRose
        CyclePhase.LUTEAL -> PetalBlush
        CyclePhase.UNKNOWN -> PeonyRose
    }

    Box(
        modifier = modifier
            .size(240.dp)
            .testTag("cycle_ring_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val strokeWidth = 18.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(diameter, diameter)

            // Background Track
            drawArc(
                color = PeonyLight,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(PeonyRose, phaseColor, PeonyDark)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.testTag("cycle_ring_text_column")
        ) {
            Text(
                text = "CYCLE",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    color = PeonyDark.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (cycleInfo.currentCycleDay != null) "Day ${cycleInfo.currentCycleDay}" else "Day —",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    color = PeonyDark
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = cycleInfo.currentPhase.displayName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = phaseColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (cycleInfo.averageCycleLength == null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Length: Learning",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
