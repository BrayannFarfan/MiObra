package com.example.miobra

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListToString(lista: List<String>): String {
        return lista.joinToString(",")
    }

    @TypeConverter
    fun fromStringToList(data: String): List<String> {
        if (data.isBlank()) return emptyList()
        return data.split(",")
    }
}