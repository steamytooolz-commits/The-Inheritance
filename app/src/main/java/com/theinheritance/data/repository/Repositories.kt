package com.theinheritance.data.repository

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.engine.FinancialStatementGenerator
import com.theinheritance.accounting.model.Account
import com.theinheritance.accounting.model.AccountType
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.accounting.model.JournalEntry
import com.theinheritance.accounting.model.JournalLine
import com.theinheritance.data.local.dao.AccountDao
import com.theinheritance.data.local.dao.GameStateDao
import com.theinheritance.data.local.dao.JournalDao
import com.theinheritance.data.local.entity.AccountEntity
import com.theinheritance.data.local.entity.GameStateEntity
import com.theinheritance.data.local.entity.JournalEntryEntity
import com.theinheritance.data.local.entity.JournalLineEntity
import com.theinheritance.simulation.BusinessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

fun AccountEntity.toDomain() = Account(id, code, name, AccountType.valueOf(type), isContra, parentAccountId, isLocked, isHidden)
fun Account.toEntity() = AccountEntity(id, code, name, type.name, isContra, parentAccountId, isLocked, isHidden)

@Singleton
class AccountingRepository @Inject constructor(
    private val accounts: AccountDao,
    private val journal: JournalDao
) {
    fun observeAccounts(): Flow<List<Account>> = accounts.observeAll().map { list -> list.map { it.toDomain() } }
    fun observeEntries(): Flow<List<JournalEntryEntity>> = journal.observeAll()

    suspend fun seedIfEmpty() {
        if (accounts.getAll().isEmpty()) {
            accounts.insertAll(ChartOfAccounts.default().map { it.toEntity() })
        }
        if (journal.getAllEntries().isEmpty()) {
            seedInitialBookEntries()
        }
    }

    private suspend fun seedInitialBookEntries() {
        val now = LocalDate.now()
        // Initial capital & cash
        post(
            JournalEntry(date = now.minusDays(15), memo = "Initial Owner's Capital & Bank Cheque deposit", lines = listOf(
                JournalLine(1010, debitCents = 350_000_00), // Bank Business Cheque R350,000
                JournalLine(3000, creditCents = 350_000_00) // Owner's Equity
            )),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 1010, debitCents = 350_000_00),
                JournalLineEntity(journalEntryId = 0, accountId = 3000, creditCents = 350_000_00)
            )
        )
        // Store equipment
        post(
            JournalEntry(date = now.minusDays(14), memo = "Shelving, registers, and display units", lines = listOf(
                JournalLine(1500, debitCents = 85_000_00),
                JournalLine(1010, creditCents = 85_000_00)
            )),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 1500, debitCents = 85_000_00),
                JournalLineEntity(journalEntryId = 0, accountId = 1010, creditCents = 85_000_00)
            )
        )
        // Opening stock inventory from Noor Haddad
        post(
            JournalEntry(date = now.minusDays(10), memo = "Initial inventory shipment (Noor Haddad Supplies)", lines = listOf(
                JournalLine(1200, debitCents = 120_000_00),
                JournalLine(2000, creditCents = 120_000_00) // Accounts Payable
            )),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 1200, debitCents = 120_000_00),
                JournalLineEntity(journalEntryId = 0, accountId = 2000, creditCents = 120_000_00)
            )
        )
        // Loan from Silas Vane
        post(
            JournalEntry(date = now.minusDays(8), memo = "Short-term bridge loan from Silas Vane", lines = listOf(
                JournalLine(1000, debitCents = 50_000_00), // Cash on hand
                JournalLine(2200, creditCents = 50_000_00) // Loan Payable
            )),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 1000, debitCents = 50_000_00),
                JournalLineEntity(journalEntryId = 0, accountId = 2200, creditCents = 50_000_00)
            )
        )
        // Book sales revenue
        post(
            JournalEntry(date = now.minusDays(5), memo = "First week customer retail sales", lines = listOf(
                JournalLine(1000, debitCents = 42_500_00), // Cash
                JournalLine(4000, creditCents = 42_500_00)  // Sales Revenue
            )),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 1000, debitCents = 42_500_00),
                JournalLineEntity(journalEntryId = 0, accountId = 4000, creditCents = 42_500_00)
            )
        )
        // Cost of Goods Sold
        post(
            JournalEntry(date = now.minusDays(5), memo = "Inventory reduction for weekly sales", lines = listOf(
                JournalLine(5000, debitCents = 24_000_00), // COGS
                JournalLine(1200, creditCents = 24_000_00) // Inventory
            )),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 5000, debitCents = 24_000_00),
                JournalLineEntity(journalEntryId = 0, accountId = 1200, creditCents = 24_000_00)
            )
        )
        // Rent payment
        post(
            JournalEntry(date = now.minusDays(3), memo = "Monthly shop lease paid to Piet Botha", lines = listOf(
                JournalLine(5200, debitCents = 18_000_00), // Rent Expense
                JournalLine(1010, creditCents = 18_000_00) // Bank
            )),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 5200, debitCents = 18_000_00),
                JournalLineEntity(journalEntryId = 0, accountId = 1010, creditCents = 18_000_00)
            )
        )
        // Suspicious ghost payroll transaction (Flagged!)
        post(
            JournalEntry(
                date = now.minusDays(1),
                memo = "Consulting & Archival Services — P. Vance (Ghost Entry)",
                lines = listOf(
                    JournalLine(5100, debitCents = 14_500_00), // Payroll Expense
                    JournalLine(1010, creditCents = 14_500_00) // Bank Cheque
                ),
                postedBy = "Uncle George (Auto-scheduled)"
            ),
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = 5100, debitCents = 14_500_00),
                JournalLineEntity(journalEntryId = 0, accountId = 1010, creditCents = 14_500_00)
            )
        )
    }

    suspend fun buildEngine(): AccountingEngine {
        seedIfEmpty()
        val accs = accounts.getAll().map { it.toDomain() }
        val engine = AccountingEngine(accs)
        val allEnts = journal.getAllEntries()
        val allLines = journal.getAllLines().groupBy { it.journalEntryId }

        for (e in allEnts) {
            val lines = allLines[e.id]?.map { l ->
                com.theinheritance.accounting.model.JournalLine(
                    accountId = l.accountId,
                    debitCents = l.debitCents,
                    creditCents = l.creditCents,
                    memo = l.memo
                )
            } ?: emptyList()
            val parsedDate = runCatching { LocalDate.parse(e.date) }.getOrDefault(LocalDate.now())
            val jEntry = JournalEntry(
                id = e.id,
                date = parsedDate,
                memo = e.memo,
                lines = lines,
                isPosted = e.isPosted,
                isVoided = e.isVoided,
                postedBy = e.postedBy
            )
            engine.post(jEntry)
        }
        return engine
    }

    suspend fun post(entry: JournalEntry, lines: List<JournalLineEntity>): Long {
        val entity = JournalEntryEntity(
            date = entry.date.toString(),
            memo = entry.memo,
            isPosted = true,
            createdAt = System.currentTimeMillis(),
            postedBy = entry.postedBy,
            fraudFlag = entry.fraudFlag?.name
        )
        return journal.insertWithLines(entity, lines)
    }

    suspend fun statements(from: LocalDate, to: LocalDate): Triple<Long, Long, Long> {
        val engine = buildEngine()
        val gen = FinancialStatementGenerator(engine)
        val inc = gen.incomeStatement(from, to)
        return Triple(inc.revenueCents, inc.expenseCents, inc.netIncomeCents)
    }

    suspend fun getIncomeStatement(from: LocalDate, to: LocalDate): com.theinheritance.accounting.model.IncomeStatement {
        val engine = buildEngine()
        return FinancialStatementGenerator(engine).incomeStatement(from, to)
    }

    suspend fun getBalanceSheet(asOf: LocalDate): com.theinheritance.accounting.model.BalanceSheet {
        val engine = buildEngine()
        return FinancialStatementGenerator(engine).balanceSheet(asOf)
    }

    suspend fun getCashFlow(from: LocalDate, to: LocalDate): com.theinheritance.accounting.model.CashFlowStatement {
        val engine = buildEngine()
        return FinancialStatementGenerator(engine).cashFlow(from, to)
    }

    suspend fun getLedger(accountId: Long): com.theinheritance.accounting.model.Ledger? {
        val engine = buildEngine()
        return engine.ledgerFor(accountId)
    }
}

@Singleton
class GameStateRepository @Inject constructor(private val dao: GameStateDao) {
    fun observe(): Flow<BusinessState> = dao.observe().map { e ->
        val v = e ?: GameStateEntity()
        BusinessState(v.currentDay, v.maxDays, v.cashCents, v.businessName, v.isAlive, v.epilogueId, v.runSeed)
    }
    suspend fun get(): BusinessState {
        val v = dao.get() ?: GameStateEntity()
        return BusinessState(v.currentDay, v.maxDays, v.cashCents, v.businessName, v.isAlive, v.epilogueId, v.runSeed)
    }
    suspend fun upsert(state: BusinessState) {
        dao.upsert(GameStateEntity(1, state.day, state.maxDays, state.cashCents, state.businessName, state.isAlive, state.epilogueId, state.runSeed))
    }
    suspend fun newRun(seed: Long, name: String = "The Book Nook") {
        dao.upsert(GameStateEntity(1, 1, 30, 420000, name, true, null, seed))
    }
}
