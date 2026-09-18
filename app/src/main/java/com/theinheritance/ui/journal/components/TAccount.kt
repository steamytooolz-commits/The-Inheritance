package com.theinheritance.ui.journal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.theinheritance.ui.theme.ClayCard

@Composable
fun TAccount(name: String, debit: Long, credit: Long) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("t_account")) {
        Text(name, style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("DR R${debit / 100}")
            Text("CR R${credit / 100}")
        }
    }
}
