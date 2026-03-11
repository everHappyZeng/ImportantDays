package com.example.importantdays.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.importantdays.data.local.Converters

@Entity(tableName = "persons")
@TypeConverters(Converters::class)
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatar: String? = null,
    val notes: String = "",
    val hobbies: List<String> = listOf(),
    val createdAt: Long = System.currentTimeMillis()
)
