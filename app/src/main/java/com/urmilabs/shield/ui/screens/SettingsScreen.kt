package com.urmilabs.shield.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.urmilabs.shield.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val stallingEnabled by viewModel.stallingEnabled.collectAsState()
    val deepfakeEnabled by viewModel.deepfakeEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Protection Features Section
            Text(
                text = "Protection Features",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.enable_tts_stalling), style = MaterialTheme.typography.bodyLarge) },
                supportingContent = { Text("Delays scammer prompts using AI voice", style = MaterialTheme.typography.bodyMedium) },
                leadingContent = {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                },
                trailingContent = {
                    Switch(
                        checked = stallingEnabled,
                        onCheckedChange = { viewModel.setStalling(it) },
                        modifier = Modifier.semantics {
                            contentDescription = if (stallingEnabled) "TTS Stalling is enabled, tap to disable" else "TTS Stalling is disabled, tap to enable"
                        }
                    )
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.enable_deepfake_detection), style = MaterialTheme.typography.bodyLarge) },
                supportingContent = { Text("Runs audio through local AI model", style = MaterialTheme.typography.bodyMedium) },
                leadingContent = {
                    Icon(Icons.Default.Face, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                },
                trailingContent = {
                    Switch(
                        checked = deepfakeEnabled,
                        onCheckedChange = { viewModel.setDeepfake(it) },
                        modifier = Modifier.semantics {
                            contentDescription = if (deepfakeEnabled) "Deepfake detection is enabled, tap to disable" else "Deepfake detection is disabled, tap to enable"
                        }
                    )
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            // List Management Section
            Text(
                text = "Call Filtering",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.manage_whitelist), style = MaterialTheme.typography.bodyLarge) },
                supportingContent = { Text("Always allow these numbers", style = MaterialTheme.typography.bodyMedium) },
                leadingContent = {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                },
                modifier = Modifier.clickable { navController.navigate("number_list/WHITELIST") },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.manage_blocklist), style = MaterialTheme.typography.bodyLarge) },
                supportingContent = { Text("Always block these numbers", style = MaterialTheme.typography.bodyMedium) },
                leadingContent = {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                },
                modifier = Modifier.clickable { navController.navigate("number_list/BLACKLIST") },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    }
}
