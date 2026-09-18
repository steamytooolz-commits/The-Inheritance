package com.theinheritance.accounting.fraud.schemes

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.SchemePlanter
import java.time.LocalDate

class HiddenLiabilityScheme : SchemePlanter {
    override val scheme = FraudScheme.HIDDEN_LIABILITY
    override fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String): List<Long> {
        val accs = engine.accounts()
        val prepaid = accs.values.first { it.name.contains("Prepaid", ignoreCase = true) }.id
        val bank = accs.values.first { it.name.contains("Bank", ignoreCase = true) }.id
        return listOfNotNull(engine.postSimple(date, "Prepaid — loan parked ($tag)", prepaid, bank, amountCents, tag).getOrNull()?.id)
    }
}
