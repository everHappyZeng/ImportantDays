package com.example.importantdays.util

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.example.importantdays.data.model.DateType
import com.github.szhangb.lunar.LunarModule
import java.time.LocalDate

/**
 * 通讯录联系人信息
 */
data class ContactInfo(
    val id: String,
    val name: String,
    val birthday: LocalDate?,       // 原始公历生日
    val isLunarBirthday: Boolean   // 通讯录中的生日是否为农历
)

/**
 * 通讯录读取工具类
 */
object ContactsReader {

    /**
     * 读取所有有生日的联系人
     */
    fun readContactsWithBirthday(context: Context): List<ContactInfo> {
        val contacts = mutableListOf<ContactInfo>()
        val contentResolver = context.contentResolver

        // 查询有生日的联系人
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        // 只获取有生日的联系人
        val selection = "${ContactsContract.Contacts.BIRTHDAY} IS NOT NULL"
        val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val birthdayIndex = cursor.getColumnIndex(ContactsContract.Contacts.BIRTHDAY)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex) ?: continue
                val name = cursor.getString(nameIndex) ?: continue
                val birthdayString = cursor.getString(birthdayIndex) ?: continue

                // 生日格式: YYYY-MM-DD
                try {
                    val birthday = LocalDate.parse(birthdayString)
                    contacts.add(
                        ContactInfo(
                            id = id,
                            name = name,
                            birthday = birthday,
                            isLunarBirthday = false // Android 通讯录默认是公历
                        )
                    )
                } catch (e: Exception) {
                    // 跳过无效日期
                }
            }
        }

        // 也读取 RawContacts 中的生日（可能包含农历）
        readRawContactsBirthdays(contentResolver, contacts)

        return contacts.distinctBy { it.name }
    }

    /**
     * 读取 RawContacts 中的生日信息（可能包含农历）
     */
    private fun readRawContactsBirthdays(
        contentResolver: ContentResolver,
        existingContacts: MutableList<ContactInfo>
    ) {
        val projection = arrayOf(
            ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.RawContacts.BIRTHDAY,
            ContactsContract.RawContacts.BIRTHDAY_SOURCE
        )

        contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            projection,
            "${ContactsContract.RawContacts.BIRTHDAY} IS NOT NULL",
            null,
            null
        )?.use { cursor ->
            val contactIdIndex = cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)
            val birthdayIndex = cursor.getColumnIndex(ContactsContract.RawContacts.BIRTHDAY)
            val sourceIndex = cursor.getColumnIndex(ContactsContract.RawContacts.BIRTHDAY_SOURCE)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIndex) ?: continue
                val name = cursor.getString(nameIndex) ?: continue
                val birthdayString = cursor.getString(birthdayIndex) ?: continue
                val birthdaySource = cursor.getString(sourceIndex) // 可能是 " lunar"

                try {
                    val birthday = LocalDate.parse(birthdayString)
                    val isLunar = birthdaySource?.contains("lunar", ignoreCase = true) == true

                    // 检查是否已存在
                    val existing = existingContacts.find { it.id == contactId }
                    if (existing == null) {
                        existingContacts.add(
                            ContactInfo(
                                id = contactId,
                                name = name,
                                birthday = birthday,
                                isLunarBirthday = isLunar
                            )
                        )
                    }
                } catch (e: Exception) {
                    // 跳过无效日期
                }
            }
        }
    }

    /**
     * 自动识别生日是公历还是农历
     * 逻辑：如果通讯录标记为农历，则为农历
     *      否则尝试转换，如果农历日期有效则可能是农历
     */
    fun detectBirthdayType(
        birthday: LocalDate,
        isMarkedAsLunar: Boolean
    ): DateType {
        if (isMarkedAsLunar) {
            return DateType.LUNAR
        }

        // 尝试检测：如果是闰年日期，可能原来是农历
        // 例如：农历生日对应公历2月29日（但该年非闰年）可能是农历转换来的
        // 这里简化处理：如果通讯录没标记，默认当公历处理
        // 后续用户可以在导入后手动修改
        return DateType.SOLAR
    }

    /**
     * 农历生日转换为公历
     */
    fun lunarToSolar(year: Int, lunarMonth: Int, lunarDay: Int, isLeapMonth: Boolean = false): LocalDate? {
        return try {
            val lunarModule = LunarModule.getInstance()
            val lunar = lunarModule.lunarFromYMD(year, lunarMonth, lunarDay, isLeapMonth)
            lunar?.toSolar()?.let { solar ->
                LocalDate.of(solar.year, solar.month, solar.day)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 公历转农历
     */
    fun solarToLunar(date: LocalDate): Pair<Int, Int>? {
        return try {
            val lunarModule = LunarModule.getInstance()
            val lunar = lunarModule.lunarFromSolar(date.year, date.monthValue, date.dayOfMonth)
            if (lunar != null) {
                Pair(lunar.month, lunar.day)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
