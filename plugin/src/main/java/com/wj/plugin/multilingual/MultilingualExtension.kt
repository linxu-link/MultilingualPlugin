package com.wj.plugin.multilingual

import org.gradle.api.Project
import org.gradle.api.provider.Property

/**
 * 翻译插件的DSL配置扩展
 * @author wujia
 */
open class MultilingualExtension(objectFactory: Project) {
    // 插件是否启用
    val enable: Property<Boolean>

    // Excel文件路径配置
    val excelFilePath: Property<String>

    // 默认语言配置
    val defaultLanguage: Property<String>

    // 基线目录配置
    val baselineDir: Property<String>

    init {
        val objects = objectFactory.objects
        excelFilePath = objects.property(String::class.java).convention("")
        defaultLanguage = objects.property(String::class.java).convention("")
        baselineDir = objects.property(String::class.java).convention("values")
        enable = objects.property(Boolean::class.java).convention(false)
    }
}