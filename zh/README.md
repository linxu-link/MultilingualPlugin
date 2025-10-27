
## MultilingualPlugin

一款适用于车载 Android 应用的 Gradle 插件，可从 Excel 文件自动生成多语言 string.xml，解决全球车型多语言适配中繁琐的人工操作问题。

## 🌟 核心功能

-   **Excel驱动翻译**：通过Excel文件统一管理多语言文本，只需维护一份表格即可生成所有语言的资源文件。
-   **自动匹配与生成**：插件会自动读取基准语言（如中文）的`strings.xml`，并根据Excel中的翻译内容生成其他语言的`values-xx`目录及对应文件。
-   **全项目适配**：支持多模块工程（如车载应用常见的主应用+子模块结构），只需在根目录配置一次，即可自动应用到所有`app`和`lib`模块，也支持仅配置单一模块的场景。
-   **增量更新**：新增或修改翻译时，插件会智能更新已有文件，避免重复生成导致的冲突。

![](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/b4021608c27a4cc6880c8ab31e99b9a7~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg5p6X5qCpbGluaw==:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODcwNDY4OTM5NDM0MDM5In0%3D&rk3s=e9ecf3d6&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1759599187&x-orig-sign=rwdP1KxTtbHQyj7vAdl6jce5KeQ%3D)

## 🚀 集成步骤 - Kotlin DSL
**（1）方案一 - 全局应用**

在根目录`build.gradle.kts`中应用插件并设定配置项：

```
plugins {
alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    
    id("io.github.linxu-link.multilingual") version "0.2.0"
}

multilingual {
// 启用多语言适配，默认关闭
    enable.set(true)
    // 使用project.rootDir获取项目根目录，再拼接相对路径
    excelFilePath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // 基准语言目录，必须与代码中资源文件目录一致
    baselineDir.set("values")
    // 基准语言编码，必须与Excel文件中的语言编码一致
    defaultLanguage.set("zh-rCN")
} 
```

**（2）方案二 - 单模块应用**

在模块内`build.gradle.kts`中应用插件并设定配置项：

```
plugins {
alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    
    id("io.github.linxu-link.multilingual") version "0.2.0"
}

multilingual {
// 启用多语言适配，默认关闭
    enable.set(true)
    // 使用project.rootDir获取项目根目录，再拼接相对路径
    excelFilePath.set(file("${project.rootDir}/language/多语言V1.0.xlsx").absolutePath)
    // 基准语言目录，必须与代码中资源文件目录一致
    baselineDir.set("values")
    // 基准语言编码，必须与Excel文件中的语言编码一致
    defaultLanguage.set("zh-rCN")
} 
```

全局应用和单模块应用，两种应用方式是互斥的，根据你的需要只在一个build.gradle中配置即可。

**MultilingualPlugin**有四个配置项

-   **enable**：是否启用插件，默认为false。在生成多语言字符串资源后，应该将插件关闭，防止拖慢正常的编译流程。
-   **excelFilePath**：Excel翻译文件的路径。
-   **baselineDir**：基准语言的目录，默认为**values**。**MultilingualPlugin**会以基准语言目录下的string.xml为蓝本，获取生成其他语言需要的string name，所以**`baselineDir`**下的**string.xml**必须是完整的。
-   **defaultLanguage**：基准语言在Excel内的编码，默认为**zh-rCN**。




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

**（1）方案一 - 执行Gradle任务**

```
./gradlew generateTranslations  # 生成所有模块的多语言文件
./gradlew :app:generateTranslations  # 生成指定模块的文件
```

**（2）方案二 - 执行build Task**

![](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/90315de7284642368a50e50312c01fde~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg5p6X5qCpbGluaw==:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODcwNDY4OTM5NDM0MDM5In0%3D&rk3s=e9ecf3d6&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1759599185&x-orig-sign=dnIg1l5GLoRyNTVhdARwku8EjR8%3D)

插件会自动在`res`目录下生成`values-en`、`values-ja`等目录，并创建对应的`strings.xml`，内容基于Excel翻译生成。

## 🔙 Switch Language
- [Back to Root](https://github.com/linxu-link/MultilingualPlugin#%F0%9F%8C%90--language-switch)
- [English Documentation](https://github.com/linxu-link/MultilingualPlugin//tree/master/en/README.md)
