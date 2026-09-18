package com.theinheritance.ui.pocketbase

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.data.pocketbase.DatabaseBackendType
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard
import com.theinheritance.ui.theme.ClayTextField

@Composable
fun PocketBaseScreen(
    vm: PocketBaseViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp).testTag("pocketbase_screen")) {
        val isWide = maxWidth >= 800.dp

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header & Backend Selector
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "PocketBase & Trailbase SQL Sync",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Hybrid Cloud Database & SQL Console (pocketbase.io & trailbase.io)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        DatabaseBackendType.entries.forEach { type ->
                            FilterChip(
                                selected = state.backendType == type,
                                onClick = { vm.selectBackend(type) },
                                label = { Text(type.displayName) }
                            )
                        }
                    }
                }
            }

            if (!isWide) {
                PrimaryTabRow(selectedTabIndex = state.activeTab) {
                    Tab(
                        selected = state.activeTab == 0,
                        onClick = { vm.setActiveTab(0) },
                        text = { Text("Config & Auth") }
                    )
                    Tab(
                        selected = state.activeTab == 1,
                        onClick = { vm.setActiveTab(1) },
                        text = { Text("SQL Console") }
                    )
                    Tab(
                        selected = state.activeTab == 2,
                        onClick = { vm.setActiveTab(2) },
                        text = { Text("Cloud Backup") }
                    )
                }
            }

            // Main Content Area (Tablet Split-Pane or Mobile Tabbed Pane)
            if (isWide) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    // Left Pane: Config, Auth & Sync
                    Column(
                        modifier = Modifier.weight(0.45f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ServerConfigCard(state, vm)
                        AuthCard(state, vm)
                        CloudSyncCard(state, vm)
                    }

                    // Right Pane: SQL Console & Database Inspector
                    Column(
                        modifier = Modifier.weight(0.55f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SqlConsoleCard(state, vm)
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (state.activeTab) {
                        0 -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item { ServerConfigCard(state, vm) }
                            item { AuthCard(state, vm) }
                        }
                        1 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SqlConsoleCard(state, vm)
                        }
                        else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item { CloudSyncCard(state, vm) }
                        }
                    }
                }
            }

            // Status Bar Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.isBusy) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        Icon(
                            imageVector = if (state.isConnected) Icons.Default.CloudDone else Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = if (state.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = state.statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerConfigCard(state: PocketBaseUiState, vm: PocketBaseViewModel) {
    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Server Endpoint",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ClayTextField(
                value = state.serverUrl,
                onValueChange = vm::setServerUrl,
                label = "URL (e.g. http://10.0.2.2:8090 or https://pocketbase.io)",
                modifier = Modifier.fillMaxWidth(),
                testTag = "input_pb_url"
            )

            ClayButton(
                text = "Test Endpoint Connection 📡",
                onClick = vm::testConnection,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_test_pb_conn"
            )
        }
    }
}

@Composable
private fun AuthCard(state: PocketBaseUiState, vm: PocketBaseViewModel) {
    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Cloud User Authentication",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ClayTextField(
                value = state.email,
                onValueChange = vm::setEmail,
                label = "Identity / Email",
                modifier = Modifier.fillMaxWidth(),
                testTag = "input_pb_email"
            )

            ClayTextField(
                value = state.password,
                onValueChange = vm::setPassword,
                label = "Password",
                modifier = Modifier.fillMaxWidth(),
                testTag = "input_pb_password"
            )

            ClayButton(
                text = if (state.isAuthenticated) "Authenticated ✓" else "Log In 🔑",
                onClick = vm::login,
                modifier = Modifier.fillMaxWidth(),
                isPrimary = !state.isAuthenticated,
                testTag = "btn_pb_login"
            )
        }
    }
}

@Composable
private fun SqlConsoleCard(state: PocketBaseUiState, vm: PocketBaseViewModel) {
    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "SQL Database Console",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                ClayButton(
                    text = "Provision Schema 🛠️",
                    onClick = vm::initializeCloudSchema,
                    testTag = "btn_init_schema"
                )
            }

            ClayTextField(
                value = state.sqlQueryText,
                onValueChange = vm::setSqlQueryText,
                label = "SQL Query (SELECT, INSERT, UPDATE, CREATE TABLE)",
                modifier = Modifier.fillMaxWidth(),
                testTag = "input_sql_query"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ClayButton(
                    text = "SELECT * FROM accounts",
                    onClick = { vm.setSqlQueryText("SELECT * FROM accounts;") }
                )
                ClayButton(
                    text = "SELECT * FROM journal_entries",
                    onClick = { vm.setSqlQueryText("SELECT * FROM journal_entries;") }
                )
            }

            ClayButton(
                text = "Execute SQL Query ▶",
                onClick = vm::runSqlQuery,
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true,
                testTag = "btn_run_sql"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Query Results Inspector
            state.lastQueryResult?.let { res ->
                Text(
                    text = "Results (${res.rowCount} rows, ${res.executionTimeMs}ms):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            res.columns.forEach { col ->
                                Text(
                                    text = col,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    items(res.rows) { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            row.forEach { cell ->
                                Text(
                                    text = cell,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CloudSyncCard(state: PocketBaseUiState, vm: PocketBaseViewModel) {
    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Room SQLite to Cloud Backup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Export local Room database transactions and forensic audit states to ${state.backendType.displayName}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ClayButton(
                text = "Sync Local Ledgers to Cloud ☁️",
                onClick = vm::syncLocalDatabase,
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true,
                testTag = "btn_pb_sync"
            )
        }
    }
}
