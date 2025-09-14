plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.3.1"
    kotlin("jvm")
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

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation(kotlin("stdlib-jdk8"))
}
