package com.theinheritance.accounting.model

data class JournalLine(
    val accountId: Long,
    val debitCents: Long = 0L,
    val creditCents: Long = 0L,
    val memo: String = ""
) {
    init {
        require(debitCents >= 0) { "Debit cannot be negative" }
        require(creditCents >= 0) { "Credit cannot be negative" }
        require(!(debitCents > 0 && creditCents > 0)) { "Line cannot have both debit and credit" }
    }
    val isDebit get() = debitCents > 0
    val isCredit get() = creditCents > 0
    val amount get() = if (isDebit) debitCents else creditCents
}
