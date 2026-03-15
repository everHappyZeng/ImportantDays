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
import com.example.importantdays.data.model.DateType
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

            // 公历/农历切换
            Text("日期类型", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.dateType == DateType.SOLAR,
                    onClick = { viewModel.onDateTypeChange(DateType.SOLAR) },
                    label = { Text("公历") }
                )
                FilterChip(
                    selected = uiState.dateType == DateType.LUNAR,
                    onClick = { 
                        viewModel.onDateTypeChange(DateType.LUNAR)
                        // 默认选择农历正月
                        if (uiState.lunarMonth == null) {
                            viewModel.onLunarMonthChange(1)
                            viewModel.onLunarDayChange(1)
                        }
                    },
                    label = { Text("农历") }
                )
            }

            // 农历日期选择
            if (uiState.dateType == DateType.LUNAR) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("选择农历日期", style = MaterialTheme.typography.titleSmall)
                        
                        // 农历月份选择
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("月份：")
                            var monthExpanded by remember { mutableStateOf(false) }
                            val months = listOf("正月", "二月", "三月", "四月", "五月", "六月", 
                                              "七月", "八月", "九月", "十月", "冬月", "腊月")
                            ExposedDropdownMenuBox(
                                expanded = monthExpanded,
                                onExpandedChange = { monthExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = months.getOrElse(uiState.lunarMonth ?: 1 - 1) { "正月" },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = monthExpanded,
                                    onDismissRequest = { monthExpanded = false }
                                ) {
                                    months.forEachIndexed { index, month ->
                                        DropdownMenuItem(
                                            text = { Text(month) },
                                            onClick = {
                                                viewModel.onLunarMonthChange(index + 1)
                                                monthExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // 农历日期选择
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("日期：")
                            var dayExpanded by remember { mutableStateOf(false) }
                            val days = (1..30).map { 
                                listOf("初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
                                       "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                                       "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十")[it - 1]
                            }
                            ExposedDropdownMenuBox(
                                expanded = dayExpanded,
                                onExpandedChange = { dayExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = days.getOrElse((uiState.lunarDay ?: 1) - 1) { "初一" },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = dayExpanded,
                                    onDismissRequest = { dayExpanded = false }
                                ) {
                                    days.forEachIndexed { index, day ->
                                        DropdownMenuItem(
                                            text = { Text(day) },
                                            onClick = {
                                                viewModel.onLunarDayChange(index + 1)
                                                dayExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // 闰月开关
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("闰月")
                            Switch(
                                checked = uiState.isLunarLeapMonth,
                                onCheckedChange = viewModel::onLunarLeapMonthChange
                            )
                        }

                        // 显示农历预览
                        if (uiState.lunarMonth != null && uiState.lunarDay != null) {
                            Text(
                                text = "预览：农历${DateUtils.formatLunarDate(uiState.lunarMonth, uiState.lunarDay, uiState.isLunarLeapMonth)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Text("重复规则", style = MaterialTheme.typography.titleSmall)
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
                    label = { Text("每年公历") }
                )
                FilterChip(
                    selected = uiState.dayType == DayType.LUNAR_YEARLY_REPEAT,
                    onClick = { viewModel.onDayTypeChange(DayType.LUNAR_YEARLY_REPEAT) },
                    label = { Text("每年农历") }
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
