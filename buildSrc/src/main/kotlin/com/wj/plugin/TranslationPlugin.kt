package com.wj.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 翻译插件的主插件类
 * @author wujia
 */
class TranslationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 如果是根项目，创建扩展配置并注册子项目自动应用逻辑
        if (project == project.rootProject) {
            // 创建根项目级别的扩展配置
            project.extensions.create("translation", TranslationExtension::class.java)
            // 监听所有子项目的创建，自动应用插件
            project.rootProject.subprojects { subproject ->
                subproject.afterEvaluate {
                    // 只对Android应用和库模块应用插件
                    // 在插件的apply方法中
                    if (it.plugins.hasPlugin("com.android.application") ||
                        it.plugins.hasPlugin("com.android.library")
                    ) {
                        // 应用翻译插件到子项目
                        it.plugins.apply(TranslationModulePlugin::class.java)
                    }
                }
            }
        } else {
            // 非根项目，兼容模块级配置
            val moduleExtension =
                project.extensions.create("translation", TranslationExtension::class.java, project)
            project.logger.lifecycle("==> 翻译插件已应用 => ${project.name}")

            project.afterEvaluate {
                if (project.plugins.hasPlugin("com.android.application") ||
                    project.plugins.hasPlugin("com.android.library")
                ) {
                    // 注册生成翻译的任务
                    val generateTask = project.tasks.register(
                        "generateTranslations",
                        TranslationTask::class.java
                    ) { task ->
                        task.excelFilePath.set(moduleExtension.excelFilePath)
                        task.defaultLanguage.set(moduleExtension.defaultLanguage)
                        task.baselineDir.set(moduleExtension.baselineDir)
                    }

                    // 是否启用多语言适配
                    if (moduleExtension.enable.get()) {
                        project.logger.lifecycle("==> 翻译插件已启用 => ${project.name}")
                        // 关联到构建流程
                        project.tasks.named("preBuild").configure {
                            it.dependsOn(generateTask)
                        }
                    }
                }
            }
        }
    }
}

// 模块级插件，负责实际的翻译生成任务
class TranslationModulePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 从根项目获取配置
        var rootExtension =
            project.rootProject.extensions.getByType(TranslationExtension::class.java)
        // 注册生成翻译的任务
        project.tasks.register("generateTranslations", TranslationTask::class.java) { task ->
            // 从根配置获取参数
            task.excelFilePath.set(rootExtension.excelFilePath)
            task.defaultLanguage.set(rootExtension.defaultLanguage)
            task.baselineDir.set(rootExtension.baselineDir)
        }
        // 是否启用多语言适配
        if (rootExtension.enable.get()) {
            project.logger.lifecycle("==> 翻译插件已应用 => ${project.name}")
            // 关联到构建流程
            val generateTask = project.tasks.named("generateTranslations")
            project.tasks.named("preBuild").configure {
                it.dependsOn(generateTask)
            }
        }
    }
}