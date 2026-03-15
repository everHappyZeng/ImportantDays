package com.example.importantdays.presentation.contacts

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.importantdays.util.ContactInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportContactsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ImportContactsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.loadContacts()
        }
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            viewModel.loadContacts()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("导入通讯录生日") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.contacts.isNotEmpty()) {
                        TextButton(onClick = { viewModel.selectAll() }) {
                            Text("全选")
                        }
                        TextButton(onClick = { viewModel.deselectAll() }) {
                            Text("取消")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !hasPermission -> {
                    // 需要权限
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ImportContacts,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "需要读取通讯录权限",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "用于读取联系人的生日信息",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        ) {
                            Text("授权")
                        }
                    }
                }

                uiState.isLoading -> {
                    // 加载中
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("正在读取通讯录...")
                    }
                }

                uiState.errorMessage != null -> {
                    // 错误
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "未知错误",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadContacts() }) {
                            Text("重试")
                        }
                    }
                }

                uiState.contacts.isEmpty() -> {
                    // 空结果
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "通讯录中没有找到生日信息",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "请先在通讯录中添加联系人的生日",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    // 联系人列表
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 统计和操作栏
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "找到 ${uiState.totalCount} 个有生日的联系人",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "已选择 ${uiState.selectedContacts.size} 个",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.importSelectedContacts() },
                                    enabled = uiState.selectedContacts.isNotEmpty() && !uiState.isImporting,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (uiState.isImporting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.height(20.dp).width(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text("导入选中联系人")
                                }
                            }
                        }

                        // 联系人列表
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.contacts, key = { it.id }) { contact ->
                                ContactItem(
                                    contact = contact,
                                    isSelected = uiState.selectedContacts.contains(contact.id),
                                    isLunar = uiState.lunarContacts.contains(contact.id),
                                    onSelectionToggle = { viewModel.toggleContactSelection(contact.id) },
                                    onLunarToggle = { viewModel.toggleLunarForContact(contact.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 导入完成对话框
    if (uiState.isImportComplete) {
        AlertDialog(
            onDismissRequest = { 
                viewModel.resetImportState()
                onNavigateBack()
            },
            title = { Text("导入完成") },
            text = {
                Column {
                    if (uiState.importSuccessCount > 0) {
                        Text("✅ 成功导入 ${uiState.importSuccessCount} 个生日")
                    }
                    if (uiState.importSkippedCount > 0) {
                        Text("⏭️ 跳过 ${uiState.importSkippedCount} 个")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { 
                    viewModel.resetImportState()
                    onNavigateBack()
                }) {
                    Text("完成")
                }
            }
        )
    }
}

@Composable
private fun ContactItem(
    contact: ContactInfo,
    isSelected: Boolean,
    isLunar: Boolean,
    onSelectionToggle: () -> Unit,
    onLunarToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionToggle() }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                contact.birthday?.let { date ->
                    Text(
                        text = "生日: ${date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 农历开关
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "农历",
                    style = MaterialTheme.typography.bodySmall
                )
                Switch(
                    checked = isLunar,
                    onCheckedChange = { onLunarToggle() }
                )
            }
        }
    }
}
