package com.theinheritance.accounting.fraud

data class FraudProfile(
    val primaryScheme: FraudScheme,
    val secondaryScheme: FraudScheme?,
    val tertiaryScheme: FraudScheme?,
    val hiddenDeepestTruth: HiddenTruth
)

sealed class HiddenTruth {
    data class Embezzlement(val byNpcId: Long, val totalCents: Long) : HiddenTruth()
    data class LoanToFamilyMember(val name: String, val amountCents: Long) : HiddenTruth()
    data class GamblingDebt(val creditorName: String, val amountCents: Long) : HiddenTruth()
    data class AffairFundedByBusiness(val partnerName: String, val totalSpentCents: Long) : HiddenTruth()
    data class InsuranceFraud(val policyNumber: String) : HiddenTruth()
}

class FraudGenerator(private val seed: Long) {
    fun generate(): FraudProfile {
        val random = kotlin.random.Random(seed)
        val schemes = FraudSchemeLibrary.all
        val primary = schemes.random(random)
        val secondary = if (random.nextBoolean()) schemes.random(random) else null
        val tertiary = if (random.nextFloat() < 0.3f) schemes.random(random) else null
        val truth = generateHiddenTruth(random)
        return FraudProfile(primary, secondary, tertiary, truth)
    }

    private fun generateHiddenTruth(random: kotlin.random.Random): HiddenTruth {
        return when (random.nextInt(5)) {
            0 -> HiddenTruth.Embezzlement(byNpcId = (1..5).random(random).toLong(), totalCents = (50_000..900_000L).random(random) * 100)
            1 -> HiddenTruth.LoanToFamilyMember(name = listOf("Cousin Mara", "Uncle Piet", "Sister Ana").random(random), amountCents = (20_000..300_000L).random(random) * 100)
            2 -> HiddenTruth.GamblingDebt(creditorName = listOf("Silas Vane", "The Track", "Mr. K").random(random), amountCents = (30_000..500_000L).random(random) * 100)
            3 -> HiddenTruth.AffairFundedByBusiness(partnerName = listOf("J.", "Noor", "R.").random(random), totalSpentCents = (10_000..150_000L).random(random) * 100)
            else -> HiddenTruth.InsuranceFraud(policyNumber = "POL-${random.nextInt(100000, 999999)}")
        }
    }
}
