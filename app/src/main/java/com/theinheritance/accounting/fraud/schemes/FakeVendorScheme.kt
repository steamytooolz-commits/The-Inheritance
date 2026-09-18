package com.theinheritance.accounting.fraud.schemes

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.fraud.FraudScheme
import com.theinheritance.accounting.fraud.SchemePlanter
import java.time.LocalDate

class FakeVendorScheme : SchemePlanter {
    override val scheme = FraudScheme.FAKE_VENDOR
    override fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String): List<Long> {
        val accs = engine.accounts()
        val repairs = accs.values.first { it.name.contains("Repairs", ignoreCase = true) }.id
        val ap = accs.values.first { it.code == "2000" }.id
        return listOfNotNull(engine.postSimple(date, "Invoice — shell vendor ($tag)", repairs, ap, amountCents, tag).getOrNull()?.id)
    }
}
