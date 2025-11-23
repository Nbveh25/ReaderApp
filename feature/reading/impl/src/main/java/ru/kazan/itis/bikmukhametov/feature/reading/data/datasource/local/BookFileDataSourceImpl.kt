package ru.kazan.itis.bikmukhametov.feature.reading.data.datasource.local

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.datasource.local.BookFileDataSource
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.zip.ZipFile
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document


internal class BookFileDataSourceImpl @Inject constructor() : BookFileDataSource {

    override suspend fun readBookFile(filePath: String, format: BookFormat): String {
        return withContext(Dispatchers.IO) {
            val file = File(filePath)
            if (!file.exists()) {
                throw FileNotFoundException("Файл не найден: $filePath")
            }

            when (format) {
                BookFormat.TXT -> readTxtFile(file)
                BookFormat.PDF -> readPdfFile(file)
                BookFormat.EPUB -> readEpubFile(file)
            }
        }
    }

    private fun readTxtFile(file: File): String {
        return file.readText(charset = Charsets.UTF_8)
    }

    private fun readPdfFile(file: File): String {
        return try {
            FileInputStream(file).use { inputStream ->
                val document = PDDocument.load(inputStream)
                val text = StringBuilder()

                try {
                    val stripper = PDFTextStripper()
                    text.append(stripper.getText(document))
                } finally {
                    document.close()
                }

                text.toString().takeIf { it.isNotBlank() }
                    ?: throw Exception("Не удалось извлечь текст из PDF файла")
            }
        } catch (e: Exception) {
            throw Exception("Ошибка чтения PDF файла: ${e.message}", e)
        }
    }

    private fun readEpubFile(file: File): String {
        return try {
            ZipFile(file).use { zipFile ->
                // Читаем container.xml для определения пути к content.opf
                val containerEntry = zipFile.getEntry("META-INF/container.xml")
                    ?: throw Exception("EPUB файл не содержит container.xml")
                
                val containerXml = zipFile.getInputStream(containerEntry).bufferedReader().use { it.readText() }
                val containerDoc = parseXml(containerXml)
                val rootfileNode = containerDoc.getElementsByTagName("rootfile").item(0)
                    ?: throw Exception("Не найден rootfile в container.xml")
                
                val fullPathAttr = rootfileNode.attributes.getNamedItem("full-path")
                    ?: throw Exception("Не найден атрибут full-path в rootfile")
                val contentOpfPath = fullPathAttr.nodeValue
                
                // Читаем content.opf для получения списка файлов
                val contentOpfEntry = zipFile.getEntry(contentOpfPath)
                    ?: throw Exception("Не найден файл content.opf: $contentOpfPath")
                
                val contentOpfXml = zipFile.getInputStream(contentOpfEntry).bufferedReader().use { it.readText() }
                val contentDoc = parseXml(contentOpfXml)
                
                // Получаем все элементы itemref (ссылки на главы)
                val manifestItems = mutableMapOf<String, String>()
                val itemNodes = contentDoc.getElementsByTagName("item")
                for (i in 0 until itemNodes.length) {
                    val itemNode = itemNodes.item(i)
                    val id = itemNode.attributes.getNamedItem("id")?.nodeValue
                    val href = itemNode.attributes.getNamedItem("href")?.nodeValue
                    val mediaType = itemNode.attributes.getNamedItem("media-type")?.nodeValue
                    
                    if (id != null && href != null && (mediaType == "application/xhtml+xml" || mediaType == "text/html")) {
                        // Вычисляем полный путь к файлу
                        val basePath = contentOpfPath.substringBeforeLast("/")
                        val fullHref = if (basePath.isNotEmpty()) "$basePath/$href" else href
                        manifestItems[id] = fullHref
                    }
                }
                
                // Получаем порядок чтения из spine
                val spineNode = contentDoc.getElementsByTagName("spine").item(0)
                    ?: throw Exception("Не найден элемент spine в content.opf")
                
                val itemrefNodes = spineNode.childNodes
                val text = StringBuilder()
                
                for (i in 0 until itemrefNodes.length) {
                    val node = itemrefNodes.item(i)
                    if (node.nodeName == "itemref") {
                        val idref = node.attributes.getNamedItem("idref")?.nodeValue
                        if (idref != null) {
                            val href = manifestItems[idref]
                            if (href != null) {
                                try {
                                    val chapterEntry = zipFile.getEntry(href)
                                        ?: zipFile.getEntry(href.replace("\\", "/"))
                                    
                                    if (chapterEntry != null) {
                                        zipFile.getInputStream(chapterEntry).bufferedReader().use { reader ->
                                            val htmlContent = reader.readText()
                                            val doc = Jsoup.parse(htmlContent)
                                            val chapterText = doc.text()
                                            if (chapterText.isNotBlank()) {
                                                if (text.isNotEmpty()) {
                                                    text.append("\n\n")
                                                }
                                                text.append(chapterText)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Пропускаем главы, которые не удалось прочитать
                                }
                            }
                        }
                    }
                }
                
                text.toString().takeIf { it.isNotBlank() }
                    ?: throw Exception("Не удалось извлечь текст из EPUB файла")
            }
        } catch (e: Exception) {
            throw Exception("Ошибка чтения EPUB файла: ${e.message}", e)
        }
    }
    
    private fun parseXml(xmlContent: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false
        val builder = factory.newDocumentBuilder()
        return builder.parse(xmlContent.byteInputStream())
    }
}

