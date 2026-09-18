package com.theinheritance.ui.statements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.statements.components.BalanceSheetView
import com.theinheritance.ui.statements.components.CashFlowView
import com.theinheritance.ui.statements.components.IncomeStatementView

@Composable
fun StatementsScreen(
    onAccountClick: (Long) -> Unit,
    vm: StatementsViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("statements_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { IncomeStatementView() }
        item { BalanceSheetView(onAccountClick) }
        item { CashFlowView() }
    }
}
