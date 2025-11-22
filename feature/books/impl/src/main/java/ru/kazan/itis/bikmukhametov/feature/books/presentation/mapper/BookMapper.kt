package ru.kazan.itis.bikmukhametov.feature.books.presentation.mapper

import ru.kazan.itis.bikmukhametov.feature.books.api.model.BookModel
import ru.kazan.itis.bikmukhametov.feature.books.presentation.model.BookItem

object BookMapper {
    fun toBookItem(bookModel: BookModel): BookItem {
        return BookItem(
            id = bookModel.id,
            title = bookModel.title,
            author = bookModel.author,
            coverUrl = bookModel.coverUrl,
            format = bookModel.format.name,
            fileUrl = bookModel.fileUrl,
            isDownloaded = bookModel.isDownloaded,
            isAvailableInFirestore = bookModel.isAvailableInFirestore,
            localFilePath = bookModel.localFilePath
        )
    }

    fun toBookItemList(bookModels: List<BookModel>): List<BookItem> {
        return bookModels.map { toBookItem(it) }
    }
}

