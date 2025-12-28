package com.wj.plugin.multilingual.tasks

import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

/**
 * 导出Excel任务
 */
open class OutputExcelTask : DefaultTask() {

    @Optional
    @get:Input
    val excelOutputPath: Property<String> = project.objects.property(String::class.java)

    @Optional
    @get:Input
    val defaultLanguage: Property<String> = project.objects.property(String::class.java)

    @Optional
    @get:Input
    val excludeModules: ListProperty<String> = project.objects.listProperty(String::class.java)

    private val languageNameMap = mapOf(
        // 英语
        "en" to "English",
        "en-rUS" to "English (United States)",
        "en-rGB" to "English (United Kingdom)",
        "en-rCA" to "English (Canada)",
        "en-rAU" to "English (Australia)",

        // 中文
        "zh" to "Chinese",
        "zh-rCN" to "Chinese (Simplified, China)",
        "zh-rTW" to "Chinese (Traditional, Taiwan)",
        "zh-rHK" to "Chinese (Traditional, Hong Kong)",
        "zh-rSG" to "Chinese (Simplified, Singapore)",

        // 日语
        "ja" to "Japanese",
        "ja-rJP" to "Japanese (Japan)",

        // 韩语
        "ko" to "Korean",
        "ko-rKR" to "Korean (South Korea)",

        // 法语
        "fr" to "French",
        "fr-rFR" to "French (France)",
        "fr-rCA" to "French (Canada)",
        "fr-rBE" to "French (Belgium)",

        // 德语
        "de" to "German",
        "de-rDE" to "German (Germany)",
        "de-rAT" to "German (Austria)",
        "de-rCH" to "German (Switzerland)",

        // 西班牙语
        "es" to "Spanish",
        "es-rES" to "Spanish (Spain)",
        "es-rUS" to "Spanish (United States)",
        "es-rMX" to "Spanish (Mexico)",
        "es-rAR" to "Spanish (Argentina)",

        // 葡萄牙语
        "pt" to "Portuguese",
        "pt-rBR" to "Portuguese (Brazil)",
        "pt-rPT" to "Portuguese (Portugal)",

        // 俄语
        "ru" to "Russian",
        "ru-rRU" to "Russian (Russia)",

        // 阿拉伯语
        "ar" to "Arabic",
        "ar-rSA" to "Arabic (Saudi Arabia)",
        "ar-rEG" to "Arabic (Egypt)",
        "ar-rAE" to "Arabic (United Arab Emirates)",

        // 意大利语
        "it" to "Italian",
        "it-rIT" to "Italian (Italy)",

        // 荷兰语
        "nl" to "Dutch",
        "nl-rNL" to "Dutch (Netherlands)",
        "nl-rBE" to "Dutch (Belgium)",

        // 波兰语
        "pl" to "Polish",
        "pl-rPL" to "Polish (Poland)",

        // 土耳其语
        "tr" to "Turkish",
        "tr-rTR" to "Turkish (Turkey)",

        // 印尼语
        "in" to "Indonesian",
        "in-rID" to "Indonesian (Indonesia)",

        // 泰语
        "th" to "Thai",
        "th-rTH" to "Thai (Thailand)",

        // 越南语
        "vi" to "Vietnamese",
        "vi-rVN" to "Vietnamese (Vietnam)",

        // 印地语
        "hi" to "Hindi",
        "hi-rIN" to "Hindi (India)",

        // 马来语
        "ms" to "Malay",
        "ms-rMY" to "Malay (Malaysia)",

        // 瑞典语
        "sv" to "Swedish",
        "sv-rSE" to "Swedish (Sweden)",

        // 丹麦语
        "da" to "Danish",
        "da-rDK" to "Danish (Denmark)",

        // 芬兰语
        "fi" to "Finnish",
        "fi-rFI" to "Finnish (Finland)",

        // 挪威语
        "nb" to "Norwegian Bokmål",
        "nb-rNO" to "Norwegian Bokmål (Norway)",

        // 匈牙利语
        "hu" to "Hungarian",
        "hu-rHU" to "Hungarian (Hungary)",

        // 捷克语
        "cs" to "Czech",
        "cs-rCZ" to "Czech (Czech Republic)",

        // 希腊语
        "el" to "Greek",
        "el-rGR" to "Greek (Greece)",

        // 罗马尼亚语
        "ro" to "Romanian",
        "ro-rRO" to "Romanian (Romania)",

        // 乌克兰语
        "uk" to "Ukrainian",
        "uk-rUA" to "Ukrainian (Ukraine)",

        // 希伯来语（RTL）
        "iw" to "Hebrew",
        "iw-rIL" to "Hebrew (Israel)",

        // 波斯语（RTL）
        "fa" to "Persian",
        "fa-rIR" to "Persian (Iran)"
    )

