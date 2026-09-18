package com.theinheritance.accounting.fraud.schemes

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.SchemePlanter
import java.time.LocalDate

class GhostEmployeeScheme : SchemePlanter {
    override val scheme = FraudScheme.GHOST_EMPLOYEE
    override fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String): List<Long> {
        val accs = engine.accounts()
        val payroll = accs.values.first { it.name.contains("Payroll Expense", ignoreCase = true) }.id
        val bank = accs.values.first { it.name.contains("Bank", ignoreCase = true) }.id
        return listOfNotNull(engine.postSimple(date, "Payroll — ghost ($tag)", payroll, bank, amountCents, tag).getOrNull()?.id)
    }
}
