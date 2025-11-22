package ru.kazan.itis.bikmukhametov.util.enum

enum class BookFormat(val extension: String, val mimeType: String) {
    PDF(".pdf", "application/pdf"),
    EPUB(".epub", "application/epub+zip"),
    TXT(".txt", "text/plain");

    companion object {

        fun fromExtension(extension: String): BookFormat? {
            return values().find { it.extension.equals(extension, ignoreCase = true) }
        }

        fun fromFileName(fileName: String): BookFormat? {
            val extension = fileName.substringAfterLast('.', "")
            return if (extension.isNotEmpty()) {
                fromExtension(".$extension")
            } else {
                null
            }
        }
    }
}