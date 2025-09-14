plugins {
    `java-gradle-plugin` // 核心插件，提供Gradle插件开发支持
    `maven-publish`      // 用于发布插件
    id("com.gradle.plugin-publish") version "1.3.1"  // 发布到Gradle插件门户
    kotlin("jvm") version "1.9.0"
    id("maven-publish")
//    signing
}

group = "io.github.wujia"
version = "0.1.0"
val pluginDescription = "A plugin that automatically generates Android multi-language resources based on Excel."

gradlePlugin {
    website.set("https://github.com/linxu-link/MultilingualPlugin")
    vcsUrl.set("https://github.com/linxu-link/MultilingualPlugin")
    plugins {
        register("multilingualPlugin") {
            id = "io.github.wujia.multilingual"
            implementationClass = "com.wj.plugin.multilingual.MultilingualPlugin"
            displayName = "Android Multilingual Plugin"
            description = pluginDescription
            tags.set(listOf("android", "translation", "excel", "localization"))
        }
    }
}

// 签名配置
//signing {
//    if (project.hasProperty("signingKey")) {
//        sign(publishing.publications)
//    }
//}


java {
    withSourcesJar()
    withJavadocJar()
}

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

repositories {
    maven(url = "https://maven.aliyun.com/repository/central")
    maven(url = "https://maven.aliyun.com/repository/google")
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    // Apache POI 用于处理Excel文件
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation(kotlin("stdlib-jdk8"))
}