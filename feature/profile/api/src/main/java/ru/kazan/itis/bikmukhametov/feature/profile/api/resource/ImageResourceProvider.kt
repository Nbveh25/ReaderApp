package ru.kazan.itis.bikmukhametov.feature.profile.api.resource

import java.io.InputStream

/**
 * Провайдер для работы с ресурсами изображений.
 * Инкапсулирует работу с системными ресурсами для чтения изображений.
 */
interface ImageResourceProvider {
    /**
     * Открывает InputStream для чтения данных по URI.
     * 
     * @param uriString Строковое представление URI ресурса
     * @return InputStream или null, если не удалось открыть
     */
    fun openInputStream(uriString: String): InputStream?
    
    /**
     * Получает имя файла из URI.
     * 
     * @param uriString Строковое представление URI ресурса
     * @return Имя файла или null, если не удалось определить
     */
    fun getFileName(uriString: String): String?
}

