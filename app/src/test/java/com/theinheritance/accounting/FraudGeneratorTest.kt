package com.theinheritance.accounting

import com.theinheritance.accounting.fraud.FraudGenerator
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FraudGeneratorTest {
    @Test fun `generates consistent profile for same seed`() {
        val p1 = FraudGenerator(seed = 42L).generate()
        val p2 = FraudGenerator(seed = 42L).generate()
        assertEquals(p1.primaryScheme, p2.primaryScheme)
        assertEquals(p1.hiddenDeepestTruth, p2.hiddenDeepestTruth)
    }
}
