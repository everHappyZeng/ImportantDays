# 提醒功能说明

## 功能概述

ImportantDays 应用现在支持完整的提醒功能，可以在重要日期到来前提醒用户。

## 已实现的功能

### 1. 本机通知提醒 ✅
- 使用 Android 系统通知
- 支持后台运行
- 使用 WorkManager 确保可靠性

### 2. 提醒设置
- **提醒开关**: 为每个事件启用/禁用提醒
- **提前天数**: 设置提前多少天收到提醒
- **精确时间**: 如果启用了时间设置，提醒会在指定时间发送
- **默认时间**: 如果未设置时间，默认在上午9:00发送提醒

### 3. 通知渠道
- **APP/SYSTEM**: 本机通知（已实现）
- **SMS**: 短信提醒（接口已预留）
- **WECHAT**: 微信公众号提醒（接口已预留）

## 使用方法

### 创建带提醒的事件

1. 点击"Add Day"按钮
2. 填写事件信息（标题、日期等）
3. 打开"Enable Reminder"开关
4. 设置"Remind me (days before)"天数
5. （可选）打开"Set Specific Time"设置精确提醒时间
6. 点击"Save"

### 测试提醒功能

在 Profile 页面有两个测试按钮：

1. **Send Test Notification**: 立即发送测试通知
2. **Schedule Test Reminder (1 min)**: 安排1分钟后的测试提醒

## 技术实现

### 核心组件

1. **ReminderWorker**: 后台任务执行器
   - 在指定时间发送通知
   - 支持多种通知渠道

2. **ReminderScheduler**: 提醒调度管理器
   - 计算提醒时间
   - 使用 WorkManager 安排任务
   - 支持取消提醒

3. **NotificationHelper**: 通知辅助类
   - 创建通知渠道
   - 发送系统通知

4. **NotificationService 接口**: 外部服务接口
   - SmsNotificationService: 短信服务
   - WeChatNotificationService: 微信服务

### 数据流程

```
用户保存事件 
  → SaveImportantDayUseCase 
  → ReminderScheduler.scheduleReminder()
  → WorkManager 安排任务
  → 到达提醒时间
  → ReminderWorker 执行
  → 发送通知
```

## 待实现功能（需要后端支持）

### 短信提醒

**接口位置**: `SmsNotificationService.kt`

**需要实现**:
1. 创建后端 API 接口
2. 在 `SmsNotificationService.sendNotification()` 中调用 API
3. 在用户设置中添加手机号码配置

**示例代码**:
```kotlin
override suspend fun sendNotification(recipient: String, title: String, message: String): Boolean {
    val response = apiClient.post("https://your-backend.com/api/sms") {
        json {
            "phone" to recipient
            "title" to title
            "message" to message
        }
    }
    return response.isSuccessful
}
```

### 微信公众号提醒

**接口位置**: `WeChatNotificationService.kt`

**需要实现**:
1. 创建后端 API 接口
2. 配置微信公众号模板消息
3. 在 `WeChatNotificationService.sendNotification()` 中调用 API
4. 在用户设置中添加微信 OpenID 配置

**示例代码**:
```kotlin
override suspend fun sendNotification(recipient: String, title: String, message: String): Boolean {
    val response = apiClient.post("https://your-backend.com/api/wechat") {
        json {
            "openid" to recipient
            "title" to title
            "message" to message
        }
    }
    return response.isSuccessful
}
```

## 注意事项

1. **权限**: 应用需要 `POST_NOTIFICATIONS` 权限（Android 13+）
2. **电池优化**: 某些设备可能需要关闭电池优化才能确保提醒准时
3. **WorkManager**: 使用 WorkManager 确保即使应用关闭也能发送提醒
4. **时区**: 所有时间计算使用系统默认时区

## 故障排查

### 提醒没有发送
1. 检查通知权限是否授予
2. 检查提醒是否启用
3. 检查提醒时间是否已过
4. 查看 Logcat 日志

### 测试提醒
使用 Profile 页面的测试按钮验证功能是否正常。
