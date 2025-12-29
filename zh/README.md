
## MultilingualPlugin

一款适用于车载 Android 应用的 Gradle 插件，既可以根据 Excel 文件自动生成多语言 string.xml，也可以将工程内的多语言资源导出到 Excel 文件，解决全球车型多语言适配中繁琐的人工操作问题。

## 🌟 核心功能

-   **Excel驱动翻译**：通过Excel文件统一管理多语言文本，只需维护一份表格即可生成所有语言的资源文件。
-   **导出多语言资源**：0.3.0 新增功能，支持将工程内的多语言资源导出到 Excel 文件，方便翻译和管理。
-   **自动匹配与生成**：插件会自动读取基准语言（如中文）的`strings.xml`，并根据Excel中的翻译内容生成其他语言的`values-xx`目录及对应文件。
-   **全项目适配**：支持多模块工程（如车载应用常见的主应用+子模块结构），只需在根目录配置一次，即可自动应用到所有`app`和`lib`模块，也支持仅配置单一模块的场景。
-   **增量更新**：新增或修改翻译时，插件会智能更新已有文件，避免重复生成导致的冲突。

## 🚀 集成步骤 - Kotlin DSL
**（1）方案一 - 全局应用**

在根目录`build.gradle.kts`中应用插件并设定配置项：

```
plugins {
alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    
    id("io.github.linxu-link.multilingual") version "0.3.0"
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

```

**（2）方案二 - 单模块应用**

在模块内`build.gradle.kts`中应用插件并设定配置项。

注意，0.3.0版本新增的输出excel不支持单模块应用，即使配置成单模块也会将所有模块的多语言资源一次性导出。
```
plugins {
alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    
    id("io.github.linxu-link.multilingual") version "0.3.0"
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
    
} 
```

全局应用和单模块应用，两种应用方式是互斥的，根据你的需要只在一个build.gradle中配置即可。



## 📊Excel文件格式规范

**表头**：定义语言类型，格式为**`语言名称/语言编码`**（如`Chinese/zh-rCN`、`English/en`）。

`语言名称`可以自行定义，插件不会进行解析，`/`后的`语言编码`必须是符合Android多语言规范的编码，插件会根据`语言编码`生成对应的values文件夹。示例如下：

| Chinese/zh-rCN | English/en-rUS      | Japanese/ja-rJP | Korean/ko-rKR   |
| -------------- | ------------------- | --------------- | --------------- |
| 我的应用           | My Application      | マイアプリ           | 내 앱             |
| 你好，世界！         | Hello World!        | こんにちは、世界！       | 안녕, 세계!         |
| 欢迎使用本应用。       | Welcome to the app. | アプリへようこそ。       | 앱에 오신 것을 환영합니다. |
| 设置             | Settings            | 設定              | 설정              |
| 登录             | Login               | ログイン            | 로그인             |
| 退出登录           | Logout              | ログアウト           | 로그아웃            |
| 用户名            | Username            | ユーザー名           | 사용자 이름          |
| 密码             | Password            | パスワード           | 비밀번호            |

## 🛠️生成多语言文件

**执行Gradle任务**

```
./gradlew generateTranslations  # 生成所有模块的多语言文件
./gradlew :app:generateTranslations  # 生成指定模块的文件
```

## 🛠导出多语言资源

**执行Gradle任务**

```
./gradlew generateExcel  # 导出所有模块的多语言文件
```

## 🔙 Switch Language
- [Back to Root](https://github.com/linxu-link/MultilingualPlugin#%F0%9F%8C%90--language-switch)
- [English Documentation](https://github.com/linxu-link/MultilingualPlugin//tree/master/en/README.md)
