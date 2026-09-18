package com.theinheritance.gm.actions

import com.theinheritance.gm.Deadline
import com.theinheritance.gm.GmAction
import com.theinheritance.simulation.NpcAgent

// A. Book actions — ledger mutations the GM may request. Engine validates.
sealed interface BookAction : GmAction {
    data class PlantDiscrepancy(val accountId: Long, val amountCents: Long, val memo: String) : BookAction
    data class BackdateEntry(val entryId: Long, val newDate: String) : BookAction
    data class DuplicatePayroll(val originalEntryId: Long, val ghostName: String) : BookAction
    data class RoundDownMysteriously(val accountId: Long, val centsLost: Long) : BookAction
    data class FabricateInvoice(val vendorName: String, val amountCents: Long) : BookAction
    data class InflateReceivable(val customerId: Long, val amountCents: Long) : BookAction
    data class HideLiability(val liabilityAccountId: Long, val hideInAccountId: Long) : BookAction
    data class ReclassifyExpense(val fromAccountId: Long, val toAccountId: Long, val amountCents: Long) : BookAction
    data class SweepPettyCash(val amountCents: Long) : BookAction
    data class ReverseAReversal(val entryId: Long) : BookAction
    data class HighlightLine(val journalLineId: Long) : BookAction
    data class AnnotateMargin(val entryId: Long, val note: String) : BookAction
    data class LeaveVoicemail(val audioAssetId: String) : BookAction
    data class SurfaceReceipt(val receiptId: String, val x: Float, val y: Float) : BookAction
    data class CrossReference(val entryIdA: Long, val entryIdB: Long) : BookAction
    data class OpenTheShoebox(val documentId: String) : BookAction
    data class ShowTheOriginal(val tamperedEntryId: Long, val originalSnapshot: String) : BookAction
    data class LoopAReceipt(val receiptId: String) : BookAction
    data class PostTransaction(val debitAccountId: Long, val creditAccountId: Long, val amountCents: Long, val memo: String) : BookAction
    data class VoidEntry(val entryId: Long, val reason: String) : BookAction
    data class AdjustOpeningBalance(val accountId: Long, val newBalanceCents: Long) : BookAction
    data class ClosePeriod(val endDate: String) : BookAction
    data class LockAccount(val accountId: Long, val reason: String) : BookAction
    data class UnlockAccount(val accountId: Long, val reason: String) : BookAction
    data class InjectNoise(val count: Int) : BookAction
    data class DeleteFolder(val folderId: String) : BookAction
    data class RestoreFolder(val folderId: String, val note: String) : BookAction
    data class FreezeBankFeed(val bankAccountId: Long) : BookAction
    data class OpenTheVault(val reason: String) : BookAction
    data class CallInDebt(val creditorNpcId: Long, val amountCents: Long) : BookAction
    data class BounceCheque(val paymentId: Long) : BookAction
    data class TriggerAudit(val inspectorName: String) : BookAction
    data class LeakToLandlord(val info: String) : BookAction
    data class FreezePayroll(val reason: String) : BookAction
}
