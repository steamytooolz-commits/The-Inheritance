package com.theinheritance.accounting.fraud.schemes

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.SchemePlanter
import java.time.LocalDate

class RoundingLeakScheme : SchemePlanter {
    override val scheme = FraudScheme.ROUNDING_LEAK
    override fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String): List<Long> {
        val accs = engine.accounts()
        val sundry = accs.values.first { it.name.contains("Sundry", ignoreCase = true) }.id
        val cash = accs.values.first { it.name.contains("Cash on Hand", ignoreCase = true) }.id
        return (1..6).mapNotNull { i ->
            engine.postSimple(date.minusDays(i.toLong()), "Rounding adj $i ($tag)", sundry, cash, (amountCents % 50).coerceAtLeast(1), tag).getOrNull()?.id
        }
    }
}
