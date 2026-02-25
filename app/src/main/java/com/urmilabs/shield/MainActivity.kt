package com.urmilabs.shield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.urmilabs.shield.config.ShieldRemoteConfig
import com.urmilabs.shield.ui.ShieldNavHost
import com.urmilabs.shield.ui.theme.UrmiShieldTheme
import com.urmilabs.shield.worker.WorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var remoteConfig: ShieldRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Schedule the worker off the main thread using RemoteConfig interval
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                WorkerScheduler.scheduleScamPatternUpdates(
                    this@MainActivity,
                    remoteConfig.patternUpdateIntervalHours
                )
            } catch (e: Exception) {
                android.util.Log.w("MainActivity", "Worker scheduling failed", e)
            }
        }

        setContent {
            UrmiShieldTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    ShieldNavHost(navController = navController)
                }
            }
        }
    }
}
