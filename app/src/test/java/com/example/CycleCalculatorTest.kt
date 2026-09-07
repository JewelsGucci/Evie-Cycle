package com.example

import com.example.data.model.PeriodLog
import com.example.data.model.UserSettings
import com.example.domain.CycleCalculator
import com.example.domain.CyclePhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class CycleCalculatorTest {

    @Test
    fun `initial data gives day 10 on 6 Sep 2026 and unknown cycle length`() {
        val initialPeriods = listOf(
            PeriodLog(
                startDate = LocalDate.of(2026, 8, 28).toEpochDay(),
                endDate = LocalDate.of(2026, 8, 31).toEpochDay(),
                flow = "MEDIUM"
            )
        )
        val settings = UserSettings(usualPeriodDuration = 4)
        val today = LocalDate.of(2026, 9, 6)

        val cycleInfo = CycleCalculator.calculate(initialPeriods, settings, today)

        // Day 10 (28 Aug = Day 1, 6 Sep = Day 10)
        assertEquals(10, cycleInfo.currentCycleDay)
        // Cycle length is not known yet because only 1 period exists
        assertNull(cycleInfo.averageCycleLength)
        assertNull(cycleInfo.nextEstimatedPeriodStart)
        assertNull(cycleInfo.estimatedOvulationDate)
        assertEquals(4, cycleInfo.averagePeriodDuration)
        assertEquals(CyclePhase.FOLLICULAR, cycleInfo.currentPhase)
    }

    @Test
    fun `two periods calculate accurate cycle length and next period`() {
        val periods = listOf(
            PeriodLog(
                startDate = LocalDate.of(2026, 8, 28).toEpochDay(),
                endDate = LocalDate.of(2026, 8, 31).toEpochDay(),
                flow = "MEDIUM"
            ),
            PeriodLog(
                startDate = LocalDate.of(2026, 9, 26).toEpochDay(),
                endDate = LocalDate.of(2026, 9, 29).toEpochDay(),
                flow = "MEDIUM"
            )
        )
        val settings = UserSettings(usualPeriodDuration = 4)
        val today = LocalDate.of(2026, 9, 28)

        val cycleInfo = CycleCalculator.calculate(periods, settings, today)

        // 26 Sep - 28 Aug = 29 days interval
        assertEquals(29, cycleInfo.averageCycleLength)
        // Next period estimated: 26 Sep + 29 days = 25 Oct 2026
        assertEquals(LocalDate.of(2026, 10, 25), cycleInfo.nextEstimatedPeriodStart)
        // Ovulation ~14 days before 25 Oct = 11 Oct 2026
        assertEquals(LocalDate.of(2026, 10, 11), cycleInfo.estimatedOvulationDate)
    }
}
