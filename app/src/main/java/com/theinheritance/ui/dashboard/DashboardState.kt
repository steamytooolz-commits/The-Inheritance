package com.theinheritance.ui.dashboard

data class DailyReport(
    val dayResolved: Int,
    val revenueCents: Long,
    val costCents: Long,
    val netCents: Long,
    val cashEndingCents: Long,
    val gmVerdict: String,
    val eventTitle: String? = null
)

data class ForensicClue(
    val id: String,
    val title: String,
    val description: String,
    val targetNpc: String,
    val isSolved: Boolean = false
)

data class DashboardState(
    val day: Int = 1,
    val maxDays: Int = 30,
    val cashCents: Long = 420000,
    val businessName: String = "The Book Nook",
    val gmLine: String = "Your uncle died. The books are lying. You have 30 days.",
    val revenueCents: Long = 4250000,
    val expenseCents: Long = 5650000,
    val netIncomeCents: Long = -1400000,
    val debtCents: Long = 17000000,
    val equityCents: Long = 26500000,
    val isAlive: Boolean = true,
    val isVictorious: Boolean = false,
    val isGameOver: Boolean = false,
    val finalScore: Int = 0,
    val endOfDayReport: DailyReport? = null,
    val showShoebox: Boolean = false,
    val clues: List<ForensicClue> = listOf(
        ForensicClue("clue_ghost", "Ghost Payroll: P. Vance", "Disbursements made on the 25th for consulting to a name not on staff.", "Mara Voss"),
        ForensicClue("clue_stock", "Basement Stock Discrepancy", "R24,000 in inventory was written off as water damage, but Noor has no record of damaged boxes.", "Noor Haddad"),
        ForensicClue("clue_loan", "Off-Book Promissory Note", "Silas Vane claims the shop owes an unrecorded R50,000 balloon payment due Day 20.", "Silas Vane"),
        ForensicClue("clue_rounding", "Rounding Leak in Sales Till", "Every daily register batch ends in a 99-cent discrepancy channeled to a petty cash pocket.", "Mara Voss")
    ),
    val accusationOutcome: String? = null
)
