# Voyager Web Enhancer (Android Xposed / LSPosed 插件)

专门针对手机端网页版 **Gemini / ChatGPT / Claude / DeepSeek** 的增强插件。
通过 Hook 系统 WebView 注入前端增强脚本，在手机网页左侧自动增加 **文件夹抽屉管理** 与 **常用 Prompt 库**。

## 🚀 如何在 GitHub 上一键免费在线编译 APK？

本项目已经配置好 **GitHub Actions**（无需安装任何本地 Android 开发环境）：

1. 打开 GitHub，点击右上角 **「New repository」** 新建一个仓库（例如 `voyager-web-enhancer`）。
2. 将本工程的所有文件（**包括隐藏的 `.github` 文件夹**）全部上传/推送到新仓库。
3. 点击仓库上方的 **「Actions」** 选项卡。
4. 你会看到名为 `自动编译 Voyager Xposed 增强模块 APK` 的任务已经在自动运行！
5. 约 1 分钟编译完毕后，点击任务详情，在下方的 **Artifacts** 直接下载编译好的 `Voyager-Web-Enhancer-APK.zip`，解压即可安装使用！

## 📱 手机端使用

1. 手机需安装 LSPosed。
2. 安装生成的 APK，打开 LSPosed，勾选启用此模块。
3. 在作用域中勾选你想增强的浏览器（如 Chrome、Edge、Via、X浏览器）或官方 App。
4. 使用该浏览器打开 `chatgpt.com`、`gemini.google.com` 或 `chat.deepseek.com`，屏幕左侧即可看到绿色文件夹悬浮球！
