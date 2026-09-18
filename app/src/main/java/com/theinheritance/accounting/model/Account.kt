package com.theinheritance.accounting.model

data class Account(
    val id: Long,
    val code: String,
    val name: String,
    val type: AccountType,
    val isContra: Boolean = false,
    val parentAccountId: Long? = null,
    val isLocked: Boolean = false,
    val isHidden: Boolean = false
) {
    /** Normal balance side: debit for ASSET/EXPENSE, credit otherwise (contra flips). */
    val normalDebit: Boolean = when (type) {
        AccountType.ASSET, AccountType.EXPENSE -> !isContra
        AccountType.LIABILITY, AccountType.EQUITY, AccountType.REVENUE -> isContra
    }
}

object ChartOfAccounts {
    fun default(): List<Account> = listOf(
        Account(1000, "1000", "Cash on Hand", AccountType.ASSET),
        Account(1010, "1010", "Bank — Business Cheque", AccountType.ASSET),
        Account(1100, "1100", "Accounts Receivable", AccountType.ASSET),
        Account(1200, "1200", "Inventory", AccountType.ASSET),
        Account(1300, "1300", "Prepaid Expenses", AccountType.ASSET),
        Account(1500, "1500", "Equipment", AccountType.ASSET),
        Account(1510, "1510", "Accumulated Depreciation — Equipment", AccountType.ASSET, isContra = true),
        Account(1600, "1600", "Petty Cash", AccountType.ASSET),
        Account(2000, "2000", "Accounts Payable", AccountType.LIABILITY),
        Account(2100, "2100", "Payroll Payable", AccountType.LIABILITY),
        Account(2200, "2200", "Loan Payable — Family", AccountType.LIABILITY),
        Account(2300, "2300", "Tax Payable", AccountType.LIABILITY),
        Account(3000, "3000", "Owner's Equity", AccountType.EQUITY),
        Account(3100, "3100", "Retained Earnings", AccountType.EQUITY),
        Account(4000, "4000", "Sales Revenue", AccountType.REVENUE),
        Account(4100, "4100", "Service Revenue", AccountType.REVENUE),
        Account(5000, "5000", "Cost of Goods Sold", AccountType.EXPENSE),
        Account(5100, "5100", "Payroll Expense", AccountType.EXPENSE),
        Account(5200, "5200", "Rent Expense", AccountType.EXPENSE),
        Account(5300, "5300", "Utilities Expense", AccountType.EXPENSE),
        Account(5400, "5400", "Repairs & Maintenance", AccountType.EXPENSE),
        Account(5500, "5500", "Depreciation Expense", AccountType.EXPENSE),
        Account(5600, "5600", "Interest Expense", AccountType.EXPENSE),
        Account(5700, "5700", "Sundry / Petty Expense", AccountType.EXPENSE),
        Account(5800, "5800", "Loss on Shrinkage", AccountType.EXPENSE),
    )
}
