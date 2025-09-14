// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    id("TranslationPlugin") apply true
}

translation {
    // 启用多语言适配，默认关闭
    enable.set(true)
    // 使用project.rootDir获取项目根目录，再拼接相对路径
    excelFilePath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // 基准语言目录，必须与代码中资源文件目录一致
    baselineDir.set("values")
    // 基准语言编码，必须与Excel文件中的语言编码一致
    defaultLanguage.set("zh-rCN")
}