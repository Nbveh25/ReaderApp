package ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.converter.BookConverters
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.dao.BookDao
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.entity.BookEntity

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
@TypeConverters(BookConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun bookDao(): BookDao
    
}