    @TaskAction
    fun generateExcel() {
        logger.lifecycle("==> 开始执行 generateExcel 任务")
        try {
            // 1. 查找所有Android module的res目录
            val allResDirs = findAllAndroidResDirectories()

            if (allResDirs.isEmpty()) {
                throw GradleException("==> 未找到任何Android module的res目录")
            }

            logger.lifecycle("==> 找到以下res目录: ${allResDirs.joinToString(", ") { it.absolutePath }}")

            // 2. 扫描所有 values 目录，收集语言信息（修改：支持多dir per langCode）
            val languageDirs = mutableMapOf<String, MutableList<File>>() // langCode -> list of dirs

            val sourceLangCode = defaultLanguage.getOrElse("zh-rCN")

            for (resDir in allResDirs) {
                // 添加默认的 values 目录到 sourceLangCode
                val defaultValuesDir = File(resDir, "values")
                if (defaultValuesDir.exists()) {
                    languageDirs.getOrPut(sourceLangCode) { mutableListOf() }.add(defaultValuesDir)
                }

                // 扫描 values-* 目录
                resDir.listFiles { file -> file.isDirectory && file.name.startsWith("values-") }
                    ?.forEach { dir ->
                        val langCode = dir.name.substringAfter("values-")
                        languageDirs.getOrPut(langCode) { mutableListOf() }.add(dir)
                    }
            }

            if (languageDirs.isEmpty()) {
                throw GradleException("==> 未找到任何 values 目录")
            }

            logger.lifecycle("==> 找到以下语言: ${languageDirs.keys.joinToString(", ")}")

            // 3. 读取所有 strings.xml 文件，构建数据模型
            val allStrings = mutableMapOf<String, MutableMap<String, String>>()

            for ((langcode, dirs) in languageDirs) {
                for (dir in dirs) {
                    val stringsFile = File(dir, "strings.xml")
                    if (!stringsFile.exists()) {
                        logger.warn("==> ${dir.absolutePath}/strings.xml 不存在，跳过")
                        continue
                    }

                    val stringsMap = parseStringsXml(stringsFile)
                    logger.lifecycle("==> 从 ${stringsFile.absolutePath} 读取到 ${stringsMap.size} 个字符串")

                    for ((key, value) in stringsMap) {
                        val langMap = allStrings.getOrPut(key) { mutableMapOf() }
                        val existing = langMap[langcode]
                        if (existing != null && existing != value) {
                            logger.warn("==> Key 冲突 for $key in $langcode: overwriting '$existing' with '$value'")
                        }
                        langMap[langcode] = value  // 覆盖
                    }
                }
            }

            if (allStrings.isEmpty()) {
                throw GradleException("==> 未从任何 strings.xml 文件中读取到字符串")
            }

            // 4. 准备 Excel 表头
            val otherLanguages = languageDirs.keys.filter { it != sourceLangCode }.sorted()

            val headers = mutableListOf<String>()
            val languageHeader = buildLanguageHeader(sourceLangCode)
            if (languageHeader == null) {
                logger.warn("==> 无法构建默认语言头，跳过")
                return
            }
            headers.add(languageHeader)

            otherLanguages.forEach {
                val languageHeader = buildLanguageHeader(it)
                if (languageHeader == null) {
                    logger.warn("==> 无法构建语言头 $it，跳过")
                } else {
                    headers.add(languageHeader)
                }
            }

            // 5. 创建并写入 Excel
            val outputFile = File(excelOutputPath.get())
            if (outputFile.exists()) {
                outputFile.delete()
            }
            outputFile.parentFile?.mkdirs()
            logger.lifecycle("==> 输出路径: ${outputFile.absolutePath}")

            XSSFWorkbook().use { workbook ->
                val sheet = workbook.createSheet("Sheet1")

                val headerStyle = workbook.createCellStyle()
                headerStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

                val headerRow = sheet.createRow(0)
                headers.forEachIndexed { index, header ->
                    val cell = headerRow.createCell(index)
                    cell.setCellValue(header)
                    cell.cellStyle = headerStyle
                }

                var rowIndex = 1
                for ((key, translations) in allStrings.toSortedMap()) {
                    val row = sheet.createRow(rowIndex++)
                    for (i in headers.indices) {
                        val langCode = if (i == 0) sourceLangCode else otherLanguages[i - 1]
                        val text = translations[langCode] ?: ""
                        row.createCell(i).setCellValue(text)
                    }
                }

                for (i in headers.indices) {
                    sheet.autoSizeColumn(i)
                }

                FileOutputStream(outputFile).use { fos ->
                    workbook.write(fos)
                    fos.flush()
                }
            }

            logger.lifecycle("==> Excel 文件已成功生成: ${outputFile.absolutePath}")
        } catch (e: Exception) {
            logger.error("==> 生成 Excel 失败: ${e.message}", e)
            throw GradleException("生成 Excel 失败", e)
        }
    }


