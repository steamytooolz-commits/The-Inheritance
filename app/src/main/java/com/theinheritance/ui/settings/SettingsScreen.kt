package com.theinheritance.ui.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.monetization.BillingManager
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard
import com.theinheritance.ui.theme.ClayTextField
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onOpenTutorial: () -> Unit,
    vm: SettingsViewModel = hiltViewModel(),
    billing: BillingManager? = null
) {
    val s by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Settings", style = MaterialTheme.typography.headlineMedium) }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text("Online GM (optional)")
                ClayTextField(s.remoteUrl, vm::setRemoteUrl, "Base URL", testTag = "settings_url")
                ClayTextField(s.remoteKey, vm::setRemoteKey, "API key", testTag = "settings_key")
                ClayButton(
                    if (s.useRemote) "Using Remote — tap to go offline" else "Use Remote",
                    vm::toggleRemote,
                    testTag = "btn_toggle_remote"
                )
            }
        }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth().testTag("pro_card")) {
                Text(if (s.isPro) "Pro unlocked — the vault is open." else "Go Pro (one-time)")
                if (!s.isPro) {
                    ClayButton(
                        "Unlock Pro",
                        {
                            scope.launch {
                                if (activity != null) billing?.launchProPurchase(activity)
                                billing?.refreshEntitlements()
                            }
                        },
                        isPrimary = false,
                        testTag = "btn_unlock_pro"
                    )
                }
            }
        }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text("New run (30 days, NG+ keeps GM memory)")
                ClayButton("Start new 30-day run", {}, testTag = "btn_new_run")
            }
        }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text("Help")
                ClayButton("How to play", onOpenTutorial, isPrimary = false, testTag = "btn_open_tutorial")
            }
        }
    }
}
