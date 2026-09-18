package com.theinheritance.data.llm

import com.theinheritance.data.local.dao.AccountDao
import com.theinheritance.data.local.dao.GameStateDao
import com.theinheritance.data.local.dao.NpcDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Intelligent Offline Forensic AI Engine for Uncle George.
 * Provides rich, dynamic, persona-driven responses with forensic accounting
 * knowledge, suspect analysis, journal clues, and tactical advice even without internet or GGUF models.
 */
@Singleton
class RuleBasedEngine @Inject constructor(
    private val npcDao: NpcDao,
    private val gameStateDao: GameStateDao,
    private val accountDao: AccountDao
) : LlmEngine {

    constructor() : this(DummyNpcDao(), DummyGameStateDao(), DummyAccountDao())

    override suspend fun initialize(modelPath: String): Result<Unit> = Result.success(Unit)

    override suspend fun generate(systemPrompt: String, userPrompt: String): String {
        val query = userPrompt.lowercase()

        // Fetch actual live game state
        val state = gameStateDao?.get()
        val currentDay = state?.currentDay ?: 1
        val cashCents = state?.cashCents ?: 420000L
        val cashString = "R" + String.format("%,.2f", cashCents / 100.0)

        // Fetch actual NPCs
        val npcs = npcDao?.getAll() ?: emptyList()
        val mara = npcs.find { it.name.lowercase().contains("mara") }
        val silas = npcs.find { it.name.lowercase().contains("silas") }
        val noor = npcs.find { it.name.lowercase().contains("noor") }
        val piet = npcs.find { it.name.lowercase().contains("piet") }

        return when {
            query.contains("mara") || query.contains("payroll") || query.contains("vance") -> {
                val trustText = mara?.let { " (Trust: ${it.trustLevel}%)" } ?: ""
                "Uncle George leans back in the smoke: \"Mara Voss${trustText} is sharp as a tack, but she gets terrified when Silas visits. That 'P. Vance' consulting voucher on the 25th? I never hired any consultant by that name. Check who cashed those checks at the district bank, kid.\""
            }
            query.contains("silas") || query.contains("vane") || query.contains("loan") || query.contains("debt") -> {
                val trustText = silas?.let { " (Trust: ${it.trustLevel}%)" } ?: ""
                "Uncle George chuckles dryly: \"Silas Vane${trustText} carries a smile like a razor blade. He claims a R50,000 balloon note, but check the liabilities account (2000). If it's not on our balance sheet, he's trying to strongarm you before the probate court seals the estate.\""
            }
            query.contains("noor") || query.contains("inventory") || query.contains("stock") || query.contains("damage") -> {
                val trustText = noor?.let { " (Trust: ${it.trustLevel}%)" } ?: ""
                "Uncle George taps his pipe: \"Noor Haddad${trustText}'s art books are gold, but that R24,000 water-damage write-off in the basement? We had waterproof crates. Check if that inventory was actually lost, or if someone moved it out the back alley door.\""
            }
            query.contains("piet") || query.contains("rent") || query.contains("lease") || query.contains("landlord") -> {
                val trustText = piet?.let { " (Trust: ${it.trustLevel}%)" } ?: ""
                "Uncle George scoffs: \"Piet Botha${trustText} would charge rent to his own mother. Pay the R18,000 lease on time, or he'll lock the front display. But don't let him double-dip on maintenance charges.\""
            }
            query.contains("cash") || query.contains("runway") || query.contains("bankrupt") || query.contains("money") -> {
                "Uncle George points to the cash ledger (Account 1000): \"Rule number one of commerce, kid: Profit is an opinion, but cash is a fact. We currently have $cashString on hand on Day $currentDay. Keep at least 10 days of runway or the creditors will dismantle this place before the 30 days are up.\""
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
                "Uncle George advises: \"1. Check the Statements tab to see our current Net Income. 2. Post daily reconciliations in the Journal. 3. Check the prepaid account. 4. Talk to Mara and Noor in People. We are on Day $currentDay, with $cashString left. Advance the day when ready.\""
            }
            else -> {
                val quips = listOf(
                    "Uncle George mutters: \"$userPrompt? A curious angle. Remember, on Day $currentDay, with $cashString, nothing vanishes—it only changes accounts. Check the prepaid account if you're stuck.\"",
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

private class DummyNpcDao : NpcDao {
    override fun observeAll() = kotlinx.coroutines.flow.flowOf(emptyList<com.theinheritance.data.local.entity.NpcEntity>())
    override suspend fun getAll() = emptyList<com.theinheritance.data.local.entity.NpcEntity>()
    override suspend fun getById(id: Long) = null
    override suspend fun upsert(npc: com.theinheritance.data.local.entity.NpcEntity) {}
    override suspend fun upsertAll(npcs: List<com.theinheritance.data.local.entity.NpcEntity>) {}
    override suspend fun setTrust(id: Long, trust: Int) {}
    override suspend fun delete(id: Long) {}
}

private class DummyGameStateDao : GameStateDao {
    override fun observe() = kotlinx.coroutines.flow.flowOf(null)
    override suspend fun get() = null
    override suspend fun upsert(state: com.theinheritance.data.local.entity.GameStateEntity) {}
    override suspend fun setCurrentDay(day: Int) {}
}

private class DummyAccountDao : AccountDao {
    override fun observeAll() = kotlinx.coroutines.flow.flowOf(emptyList<com.theinheritance.data.local.entity.AccountEntity>())
    override suspend fun getAll() = emptyList<com.theinheritance.data.local.entity.AccountEntity>()
    override suspend fun getById(id: Long) = null
    override suspend fun insertAll(accounts: List<com.theinheritance.data.local.entity.AccountEntity>) {}
    override suspend fun setLocked(id: Long, locked: Boolean) {}
    override suspend fun setHidden(id: Long, hidden: Boolean) {}
}
