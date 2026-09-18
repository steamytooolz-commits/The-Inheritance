package com.theinheritance.ui.npc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.npc.components.NpcDialogueSheet
import com.theinheritance.ui.npc.components.NpcPortraitCard
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard

@Composable
fun NpcHubScreen(vm: NpcHubViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("npc_hub_screen")
    ) {
        val isWideTablet = maxWidth >= 800.dp

        if (isWideTablet) {
            // Tablet Split-Pane: Left side = Suspect Dossiers & Portraits, Right side = Active Dialogue & Interrogation Sheet
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(0.45f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "People & Suspects",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Select a person to interrogate them, gauge trust, and cross-examine suspect claims against ledger evidence.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    items(s.npcs) { npc ->
                        NpcPortraitCard(
                            npc = npc,
                            onTalk = { vm.openDialogue(npc) }
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(0.55f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    s.activeNpc?.let { npc ->
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Interrogating: ${npc.name} (${npc.role})",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                s.currentDialogue?.let { diag ->
                                    Text(
                                        text = diag.npcResponse,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "Topics to Cross-Examine:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ClayButton(
                                        text = "Ask about P. Vance & Payroll 🧾",
                                        onClick = { vm.askQuestion("payroll") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    ClayButton(
                                        text = "Ask about Loan Liabilities 💰",
                                        onClick = { vm.askQuestion("loan") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    ClayButton(
                                        text = "Inquire about Inventory Write-offs 📦",
                                        onClick = { vm.askQuestion("inventory") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    ClayButton(
                                        text = "Inquire about Lease & Rent Payments 🔑",
                                        onClick = { vm.askQuestion("rent") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    } ?: run {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Select a suspect from the left dossier to initiate interrogation.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile single column view with bottom sheet
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ClayCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "People & Suspects",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Every person connected to Uncle George has secrets in their ledgers. Interrogate them, build trust, and compare their stories to the journal entries.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                items(s.npcs) { npc ->
                    NpcPortraitCard(
                        npc = npc,
                        onTalk = { vm.openDialogue(npc) }
                    )
                }
            }

            s.activeNpc?.let { npc ->
                NpcDialogueSheet(
                    npc = npc,
                    dialogue = s.currentDialogue,
                    onAskTopic = vm::askQuestion,
                    onClose = vm::closeDialogue
                )
            }
        }
    }
}
