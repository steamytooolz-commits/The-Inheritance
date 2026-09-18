package com.theinheritance.accounting.fraud

data class FraudFinding(
    val scheme: FraudScheme,
    val confidence: Float,
    val evidence: List<String>,
    val entryIds: List<Long>
)

class FraudDetector {
    fun detectRoundingLeak(deltasCents: List<Long>): FraudFinding? {
        val small = deltasCents.filter { it in 1..50 }
        if (small.size >= 5) {
            return FraudFinding(FraudScheme.ROUNDING_LEAK, 0.8f, listOf("${small.size} sub-R1 discrepancies"), emptyList())
        }
        return null
    }

    fun detectDuplicates(memos: List<String>): List<String> =
        memos.groupBy { it }.filter { it.value.size > 1 }.keys.toList()

    fun detectDuplicatePayroll(entries: List<Pair<String, Long>>): FraudFinding? {
        val dupes = entries.groupBy { it }.filter { it.value.size > 1 }.keys.toList()
        if (dupes.isNotEmpty()) {
            return FraudFinding(FraudScheme.DUPLICATE_PAYROLL, 0.85f, dupes.map { (memo, amount) -> "Duplicate: $memo R${amount / 100}" }, emptyList())
        }
        return null
    }

    fun detectAgingPrepaid(prepaidCents: Long, daysOld: Int): FraudFinding? {
        if (prepaidCents > 50_000_00 && daysOld > 90) {
            return FraudFinding(FraudScheme.EXPENSE_RECLASS, 0.7f, listOf("Prepaid balance R${prepaidCents / 100} aged $daysOld days"), emptyList())
        }
        return null
    }

    fun detectBackdated(documentedDay: Int, postedDay: Int): FraudFinding? {
        if (postedDay < documentedDay) {
            return FraudFinding(FraudScheme.BACKDATED_ENTRY, 0.75f, listOf("Posted day $postedDay precedes documented day $documentedDay"), emptyList())
        }
        return null
    }

    fun detectRevenueSkim(posTotalCents: Long, depositTotalCents: Long, toleranceCents: Long = 500): FraudFinding? {
        val gap = posTotalCents - depositTotalCents
        if (gap > toleranceCents) {
            return FraudFinding(FraudScheme.REVENUE_SKIM, 0.8f, listOf("POS exceeds deposits by R${gap / 100}"), emptyList())
        }
        return null
    }

    fun detectInventoryShrink(writeOffCents: Long, revenueCents: Long, industryRateBps: Long = 150): FraudFinding? {
        if (revenueCents <= 0) return null
        val actualBps = writeOffCents * 10_000 / revenueCents
        if (actualBps > industryRateBps * 2) {
            return FraudFinding(FraudScheme.INVENTORY_SHRINK, 0.65f, listOf("Write-offs ${actualBps}bps vs industry ${industryRateBps}bps"), emptyList())
        }
        return null
    }

    fun detectInflatedReceivable(arCents: Long, confirmedCents: Long): FraudFinding? {
        val gap = arCents - confirmedCents
        if (gap > 0) {
            return FraudFinding(FraudScheme.INFLATED_RECEIVABLE, 0.7f, listOf("R${gap / 100} of receivables unconfirmed"), emptyList())
        }
        return null
    }
}
