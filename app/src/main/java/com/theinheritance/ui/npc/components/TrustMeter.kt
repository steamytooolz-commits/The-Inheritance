package com.theinheritance.ui.npc.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun TrustMeter(trust: Int) {
    LinearProgressIndicator(
        progress = { trust / 100f },
        modifier = Modifier.testTag("trust_meter")
    )
    Text("$trust / 100")
}
