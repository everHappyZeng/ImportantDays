package com.example.importantdays.presentation.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.data.model.DayType
import com.example.importantdays.util.DateUtils
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    dayId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val viewModel = AddEditViewModel(
        dayId = dayId,
        repository = app.container.repository,
        personRepository = app.container.personRepository,
        saveImportantDayUseCase = app.container.saveImportantDayUseCase
    )

    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var personMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "编辑日子" else "添加日子") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage != null
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Box {
                val selectedPersonName = uiState.persons
                    .firstOrNull { it.id == uiState.personId }
                    ?.name ?: "不关联任何人"
                OutlinedTextField(
                    value = selectedPersonName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("关联人员") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    trailingIcon = {
                        TextButton(onClick = { personMenuExpanded = true }) {
                            Text("选择")
                        }
                    }
                )

                DropdownMenu(
                    expanded = personMenuExpanded,
                    onDismissRequest = { personMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("不关联任何人") },
                        onClick = {
                            viewModel.onPersonChange(null)
                            personMenuExpanded = false
                        }
                    )
                    uiState.persons.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                viewModel.onPersonChange(person.id)
                                personMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("日期：${uiState.date}")
            }

            Text("日子类型", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.dayType == DayType.ONE_TIME,
                    onClick = { viewModel.onDayTypeChange(DayType.ONE_TIME) },
                    label = { Text("一次性") }
                )
                FilterChip(
                    selected = uiState.dayType == DayType.YEARLY_REPEAT,
                    onClick = { viewModel.onDayTypeChange(DayType.YEARLY_REPEAT) },
                    label = { Text("每年重复") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("开启提醒")
                Switch(
                    checked = uiState.reminderEnabled,
                    onCheckedChange = viewModel::onReminderEnabledChange
                )
            }

            if (uiState.reminderEnabled) {
                OutlinedTextField(
                    value = uiState.reminderDaysBefore.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { days ->
                            viewModel.onReminderDaysBeforeChange(days)
                        }
                    },
                    label = { Text("提前提醒（天数）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("设置具体时间")
                Switch(
                    checked = uiState.timeEnabled,
                    onCheckedChange = viewModel::onTimeEnabledChange
                )
            }

            if (uiState.timeEnabled) {
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("时间：${DateUtils.formatTime(uiState.time, includeSeconds = true)}")
                }
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.saveDay(onNavigateBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isEditMode) "更新" else "保存")
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                selectedDate = uiState.date,
                onDateSelected = { date ->
                    viewModel.onDateChange(date)
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }

        if (showTimePicker) {
            TimePickerDialog(
                selectedTime = uiState.time,
                onTimeSelected = { time ->
                    viewModel.onTimeChange(time)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                    onDateSelected(date)
                }
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableIntStateOf(selectedTime.hour) }
    var minute by remember { mutableIntStateOf(selectedTime.minute) }
    var second by remember { mutableIntStateOf(selectedTime.second) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择时间") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val timePickerState = rememberTimePickerState(
                    initialHour = hour,
                    initialMinute = minute,
                    is24Hour = true
                )

                TimePicker(state = timePickerState)

                LaunchedEffect(timePickerState.hour, timePickerState.minute) {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                }

                OutlinedTextField(
                    value = second.toString(),
                    onValueChange = { value ->
                        value.toIntOrNull()?.let {
                            if (it in 0..59) {
                                second = it
                            }
                        }
                    },
                    label = { Text("秒") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(LocalTime.of(hour, minute, second))
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
