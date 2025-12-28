# MultilingualPlugin

Android应用多语言资源自动生成插件。

Auto-generate multi-language string.xml for Android apps.

## 📦 最新版本 | Latest Version

- **0.2.0**（2025-10-20）
- **0.3.0**（2025-12-28）

## 🌐 使用说明 | Usage

- [中文文档](zh/README.md) — 包含详细集成步骤、Excel格式说明
- [English Documentation](en/README.md) — Includes integration steps, Excel specs

## 📌 核心功能预览
- **Excel驱动翻译**：通过Excel文件统一管理多语言文本，只需维护一份表格即可生成所有语言的资源文件。
- **自动匹配与生成**：插件会自动读取基准语言（如中文）的`strings.xml`，并根据Excel中的翻译内容生成其他语言的
  `values-xx`目录及对应文件。
- **全项目适配**：支持多模块工程（如车载应用常见的主应用+子模块结构），只需在根目录配置一次，即可自动应用到所有
  `app`和`lib`模块，也支持仅配置单一模块的场景。
- **增量更新**：新增或修改翻译时，插件会智能更新已有文件，避免重复生成导致的冲突。

## 🔧 新增功能 | New Features
- **输出Excel**：0.3.0 支持将工程内的多语言资源导出到Excel文件，方便翻译和管理。


## 📄 开源协议 | License

本项目采用 **MIT 许可证**，您可自由地使用、修改、分发本项目代码，无需承担额外义务，仅需在分发时保留原始许可证声明。

The project is licensed under the **MIT License**, You are free to use, modify, and distribute the
project code without additional obligations, except for retaining the original license notice when
distributing.

完整许可证文本详见：[LICENSE](LICENSE)  
Full license text: [LICENSE](LICENSE)