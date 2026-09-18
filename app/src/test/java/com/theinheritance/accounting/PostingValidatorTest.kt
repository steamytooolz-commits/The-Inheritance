package com.theinheritance.accounting

import com.theinheritance.accounting.engine.PostingValidator
import com.theinheritance.accounting.model.AccountType
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.accounting.model.JournalEntry
import com.theinheritance.accounting.model.JournalLine
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PostingValidatorTest {
    private fun mockAccounts() = ChartOfAccounts.default().associateBy { it.id }

    @Test fun `unbalanced entry is rejected`() {
        val entry = JournalEntry(
            date = LocalDate.of(2026, 1, 15), memo = "Test",
            lines = listOf(JournalLine(1010, debitCents = 10000), JournalLine(4000, creditCents = 5000))
        )
        assertTrue(PostingValidator().validate(entry, mockAccounts()).isFailure)
    }

    @Test fun `balanced entry is accepted`() {
        val entry = JournalEntry(
            date = LocalDate.of(2026, 1, 15), memo = "Test",
            lines = listOf(JournalLine(1010, debitCents = 10000), JournalLine(4000, creditCents = 10000))
        )
        assertTrue(PostingValidator().validate(entry, mockAccounts()).isSuccess)
    }

    @Test fun `empty entry is rejected`() {
        val entry = JournalEntry(date = LocalDate.of(2026, 1, 15), memo = "Empty", lines = emptyList())
        assertTrue(PostingValidator().validate(entry, mockAccounts()).isFailure)
    }

    @Test fun `locked account is rejected`() {
        val locked = ChartOfAccounts.default().first().copy(isLocked = true)
        val accs = mapOf(locked.id to locked, 4000L to mockAccounts().getValue(4000L))
        val entry = JournalEntry(
            date = LocalDate.of(2026, 1, 15), memo = "Locked",
            lines = listOf(JournalLine(locked.id, debitCents = 1000), JournalLine(4000, creditCents = 1000))
        )
        assertTrue(PostingValidator().validate(entry, accs).isFailure)
    }

    @Test fun `money is never Double`() {
        // Compile-time guard: Money wraps Long cents only.
        val m = com.theinheritance.accounting.money.Money(10050)
        assertTrue(m.cents == 10050L)
    }
}
