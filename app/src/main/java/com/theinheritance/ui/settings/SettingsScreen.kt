package com.theinheritance.ui.settings

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    val activity = LocalActivity.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Online GM (Optional)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    ClayTextField(s.remoteUrl, vm::setRemoteUrl, "Base URL", Modifier.fillMaxWidth(), testTag = "settings_url")
                    ClayTextField(s.remoteKey, vm::setRemoteKey, "API key", Modifier.fillMaxWidth(), testTag = "settings_key")
                    ClayButton(
                        text = if (s.useRemote) "Using Remote — Tap to go offline" else "Use Remote",
                        onClick = vm::toggleRemote,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_toggle_remote"
                    )
                }
            }
        }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth().testTag("pro_card")) {
                Text(
                    text = if (s.isPro) "Pro Unlocked — The vault is open." else "Go Pro (One-time purchase)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!s.isPro) {
                    ClayButton(
                        text = "Unlock Pro",
                        onClick = {
                            scope.launch {
                                if (activity != null) billing?.launchProPurchase(activity)
                                billing?.refreshEntitlements()
                            }
                        },
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        testTag = "btn_unlock_pro"
                    )
                }
            }
        }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "New Run",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Start a fresh 30-day run. New Game+ preserves uncle memories.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )
                ClayButton("Start New 30-Day Run", {}, modifier = Modifier.fillMaxWidth(), testTag = "btn_new_run")
            }
        }
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Help & Onboarding",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                ClayButton(
                    text = "How to Play Tutorial",
                    onClick = onOpenTutorial,
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    testTag = "btn_open_tutorial"
                )
            }
        }
    }
}

