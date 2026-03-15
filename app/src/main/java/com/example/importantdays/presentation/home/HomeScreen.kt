package com.example.importantdays.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.presentation.components.EmptyContent
import com.example.importantdays.presentation.components.ImportantDayCard
import com.example.importantdays.presentation.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val viewModel = HomeViewModel(
        getUpcomingDaysUseCase = app.container.getUpcomingDaysUseCase,
        toggleFavoriteUseCase = app.container.toggleFavoriteUseCase
    )
    val persons by app.container.personRepository.getAllPersons().collectAsState(initial = emptyList())
    val personNameMap = remember(persons) { persons.associate { it.id to it.name } }

    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "重要日子",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToAddEdit(0L) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("添加日子") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }
            is HomeUiState.Empty -> {
                EmptyContent(
                    message = "暂无即将到来的重要日子。\n点击下方按钮添加第一个！",
                    icon = Icons.Outlined.Celebration,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val groupedDays = linkedMapOf<String, MutableList<ImportantDay>>()
                    state.days.forEach { day ->
                        val ownerLabel = day.personId?.let(personNameMap::get) ?: "未归属"
                        groupedDays.getOrPut(ownerLabel) { mutableListOf() }.add(day)
                    }

                    // 统计卡片
                    item {
                        HomeSummaryCard(
                            totalCount = state.days.size,
                            ownedCount = state.days.count { it.personId != null },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // 分组显示
                    groupedDays.forEach { (ownerLabel, days) ->
                        item(key = "group_$ownerLabel") {
                            GroupHeader(
                                ownerLabel = ownerLabel,
                                count = days.size
                            )
                        }
                        items(days, key = { it.id }) { day ->
                            ImportantDayCard(
                                day = day,
                                ownerName = day.personId?.let(personNameMap::get),
                                onCardClick = { onNavigateToDetail(day.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(day) }
                            )
                        }
                    }
                    
                    // 底部间距
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSummaryCard(
    totalCount: Int,
    ownedCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$totalCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "即将到来",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$ownedCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "已关联人员",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun GroupHeader(
    ownerLabel: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ownerLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = "$count 条",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
