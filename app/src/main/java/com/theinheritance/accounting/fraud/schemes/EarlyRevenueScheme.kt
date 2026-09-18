package com.theinheritance.accounting.fraud.schemes

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.SchemePlanter
import java.time.LocalDate

class EarlyRevenueScheme : SchemePlanter {
    override val scheme = FraudScheme.EARLY_REVENUE
    override fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String): List<Long> {
        val accs = engine.accounts()
        val ar = accs.values.first { it.name.contains("Receivable", ignoreCase = true) }.id
        val rev = accs.values.first { it.code == "4000" }.id
        return listOfNotNull(engine.postSimple(date, "Early revenue recognition ($tag)", ar, rev, amountCents, tag).getOrNull()?.id)
    }
}
