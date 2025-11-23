package ru.kazan.itis.bikmukhametov.feature.reading.api.data.datasource.local

import ru.kazan.itis.bikmukhametov.util.enum.BookFormat


interface BookFileDataSource {

    suspend fun readBookFile(filePath: String, format: BookFormat): String
}