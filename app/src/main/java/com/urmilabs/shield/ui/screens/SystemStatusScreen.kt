package com.urmilabs.shield.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemStatusScreen(
    navController: NavController,
    viewModel: SystemStatusViewModel = hiltViewModel()
) {
    val isAiModelAvailable by viewModel.isAiModelAvailable.collectAsState()
    val isAccessibilityEnabled = viewModel.isAccessibilityServiceEnabled()
    val lastPatternUpdate = viewModel.getLastPatternUpdateTime()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Status", fontWeight = FontWeight.Bold) },
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

            Text(
                text = "Core Systems",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            StatusItem(
                label = "Cognitive Assistance Service",
                value = if (isAccessibilityEnabled) "Enabled" else "Disabled",
                icon = Icons.Default.Accessibility,
                isActive = isAccessibilityEnabled
            )

            StatusItem(
                label = "AI Deepfake Detection Model",
                value = if (isAiModelAvailable) "Loaded" else "Not Found",
                icon = Icons.Default.Memory,
                isActive = isAiModelAvailable
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Data Updates",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            StatusItem(
                label = "Scam Pattern Definitions",
                value = lastPatternUpdate,
                icon = Icons.Default.Update,
                isActive = true
            )
        }
    }
}

@Composable
fun StatusItem(label: String, value: String, icon: ImageVector, isActive: Boolean) {
    ListItem(
        headlineContent = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        supportingContent = { 
            Text(
                text = value, 
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            ) 
        },
        leadingContent = {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        },
        modifier = Modifier.semantics { contentDescription = "$label: $value" },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
    )
}
