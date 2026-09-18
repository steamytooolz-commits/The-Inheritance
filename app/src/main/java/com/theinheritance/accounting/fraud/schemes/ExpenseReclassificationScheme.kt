package com.theinheritance.accounting.fraud.schemes

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.SchemePlanter
import java.time.LocalDate

class ExpenseReclassificationScheme : SchemePlanter {
    override val scheme = FraudScheme.EXPENSE_RECLASS
    override fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String): List<Long> {
        val accs = engine.accounts()
        val prepaid = accs.values.first { it.name.contains("Prepaid", ignoreCase = true) }.id
        val sundry = accs.values.first { it.name.contains("Sundry", ignoreCase = true) }.id
        return listOfNotNull(engine.postSimple(date, "Reclass loss to prepaid ($tag)", prepaid, sundry, amountCents, tag).getOrNull()?.id)
    }
}
