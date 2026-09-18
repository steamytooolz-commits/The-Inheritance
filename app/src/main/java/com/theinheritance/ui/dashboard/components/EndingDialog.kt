package com.theinheritance.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theinheritance.accounting.money.Money
import com.theinheritance.accounting.money.MoneyFormatter
import com.theinheritance.ui.theme.ClayButton

@Composable
fun EndingDialog(
    isVictorious: Boolean,
    daysSurvived: Int,
    endingCashCents: Long,
    score: Int,
    onRestart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        modifier = Modifier.testTag("ending_dialog"),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isVictorious) Icons.Default.EmojiEvents else Icons.Default.SentimentVeryDissatisfied,
                    contentDescription = null,
                    tint = if (isVictorious) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = if (isVictorious) "Day 30 Audit Passed!" else "Business Insolvent (Game Over)",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isVictorious) {
                        "You survived all 30 days of probate. The books have passed the forensic accountant's inspection, and Uncle George's estate is officially yours."
                    } else {
                        "Cash reserves hit bottom. The court seized the registers and closed the shop."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Days Survived:", style = MaterialTheme.typography.bodyMedium)
                            Text("$daysSurvived / 30", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Final Cash Balance:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                MoneyFormatter.format(Money(endingCashCents)),
                                fontWeight = FontWeight.Bold,
                                color = if (endingCashCents >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Forensic Score:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "$score pts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Text(
                    text = if (isVictorious) {
                        "\"Not bad, kid. You're twice the accountant I ever was.\" — Uncle George"
                    } else {
                        "\"Well, at least you tried. Next time, watch the accounts receivable.\" — Uncle George"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            ClayButton(
                text = "Start New 30-Day Run",
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_ending_restart"
            )
        }
    )
}
