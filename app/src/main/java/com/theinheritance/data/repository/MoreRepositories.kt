package com.theinheritance.data.repository

import com.theinheritance.data.local.dao.GmMemoryDao
import com.theinheritance.data.local.dao.MarketEventDao
import com.theinheritance.data.local.dao.NpcDao
import com.theinheritance.data.local.entity.GmMemoryEntity
import com.theinheritance.data.local.entity.MarketEventEntity
import com.theinheritance.data.local.entity.NpcEntity
import com.theinheritance.simulation.MarketEvent
import com.theinheritance.simulation.NpcAgent
import com.theinheritance.simulation.NpcMemory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

fun NpcEntity.toDomain() = NpcAgent(id, name, role, personality, trustLevel, isAlive)
fun NpcAgent.toEntity() = NpcEntity(id, name, role, personality, trustLevel, isAlive)

@Singleton
class NpcRepository @Inject constructor(private val dao: NpcDao) {
    fun observe(): Flow<List<NpcAgent>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    suspend fun seedDefaults() {
        if (dao.getAll().isNotEmpty()) return
        dao.upsertAll(
            listOf(
                NpcEntity(1, "Mara Voss", "Bookkeeper", "Precise, nervous, loyal to the uncle", 55, true, true, true),
                NpcEntity(2, "Silas Vane", "Creditor", "Polite menace in a good coat", 20, true, false, true),
                NpcEntity(3, "Noor Haddad", "Supplier", "Warm, sharp, remembers every invoice", 60, true, false, false),
                NpcEntity(4, "Piet Botha", "Landlord", "Blunt, impatient, cash-first", 35, true, false, false),
            )
        )
    }
    suspend fun shiftTrust(id: Long, delta: Int): Int {
        val cur = dao.getById(id) ?: return 50
        val v = (cur.trustLevel + delta).coerceIn(0, 100)
        dao.setTrust(id, v)
        return v
    }
}

@Singleton
class GmMemoryRepository @Inject constructor(private val dao: GmMemoryDao) {
    fun observe() = dao.observeAll()
    suspend fun remember(day: Int, type: String, content: String) { dao.insert(GmMemoryEntity(day = day, actionType = type, content = content)) }
    suspend fun unrevealed(limit: Int = 5) = dao.getUnrevealed(limit)
}

@Singleton
class RunRepository @Inject constructor(private val events: MarketEventDao) {
    fun observeEvents() = events.observeAll()
    suspend fun recordEvent(e: MarketEvent) {
        events.insert(MarketEventEntity(day = e.day, eventType = e.eventType, severity = e.severity, narrative = e.narrative))
    }
}
