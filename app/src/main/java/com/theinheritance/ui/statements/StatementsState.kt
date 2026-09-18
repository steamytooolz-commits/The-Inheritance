package com.theinheritance.ui.statements

import com.theinheritance.accounting.model.BalanceSheet
import com.theinheritance.accounting.model.CashFlowStatement
import com.theinheritance.accounting.model.IncomeStatement

data class UncleExplanation(
    val title: String,
    val amountFormatted: String,
    val plainEnglish: String,
    val uncleComment: String,
    val relatedAccountId: Long? = null
)

data class StatementsState(
    val incomeStatement: IncomeStatement? = null,
    val balanceSheet: BalanceSheet? = null,
    val cashFlow: CashFlowStatement? = null,
    val selectedExplanation: UncleExplanation? = null,
    val isLoading: Boolean = false
)
