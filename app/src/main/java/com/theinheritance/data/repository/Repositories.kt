package com.theinheritance.data.repository

import com.theinheritance.accounting.engine.AccountingEngine
import com.theinheritance.accounting.engine.FinancialStatementGenerator
import com.theinheritance.accounting.model.Account
import com.theinheritance.accounting.model.AccountType
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.accounting.model.JournalEntry
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
class AccountingRepository @Inject constructor(private val accounts: AccountDao, private val journal: JournalDao) {
    fun observeAccounts(): Flow<List<Account>> = accounts.observeAll().map { list -> list.map { it.toDomain() } }
    fun observeEntries(): Flow<List<JournalEntryEntity>> = journal.observeAll()

    suspend fun seedIfEmpty() {
        if (accounts.getAll().isEmpty()) accounts.insertAll(ChartOfAccounts.default().map { it.toEntity() })
    }

    suspend fun buildEngine(): AccountingEngine {
        seedIfEmpty()
        val accs = accounts.getAll().map { it.toDomain() }
        val engine = AccountingEngine(accs)
        return engine
    }

    suspend fun post(entry: JournalEntry, lines: List<JournalLineEntity>): Long {
        val entity = JournalEntryEntity(date = entry.date.toString(), memo = entry.memo, isPosted = true, createdAt = System.currentTimeMillis(), postedBy = entry.postedBy, fraudFlag = entry.fraudFlag?.name)
        return journal.insertWithLines(entity, lines)
    }

    suspend fun statements(from: LocalDate, to: LocalDate): Triple<Long, Long, Long> {
        val engine = buildEngine()
        val gen = FinancialStatementGenerator(engine)
        val inc = gen.incomeStatement(from, to)
        return Triple(inc.revenueCents, inc.expenseCents, inc.netIncomeCents)
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
