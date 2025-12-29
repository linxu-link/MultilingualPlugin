## MultilingualPlugin
A Gradle plugin designed for vehicle-mounted Android applications. It can automatically generate multi-language string.xml files based on Excel files, and also export multi-language resources from the project to Excel files, solving the cumbersome manual operations in multi-language adaptation for global vehicle models.

## 🌟 Core Features
- **Excel-Driven Translation**: Manage multi-language text uniformly through Excel files, generating resource files for all languages by maintaining just one spreadsheet.
- **Export Multi-Language Resources**: New in version 0.3.0, supports exporting multi-language resources from the project to Excel files for easy translation and management.
- **Automatic Matching and Generation**: The plugin automatically reads the baseline language (e.g., Chinese) strings.xml and generates other languages' values-xx directories and corresponding files based on the translation content in Excel.
- **Full Project Adaptation**: Supports multi-module projects (common in vehicle-mounted apps with main app + sub-modules). Configure once in the root directory to automatically apply to all app and lib modules, or support single-module configuration scenarios.
- **Incremental Updates**: When adding or modifying translations, the plugin intelligently updates existing files to avoid conflicts caused by repeated generation.

![](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/b4021608c27a4cc6880c8ab31e99b9a7~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg5p6X5qCpbGluaw==:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODcwNDY4OTM5NDM0MDM5In0%3D&rk3s=e9ecf3d6&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1759599187&x-orig-sign=rwdP1KxTtbHQyj7vAdl6jce5KeQ%3D=rwdP1KxTtbHQyj7vAdl6jce5KeQ%3D)

## 🚀 Integration Steps
- Kotlin DSL **(1) Option One - Global Application** Apply the plugin and set configuration items in the root build.gradle.kts:
```
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    id("io.github.linxu-link.multilingual") version "0.3.0"
}

multilingual {
    /********* Configuration for Generating Multi-Language Resources ************/
    // Input Excel file path
    inputExcelPath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // Baseline language directory, must match the resource file directory in the code
    inputBaselineDir.set("values")
    // Baseline language code, must match the language code in the Excel file
    inputDefaultLanguage.set("zh-rCN")
    /********* Configuration for Generating Multi-Language Resources ************/

    /********* Configuration for Exporting Multi-Language Resources ************/
    // Output default language, used to identify the default language resources under the values directory
    outputDefaultLanguage.set("zh-rCN")
    // Output Excel file path
    outputExcelPath.set(file("${project.rootDir}/language/多语言V2.0.xlsx").absolutePath)
    // Since exporting Excel will export multi-language resources from all modules at once, you can exclude specified modules as needed to prevent them from participating in the export
    outputExcludeModules.set(listOf("library"))
    /********* Configuration for Exporting Multi-Language Resources ************/
}
```
**(2) Option Two - Single-Module Application** Apply the plugin and set configuration items in the module's build.gradle.kts. Note that the new export Excel feature in version 0.3.0 does not support single-module application; even if configured for a single module, it will export multi-language resources from all modules at once.
```
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("io.github.linxu-link.multilingual") version "0.3.0"
}

multilingual {
    /********* Configuration for Generating Multi-Language Resources ************/
    // Input Excel file path
    inputExcelPath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // Baseline language directory, must match the resource file directory in the code
    inputBaselineDir.set("values")
    // Baseline language code, must match the language code in the Excel file
    inputDefaultLanguage.set("zh-rCN")
    /********* Configuration for Generating Multi-Language Resources ************/
}
```
Global application and single-module application are mutually exclusive; configure only in one build.gradle file based on your needs.

## 📊 Excel File Format Specification
**Header**: Define language types in the format **`Language Name/Language Code`** (e.g., `Chinese/zh-rCN`, `English/en`). The `Language Name` can be defined freely as the plugin does not parse it; the `/` followed by the `Language Code` must conform to Android's multi-language specification, and the plugin will generate the corresponding values folder based on the `Language Code`. Example as follows:

| Chinese/zh-rCN | English/en-rUS | Japanese/ja-rJP | Korean/ko-rKR |
| -------------- | -------------- | --------------- | ------------- |
| 我的应用 | My Application | マイアプリ | 내 앱 |
| 你好，世界！ | Hello World! | こんにちは、世界！ | 안녕, 세계! |
| 欢迎使用本应用。 | Welcome to the app. | アプリへようこそ。 | 앱에 오신 것을 환영합니다. |
| 设置 | Settings | 設定 | 설정 |
| 登录 | Login | ログイン | 로그인 |
| 退出登录 | Logout | ログアウト | 로그아웃 |
| 用户名 | Username | ユーザー名 | 사용자 이름 |
| 密码 | Password | パスワード | 비밀번호 |

## 🛠️ Generate Multi-Language Files
**Execute Gradle Task**
```
./gradlew generateTranslations # Generate multi-language files for all modules
./gradlew :app:generateTranslations # Generate files for the specified module
```

## 🛠 Export Multi-Language Resources
**Execute Gradle Task**
```
./gradlew generateExcel # Export multi-language files for all modules
```

## 🔙 Switch Language
- [Back to Root](https://github.com/linxu-link/MultilingualPlugin#%F0%9F%8C%90--language-switch)
- [English Documentation](https://github.com/linxu-link/MultilingualPlugin//tree/master/en/README.md)