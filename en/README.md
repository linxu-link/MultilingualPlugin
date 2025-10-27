## MultilingualPlugin

A Gradle Plugin for Automotive Android Apps that automatically generates multi-language `string.xml` files from Excel, solving the tedious manual work required for multi-language adaptation of global vehicle models.


## 🌟 Core Features

- **Excel-Driven Translation**: Manage multi-language texts uniformly through an Excel file. Maintain just one spreadsheet to generate resource files for all languages.
- **Auto-Matching & Generation**: The plugin automatically reads the base language (e.g., Chinese) `strings.xml`, and generates `values-xx` directories and corresponding files for other languages based on the translation content in Excel.
- **Full Project Adaptation**: Supports multi-module projects (e.g., the common structure of "main app + sub-modules" in automotive apps). Configure once in the root directory to apply automatically to all `app` and `lib` modules; it also supports configuring only a single module.
- **Incremental Update**: When new translations are added or existing ones are modified, the plugin intelligently updates existing files to avoid conflicts caused by repeated generation.


![](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/b4021608c27a4cc6880c8ab31e99b9a7~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg5p6X5qCpbGluaw==:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODcwNDY4OTM5NDM0MDM5In0%3D&rk3s=e9ecf3d6&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1759599187&x-orig-sign=rwdP1KxTtbHQyj7vAdl6jce5KeQ%3D)


## 🚀 Integration Steps - Kotlin DSL

**(1) Option 1 - Global Application**

Apply the plugin and set configuration items in the root directory’s `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false

    id("io.github.linxu-link.multilingual") version "0.2.0"
}

multilingual {
    // Enable multi-language adaptation (disabled by default)
    enable.set(true)
    // Use project.rootDir to get the project root directory, then append the relative path
    excelFilePath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // Base language directory (must match the resource file directory in the code)
    baselineDir.set("values")
    // Base language code (must match the language code in the Excel file)
    defaultLanguage.set("zh-rCN")
} 
```  


**(2) Option 2 - Single Module Application**

Apply the plugin and set configuration items in the module’s `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("io.github.linxu-link.multilingual") version "0.2.0"
}

multilingual {
    // Enable multi-language adaptation (disabled by default)
    enable.set(true)
    // Use project.rootDir to get the project root directory, then append the relative path
    excelFilePath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // Base language directory (must match the resource file directory in the code)
    baselineDir.set("values")
    // Base language code (must match the language code in the Excel file)
    defaultLanguage.set("zh-rCN")
} 
```  


Global application and single-module application are mutually exclusive. Choose one `build.gradle` file to configure based on your needs.


## Configuration Items of MultilingualPlugin

The plugin has four configuration items:

- **enable**: Whether to enable the plugin (default: `false`). After generating multi-language string resources, the plugin should be disabled to avoid slowing down the normal compilation process.
- **excelFilePath**: Path to the Excel translation file.
- **baselineDir**: Directory of the base language (default: `values`). MultilingualPlugin uses `strings.xml` in the base language directory as the template to obtain the `string name` required for generating other languages. Therefore, `strings.xml` under `baselineDir` must be complete.
- **defaultLanguage**: Code of the base language in Excel (default: `zh-rCN`).


## 📊 Excel File Format Specification

**Header**: Defines the language type, formatted as **`Language Name/Language Code`** (e.g., `Chinese/zh-rCN`, `English/en`).

The `Language Name` can be defined arbitrarily (the plugin does not parse it), while the `Language Code` after `/` must comply with Android multi-language specifications. The plugin generates corresponding `values` folders based on the `Language Code`. Example:

| Chinese/zh-rCN | English/en-rUS      | Japanese/ja-rJP | Korean/ko-rKR   |
| -------------- | ------------------- | --------------- | --------------- |
| 我的应用       | My Application      | マイアプリ      | 내 앱           |
| 你好，世界！   | Hello World!        | こんにちは、世界！| 안녕, 세계!    |
| 欢迎使用本应用。| Welcome to the app. | アプリへようこそ。| 앱에 오신 것을 환영합니다. |
| 设置           | Settings            | 設定            | 설정            |
| 登录           | Login               | ログイン        | 로그인          |
| 退出登录       | Logout              | ログアウト      | 로그아웃        |
| 用户名         | Username            | ユーザー名      | 사용자 이름     |
| 密码           | Password            | パスワード      | 비밀번호        |  


## 🛠️ Generate Multi-Language Files

**(1) Option 1 - Execute Gradle Task**

```bash
./gradlew generateTranslations  # Generate multi-language files for all modules
./gradlew :app:generateTranslations  # Generate files for a specific module (e.g., "app")
```

**(2) Option 2 - Execute Build Task**

![](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/90315de7284642368a50e50312c01fde~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg5p6X5qCpbGluaw==:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODcwNDY4OTM5NDM0MDM5In0%3D&rk3s=e9ecf3d6&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1759599185&x-orig-sign=dnIg1l5GLoRyNTVhdARwku8EjR8%3D)


The plugin will automatically generate directories like `values-en` and `values-ja` under the `res` directory, and create corresponding `strings.xml` files whose content is generated based on the translations in Excel.

## 🔙 Switch Language
- [Back to Root](https://github.com/linxu-link/MultilingualPlugin#%F0%9F%8C%90--language-switch)
- [中文文档](https://github.com/linxu-link/MultilingualPlugin//tree/master/zh/README.md)