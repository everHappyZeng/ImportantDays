package com.example.importantdays.presentation.persons

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.model.PersonWithNextDay
import com.example.importantdays.presentation.components.EmptyContent
import com.example.importantdays.presentation.components.LoadingContent
import com.example.importantdays.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val viewModel = PersonsViewModel(
        personRepository = app.container.personRepository
    )
    val uiState by viewModel.uiState.collectAsState()
    var editingPerson by remember { mutableStateOf<Person?>(null) }
    var showEditorDialog by remember { mutableStateOf(false) }
    var deletingPerson by remember { mutableStateOf<Person?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("人员管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingPerson = Person(name = "", notes = "")
                showEditorDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "添加人员")
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is PersonsUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }

            is PersonsUiState.Empty -> {
                EmptyContent(
                    message = "还没有人员。\n点击 + 添加一个。",
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is PersonsUiState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.persons, key = { it.person.id }) { personSummary ->
                        PersonCard(
                            personSummary = personSummary,
                            onEdit = {
                                editingPerson = personSummary.person
                                showEditorDialog = true
                            },
                            onDelete = {
                                deletingPerson = personSummary.person
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditorDialog && editingPerson != null) {
        EditPersonDialog(
            person = editingPerson!!,
            onDismiss = { showEditorDialog = false },
            onConfirm = { person ->
                viewModel.savePerson(person) {
                    showEditorDialog = false
                }
            }
        )
    }

    if (deletingPerson != null) {
        AlertDialog(
            onDismissRequest = { deletingPerson = null },
            title = { Text("删除人员") },
            text = { Text("确定删除 ${deletingPerson?.name} 吗？") },
            confirmButton = {
                TextButton(onClick = {
                    deletingPerson?.let { viewModel.deletePerson(it.id) }
                    deletingPerson = null
                }) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPerson = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun PersonCard(
    personSummary: PersonWithNextDay,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AvatarPreview(
                        avatarUri = personSummary.person.avatar,
                        fallbackText = personSummary.person.name.take(1)
                    )
                    Column {
                        Text(
                            text = personSummary.person.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (!personSummary.person.avatar.isNullOrBlank()) {
                            Text(
                                text = "已设置头像",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            }
            if (personSummary.person.notes.isNotBlank()) {
                Text(
                    text = personSummary.person.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (personSummary.nextDayDate != null && personSummary.nextDayTitle != null) {
                val dateText = DateUtils.formatDateReadable(personSummary.nextDayDate)
                val daysText = personSummary.daysUntil?.let { DateUtils.formatDaysUntil(it) } ?: ""
                Text(
                    text = "最近日子：${personSummary.nextDayTitle}（$dateText）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (daysText.isNotBlank()) {
                    Text(
                        text = daysText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                Text(
                    text = "最近日子：暂无",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EditPersonDialog(
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
        title = { Text(if (person.id == 0L) "添加人员" else "编辑人员") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AvatarPreview(
                        avatarUri = avatarUri.ifBlank { null },
                        fallbackText = name.take(1)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = { picker.launch("image/*") }) {
                            Text("选择头像")
                        }
                        if (avatarUri.isNotBlank()) {
                            TextButton(onClick = { avatarUri = "" }) {
                                Text("移除头像")
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
                    label = { Text("备注（关系）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        person.copy(
                            name = name,
                            notes = notes,
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
private fun AvatarPreview(
    avatarUri: String?,
    fallbackText: String
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
                .size(42.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = fallbackText.ifBlank { "?" },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}
