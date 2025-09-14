plugins {
    `java-gradle-plugin` // 核心插件，提供Gradle插件开发支持
    `maven-publish`      // 用于发布插件（可选）
    kotlin("jvm") version "1.9.0"
    id("maven-publish")
    signing                      // 用于签名发布物
    id("com.gradle.plugin-publish") version "1.2.1"  // 发布到Gradle插件门户
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

gradlePlugin {
    plugins {
        create("TranslationPlugin") {
            id = "com.wj.plugin"
            implementationClass = "com.wj.plugin.TranslationPlugin"
        }
    }
}

// 配置Maven发布
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "translation-plugin"
            version = project.version.toString()

            // 发布元数据
            pom {
                name.set("Android Translation Plugin")
                description.set(project.description)
                url.set("https://github.com/yourusername/TranslationPlugin")  // 项目GitHub地址

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("WuJia")
                        name.set("WuJia")
                        email.set("linxu_link@foxmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git@github.com:yourusername/TranslationPlugin.git")
                    developerConnection.set("scm:git:git@github.com:yourusername/TranslationPlugin.git")
                    url.set("https://github.com/yourusername/TranslationPlugin")
                }
            }
        }
    }

    // 配置发布到Maven Central（需要Sonatype账号）
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("sonatype.username") as String?
                password = project.findProperty("sonatype.password") as String?
            }
        }
    }
}

// 签名配置（Maven Central要求）
signing {
    sign(publishing.publications["maven"])
}

// 源代码和Javadoc发布（可选，提升可用性）
java {
    withSourcesJar()
    withJavadocJar()
}


//publishing {
//    repositories {
//        maven {
//            url = uri("$buildDir/repository")
//        }
//    }
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["java"])
//        }
//    }
//}