package ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.converter

import androidx.room.TypeConverter
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat

class BookConverters {
    @TypeConverter
    fun fromBookFormat(format: BookFormat): String {
        return format.name
    }

    @TypeConverter
    fun toBookFormat(format: String): BookFormat {
        return BookFormat.valueOf(format)
    }
}

