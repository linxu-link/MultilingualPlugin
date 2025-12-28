package com.wj.plugin.multilingual

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

/**
 * 翻译插件的DSL配置扩展
 * @author WuJia
 */
open class MultilingualExtension(objectFactory: Project) {
    // Excel文件路径配置
    val inputExcelPath: Property<String>

    // 默认语言配置
    val inputDefaultLanguage: Property<String>

    // 基线目录配置
    val inputBaselineDir: Property<String>

    // 输出目录配置
    val outputExcelPath: Property<String>
    // 输出默认语言配置
    val outputDefaultLanguage: Property<String>
    // 输出排除模块配置
    val outputExcludeModules: ListProperty<String>


    init {
        val objects = objectFactory.objects
        inputExcelPath = objects.property(String::class.java).convention("")
        outputExcelPath = objects.property(String::class.java).convention("")
        outputDefaultLanguage = objects.property(String::class.java).convention("")
        inputDefaultLanguage = objects.property(String::class.java).convention("")
        inputBaselineDir = objects.property(String::class.java).convention("values")
        outputExcludeModules =
            objects.listProperty<String>(String::class.java).convention(mutableListOf())

    }
}