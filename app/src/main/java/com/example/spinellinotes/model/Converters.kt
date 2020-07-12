package com.example.spinellinotes.model

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun toCalendar(value: Long?): Calendar? {
        val calendar = Calendar.getInstance()
        if (value != null) calendar.timeInMillis = value
        return calendar
    }

    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? {
        return calendar?.time?.time
    }

    @TypeConverter
    fun toDate(value: Long): Date {
        return value.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time.toLong()
    }
}