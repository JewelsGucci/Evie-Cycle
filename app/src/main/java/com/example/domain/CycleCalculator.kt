package com.example.domain

import com.example.data.model.PeriodLog
import com.example.data.model.UserSettings
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class CycleInfo(
    val currentCycleDay: Int?,
    val isPeriodActiveToday: Boolean,
    val averageCycleLength: Int?, // null if not known yet
    val averagePeriodDuration: Int,
    val nextEstimatedPeriodStart: LocalDate?,
    val nextEstimatedPeriodEnd: LocalDate?,
    val daysUntilNextPeriod: Long?,
    val estimatedOvulationDate: LocalDate?,
    val fertileWindowStart: LocalDate?,
    val fertileWindowEnd: LocalDate?,
    val currentPhase: CyclePhase,
    val completedCyclesCount: Int,
    val cycleLengthExplanation: String
)

enum class CyclePhase(val displayName: String, val description: String) {
    MENSTRUATION("Menstruation", "Bleeding and shedding of the uterine lining"),
    FOLLICULAR("Follicular Phase", "Estrogen rises as follicles prepare an egg"),
    OVULATION("Ovulation", "Egg is released; peak fertility window"),
    FERTILE("Fertile Window", "High likelihood of conception"),
    LUTEAL("Luteal Phase", "Progesterone rises; preparing for next cycle"),
    UNKNOWN("Cycle Underway", "Recording your personal rhythm")
}

object AppDateProvider {
    fun getToday(): LocalDate {
        val now = LocalDate.now()
        // Anchor context to Sep 6, 2026 if emulator system clock is in an earlier year
        return if (now.year < 2026) LocalDate.of(2026, 9, 6) else now
    }
}

object CycleCalculator {

    fun calculate(
        periods: List<PeriodLog>,
        settings: UserSettings,
        today: LocalDate = AppDateProvider.getToday()
    ): CycleInfo {
        val sortedPeriods = periods.sortedBy { it.startDate }
        val latestPeriod = sortedPeriods.lastOrNull()

        // 1. Average Period Duration
        val periodsWithDuration = sortedPeriods.mapNotNull { p ->
            if (p.endDate != null && p.endDate >= p.startDate) {
                (p.endDate - p.startDate + 1).toInt()
            } else null
        }
        val avgPeriodDuration = if (periodsWithDuration.isNotEmpty()) {
            Math.round(periodsWithDuration.average()).toInt().coerceAtLeast(1)
        } else {
            settings.usualPeriodDuration.coerceAtLeast(1)
        }

        // 2. Average Cycle Length
        // "Important: I won't invent her cycle length. Once she records a few more period start dates,
        // the app can calculate her personal average and make better estimates for the next cycle automatically."
        val cycleIntervals = mutableListOf<Long>()
        if (sortedPeriods.size >= 2) {
            for (i in 0 until sortedPeriods.size - 1) {
                val interval = sortedPeriods[i + 1].startDate - sortedPeriods[i].startDate
                if (interval in 15..90) { // Reasonable cycle threshold
                    cycleIntervals.add(interval)
                }
            }
        }

        val avgCycleLength: Int? = if (cycleIntervals.isNotEmpty()) {
            Math.round(cycleIntervals.average()).toInt()
        } else {
            null // Cycle length: Not known yet
        }

        val completedCyclesCount = cycleIntervals.size

        val cycleLengthExplanation = when {
            avgCycleLength == null ->
                "Cycle length: Not known yet. Once you record your next period start date, Evelyn will calculate your personal average and make automatic estimates."
            completedCyclesCount == 1 ->
                "Based on your last recorded cycle ($avgCycleLength days). Will become even more precise with each new log."
            else ->
                "Personal average of $avgCycleLength days across $completedCyclesCount recorded cycles."
        }

        // 3. Current Cycle Day
        val currentCycleDay = if (latestPeriod != null) {
            val daysSinceStart = today.toEpochDay() - latestPeriod.startDate
            if (daysSinceStart >= 0) {
                (daysSinceStart + 1).toInt()
            } else null
        } else null

        // 4. Is period active today?
        val isPeriodActiveToday = if (latestPeriod != null) {
            val periodEnd = latestPeriod.endDate ?: (latestPeriod.startDate + avgPeriodDuration - 1)
            today.toEpochDay() in latestPeriod.startDate..periodEnd
        } else false

        // 5. Next Estimated Period & Fertility (Only calculated when cycle length is known)
        var nextEstStart: LocalDate? = null
        var nextEstEnd: LocalDate? = null
        var daysUntilNext: Long? = null
        var ovulationDate: LocalDate? = null
        var fertileStart: LocalDate? = null
        var fertileEnd: LocalDate? = null

        if (latestPeriod != null && avgCycleLength != null) {
            val nextStartEpoch = latestPeriod.startDate + avgCycleLength
            val nextEndEpoch = nextStartEpoch + avgPeriodDuration - 1

            nextEstStart = LocalDate.ofEpochDay(nextStartEpoch)
            nextEstEnd = LocalDate.ofEpochDay(nextEndEpoch)
            daysUntilNext = nextStartEpoch - today.toEpochDay()

            // Ovulation typically occurs ~14 days before next period start
            val ovulationEpoch = nextStartEpoch - 14
            ovulationDate = LocalDate.ofEpochDay(ovulationEpoch)
            fertileStart = LocalDate.ofEpochDay(ovulationEpoch - 5)
            fertileEnd = LocalDate.ofEpochDay(ovulationEpoch + 1)
        }

        // 6. Current Phase Determination
        val phase = when {
            isPeriodActiveToday -> CyclePhase.MENSTRUATION
            ovulationDate != null && today == ovulationDate -> CyclePhase.OVULATION
            fertileStart != null && fertileEnd != null && !today.isBefore(fertileStart) && !today.isAfter(fertileEnd) -> CyclePhase.FERTILE
            ovulationDate != null && today.isAfter(ovulationDate) -> CyclePhase.LUTEAL
            currentCycleDay != null && currentCycleDay > avgPeriodDuration -> CyclePhase.FOLLICULAR
            else -> CyclePhase.UNKNOWN
        }

        return CycleInfo(
            currentCycleDay = currentCycleDay,
            isPeriodActiveToday = isPeriodActiveToday,
            averageCycleLength = avgCycleLength,
            averagePeriodDuration = avgPeriodDuration,
            nextEstimatedPeriodStart = nextEstStart,
            nextEstimatedPeriodEnd = nextEstEnd,
            daysUntilNextPeriod = daysUntilNext,
            estimatedOvulationDate = ovulationDate,
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileEnd,
            currentPhase = phase,
            completedCyclesCount = completedCyclesCount,
            cycleLengthExplanation = cycleLengthExplanation
        )
    }
}
