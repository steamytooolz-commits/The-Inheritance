package com.theinheritance.accounting.fraud

import com.theinheritance.accounting.engine.AccountingEngine
import java.time.LocalDate

/**
 * Procedural fraud-scheme library. Every scheme names its mechanism and the
 * discovery path the player must walk to expose it. Pure Kotlin.
 */
enum class FraudScheme(val title: String, val mechanism: String, val discoveryPath: String) {
    // — The original fifteen (spec table) —
    GHOST_EMPLOYEE("Ghost Employee", "Payroll to a name with no ID number", "Cross-reference payroll with tax records"),
    ROUNDING_LEAK("Rounding Leak", "Small amounts rounded down, pocketed", "Find recurring R0.01–R0.50 discrepancies"),
    FAKE_VENDOR("Fake Vendor", "Invoices from a shell company", "Check vendor address against known locations"),
    EARLY_REVENUE("Early Revenue", "Revenue recognized before delivery", "Compare invoices to delivery notes"),
    HIDDEN_LIABILITY("Hidden Liability", "Off-book loan in a prepaid account", "Trace suspicious prepaid amounts"),
    EXPENSE_RECLASS("Expense Reclassification", "Loss moved to prepaid expenses", "Check aging of prepaid balances"),
    PETTY_CASH_SWEEP("Petty Cash Sweep", "Regular small cash withdrawals", "Match petty cash to receipts"),
    DUPLICATE_PAYROLL("Duplicate Payroll", "Same payment posted twice", "Find duplicate journal entries"),
    BACKDATED_ENTRY("Backdated Entry", "Transaction moved to earlier period", "Check timestamps vs. documented dates"),
    INFLATED_RECEIVABLE("Inflated Receivable", "Revenue recognized for unearned work", "Verify AR with customer confirmations"),
    ASSET_MISCLASS("Asset Misclassification", "Equipment purchased expensed immediately", "Check capital expenditure vs. repairs"),
    LOAN_TO_SELF("Loan To Self", "Business loan to a related party", "Check loan agreements for names"),
    REVENUE_SKIM("Revenue Skimming", "Cash sales not recorded", "Compare POS totals to deposits"),
    VENDOR_KICKBACK("Vendor Kickback", "Vendor paid above market, difference kicked back", "Compare vendor prices to market rates"),
    INVENTORY_SHRINK("Inventory Shrinkage", "Goods disappear, written off as normal", "Compare write-offs to industry averages"),

    // — Expansion: payroll & people —
    OVERTIME_PAD("Overtime Padding", "Hours inflated on quiet weeks", "Match timesheets to rostered shifts"),
    BONUS_SELF_DEAL("Bonus Self-Deal", "Unauthorised bonus to a related staffer", "Check bonus approvals against contracts"),
    TERMINATED_KEPT_ON("Terminated Kept On", "Leaver stays on payroll one cycle too long", "Reconcile payroll headcount with HR exits"),
    CASH_WAGE_SPLIT("Cash Wage Split", "Wages split cash/bank to hide the top-up", "Compare net pay to bank outflows"),

    // — Expansion: purchasing & vendors —
    SPLIT_INVOICE("Split Invoice", "One purchase split under approval threshold", "Cluster invoices by vendor and week"),
    PHANTOM_FREIGHT("Phantom Freight", "Delivery fees for goods collected in person", "Match freight lines to delivery notes"),
    FAVOURED_SUPPLIER("Favoured Supplier", "Orders routed to a friend at premium rates", "Benchmark unit costs across suppliers"),
    PREPAYMENT_DRAIN("Prepayment Drain", "Large deposits to vendors that never deliver", "Age all vendor prepayments past 60 days"),

    // — Expansion: revenue & receivables —
    ROUND_TRIP_SALE("Round-Trip Sale", "Goods sold and bought back to inflate turnover", "Trace paired sale/purchase memos"),
    CHANNEL_STUFF("Channel Stuffing", "Quarter-end bulk sale with silent return rights", "Check post-period credit notes"),
    DEPOSIT_AS_REVENUE("Deposit As Revenue", "Customer deposits booked as sales", "Reconcile deposits to the liability account"),
    LAPPED_RECEIPT("Lapped Receipt", "Customer A pays customer B's receipt", "Age receivables and confirm with debtors"),

    // — Expansion: cash & bank —
    KITED_CHEQUE("Kited Cheque", "Cheque shuttled between accounts to fake balance", "Match bank dates across both statements"),
    DELAYED_DEPOSIT("Delayed Deposit", "Takings banked days late, float pocketed", "Compare POS close to deposit slip dates"),
    PETTY_TOP_UP("Petty Top-Up Loop", "Petty cash reimbursed twice for one spend", "Sequence petty vouchers by number"),
    CASH_DISCOUNT_POCKET("Discount Pocketing", "Supplier discount kept instead of booked", "Match remittance to invoice terms"),

    // — Expansion: inventory & assets —
    OBSOLETE_HIDE("Obsolete Hide", "Dead stock carried at full value", "Sample-count shelves vs. the ledger"),
    PRIVATE_USE_ASSET("Private-Use Asset", "Business van funds family mileage", "Check logbook against fuel claims"),
    REPAIR_CAPITAL_FLIP("Repair/Capital Flip", "Repairs capitalised to flatter profit", "Review asset additions under R50k"),
    SCRAP_SALE_OFF_BOOK("Off-Book Scrap", "Scrap proceeds never banked", "Weigh scrap dockets against deposits"),

    // — Expansion: liabilities & equity —
    ACCRUAL_RELEASE("Accrual Release", "Old provisions released to flatter a month", "Walk every provision release to evidence"),
    DIRECTOR_CURRENT_ABUSE("Director Current Abuse", "Personal spend routed via current account", "Scan current-account memos for retail names"),
    UNCLAIMED_WAGE_THEFT("Unclaimed Wage Theft", "Uncollected wages written back and taken", "Reconcile unclaimed wages to payouts"),
    TAX_UNDER_PROVISION("Tax Under-Provision", "Tax provision shaved to flatter equity", "Recompute tax on stated profit"),

    // — Expansion: period & presentation —
    CUT_OFF_SHIFT("Cut-Off Shift", "January sales pulled into December", "Match December invoices to dispatch dates"),
    SUSPENSE_PARKING("Suspense Parking", "Losses parked in suspense indefinitely", "Age the suspense account to zero"),
    INTERCOMPANY_WASH("Intercompany Wash", "Fees bounced between related entities", "Confirm balances with the counterparty"),
    ROUND_NUMBER_RESERVE("Round-Number Reserve", "Suspiciously round provisions each month", "Ask for the maths behind round thousands"),
    STALE_CHEQUE_REVIVE("Stale Cheque Revive", "Cancelled cheques re-presented quietly", "Match cheque numbers to bank clears"),
    FICTITIOUS_INTEREST("Fictitious Interest", "Interest charged to a dormant loan", "Recalculate interest from the signed schedule"),
    REBATE_DIVERSION("Rebate Diversion", "Supplier rebates diverted past the ledger", "Request rebate statements from suppliers");

    /** Backwards-compatible short clue for UI hints. */
    val clue: String get() = mechanism
}

object FraudSchemeLibrary {
    val all: List<FraudScheme> = FraudScheme.entries.toList()
}

/**
 * Contract for schemes that plant seed transactions into a fresh run.
 * Lives here so every `schemes/*` file stays a single spec-named class.
 */
interface SchemePlanter {
    val scheme: FraudScheme
    fun plant(engine: AccountingEngine, date: LocalDate, amountCents: Long, tag: String = "system"): List<Long>
}
