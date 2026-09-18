package com.theinheritance.ui.modelmanager

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.data.llm.AvailableModels
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard

@Composable
fun ModelManagerScreen(vm: ModelManagerViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("model_manager_screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("AI Models (offline-first)", style = MaterialTheme.typography.headlineMedium) }
        item { Text("FunctionGemma 270M is bundled. Larger models download on unmetered Wi-Fi only (WorkManager).") }
        items(AvailableModels.all) { m ->
            ClayCard(modifier = Modifier.fillMaxWidth().testTag("model_${m.id}")) {
                Text(m.label)
                Text("${m.sizeBytes / 1024 / 1024} MB ${if (m.bundled) "(bundled)" else "(download)"}")
                if (!m.bundled) {
                    if (s.downloadingId == m.id) {
                        Text("Downloading… ${s.progress}%")
                    } else {
                        ClayButton("Download on Wi-Fi", { vm.download(m) }, isPrimary = false, testTag = "btn_download_${m.id}")
                    }
                }
            }
        }
    }
}
