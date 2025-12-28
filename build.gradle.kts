// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
//    id("io.github.linxu-link.multilingual") version "0.2.0"

    id("MultilingualPlugin")
}

multilingual {

    /********* 用于生成多语言资源的配置项 ************/
    // 输入Excel文件路径
    inputExcelPath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // 基准语言目录，必须与代码中资源文件目录一致
    inputBaselineDir.set("values")
    // 基准语言编码，必须与Excel文件中的语言编码一致
    inputDefaultLanguage.set("zh-rCN")
    /********* 用于生成多语言资源的配置项 ************/


    /********* 用于导出多语言资源的配置项 ************/
    // 输出默认语言，用于识别values目录下的默认语言资源
    outputDefaultLanguage.set("zh-rCN")
    // 输出Excel文件路径
    outputExcelPath.set(file("${project.rootDir}/language/多语言V2.0.xlsx").absolutePath)
    // 由于输出excel会将所有模块的多语言资源一次性导出，所以可以根据需要排除指定的模块，不参与excel导出
    outputExcludeModules.set(listOf("library"))
    /********* 用于导出多语言资源的配置项 ************/
}