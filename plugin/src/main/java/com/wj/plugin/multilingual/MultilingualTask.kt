package com.wj.plugin.multilingual

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * 翻译插件的任务类
 * @author wujia
 */
open class MultilingualTask : DefaultTask() {
    @get:Input
    val excelFilePath: Property<String> = project.objects.property(String::class.java)

    @get:Input
    val defaultLanguage: Property<String> = project.objects.property(String::class.java)

    @get:Input
    val baselineDir: Property<String> = project.objects.property(String::class.java)

    @TaskAction
    fun generateTranslations() {
        val excelFile = File(excelFilePath.get())
        if (!excelFile.exists()) {
            throw GradleException("==> Excel文件不存在: ${excelFile.absolutePath}")
        }
        // 查找Android项目的res目录
        val resDir = findAndroidResDirectory()
        // 读取默认语言的string.xml文件
        val baselineValuesDir = File(resDir, baselineDir.get())
        if (!baselineValuesDir.exists()) {
            throw GradleException("==> 基准语言目录不存在: ${baselineValuesDir.absolutePath}")
        }

        val defaultStringsFile = File(baselineValuesDir, "strings.xml")
        if (!defaultStringsFile.exists()) {
            throw GradleException("==> 默认语言的strings.xml不存在: ${defaultStringsFile.absolutePath}")
        }

        // 解析默认strings.xml获取键值对
        val defaultStrings = parseStringsXml(defaultStringsFile)
        logger.lifecycle("==> 从${defaultStringsFile.name}读取到${defaultStrings.size}个字符串")

        // 读取并解析Excel文件
        WorkbookFactory.create(excelFile.inputStream()).use { workbook ->
            val sheet = workbook.getSheetAt(0) ?: throw GradleException("Excel中没有工作表")

            // 解析第一行获取语言编码信息
            val headerRow = sheet.getRow(0) ?: throw GradleException("Excel中没有标题行")
            val languageCodes = mutableMapOf<Int, String>() // 列索引 -> 语言编码

            for (col in 0 until headerRow.lastCellNum) {
                val cell = headerRow.getCell(col)?.stringCellValue ?: continue
                val code = cell.split("/").lastOrNull()?.trim()
                if (code != null && code.isNotEmpty()) {
                    languageCodes[col] = code
                    logger.lifecycle("==> 检测到语言: $code (列索引: $col)")
                }
            }

            // 找到默认语言在Excel中的列索引
            val defaultLangCol = languageCodes.entries
                .find { it.value == defaultLanguage.get() }?.key
                ?: throw GradleException("==> Excel中未找到默认语言: ${defaultLanguage.get()}")

            // 处理excel每一行数据
            for (rowNum in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowNum) ?: continue
                val defaultLangCell = row.getCell(defaultLangCol) ?: continue
                val defaultText = defaultLangCell.stringCellValue.trim()

                if (defaultText.isEmpty()) {
                    continue
                }
                // 找到对应的key
                val key = defaultStrings.entries.find { it.value == defaultText }?.key
                if (key == null) {
                    logger.warn("==> 在默认strings.xml中未找到文本对应的key: $defaultText (行号: ${rowNum + 1})")
                    continue
                }

                // 为每种语言生成翻译
                languageCodes.forEach { (colIndex, langCode) ->
                    val translationCell = row.getCell(colIndex) ?: return@forEach
                    val translationText = translationCell.stringCellValue.trim()

                    // 跳过默认语言，因为它已经存在
                    if (langCode == defaultLanguage.get()) return@forEach

                    // 生成对应语言的strings.xml
                    generateLanguageFile(resDir, langCode, key, translationText)
                }
            }
        }
    }

    /**
     * 自动查找Android项目的res目录
     */
    private fun findAndroidResDirectory(): File {
        // 尝试标准Android项目结构路径
        val standardResDir = File(project.projectDir, "src/main/res")
        if (standardResDir.exists() && standardResDir.isDirectory) {
            return standardResDir
        }

        // 如果标准路径不存在，尝试查找Android插件配置的资源目录
        try {
            // 检查是否是Android应用或库项目
            val isAndroidApp = project.plugins.hasPlugin("com.android.application")
            val isAndroidLib = project.plugins.hasPlugin("com.android.library")

            if (isAndroidApp || isAndroidLib) {
                // 通过Android插件获取资源目录
                val androidExtension = project.extensions.findByName("android")
                val sourceSets = androidExtension?.javaClass?.getMethod("getSourceSets")
                    ?.invoke(androidExtension)

                // 反射获取main source set的res目录
                val mainSourceSet =
                    sourceSets?.javaClass?.getMethod("getByName", String::class.java)
                        ?.invoke(sourceSets, "main")
                val resDirectories = mainSourceSet?.javaClass?.getMethod("getResDirectories")
                    ?.invoke(mainSourceSet) as? Collection<*>

                resDirectories?.forEach { dir ->
                    if (dir is File && dir.exists()) {
                        return dir
                    }
                }
            }
        } catch (e: Exception) {
            logger.debug("通过Android插件获取资源目录失败: ${e.message}")
        }

        // 如果所有方法都失败，抛出异常
        throw GradleException("无法找到Android项目的res目录，请确保这是一个Android项目，且目录结构正确")
    }

    /**
     * 解析strings.xml文件，返回key-value映射
     */
    private fun parseStringsXml(file: File): Map<String, String> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        doc.documentElement.normalize()

        val strings = mutableMapOf<String, String>()
        val stringNodes: NodeList = doc.getElementsByTagName("string")

        for (i in 0 until stringNodes.length) {
            val node = stringNodes.item(i) as Element
            val key = node.getAttribute("name")
            val value = node.textContent
            strings[key] = value
        }

        return strings
    }

    /**
     * 生成或更新特定语言的strings.xml文件
     */
    private fun generateLanguageFile(resDir: File, langCode: String, key: String, value: String) {
        val langDir = if (langCode.isEmpty()) {
            File(resDir, "values")
        } else {
            File(resDir, "values-$langCode")
        }

        // 确保目录存在
        if (!langDir.exists()) {
            langDir.mkdirs()
        }

        val stringsFile = File(langDir, "strings.xml")
        val doc = if (stringsFile.exists()) {
            // 如果文件存在，读取现有内容
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringsFile)
        } else {
            // 如果文件不存在，创建新的XML文档
            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = docBuilder.newDocument()
            val resources = doc.createElement("resources")
            doc.appendChild(resources)
            doc
        }

        doc.documentElement.normalize()
        val resources = doc.documentElement

        // 检查是否已有该key的翻译
        var stringNode: Element? = null
        val existingNodes: NodeList = resources.getElementsByTagName("string")
        for (i in 0 until existingNodes.length) {
            val node = existingNodes.item(i) as Element
            if (node.getAttribute("name") == key) {
                stringNode = node
                break
            }
        }

        // 如果存在则更新，不存在则创建
        if (stringNode != null) {
            stringNode.textContent = escapeXml(value)
        } else {
            stringNode = doc.createElement("string")
            stringNode.setAttribute("name", key)
            stringNode.textContent = escapeXml(value)
            resources.appendChild(stringNode)
        }
        // 清理可能的空文本节点
        // TODO 可能产生bug
        cleanEmptyTextNodes(resources)

        // 保存文件 - 优化XML格式化配置
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()

        // 关键优化：设置缩进和编码，避免多余空行
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(OutputKeys.METHOD, "xml")
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
        // 写入文件
        val result = StreamResult(stringsFile)
        transformer.transform(DOMSource(doc), result)

        logger.lifecycle("==> 已更新、翻译: $langCode/$key = $value")
    }

    /**
     * 清理XML文档中的空文本节点（解决无效换行问题的核心）
     */
    private fun cleanEmptyTextNodes(node: Node) {
        val childNodes = node.childNodes
        var i = 0
        while (i < childNodes.length) {
            val child = childNodes.item(i)
            if (child.nodeType == Node.TEXT_NODE) {
                // 移除仅包含空白字符的文本节点
                if (child.textContent.trim().isEmpty()) {
                    node.removeChild(child)
                    i-- // 因为移除了节点，索引需要回退
                }
            } else if (child.nodeType == Node.ELEMENT_NODE) {
                // 递归清理子元素
                cleanEmptyTextNodes(child)
            }
            i++
        }
    }

    /**
     * 清除转义XML特殊字符
     */
    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}