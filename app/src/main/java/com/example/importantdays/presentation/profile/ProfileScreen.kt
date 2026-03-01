package com.example.importantdays.presentation.profile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.util.NotificationHelper
import com.example.importantdays.util.ReminderChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToPersons: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.sendTestNotification(
                context,
                "测试通知",
                "这是来自重要日子应用的测试通知！"
            )
            snackbarMessage = "测试通知已发送！"
            showSnackbar = true
        } else {
            snackbarMessage = "通知权限被拒绝"
            showSnackbar = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("个人中心") }
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("确定")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "调试工具",
                style = MaterialTheme.typography.headlineMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "通知测试",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "测试通知系统，确保其在您的设备上正常工作。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onNavigateToPersons,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("管理人员")
                    }

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                NotificationHelper.sendTestNotification(
                                    context,
                                    "测试通知",
                                    "这是来自重要日子应用的测试通知！"
                                )
                                snackbarMessage = "测试通知已发送！"
                                showSnackbar = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("发送测试通知")
                    }

                    Button(
                        onClick = {
                            val app = context.applicationContext as ImportantDaysApplication
                            CoroutineScope(Dispatchers.IO).launch {
                                ReminderChecker.checkReminders(context, app.container.repository)
                            }
                            snackbarMessage = "已立即检查一次提醒！"
                            showSnackbar = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("立即检查提醒")
                    }
                }
            }
        }
    }
}
