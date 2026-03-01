package com.example.importantdays.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.presentation.components.LoadingContent
import com.example.importantdays.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    dayId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val viewModel = DetailViewModel(
        dayId = dayId,
        repository = app.container.repository,
        deleteImportantDayUseCase = app.container.deleteImportantDayUseCase,
        toggleFavoriteUseCase = app.container.toggleFavoriteUseCase
    )

    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is DetailUiState.Success -> {
                            IconButton(onClick = { viewModel.toggleFavorite() }) {
                                Icon(
                                    imageVector = if (state.day.isFavorite) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.Filled.FavoriteBorder
                                    },
                                    contentDescription = "切换收藏"
                                )
                            }
                            IconButton(onClick = { onNavigateToEdit(dayId) }) {
                                Icon(Icons.Default.Edit, "编辑")
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, "删除")
                            }
                        }
                        else -> {}
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }
            is DetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(state.message)
                }
            }
            is DetailUiState.Success -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = state.day.title,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            if (state.day.description.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.day.description,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailInfoRow("日期", DateUtils.formatDateTime(state.day.date, state.day.time))
                            DetailInfoRow("类型", state.day.dayType.name.replace("_", " "))
                            DetailInfoRow("下次日期", DateUtils.formatDateReadable(state.day.nextOccurrence))
                            DetailInfoRow("距今天数", DateUtils.formatDaysUntil(state.day.daysUntil))
                            DetailInfoRow("提醒", if (state.day.reminderEnabled) {
                                "已开启（提前 ${state.day.reminderDaysBefore} 天）"
                            } else {
                                "已关闭"
                            })
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("删除日子") },
                text = { Text("确定要删除这个日子吗？") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteDay(onNavigateBack)
                        showDeleteDialog = false
                    }) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
