package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.resource

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import ru.kazan.itis.bikmukhametov.feature.profile.api.resource.ImageResourceProvider
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация ImageResourceProvider для работы с ContentResolver.
 */
@Singleton
class ImageResourceProviderImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ImageResourceProvider {

    override fun openInputStream(uriString: String): InputStream? {
        return try {
            val uri = Uri.parse(uriString)
            contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            null
        }
    }

    override fun getFileName(uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            var name: String? = null
            
            // Для content:// URI пытаемся получить имя через ContentResolver
            if (uri.scheme == "content") {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            name = cursor.getString(nameIndex)
                        }
                    }
                }
            }
            
            // Если не получили имя через ContentResolver, используем lastPathSegment
            name ?: uri.lastPathSegment
        } catch (e: Exception) {
            null
        }
    }
}

