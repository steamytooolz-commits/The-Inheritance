package com.theinheritance.accounting.fraud.schemes

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.SchemePlanter
import java.time.LocalDate

class PettyCashSweepScheme : SchemePlanter {
    override val scheme = FraudScheme.PETTY_CASH_SWEEP
    override fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String): List<Long> {
        val accs = engine.accounts()
        val sundry = accs.values.first { it.name.contains("Sundry", ignoreCase = true) }.id
        val petty = accs.values.first { it.name.contains("Petty Cash", ignoreCase = true) }.id
        return listOfNotNull(engine.postSimple(date, "Petty sweep ($tag)", sundry, petty, amountCents, tag).getOrNull()?.id)
    }
}
