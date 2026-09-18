package com.theinheritance.data.llm

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Intelligent Offline Forensic AI Engine for Uncle George.
 * Provides rich, dynamic, persona-driven responses with forensic accounting
 * knowledge, suspect analysis, journal clues, and tactical advice even without internet or GGUF models.
 */
@Singleton
class RuleBasedEngine @Inject constructor() : LlmEngine {

    override suspend fun initialize(modelPath: String): Result<Unit> = Result.success(Unit)

    override suspend fun generate(systemPrompt: String, userPrompt: String): String {
        val query = userPrompt.lowercase()

        return when {
            query.contains("mara") || query.contains("payroll") || query.contains("vance") -> {
                "Uncle George leans back in the smoke: \"Mara is sharp, but she gets terrified when Silas visits. That 'P. Vance' consulting voucher on the 25th? I never hired any consultant by that name. Look into who cashed those checks at the district bank.\""
            }
            query.contains("silas") || query.contains("vane") || query.contains("loan") || query.contains("debt") -> {
                "Uncle George chuckles dryly: \"Silas Vane carries a smile like a razor blade. He claims a R50,000 balloon note, but check the liabilities account (2000). If it's not on the balance sheet, he's trying to strongarm you before the probate court seals the estate.\""
            }
            query.contains("noor") || query.contains("inventory") || query.contains("stock") || query.contains("damage") -> {
                "Uncle George taps his pipe: \"Noor's art books are gold, but that R24,000 water-damage write-off in the basement? We had waterproof crates. Check if that inventory was actually lost, or if someone moved it out the back alley door.\""
            }
            query.contains("piet") || query.contains("rent") || query.contains("lease") || query.contains("landlord") -> {
                "Uncle George scoffs: \"Piet Botha would charge rent to his own mother. Pay the R18,000 lease on time, or he'll lock the front display. But don't let him double-dip on maintenance charges.\""
            }
            query.contains("cash") || query.contains("runway") || query.contains("bankrupt") || query.contains("money") -> {
                "Uncle George points to the cash ledger (Account 1000): \"Rule number one of commerce, kid: Profit is an opinion, but cash is a fact. Keep at least 10 days of runway or the creditors will dismantle this place before the 30 days are up.\""
            }
            query.contains("fraud") || query.contains("cheat") || query.contains("steal") || query.contains("shoebox") -> {
                "Uncle George taps the ledger cover: \"Every fraud leaves a trail in the journal. Look for round-sum transfers, off-book notes, and unmatched debits. Open the Evidence Shoebox on your board when you're ready to cross-examine.\""
            }
            query.contains("balance sheet") || query.contains("equity") || query.contains("assets") -> {
                "Uncle George nods: \"Assets must equal Liabilities plus Equity. If the equation tilts, someone is hiding numbers in suspense accounts or petty cash.\""
            }
            query.contains("income") || query.contains("sales") || query.contains("revenue") || query.contains("profit") -> {
                "Uncle George smiles: \"Revenue comes from trade sales, but watch the Cost of Goods Sold (COGS). If margins fall below 35%, we are subsidizing customers with our own blood.\""
            }
            query.contains("hello") || query.contains("who are you") || query.contains("uncle") -> {
                "The uncle clears his throat from inside the ledger. \"$userPrompt\" — careful, kid. Check the prepaid account first. I built this shop with paper, ink, and a few creative entries."
            }
            query.contains("help") || query.contains("what should i do") || query.contains("advice") -> {
                "Uncle George advises: \"1. Check the Statements tab to see our current Net Income. 2. Post daily reconciliations in the Journal. 3. Check the prepaid account. 4. Talk to Mara and Noor in People. Advance the day when ready.\""
            }
            else -> {
                val quips = listOf(
                    "Uncle George mutters: \"$userPrompt? A curious angle. Remember, in double-entry bookkeeping, nothing vanishes—it only changes accounts. Check the prepaid account if you're stuck.\"",
                    "Uncle George scratches his chin: \"Every transaction tells a story. Look at the debit column—who authorized it?\"",
                    "Uncle George watches from the shadows: \"Keep your wits about you. Someone in this district is counting on you making a mistake before Day 30.\"",
                    "Uncle George murmurs: \"Interesting thought, kid. But double-check the journal first. The numbers never lie, even when the bookkeepers do.\""
                )
                quips[Random.nextInt(quips.size)]
            }
        }
    }

    override fun generateStreaming(
        systemPrompt: String,
        userPrompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val result = "The uncle speaks from the ledger margins: " + userPrompt
        onToken(result)
        onComplete()
    }

    override fun shutdown() {}

    override val isInitialized: Boolean get() = true
}
