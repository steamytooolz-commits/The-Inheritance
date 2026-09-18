package com.theinheritance.monetization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MonetizationTest {
    @Test fun `pro product id is stable`() {
        assertEquals("the_inheritance_pro", BillingManager.PRO_PRODUCT_ID)
    }

    @Test fun `analytics event names are stable`() {
        assertEquals("run_started", AnalyticsLogger.EVENT_RUN_STARTED)
        assertEquals("day_advanced", AnalyticsLogger.EVENT_DAY_ADVANCED)
        assertEquals("fraud_found", AnalyticsLogger.EVENT_FRAUD_FOUND)
        assertEquals("run_ended", AnalyticsLogger.EVENT_RUN_ENDED)
        assertEquals("pro_unlocked", AnalyticsLogger.EVENT_PRO_UNLOCKED)
    }

    @Test fun `ads tag is stable`() {
        assertTrue(AdsManager.INTERSTITIAL_TAG.isNotBlank())
    }
}
