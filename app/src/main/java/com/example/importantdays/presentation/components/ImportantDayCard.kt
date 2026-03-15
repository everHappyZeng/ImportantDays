package com.example.importantdays.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.DayType
import com.example.importantdays.data.model.DateType
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

    // 根据类型显示不同的标签
    val typeLabel = when (day.dayType) {
        DayType.ONE_TIME -> "一次性"
        DayType.YEARLY_REPEAT -> "每年公历"
        DayType.LUNAR_YEARLY_REPEAT -> "每年农历"
    }

    // 动态获取日期类型文本
    val dateTypeLabel = if (day.dateType == DateType.LUNAR) {
        day.getLunarDateText()
    } else ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // 标签行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 关联人员标签
                    if (ownerName != null) {
                        AssistChip(
                            onClick = {},
                            label = { Text(ownerName, style = MaterialTheme.typography.labelSmall) },
                            enabled = false,
                            colors = AssistChipDefaults.assistChipColors(
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    
                    // 类型标签
                    AssistChip(
                        onClick = {},
                        label = { 
                            Text(
                                if (dateTypeLabel.isNotEmpty()) "$typeLabel ($dateTypeLabel)" else typeLabel,
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        enabled = false,
                        colors = AssistChipDefaults.assistChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 标题
                Text(
                    text = day.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                // 描述
                if (day.description.isNotEmpty()) {
                    Text(
                        text = day.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 日期和倒计时
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 日期
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // 倒计时标签
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = countdownText,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                // 提醒状态
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (day.reminderEnabled) {
                            Icons.Outlined.Notifications
                        } else {
                            Icons.Outlined.NotificationsOff
                        },
                        contentDescription = if (day.reminderEnabled) "提醒已开启" else "提醒未开启",
                        modifier = Modifier.size(16.dp),
                        tint = if (day.reminderEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        text = if (day.reminderEnabled) "提醒已开启" else "提醒未开启",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 收藏按钮
            val favoriteColor by animateColorAsState(
                targetValue = if (day.isFavorite) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(300),
                label = "favoriteColor"
            )
            
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
                    tint = favoriteColor
                )
            }
        }
    }
}
