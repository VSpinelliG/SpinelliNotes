package com.example.spinellinotes.model

import java.util.*

data class Note(val id: Int, var title: String = "", val resume: String,
                val description: String, val background:Int,
                val notifyDateTime: Calendar, val notify: String,
                val createDate: Date = Date()

)