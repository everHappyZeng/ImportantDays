package com.example.importantdays.presentation.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.importantdays.data.model.ActivityType
import com.example.importantdays.domain.model.Person
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    viewModel: ActivityRecordsViewModel,
    persons: List<Person>,
    editRecord: com.example.importantdays.domain.model.ActivityRecord? = null,
    preselectedPersonId: Long? = null,
    preselectedImportantDayId: Long? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var selectedPersonId by remember { mutableStateOf(editRecord?.personId ?: preselectedPersonId ?: 0L) }
    var selectedImportantDayId by remember { mutableStateOf(editRecord?.importantDayId ?: preselectedImportantDayId) }
    var selectedType by remember { mutableStateOf(editRecord?.activityType ?: ActivityType.GIFT) }
    var title by remember { mutableStateOf(editRecord?.title ?: "") }
    var description by remember { mutableStateOf(editRecord?.description ?: "") }
    var date by remember { mutableStateOf(editRecord?.date ?: LocalDate.now()) }
    var notes by remember { mutableStateOf(editRecord?.notes ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }
    var personDropdownExpanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.toEpochDay() * 24 * 60 * 60 * 1000
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editRecord == null) "添加记录" else "编辑记录") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (selectedPersonId > 0 && title.isNotBlank()) {
                                viewModel.saveRecord(
                                    id = editRecord?.id ?: 0,
                                    personId = selectedPersonId,
                                    importantDayId = selectedImportantDayId,
                                    activityType = selectedType,
                                    title = title,
                                    description = description,
                                    date = date,
                                    notes = notes,
                                    onDone = onSaveSuccess
                                )
                            }
                        },
                        enabled = selectedPersonId > 0 && title.isNotBlank()
                    ) {
                        Text("保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 选择联系人
            ExposedDropdownMenuBox(
                expanded = personDropdownExpanded,
                onExpandedChange = { personDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = persons.find { it.id == selectedPersonId }?.name ?: "选择联系人",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("联系人 *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = personDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = personDropdownExpanded,
                    onDismissRequest = { personDropdownExpanded = false }
                ) {
                    persons.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                selectedPersonId = person.id
                                personDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // 记录类型
            Text("记录类型", style = MaterialTheme.typography.labelLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = {
                            Text(
                                when (type) {
                                    ActivityType.GIFT -> "🎁 送礼"
                                    ActivityType.ACTIVITY -> "🎉 活动"
                                    ActivityType.OTHER -> "📝 其他"
                                }
                            )
                        }
                    )
                }
            }

            // 标题
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("标题 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 描述
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // 日期选择
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("选择日期: ${date}")
            }

            // 备注
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                    }
                    showDatePicker = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
