package com.theinheritance.ui.statements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.accounting.money.Money
import com.theinheritance.accounting.money.MoneyFormatter
import com.theinheritance.data.repository.AccountingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatementsViewModel @Inject constructor(
    private val repo: AccountingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StatementsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadStatements()
    }

    fun loadStatements() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val now = LocalDate.now()
            val startOfMonth = now.withDayOfMonth(1)
            val is_ = repo.getIncomeStatement(startOfMonth, now)
            val bs = repo.getBalanceSheet(now)
            val cf = repo.getCashFlow(startOfMonth, now)
            _state.value = _state.value.copy(
                incomeStatement = is_,
                balanceSheet = bs,
                cashFlow = cf,
                isLoading = false
            )
        }
    }

    fun explainLine(title: String, amountCents: Long, accountId: Long? = null) {
        val formatted = MoneyFormatter.format(Money(amountCents))
        val (plain, uncle) = generateUncleCommentary(title, amountCents)
        _state.value = _state.value.copy(
            selectedExplanation = UncleExplanation(
                title = title,
                amountFormatted = formatted,
                plainEnglish = plain,
                uncleComment = uncle,
                relatedAccountId = accountId
            )
        )
    }

    fun dismissExplanation() {
        _state.value = _state.value.copy(selectedExplanation = null)
    }

    private fun generateUncleCommentary(title: String, amountCents: Long): Pair<String, String> {
        return when {
            title.contains("Revenue", ignoreCase = true) -> Pair(
                "Gross inflow of economic benefits arising from the ordinary activities of the bookshop.",
                "\"Every rand here came from someone buying a book or a coffee. But watch out: if revenue looks too neat, check the delivery dates. Some sales were booked before the crates even left the harbor.\""
            )
            title.contains("Cost of Goods", ignoreCase = true) || title.contains("COGS", ignoreCase = true) -> Pair(
                "Direct expenditure incurred to acquire and prepare inventory sold to customers.",
                "\"Noor delivers our stock on credit. Her prices are reasonable, but if COGS drops while sales go up without new shipments, someone is playing shell games with old inventory numbers.\""
            )
            title.contains("Payroll", ignoreCase = true) -> Pair(
                "Compensation, wages, tax withholdings, and consultant fees paid to staff.",
                "\"Mara runs payroll on the 25th like clockwork. But look at the disbursement register: who is 'P. Vance'? You won't find a desk for him in the shop. Figure out where those checks were cashed.\""
            )
            title.contains("Rent", ignoreCase = true) -> Pair(
                "Contractual occupancy cost paid to landlord Piet Botha for the premises.",
                "\"Piet will bang on the glass on the 1st of every month. Pay him on time, or he'll lock the front doors before you can count yesterday's till.\""
            )
            title.contains("Cash on Hand", ignoreCase = true) -> Pair(
                "Physical currency and coins kept in the cash registers and petty cash drawer.",
                "\"The physical drawer. Count the coins every evening. If the petty cash float keeps mysteriously shrinking by small amounts, check who carries the spare set of keys.\""
            )
            title.contains("Bank", ignoreCase = true) -> Pair(
                "Liquid funds held in the primary business operating account at the bank.",
                "\"Our lifeline. Once this drops below zero, the bank flags the business for distress. Keep enough here to clear payroll and vendor cheques.\""
            )
            title.contains("Receivable", ignoreCase = true) -> Pair(
                "Legally enforceable claims for payment held by the business for goods delivered.",
                "\"Paper promises. Half our corporate accounts are 60 days overdue. If you need cash fast, offer them a 5% discount to settle early.\""
            )
            title.contains("Inventory", ignoreCase = true) -> Pair(
                "Valuation of books, stationery, and merchandise currently held for resale.",
                "\"Looks pretty on paper, but check the basement. Damp damage took out three boxes of rare prints last winter. If the ledger hasn't written them off, our asset value is a lie.\""
            )
            title.contains("Equipment", ignoreCase = true) -> Pair(
                "Long-term tangible assets utilized in business operations, recorded at historical cost.",
                "\"The mahogany bookshelves and the espresso bar. Old Silas holds a lien against the fixtures until his loan note is extinguished.\""
            )
            title.contains("Prepaid", ignoreCase = true) -> Pair(
                "Expenditures paid for in advance of receiving the underlying goods or services.",
                "\"The classic graveyard of suspicious expenses. When an entry doesn't make sense, accountants often dump it into 'Prepaid' hoping no auditor notices.\""
            )
            title.contains("Payable", ignoreCase = true) -> Pair(
                "Short-term liabilities due to suppliers, vendors, and service providers.",
                "\"What we owe Noor and the printers. Noor is patient, but Silas is not. Prioritize creditors who have keys to your back alley.\""
            )
            title.contains("Loan", ignoreCase = true) -> Pair(
                "Principal financial liabilities borrowed to finance business operations.",
                "\"The private note held by Silas Vane. He lent me capital when the bank refused. He expects his interest on the 15th, no excuses.\""
            )
            title.contains("Equity", ignoreCase = true) || title.contains("Earnings", ignoreCase = true) -> Pair(
                "Residual interest in the assets of the enterprise after deducting all liabilities.",
                "\"What remains when everything is settled. If equity goes negative, the shop is legally insolvent. Keep it positive to survive the 30 days.\""
            )
            title.contains("Net Income", ignoreCase = true) -> Pair(
                "The excess of total revenues over total expenses for the period.",
                "\"The ultimate score. Positive means we live to fight another week; negative means the hourglass is emptying.\""
            )
            title.contains("Operating", ignoreCase = true) -> Pair(
                "Cash flows generated directly by operational trading activities.",
                "\"The heartbeat of the shop. You can fabricate paper profit with journal tricks, but cash flow never lies.\""
            )
            else -> Pair(
                "Financial line item representing current balance or transaction total in the books.",
                "\"Every number in this ledger tells a story. Look at who signed off on the posting and where the offsetting entry went.\""
            )
        }
    }
}
