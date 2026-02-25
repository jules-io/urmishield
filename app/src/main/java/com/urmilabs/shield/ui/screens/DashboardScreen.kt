package com.urmilabs.shield.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.urmilabs.shield.R
import com.urmilabs.shield.service.UrmiShieldAccessibilityService
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val totalCount by viewModel.totalScreenedCount.collectAsState()
    val blockedCount by viewModel.blockedCount.collectAsState()

    var isAccessibilityServiceEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    var showDisclosureDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityServiceEnabled = isAccessibilityServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showDisclosureDialog) {
        AlertDialog(
            onDismissRequest = { showDisclosureDialog = false },
            icon = { Icon(Icons.Default.Security, contentDescription = null) },
            title = { Text(stringResource(R.string.prominent_disclosure_title)) },
            text = { Text(stringResource(R.string.prominent_disclosure_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDisclosureDialog = false
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                    modifier = Modifier.semantics { contentDescription = context.getString(R.string.i_agree) }
                ) {
                    Text(stringResource(R.string.i_agree))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisclosureDialog = false }) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "Urmi Shield",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Warning Banner for deepfake protection (Accessibility Service)
            AnimatedVisibility(
                visible = !isAccessibilityServiceEnabled,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = context.getString(R.string.desc_a11y_warning) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.warning_ai_deepfake),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Hero Animated Shield
            HeroShield(isProtected = isAccessibilityServiceEnabled)

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Screened",
                    value = totalCount.toString(),
                    icon = Icons.Default.Search,
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Blocked",
                    value = blockedCount.toString(),
                    icon = Icons.Default.Block,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Primary Action Button
            Button(
                onClick = {
                    if (!isAccessibilityServiceEnabled) {
                        showDisclosureDialog = true
                    } else {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics {
                        contentDescription = if (isAccessibilityServiceEnabled) {
                            context.getString(R.string.desc_accessibility_enabled)
                        } else {
                            context.getString(R.string.desc_tap_to_enable_cognitive)
                        }
                    },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (isAccessibilityServiceEnabled) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAccessibilityServiceEnabled) stringResource(R.string.accessibility_service_enabled) else stringResource(R.string.enable_cognitive_assistance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NavActionCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.system_status),
                    icon = Icons.Default.Info,
                    onClick = { navController.navigate("system_status") }
                )
                NavActionCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.view_history),
                    icon = Icons.Default.History,
                    onClick = { navController.navigate("history") }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NavActionCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.settings),
                    icon = Icons.Default.Settings,
                    onClick = { navController.navigate("settings") }
                )
                NavActionCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.guardian_settings),
                    icon = Icons.Default.Person,
                    onClick = { navController.navigate("guardian_settings") }
                )
            }
        }
    }
}

@Composable
fun HeroShield(isProtected: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by if (isProtected) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    val color = if (isProtected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .size(160.dp)
            .scale(scale)
            .background(color.copy(alpha = 0.1f), shape = RoundedCornerShape(80.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = "Shield Status",
            modifier = Modifier.size(80.dp),
            tint = color
        )
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, color: androidx.compose.ui.graphics.Color) {
    ElevatedCard(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = color
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NavActionCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun isAccessibilityServiceEnabled(context: android.content.Context): Boolean {
    val service = context.packageName + "/" + UrmiShieldAccessibilityService::class.java.canonicalName
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(service, ignoreCase = false) == true
}
