package com.theinheritance.accounting

import com.theinheritance.accounting.fraud.FraudDetector
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.FraudSchemeLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FraudDetectorTest {
    private val detector = FraudDetector()

    @Test fun `library holds 40 or more schemes`() {
        assertTrue(FraudSchemeLibrary.all.size >= 40)
    }

    @Test fun `every scheme has mechanism and discovery path`() {
        for (scheme in FraudSchemeLibrary.all) {
            assertTrue(scheme.title.isNotBlank())
            assertTrue(scheme.mechanism.isNotBlank())
            assertTrue(scheme.discoveryPath.isNotBlank())
        }
    }

    @Test fun `rounding leak fires on five sub-rand deltas`() {
        val finding = detector.detectRoundingLeak(listOf(3, 12, 41, 7, 50, 1200))
        assertNotNull(finding)
        assertEquals(FraudScheme.ROUNDING_LEAK, finding!!.scheme)
    }

    @Test fun `rounding leak stays quiet below threshold`() {
        assertNull(detector.detectRoundingLeak(listOf(3, 12, 1200)))
    }

    @Test fun `duplicate payroll names the repeat`() {
        val finding = detector.detectDuplicatePayroll(
            listOf("Payroll Mara" to 50_000L, "Payroll Mara" to 50_000L, "Rent" to 30_000L)
        )
        assertNotNull(finding)
        assertEquals(FraudScheme.DUPLICATE_PAYROLL, finding!!.scheme)
    }

    @Test fun `revenue skim fires when pos exceeds deposits`() {
        val finding = detector.detectRevenueSkim(posTotalCents = 100_000, depositTotalCents = 90_000)
        assertNotNull(finding)
        assertEquals(FraudScheme.REVENUE_SKIM, finding!!.scheme)
    }

    @Test fun `aged prepaid trips reclassification`() {
        val finding = detector.detectAgingPrepaid(prepaidCents = 60_000_00, daysOld = 120)
        assertNotNull(finding)
        assertEquals(FraudScheme.EXPENSE_RECLASS, finding!!.scheme)
    }
}
