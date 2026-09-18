package com.theinheritance.accounting.engine

import com.theinheritance.accounting.model.AccountType
import com.theinheritance.accounting.model.BalanceSheet
import com.theinheritance.accounting.model.BalanceSheetLine
import com.theinheritance.accounting.model.CashFlowStatement
import com.theinheritance.accounting.model.IncomeStatement
import com.theinheritance.accounting.model.IncomeStatementLine
import java.time.LocalDate

class FinancialStatementGenerator(private val engine: AccountingEngine) {
    fun incomeStatement(from: LocalDate, to: LocalDate): IncomeStatement {
        val inRange = engine.postedEntries().filter { !it.date.isBefore(from) && !it.date.isAfter(to) }
        var revenue = 0L; var expenses = 0L; var cogs = 0L
        val byAccount = mutableMapOf<Long, Long>()
        val accs = engine.accounts()
        for (e in inRange) for (l in e.lines) {
            val acc = accs[l.accountId] ?: continue
            val delta = if (acc.normalDebit) l.debitCents - l.creditCents else l.creditCents - l.debitCents
            byAccount[l.accountId] = (byAccount[l.accountId] ?: 0L) + delta
        }
        val lines = mutableListOf<IncomeStatementLine>()
        lines.add(IncomeStatementLine("Revenue", 0, 0, false))
        accs.values.filter { it.type == AccountType.REVENUE }.sortedBy { it.code }.forEach { a ->
            val v = byAccount[a.id] ?: 0L
            if (v != 0L) { lines.add(IncomeStatementLine(a.name, v, 1)); revenue += v }
        }
        lines.add(IncomeStatementLine("Total Revenue", revenue, 0, true))
        accs.values.filter { it.type == AccountType.EXPENSE }.sortedBy { it.code }.forEach { a ->
            val v = byAccount[a.id] ?: 0L
            if (v != 0L) {
                lines.add(IncomeStatementLine(a.name, v, 1))
                if (a.name.contains("Cost of Goods", ignoreCase = true)) cogs += v else expenses += v
            }
        }
        lines.add(IncomeStatementLine("Total Expenses", expenses + cogs, 0, true))
        lines.add(IncomeStatementLine("Net Income", revenue - expenses - cogs, 0, true))
        return IncomeStatement(from, to, revenue, expenses, cogs, lines)
    }

    fun balanceSheet(asOf: LocalDate): BalanceSheet {
        val accs = engine.accounts()
        var assets = 0L; var liabilities = 0L; var equity = 0L
        val lines = mutableListOf<BalanceSheetLine>()
        lines.add(BalanceSheetLine("Assets", 0))
        accs.values.filter { it.type == AccountType.ASSET }.sortedBy { it.code }.forEach { a ->
            val b = engine.balanceFor(a.id, asOf).cents
            if (b != 0L) { lines.add(BalanceSheetLine(a.name, b, 1)); assets += b }
        }
        lines.add(BalanceSheetLine("Total Assets", assets, 0, true))
        lines.add(BalanceSheetLine("Liabilities", 0))
        accs.values.filter { it.type == AccountType.LIABILITY }.sortedBy { it.code }.forEach { a ->
            val b = engine.balanceFor(a.id, asOf).cents
            if (b != 0L) { lines.add(BalanceSheetLine(a.name, b, 1)); liabilities += b }
        }
        lines.add(BalanceSheetLine("Total Liabilities", liabilities, 0, true))
        lines.add(BalanceSheetLine("Equity", 0))
        accs.values.filter { it.type == AccountType.EQUITY }.sortedBy { it.code }.forEach { a ->
            val b = engine.balanceFor(a.id, asOf).cents
            if (b != 0L) { lines.add(BalanceSheetLine(a.name, b, 1)); equity += b }
        }
        val firstDay = asOf.withDayOfMonth(1)
        val ni = incomeStatement(firstDay, asOf).netIncomeCents
        if (ni != 0L) {
            lines.add(BalanceSheetLine("Current Earnings", ni, 1))
            equity += ni
        }
        lines.add(BalanceSheetLine("Total Equity", equity, 0, true))
        return BalanceSheet(asOf, assets, liabilities, equity, lines)
    }

    fun cashFlow(from: LocalDate, to: LocalDate): CashFlowStatement {
        val ni = incomeStatement(from, to).netIncomeCents
        val accs = engine.accounts()
        fun delta(id: Long): Long {
            val end = engine.balanceFor(id, to).cents
            val start = engine.balanceFor(id, from.minusDays(1)).cents
            return end - start
        }
        val arId = accs.values.firstOrNull { it.name.contains("Receivable", ignoreCase = true) }?.id
        val apId = accs.values.firstOrNull { it.name.contains("Payable", ignoreCase = true) && !it.name.contains("Payroll", ignoreCase = true) }?.id
        val invId = accs.values.firstOrNull { it.name.contains("Inventory", ignoreCase = true) }?.id
        var operating = ni
        if (arId != null) operating -= delta(arId)
        if (invId != null) operating -= delta(invId)
        if (apId != null) operating += delta(apId)
        return CashFlowStatement(from, to, operating, 0L, 0L)
    }
}
