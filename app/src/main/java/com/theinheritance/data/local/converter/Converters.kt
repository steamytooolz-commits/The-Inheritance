package com.theinheritance.data.local.converter

import androidx.room.TypeConverter

class Converters {
    @TypeConverter fun fromList(list: List<Long>?): String = list?.joinToString(",") ?: ""
    @TypeConverter fun toList(raw: String?): List<Long> =
        if (raw.isNullOrBlank()) emptyList() else raw.split(",").mapNotNull { it.toLongOrNull() }
}
