package com.example.spinellinotes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "note")
class Note(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "resume") val resume: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "background") val background:Int,
    @ColumnInfo(name = "notify_date_time") val notifyDateTime: Calendar,
    @ColumnInfo(name = "has_value_date") val hasValueDate: Boolean,
    @ColumnInfo(name = "has_value_time") val hasValueTime: Boolean,
    @ColumnInfo(name = "notify") val notify: String?,
    @ColumnInfo(name = "create_date") val createDate: Date = Date()

)