package com.theinheritance.ui.npc.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.theinheritance.simulation.NpcAgent
import com.theinheritance.ui.npc.DialogueExchange
import com.theinheritance.ui.theme.ClayButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpcDialogueSheet(
    npc: NpcAgent,
    dialogue: DialogueExchange?,
    onAskTopic: (String) -> Unit,
    onClose: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("npc_dialogue_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = npc.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${npc.role}  •  Trust: ${npc.trustLevel}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Dialogue History / Current Response
            if (dialogue != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "You: ${dialogue.playerPrompt}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = dialogue.npcResponse,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Dialogue / Interrogation Options based on NPC
            Text(
                text = "Choose Topic to Inquire:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (npc.name) {
                    "Mara Voss" -> {
                        ClayButton(
                            text = "🔍 Inquire about 'P. Vance' Payroll Entry",
                            onClick = { onAskTopic("payroll") },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_topic_payroll"
                        )
                        ClayButton(
                            text = "📖 Ask about Uncle George's private notes",
                            onClick = { onAskTopic("uncle") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_uncle"
                        )
                        ClayButton(
                            text = "🤝 Praise her loyalty and hard work (+Trust)",
                            onClick = { onAskTopic("compliment") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_compliment"
                        )
                    }
                    "Silas Vane" -> {
                        ClayButton(
                            text = "💼 Ask about the R50,000 Promissory Note",
                            onClick = { onAskTopic("loan") },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_topic_loan"
                        )
                        ClayButton(
                            text = "📊 Warn him about current cash runway",
                            onClick = { onAskTopic("books") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_books"
                        )
                        ClayButton(
                            text = "🤝 Propose punctual interest terms (+Trust)",
                            onClick = { onAskTopic("favor") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_favor"
                        )
                    }
                    "Noor Haddad" -> {
                        ClayButton(
                            text = "📦 Ask about R24,000 water-damaged inventory",
                            onClick = { onAskTopic("inventory") },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_topic_inventory"
                        )
                        ClayButton(
                            text = "💰 Request 10% wholesale trade discount (+Trust)",
                            onClick = { onAskTopic("discount") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_discount"
                        )
                        ClayButton(
                            text = "📖 Ask what George confided in her",
                            onClick = { onAskTopic("uncle") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_uncle"
                        )
                    }
                    "Piet Botha" -> {
                        ClayButton(
                            text = "🏢 Clarify monthly commercial lease rate",
                            onClick = { onAskTopic("rent") },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_topic_rent"
                        )
                        ClayButton(
                            text = "🛠️ Demand repairs on damp basement floor",
                            onClick = { onAskTopic("maintenance") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_maintenance"
                        )
                        ClayButton(
                            text = "🤝 Guarantee early rent payment (+Trust)",
                            onClick = { onAskTopic("early_pay") },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false,
                            testTag = "btn_topic_early_pay"
                        )
                    }
                    else -> {
                        ClayButton(
                            text = "Ask general questions",
                            onClick = { onAskTopic("general") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                ClayButton(
                    text = "Leave Dialogue",
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = false,
                    testTag = "btn_close_dialogue"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
