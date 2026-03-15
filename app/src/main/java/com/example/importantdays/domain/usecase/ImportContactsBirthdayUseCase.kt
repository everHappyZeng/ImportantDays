package com.example.importantdays.domain.usecase

import android.content.Context
import com.example.importantdays.data.model.DateType
import com.example.importantdays.data.model.DayType
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.repository.ImportantDayRepository
import com.example.importantdays.domain.repository.PersonRepository
import com.example.importantdays.util.ContactInfo
import com.example.importantdays.util.ContactsReader

/**
 * 导入通讯录生日的 UseCase
 */
class ImportContactsBirthdayUseCase(
    private val context: Context,
    private val personRepository: PersonRepository,
    private val importantDayRepository: ImportantDayRepository,
    private val saveImportantDayUseCase: SaveImportantDayUseCase
) {
    /**
     * 读取通讯录中有生日的联系人
     */
    fun readContacts(): List<ContactInfo> {
        return ContactsReader.readContactsWithBirthday(context)
    }

    /**
     * 导入单个联系人
     * @param contact 通讯录联系人
     * @param isLunar 生日是否为农历
     * @return 导入结果
     */
    suspend fun importContact(contact: ContactInfo, isLunar: Boolean): ImportResult {
        // 检查是否已存在同名联系人
        var existingPersonId: Long? = null
        personRepository.getAllPersons().collect { persons ->
            existingPersonId = persons.find { it.name == contact.name }?.id
        }

        val personId = existingPersonId ?: run {
            // 创建新联系人
            val newPerson = Person(name = contact.name)
            personRepository.insertPerson(newPerson)
        }

        // 创建重要日子（生日）
        val dayType = if (isLunar) DayType.LUNAR_YEARLY_REPEAT else DayType.YEARLY_REPEAT
        val dateType = if (isLunar) DateType.LUNAR else DateType.SOLAR

        // 如果是农历，需要存储农历日期
        var lunarMonth: Int? = null
        var lunarDay: Int? = null

        if (isLunar && contact.birthday != null) {
            // 从公历反推农历
            val lunarInfo = ContactsReader.solarToLunar(contact.birthday)
            if (lunarInfo != null) {
                lunarMonth = lunarInfo.first
                lunarDay = lunarInfo.second
            }
        }

        val importantDay = ImportantDay(
            title = "${contact.name}的生日",
            description = "从通讯录导入",
            date = contact.birthday ?: return ImportResult.Skiped("无生日日期"),
            dayType = dayType,
            dateType = dateType,
            lunarMonth = lunarMonth,
            lunarDay = lunarDay,
            personId = personId
        )

        saveImportantDayUseCase(importantDay)

        return ImportResult.Success(contact.name)
    }

    /**
     * 批量导入通讯录
     */
    suspend fun importAllContacts(contacts: List<ContactInfo>, isLunarMap: Map<String, Boolean>): List<ImportResult> {
        return contacts.map { contact ->
            val isLunar = isLunarMap[contact.id] ?: false
            importContact(contact, isLunar)
        }
    }
}

sealed class ImportResult {
    data class Success(val name: String) : ImportResult()
    data class Skiped(val reason: String) : ImportResult()
    data class Error(val message: String) : ImportResult()
}
