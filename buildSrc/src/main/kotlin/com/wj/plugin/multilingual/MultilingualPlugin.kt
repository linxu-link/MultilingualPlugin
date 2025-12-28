package com.wj.plugin.multilingual

import com.wj.plugin.multilingual.tasks.InputExcelTask
import com.wj.plugin.multilingual.tasks.OutputExcelTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 翻译插件的主插件类
 * @author WuJia
 */

private const val PLUGIN_NAME = "multilingual"
private const val TASK_GENERATE_TRANSLATIONS = "generateTranslations"
private const val TASK_GENERATE_EXCEL = "generateExcel"

class MultilingualPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 如果是根项目，创建扩展配置并注册子项目自动应用逻辑
        if (project == project.rootProject) {
            // 创建根项目级别的扩展配置
            project.extensions.create(PLUGIN_NAME, MultilingualExtension::class.java)
            // 监听所有子项目的创建，自动应用插件
            project.rootProject.subprojects { subproject ->
                subproject.afterEvaluate {
                    // 只对Android应用和库模块应用插件
                    // 在插件的apply方法中
                    if (it.plugins.hasPlugin("com.android.application") ||
                        it.plugins.hasPlugin("com.android.library")
                    ) {
                        // 应用翻译插件到子项目
                        it.plugins.apply(MultilingualModulePlugin::class.java)
                    }
                }
            }
        } else {
            // 非根项目，兼容模块级配置
            val moduleExtension = project.extensions.create(PLUGIN_NAME, MultilingualExtension::class.java, project)
            project.logger.lifecycle("==> 翻译插件已应用 => ${project.name}")

            project.afterEvaluate {
                if (project.plugins.hasPlugin("com.android.application") ||
                    project.plugins.hasPlugin("com.android.library")
                ) {
                    project.tasks.register(
                        TASK_GENERATE_TRANSLATIONS,
                        InputExcelTask::class.java
                    ) { task ->
                        task.excelInputPath.set(moduleExtension.inputExcelPath)
                        task.defaultLanguage.set(moduleExtension.inputDefaultLanguage)
                        task.baselineDir.set(moduleExtension.inputBaselineDir)
                    }

                    project.tasks.register(
                        TASK_GENERATE_EXCEL,
                        OutputExcelTask::class.java
                    ) { task ->
                        task.defaultLanguage.set(moduleExtension.outputDefaultLanguage)
                    }
                }
            }
        }
    }
}

// 模块级插件，负责实际的翻译生成任务
class MultilingualModulePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 从根项目获取配置
        val rootExtension = project.rootProject.extensions.getByType(MultilingualExtension::class.java)

        // 注册生成翻译的任务
        project.tasks.register(TASK_GENERATE_TRANSLATIONS, InputExcelTask::class.java) { task ->
            // 从根配置获取参数
            task.excelInputPath.set(rootExtension.inputExcelPath)
            task.defaultLanguage.set(rootExtension.inputDefaultLanguage)
            task.baselineDir.set(rootExtension.inputBaselineDir)
        }

        project.tasks.register(TASK_GENERATE_EXCEL, OutputExcelTask::class.java) { task ->
            // 从根配置获取参数
            task.excelOutputPath.set(rootExtension.outputExcelPath)
            task.defaultLanguage.set(rootExtension.outputDefaultLanguage)
            task.excludeModules.set(rootExtension.outputExcludeModules)
        }

    }
}