    // 构建语言头 "LanguageName/langCode"（根据 languageNameMap 映射）
    private fun buildLanguageHeader(langCode: String): String? {
        if (langCode.isEmpty()) {
            return "Default/"
        }
        val languageName = languageNameMap[langCode] ?: return null
        logger.lifecycle("==> 构建语言头: $languageName/$langCode")
        return "$languageName/$langCode"
    }

    /**
     * 查找所有Android module的res目录
     */
    private fun findAllAndroidResDirectories(): List<File> {
        val allResDirs = mutableListOf<File>()
        // 迭代所有subprojects（包括root，如果适用）
        project.rootProject.allprojects.forEach { subproject ->
            val isAndroidApp = subproject.plugins.hasPlugin("com.android.application")
            val isAndroidLib = subproject.plugins.hasPlugin("com.android.library")

            if (excludeModules.get().contains(subproject.name)) {
                logger.lifecycle("==> 项目 ${subproject.name} 已在排除列表中，跳过")
                return@forEach
            }

            if (isAndroidApp || isAndroidLib) {
                logger.debug("==> 找到Android module: ${subproject.name}")

                // 尝试通过Android extension获取res目录（支持自定义sourceSets）
                val androidExtension = subproject.extensions.findByName("android")
                if (androidExtension != null) {
                    try {
                        val sourceSets = androidExtension.javaClass.getMethod("getSourceSets")
                            .invoke(androidExtension)
                        val mainSourceSet =
                            sourceSets.javaClass.getMethod("getByName", String::class.java)
                                .invoke(sourceSets, "main")
                        val resDirectories = mainSourceSet.javaClass.getMethod("getResDirectories")
                            .invoke(mainSourceSet) as? Collection<*>

                        resDirectories?.forEach { dir ->
                            if (dir is File && dir.exists()) {
                                allResDirs.add(dir)
                            }
                        }
                    } catch (e: Exception) {
                        logger.warn("==> 在 ${subproject.name} 中通过反射获取res目录失败: ${e.message}")
                    }
                }

                // fallback: 标准路径 src/main/res
                if (allResDirs.none { it.absolutePath.startsWith(subproject.projectDir.absolutePath) }) {
                    val standardResDir = File(subproject.projectDir, "src/main/res")
                    if (standardResDir.exists() && standardResDir.isDirectory) {
                        allResDirs.add(standardResDir)
                    }
                }
            }
        }
        return allResDirs
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
}