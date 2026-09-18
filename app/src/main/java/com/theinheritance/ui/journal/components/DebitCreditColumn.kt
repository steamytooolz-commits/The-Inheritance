package com.theinheritance.ui.journal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun DebitCreditColumn(debitLabel: String, creditLabel: String, amountRands: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().testTag("debit_credit_column")) {
        Column(modifier = Modifier.weight(1f)) {
            Text("DEBIT", style = MaterialTheme.typography.labelLarge)
            Text("$debitLabel — R$amountRands")
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("CREDIT", style = MaterialTheme.typography.labelLarge)
            Text("$creditLabel — R$amountRands")
        }
    }
}
