package com.urmilabs.shield.ui.screens

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.urmilabs.shield.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onPermissionsGranted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val audioGranted by viewModel.audioGranted.collectAsState()
    val overlayGranted by viewModel.overlayGranted.collectAsState()
    val roleGranted by viewModel.roleGranted.collectAsState()

    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        viewModel.onAudioPermissionResult(granted)
    }

    val overlayLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.onOverlayPermissionResult()
    }

    val roleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.onRolePermissionResult()
        if (viewModel.allPermissionsGranted) {
            viewModel.onOnboardingComplete()
            onPermissionsGranted()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissions()
        if (viewModel.allPermissionsGranted) {
            viewModel.onOnboardingComplete()
            onPermissionsGranted()
        }
    }

    val currentState = when {
        !audioGranted -> OnboardingStep.Audio
        !overlayGranted -> OnboardingStep.Overlay
        !roleGranted -> OnboardingStep.Role
        else -> OnboardingStep.Done
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                (slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))).togetherWith(
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500)))
            },
            label = "onboarding_transition"
        ) { step ->
            when (step) {
                OnboardingStep.Audio -> {
                    OnboardingPage(
                        icon = Icons.Default.Mic,
                        title = "Listen for Scams",
                        description = stringResource(R.string.mic_permission_desc),
                        buttonText = stringResource(R.string.allow_microphone),
                        onButtonClick = { audioLauncher.launch(android.Manifest.permission.RECORD_AUDIO) },
                        buttonDesc = context.getString(R.string.desc_allow_mic)
                    )
                }
                OnboardingStep.Overlay -> {
                    OnboardingPage(
                        icon = Icons.Default.Layers,
                        title = "Display Warnings",
                        description = stringResource(R.string.overlay_permission_desc),
                        buttonText = stringResource(R.string.allow_overlay),
                        onButtonClick = {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                            overlayLauncher.launch(intent)
                        },
                        buttonDesc = context.getString(R.string.desc_allow_overlay)
                    )
                }
                OnboardingStep.Role -> {
                    OnboardingPage(
                        icon = Icons.Default.Call,
                        title = "Screen Calls",
                        description = stringResource(R.string.role_permission_desc),
                        buttonText = stringResource(R.string.set_default_caller_id_app),
                        onButtonClick = {
                            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
                            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                            roleLauncher.launch(intent)
                        },
                        buttonDesc = context.getString(R.string.desc_set_caller_id)
                    )
                }
                OnboardingStep.Done -> {
                    OnboardingPage(
                        icon = Icons.Default.CheckCircle,
                        title = "You're Protected!",
                        description = stringResource(R.string.all_permissions_granted),
                        buttonText = stringResource(R.string.start_shield),
                        onButtonClick = {
                            viewModel.onOnboardingComplete()
                            onPermissionsGranted()
                        },
                        buttonDesc = context.getString(R.string.desc_start_shield)
                    )
                }
            }
        }
    }
}

enum class OnboardingStep {
    Audio, Overlay, Role, Done
}

@Composable
fun OnboardingPage(
    icon: ImageVector,
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    buttonDesc: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Hero Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Text Content
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(2f))

        // Action Button
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .semantics { contentDescription = buttonDesc },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
