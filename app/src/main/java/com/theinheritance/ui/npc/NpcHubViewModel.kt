package com.theinheritance.ui.npc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.data.repository.GmMemoryRepository
import com.theinheritance.data.repository.NpcRepository
import com.theinheritance.simulation.NpcAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NpcHubViewModel @Inject constructor(
    private val repo: NpcRepository,
    private val gmMemoryRepo: GmMemoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NpcHubState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.seedDefaults()
            repo.observe().collectLatest { list ->
                _state.value = _state.value.copy(npcs = list)
            }
        }
    }

    fun openDialogue(npc: NpcAgent) {
        val initialGreeting = when (npc.name) {
            "Mara Voss" -> DialogueExchange(
                playerPrompt = "Approach Mara's desk in the back office",
                npcResponse = "\"Oh! You startled me. I'm just balancing the day's petty receipts... Is there something specific you wanted to check in the accounts?\"",
                trustDelta = 0
            )
            "Silas Vane" -> DialogueExchange(
                playerPrompt = "Meet Silas at the corner booth",
                npcResponse = "\"Ah, the new proprietor. George spoke highly of your attention to detail. I trust our financial arrangement remains pleasant and punctual?\"",
                trustDelta = 0
            )
            "Noor Haddad" -> DialogueExchange(
                playerPrompt = "Call Noor about wholesale deliveries",
                npcResponse = "\"Hello! Good to hear from you. The bookshop always has priority on our art and history catalog. How can I assist your inventory this week?\"",
                trustDelta = 0
            )
            "Piet Botha" -> DialogueExchange(
                playerPrompt = "Piet knocks on the shop window",
                npcResponse = "\"Rent is due on the first, sharp. George knew that, and you should too. No IOUs for square footage in this district.\"",
                trustDelta = 0
            )
            else -> DialogueExchange("Hello", "\"Yes? How can I help you?\"", 0)
        }
        _state.value = _state.value.copy(activeNpc = npc, currentDialogue = initialGreeting)
    }

    fun closeDialogue() {
        _state.value = _state.value.copy(activeNpc = null, currentDialogue = null)
    }

    fun askQuestion(topic: String) {
        val npc = _state.value.activeNpc ?: return
        viewModelScope.launch {
            val (resp, delta) = when (npc.name) {
                "Mara Voss" -> when (topic) {
                    "payroll" -> Pair(
                        "\"P. Vance? (She looks down at her pen). George personally authorized that retainer voucher. He told me it was confidential archival research. Please don't dig too deep, for both our sakes.\"",
                        -5
                    )
                    "uncle" -> Pair(
                        "\"George was... complicated. He saved this shop during the 2020 slump, but he started keeping separate little notebooks in his desk during his final months.\"",
                        +5
                    )
                    "compliment" -> Pair(
                        "\"Thank you... It's been exhausting holding this ledger together alone. If you ever need me to flag suspicious invoices before posting, let me know.\"",
                        +10
                    )
                    else -> Pair("\"I've double-checked every row I entered. Anything else was George's doing.\"", 0)
                }
                "Silas Vane" -> when (topic) {
                    "loan" -> Pair(
                        "\"George took R50,000 in bridge funding when the bank hesitated. The promissory note has my signature and his. Pay the interest on time, and we remain civil gentlemen.\"",
                        0
                    )
                    "books" -> Pair(
                        "\"I don't read fiction, my friend. I read repayment schedules. Keep your cash runway above 15 days or we'll renegotiate ownership percentages.\"",
                        -5
                    )
                    "favor" -> Pair(
                        "\"A token of good faith? Acceptable. I will defer this week's interest surcharge by three days. Don't make me regret my generosity.\"",
                        +10
                    )
                    else -> Pair("\"Time is capital. Be brief.\"", 0)
                }
                "Noor Haddad" -> when (topic) {
                    "inventory" -> Pair(
                        "\"I noticed George marked down R24,000 in damaged stock. But my shipping crates were completely waterproofed. I suspect someone pulled those books for private resale.\"",
                        +5
                    )
                    "discount" -> Pair(
                        "\"Because you pay your invoices promptly, I can offer 10% off your next bulk order of rare literature. Keep supporting independent publishers!\"",
                        +10
                    )
                    "uncle" -> Pair(
                        "\"George was a gentleman, but he carried heavy secrets toward the end. He was terrified of losing the building to Silas.\"",
                        +5
                    )
                    else -> Pair("\"I have three fresh shipments arriving this Friday. Let me know what you need.\"", 0)
                }
                "Piet Botha" -> when (topic) {
                    "rent" -> Pair(
                        "\"The lease is airtight. 18,000 Rands every single month. No discounts, no delays. Commercial real estate doesn't run on charity.\"",
                        0
                    )
                    "maintenance" -> Pair(
                        "\"The damp basement? George was supposed to fix the drain pipe himself three years ago. Don't blame me for ruined paper stock.\"",
                        -5
                    )
                    "early_pay" -> Pair(
                        "\"Paid in full and early? Well, look at that. Maybe you will survive past the month after all. I'll hold off sending my legal notice.\"",
                        +15
                    )
                    else -> Pair("\"Keep the premises clean and pay on time.\"", 0)
                }
                else -> Pair("\"I have nothing further to say about that.\"", 0)
            }

            val newTrust = repo.shiftTrust(npc.id, delta)
            val updatedNpc = npc.copy(trustLevel = newTrust)
            gmMemoryRepo.remember(1, "npc_interaction", "${npc.name} responded to $topic. Trust now $newTrust%")

            _state.value = _state.value.copy(
                activeNpc = updatedNpc,
                currentDialogue = DialogueExchange(
                    playerPrompt = topic.replace("_", " ").replaceFirstChar { it.uppercase() },
                    npcResponse = resp,
                    trustDelta = delta
                )
            )
        }
    }
}
