plugins {
    `java-gradle-plugin`// 核心插件，提供插件开发支持
    `maven-publish`// 支持Maven格式发布
    id("com.gradle.plugin-publish") version "1.3.1" // 发布到Gradle Plugin Portal的专用插件
    kotlin("jvm") // 若用Kotlin编写插件
}
// 1. 插件元信息（必填）
group = "io.github.linxu-link" // 命名空间，直接用GitHub用户名
version = "0.3.0"
val pluginDescription = "A plugin that automatically generates Android multi-language resources based on Excel." // 插件描述
// 2. 插件核心配置
gradlePlugin {
    website.set("https://github.com/linxu-link/MultilingualPlugin") // 插件主页（GitHub仓库地址）
    vcsUrl.set("https://github.com/linxu-link/MultilingualPlugin") // 版本控制地址
    plugins {
        register("multilingualPlugin") { // 插件名称（自定义）
            id = "io.github.linxu-link.multilingual" // 插件唯一ID（必须全仓库唯一）
            implementationClass = "com.wj.plugin.multilingual.MultilingualPlugin" // 插件主类
            displayName = "Android Multilingual Plugin"
            description = pluginDescription
            tags.set(listOf("android", "translation", "excel", "localization")) // 用于搜索的标签
        }
    }
}

// 配置 Maven 格式的发布信息
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "multilingual-plugin"
            version = project.version.toString()
        }
    }
}

// 生成源码和文档JAR（提升可用性，推荐）
java {
    withSourcesJar() // 源码JAR（方便用户调试）
    withJavadocJar() // 文档JAR（生成API文档）
}

kotlin {
    jvmToolchain(17)
}
// 依赖配置（根据插件功能添加）
dependencies {
    implementation(gradleApi()) // Gradle API依赖
    implementation(localGroovy())
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation(kotlin("stdlib-jdk8")) // kotlin 依赖
}
