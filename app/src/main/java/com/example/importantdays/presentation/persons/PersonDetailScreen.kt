package com.example.importantdays.presentation.persons

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.domain.model.ActivityType
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.Person
import com.example.importantdays.presentation.components.LoadingContent
import com.example.importantdays.util.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PersonDetailScreen(
    personId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToAddRecord: (Long, Long) -> Unit, // personId, importantDayId
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val viewModel = PersonDetailViewModel(
        personRepository = app.container.personRepository,
        activityRecordRepository = app.container.activityRecordRepository
    )
    val uiState by viewModel.uiState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showHobbiesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(personId) {
        viewModel.loadPerson(personId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("联系人详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    val state = uiState
                    if (state is PersonDetailUiState.Success) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "编辑")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            val state = uiState
            if (state is PersonDetailUiState.Success) {
                FloatingActionButton(onClick = {
                    onNavigateToAddRecord(state.person.id, 0)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "添加记录")
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is PersonDetailUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }

            is PersonDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }
            }

            is PersonDetailUiState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 联系人基本信息
                    item {
                        PersonInfoCard(
                            person = state.person,
                            onEditHobbies = { showHobbiesDialog = true }
                        )
                    }

                    // 重要日子
                    if (state.importantDays.isNotEmpty()) {
                        item {
                            Text(
                                text = "重要日子",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(state.importantDays, key = { it.id }) { day ->
                            ImportantDayCard(
                                day = day,
                                onAddRecord = { onNavigateToAddRecord(state.person.id, day.id) }
                            )
                        }
                    }

                    // 活动记录
                    if (state.activityRecords.isNotEmpty()) {
                        item {
                            Text(
                                text = "送礼/活动记录",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(state.activityRecords, key = { it.id }) { record ->
                            ActivityRecordCard(
                                record = record,
                                onDelete = { viewModel.deleteActivityRecord(record.id) }
                            )
                        }
                    }

                    // 空状态
                    if (state.importantDays.isEmpty() && state.activityRecords.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "暂无记录",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "点击 + 添加第一条记录",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 编辑联系人对话框
    if (showEditDialog && uiState is PersonDetailUiState.Success) {
        EditPersonDialogV2(
            person = (uiState as PersonDetailUiState.Success).person,
            onDismiss = { showEditDialog = false },
            onConfirm = { person ->
                viewModel.updatePerson(person)
                showEditDialog = false
            }
        )
    }

    // 编辑爱好对话框
    if (showHobbiesDialog && uiState is PersonDetailUiState.Success) {
        EditHobbiesDialog(
            hobbies = (uiState as PersonDetailUiState.Success).person.hobbies,
            onDismiss = { showHobbiesDialog = false },
            onConfirm = { hobbies ->
                viewModel.updateHobbies(hobbies)
                showHobbiesDialog = false
            }
        )
    }

    // 删除确认对话框
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("删除联系人") },
            text = { Text("确定删除该联系人吗？这将同时删除所有相关的记录。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePerson { onNavigateBack() }
                }) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonInfoCard(
    person: Person,
    onEditHobbies: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PersonAvatar(avatarUri = person.avatar, name = person.name, size = 64)
                Column {
                    Text(
                        text = person.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (person.notes.isNotBlank()) {
                        Text(
                            text = person.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 爱好标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "爱好",
                    style = MaterialTheme.typography.titleSmall
                )
                IconButton(onClick = onEditHobbies) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑爱好")
                }
            }
            if (person.hobbies.isEmpty()) {
                Text(
                    text = "点击编辑添加爱好",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    person.hobbies.forEach { hobby ->
                        AssistChip(
                            onClick = { },
                            label = { Text(hobby) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportantDayCard(
    day: ImportantDay,
    onAddRecord: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = day.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                val dateText = DateUtils.formatDateReadable(day.date)
                val daysUntil = DateUtils.daysBetween(LocalDate.now(), day.date).let {
                    if (it >= 0) DateUtils.formatDaysUntil(it) else null
                }
                Text(
                    text = "$dateText ${daysUntil?.let { "($it)" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onAddRecord) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "添加记录",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun ActivityRecordCard(
    record: ActivityRecord,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (record.activityType) {
                    ActivityType.GIFT -> Icons.Default.CardGiftcard
                    ActivityType.ACTIVITY -> Icons.Default.LocalActivity
                    ActivityType.OTHER -> Icons.Default.Event
                },
                contentDescription = null,
                tint = when (record.activityType) {
                    ActivityType.GIFT -> MaterialTheme.colorScheme.primary
                    ActivityType.ACTIVITY -> MaterialTheme.colorScheme.tertiary
                    ActivityType.OTHER -> MaterialTheme.colorScheme.secondary
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${DateUtils.formatDateReadable(record.date)} ${record.activityType.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (record.description.isNotBlank()) {
                    Text(
                        text = record.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PersonAvatar(
    avatarUri: String?,
    name: String,
    size: Int = 42
) {
    val context = LocalContext.current
    val bitmap = remember(avatarUri) {
        if (avatarUri.isNullOrBlank()) {
            null
        } else {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(avatarUri))?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }.getOrNull()
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "头像",
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).ifBlank { "?" },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun EditPersonDialogV2(
    person: Person,
    onDismiss: () -> Unit,
    onConfirm: (Person) -> Unit
) {
    var name by remember(person.id) { mutableStateOf(person.name) }
    var notes by remember(person.id) { mutableStateOf(person.notes) }
    var avatarUri by remember(person.id) { mutableStateOf(person.avatar ?: "") }
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        avatarUri = uri?.toString().orEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑联系人") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PersonAvatar(avatarUri = avatarUri.ifBlank { null }, name = name, size = 56)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = { picker.launch("image/*") }) {
                            Text("选择头像")
                        }
                        if (avatarUri.isNotBlank()) {
                            TextButton(onClick = { avatarUri = "" }) {
                                Text("移除")
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("姓名") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        person.copy(
                            name = name.trim(),
                            notes = notes.trim(),
                            avatar = avatarUri.ifBlank { null }
                        )
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun EditHobbiesDialog(
    hobbies: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var hobbyInput by remember { mutableStateOf("") }
    var currentHobbies by remember { mutableStateOf(hobbies.toList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑爱好") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hobbyInput,
                        onValueChange = { hobbyInput = it },
                        label = { Text("添加爱好") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (hobbyInput.isNotBlank() && !currentHobbies.contains(hobbyInput.trim())) {
                                currentHobbies = currentHobbies + hobbyInput.trim()
                                hobbyInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
                if (currentHobbies.isEmpty()) {
                    Text(
                        text = "暂无爱好",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        currentHobbies.forEach { hobby ->
                            AssistChip(
                                onClick = { currentHobbies = currentHobbies - hobby },
                                label = { Text(hobby) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "删除",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(currentHobbies) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
