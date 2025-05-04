package com.example.waterme.debug

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

@Composable
fun DebugScreen(
    context: Context,
    onSendTestNotification: () -> Unit,
    onScheduleWorker: () -> Unit
) {
    val workManager = remember { WorkManager.getInstance(context) }
    val workInfos = remember { mutableStateListOf<WorkInfo>() }

    // Live tracking of WorkManager jobs with tag "WaterReminder"
    LaunchedEffect(Unit) {
        workManager.getWorkInfosByTagLiveData("WaterReminder")
            .observeForever { infos ->
                workInfos.clear()
                workInfos.addAll(infos)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🔧 Debug Panel", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = onSendTestNotification) {
            Text("Send Test Notification")
        }

        Button(onClick = onScheduleWorker) {
            Text("Schedule WaterReminderWorker")
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        Text("Notification Permission: ${if (permissionGranted) "✅ Granted" else "❌ Denied"}")

        Divider()

        Text("WorkManager Jobs:", style = MaterialTheme.typography.titleMedium)
        if (workInfos.isEmpty()) {
            Text("No jobs found.")
        } else {
            workInfos.forEach {
                Text(
                    "- ID: ${it.id}\n  State: ${it.state.name}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
