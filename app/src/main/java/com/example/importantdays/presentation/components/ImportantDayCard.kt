package com.example.importantdays.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.util.DateUtils

@Composable
fun ImportantDayCard(
    day: ImportantDay,
    ownerName: String?,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateText = if (day.timeEnabled && day.time != null) {
        "${DateUtils.formatDateReadable(day.nextOccurrence)} ${DateUtils.formatTime(day.time, includeSeconds = false)}"
    } else {
        DateUtils.formatDateReadable(day.nextOccurrence)
    }
    val countdownText = DateUtils.formatDaysUntil(day.daysUntil)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(ownerName ?: "未归属") },
                        enabled = false,
                        colors = AssistChipDefaults.assistChipColors()
                    )
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                if (day.dayType.name == "YEARLY_REPEAT") "每年重复" else "一次性"
                            )
                        },
                        enabled = false,
                        colors = AssistChipDefaults.assistChipColors()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = day.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (day.description.isNotEmpty()) {
                    Text(
                        text = day.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = countdownText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (day.reminderEnabled) {
                            Icons.Outlined.Notifications
                        } else {
                            Icons.Outlined.NotificationsOff
                        },
                        contentDescription = null,
                        tint = if (day.reminderEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (day.reminderEnabled) "提醒已开启" else "提醒未开启",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (day.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (day.isFavorite) {
                        "取消收藏"
                    } else {
                        "添加收藏"
                    },
                    tint = if (day.isFavorite) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
