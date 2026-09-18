package com.theinheritance.accounting

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.engine.FinancialStatementGenerator
import com.theinheritance.accounting.engine.PeriodCloser
import com.theinheritance.accounting.model.ChartOfAccounts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FinancialStatementsTest {
    private fun engine(): AccountingEngine {
        val e = AccountingEngine(ChartOfAccounts.default())
        // Sale R1 000 cash: DR Bank 1010 / CR Sales 4000
        e.postSimple(LocalDate.of(2026, 1, 5), "Sale", 1010, 4000, 100_000).getOrThrow()
        // Rent R300: DR Rent 5200 / CR Bank 1010
        e.postSimple(LocalDate.of(2026, 1, 6), "Rent", 5200, 1010, 30_000).getOrThrow()
        return e
    }

    @Test fun `income statement nets correctly`() {
        val gen = FinancialStatementGenerator(engine())
        val inc = gen.incomeStatement(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31))
        assertEquals(100_000L, inc.revenueCents)
        assertEquals(70_000L, inc.netIncomeCents)
    }

    @Test fun `trial balance stays balanced after postings`() {
        val tb = engine().trialBalance(LocalDate.of(2026, 1, 31))
        assertTrue(tb.isBalanced)
        assertEquals(tb.totalDebits, tb.totalCredits)
    }

    @Test fun `period closer closes balanced month`() {
        val e = engine()
        assertTrue(PeriodCloser(e).closeMonth(2026, 1).isSuccess)
        // Posting into closed period must fail
        val r = e.postSimple(LocalDate.of(2026, 1, 15), "Late", 1010, 4000, 1_000)
        assertTrue(r.isFailure)
    }

    @Test fun `cash flow operating tracks net income`() {
        val gen = FinancialStatementGenerator(engine())
        val cf = gen.cashFlow(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31))
        assertEquals(70_000L, cf.operatingCents)
    }
}
