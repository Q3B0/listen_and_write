# 听写小助手 (Listen and Write) — Android 原生英语单词听写 App

<p align="center">
  <img src="app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" width="100" height="100" alt="App Icon" />
</p>

<p align="center">
  <b>面向小学生的 Android 原生英语单词听写与背诵神器</b><br>
  清爽蓝调儿童友好界面 · 纯正英美真人原声 · 离线持久缓存 · 纸笔/拼写双模式 · 智能错词本
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208.0+-2388F8?style=flat-square&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin%202.0-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-20BF75?style=flat-square" alt="Material 3" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-FFCA58?style=flat-square" alt="MVVM" />
  <img src="https://img.shields.io/badge/Audio-Dual%20Engine%20(Human%20%2B%20TTS)-1673E6?style=flat-square" alt="Audio" />
  <img src="https://img.shields.io/badge/Offline-100%25%20Privacy%20Safe-success?style=flat-square" alt="Offline Safe" />
</p>

---

## 📖 项目简介

「听写小助手」是一款专为小学生量身研发的原生 Android 英语单词听写伴侣。严格对照 `docs/ui-reference.png` 视觉参考规范设计，具备统一的清新浅蓝圆角卡片风格、生动的儿童阅读与耳机声波插画，以及贴近小学生认知的低认知负荷交互。

核心设计理念：
- **纯正地道发音**：引入母语者**真人原声朗读**与**本地持久离线缓存**，彻底告别生硬机械音。
- **保护视力与专注**：首创「纸笔听写」沉浸播放模式，让孩子听声音在纸上写，**绝不提前剧透答案**。
- **全流程闭环**：选书 ➔ 选单元 ➔ 听写设置 ➔ 智能听写 ➔ 正误批改 ➔ 错词专项强化 ➔ 学习统计折线图。
- **绝对隐私与离线**：不依赖远程 AI 服务，无须注册登录，无广告干扰，支持纯离线/飞行模式使用。

---

## 🎨 视觉系统与设计规范

设计风格定位于：**清新、简洁、柔和、儿童友好、现代教育类 App**。

### 1. 色彩规范
| 角色 | 色值 | 用途说明 |
|---|---|---|
| **主品牌蓝** | `#2388F8` | 核心品牌色、高亮按钮、选框激活、声波光晕 |
| **深蓝强调** | `#1673E6` | 按钮点击态、重音强调、高亮指示线 |
| **页面背景** | `#F7FAFE` | 全局页面背景底色，柔和护眼 |
| **卡片背景** | `#FFFFFF` | 悬浮功能卡片、列表项白底（圆角 `16–20dp`） |
| **浅蓝底色** | `#EAF4FF` | 插画背景、功能徽章、小组件底色 |
| **成功反馈绿** | `#20BF75` | 正确反馈卡片、对勾徽标（浅底 `#EAF8F0`） |
| **错误提示红** | `#FF5C60` | 错误反馈卡片、错词对比标记（浅底 `#FFF0F1`） |
| **温馨黄色** | `#FFCA58` | 单词包卡片、金牌与奖杯插画 |
| **标题文字** | `#182638` | 大标题、突出数字（20–28sp SemiBold/Bold） |
| **正文文字** | `#435266` | 释义、描述正文（14–16sp） |
| **次级文字** | `#94A3B8` | 单元进度、副标题、辅助提示（12–13sp） |

### 2. 界面细节与交互
- **全面屏沉浸式适配**：适配 Android Edge-to-Edge 全面屏特性，顶部标题栏自动适配状态栏（避让摄像头和电量栏），底部吸底按钮自动避让手势导航横条（`navigationBarsPadding`）。
- **高对比度按钮**：所有主按钮文字显式强化为纯白（`#FFFFFF`），避免浅底低对比度。
- **动态声波律动**：在纸笔听写与屏幕拼写页面，随着朗读实时触发声波扩散光晕动画。

---

## 📱 核心功能页面全览 (11 大页面)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            听写小助手 页面流转架构图                           │
└─────────────────────────────────────────────────────────────────────────────┘

       [首页 HomeScreen]
          ├── 1. 开始听写 ────> [单元选择] ────> [听写设置] ────> [听写进行中]
          │                                                          │
          │                        ┌─────────────────────────────────┴──┐
          │                        │ (纸笔模式)                  (拼写模式)│
          │                        ▼                                    ▼
          │               [纸笔听写 Paper]                     [屏幕拼写 Input]
          │                        │                                    │
          │                        │                       ┌────────────┴──┐
          │                        │                       ▼               ▼
          │                        │                  [正确反馈]       [错误反馈]
          │                        │                       │               │
          │                        └───────────────┬───────┴───────────────┘
          │                                        ▼
          │                                 [听写结果 Result]
          │                                  (成绩统计/自查纠错)
          │
          ├── 2. 单词包 WordBook ──> (教材词库 / 我的词包 / JSON 导入)
          ├── 3. 错词本 Mistakes ──> (全部 / 按单元 / 按时间 / 错词专项听写)
          └── 4. 个人中心 Mine ────> (发音品质切换 / 离线缓存管理 / 系统 TTS 引导)
```

| 序号 | 页面名称 | 路由 | 核心功能与设计亮点 |
|:---:|---|---|---|
| **1** | **首页** | `home` | 问候语「Hi，小朋友」、原创儿童草地阅读矢量插画、四大彩色入口卡片、底部导航条 |
| **2** | **教材选择** | `word_book` | 三大 Tab（教材词库/我的词包/导入词包）；内置人教版 PEP 3-6 年级全套教材卡片；实时关键词搜索 |
| **3** | **单元选择** | `unit_selection/{id}` | 显示教材全称与单元列表；支持单选/多选/全选联动；底部动态统计「已选 X 个单元 · Y 词」 |
| **4** | **听写设置** | `dictation_settings/...` | 数量选择 Pills（全部/10/20/30/50）、朗读次数/间隔步进器、美音/英音切换、**发音模式切换**（真人/TTS）、纸笔与拼写双模式卡片 |
| **5** | **纸笔听写** | `dictation_session` | 沉浸式听写：3/20 进度与细进度条、大耳机与声波光晕动画；上一个/暂停/下一个/重读/慢速；**不提前显示单词**；退出二次确认 |
| **6** | **屏幕拼写** | `dictation_session` | 耳机朗读动画、即时重听按钮、英文软键盘输入框、一键清空、首字母智能提示、回车即提交 |
| **7** | **正确反馈** | `dictation_session` | 浅绿反馈卡片、大号英文单词（32sp Bold）、音标及重读按键、中文释义、自动/手动下一词 |
| **8** | **错误反馈** | `dictation_session` | 浅红反馈卡片、正确答案对比、用户输入（红色高亮标记）、自动加入错词本、重听与下一词 |
| **9** | **听写结果** | `dictation_session` | 金色奖杯插画、大字成绩展示、用时统计、纸笔模式逐词对照自查勾选、一键查看错词与再听一次 |
| **10** | **错词本** | `mistakes` | 全部/按单元/按时间三分页；单词发音、音标释义、错误频次、掌握标记；底部「错词专项听写」一键启动复习 |
| **11** | **学习记录** | `learning_history` | 周/月/全统计切换；Canvas 贝塞尔平滑趋势折线图与数据 Tooltip；最近听写历史卡片 |
| **12** | **个人中心** | `mine` | 用户专属卡片、发音品质切换、教材发音包一键离线预下载、系统 TTS 引擎检测与跳转引导 |

---

## 🔊 双音频发音引擎 (`TtsManager`)

针对传统 Android TTS 在不同手机 ROM 上发音生硬、机械感强甚至缺失英式发音的痛点，全新重构实现了**真人原声 + 优化本地 TTS 自适应双引擎**：

```
                              ┌───────────────────┐
                              │  用户触发单词播放  │
                              └─────────┬─────────┘
                                        │
                         ┌──────────────┴──────────────┐
                         ▼                             ▼
              【模式 A：真人原声(推荐)】        【模式 B：系统本地 TTS】
                         │                             │
                  检查本地离线缓存                       │
                  (context.filesDir)                   │
                         │                             │
             ┌───────────┴───────────┐                 │
          (已缓存)                (未缓存)              │
             │                       │                 │
             ▼                       ▼                 │
      MediaPlayer 立即播放        尝试网络下载           │
      (100% 离线 · 0 延时)      (有道/牛津高质量音频)     │
                                     │                 │
                           ┌─────────┴─────────┐       │
                        (下载成功)          (断网/离线)  │
                           │                   │       │
                           ▼                   └───────┼──────┐
                     持久化写入本地缓存                 │      │
                     并立即播放音频                    ▼      ▼
                                              智能筛选本地最优 Voice
                                              (高采样率 · 神经网络人声)
                                                       │
                                                       ▼
                                              原生 TextToSpeech 播放
```

### 亮点特性：
1. **纯正英美音标母语者原声**：支持 US（美式英语）与 UK（英式英语）真人口腔发音。
2. **持久化离线缓存**：只要朗读过的单词自动永久保存在本地，第二次复习 **100% 离线、0 流量、0 延时**。
3. **一键离线预下载**：在「个人中心」点击「一键预下载教材发音」，仅需约 1MB 存储空间，半分钟内将全书全部单词真人发音永久下载到手机，飞行模式随意听。
4. **高质量神经网络人声优选**：若选用系统 TTS，引擎自动遍历 `tts.voices` 并优选高品质（`QUALITY_VERY_HIGH`、`neural`/`natural`）语音，并配置 `AudioAttributes.CONTENT_TYPE_SPEECH` 消除杂音。
5. **系统 TTS 一键引导**：检测手机当前 TTS 引擎（如 Google 语音服务、三星 TTS 等），提供一键直达系统语音设置的快捷入口。

---

## 🗄 数据库与架构设计

采用 Google 推荐的 **MVVM + Repository + Flow** 架构模式。

```
com.codex.listenandwrite
├── data
│   ├── dao             // Room 数据访问接口 (Textbook, Unit, Word, Dictation, Mistake, WordBook)
│   ├── entity          // 数据实体表 (8 张 Room 数据表)
│   ├── local           // AppDatabase 数据库配置, SampleData 预置教材数据, PreferencesManager (DataStore)
│   └── repository      // 数据仓库层 (WordRepository, DictationRepository, MistakeRepository)
├── di                  // AppContainer 依赖注入容器
├── domain/model        // 业务领域模型 (DictationConfig, DictationMode, VoiceSource, SpeechAccent)
├── tts                 // TtsManager (真人原声 + TTS 引擎封装)
└── ui
    ├── components      // 公共组件 (AppTopBar, AppBottomNav, AppButtons, RoundedCard, 插画组件)
    ├── dictation       // 听写模块 (PaperScreen, InputScreen, CorrectScreen, WrongScreen, DictationViewModel)
    ├── history         // 学习记录模块 (LearningHistoryScreen, LineChartView)
    ├── home            // 首页模块 (HomeScreen)
    ├── mine            // 个人中心模块 (MineScreen)
    ├── mistakes        // 错词本模块 (MistakeBookScreen, MistakeBookViewModel)
    ├── navigation      // 导航路由图 (AppNavGraph, Screen)
    ├── result          // 听写结果模块 (DictationResultScreen)
    ├── settings        // 听写设置模块 (DictationSettingsScreen, DictationSettingsViewModel)
    ├── theme           // 主题配色 (Color, Dimensions, Theme, Type)
    └── unit            // 单元选择模块 (UnitSelectionScreen, UnitSelectionViewModel)
```

---

## 📥 自定义词包导入格式 (JSON)

在「单词包」➔「导入词包」标签页中，可直接粘贴自制词单一键导入：

```json
{
  "title": "五年级核心冲刺词包",
  "description": "单元高频考点重点单词",
  "words": [
    {
      "word": "family",
      "phonetic": "['fæməli]",
      "translation": "家庭，家人",
      "unit": "Unit 2"
    },
    {
      "word": "giraffe",
      "phonetic": "[dʒəˈrɑːf]",
      "translation": "长颈鹿",
      "unit": "Unit 3"
    },
    {
      "word": "watermelon",
      "phonetic": "[ˈwɔːtəmelən]",
      "translation": "西瓜",
      "unit": "Unit 5"
    }
  ]
}
```

项目根目录备有示例文件：[`sample_wordbook.json`](sample_wordbook.json)。

---

## 🛠 编译与运行

### 1. 开发环境要求
- **操作系统**：Windows / macOS / Linux
- **JDK**：Java 17 或 Java 21（推荐 Adoptium Eclipse Temurin 21）
- **Android SDK**：Platforms `android-33` 至 `android-36`，Build-Tools `35.0.0`
- **Gradle**：8.10.2（内置 Wrapper，已配置国内镜像）

### 2. 执行单元测试
```powershell
.\gradlew.bat testDebugUnitTest
```
所有关于拼写归一化（忽略大小写、首尾空格、弯直引号混用）、抽词配置约束、词包 JSON 解析的测试用例均可一键验证。

### 3. 构建 Debug APK
```powershell
.\gradlew.bat assembleDebug
```
构建成功后生成的安装包位于：
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 开源与免责声明

本项目为独立开发的原创教育类开源项目，致力于为广大小学生和家长提供清爽无扰的听写工具。词库数据基于公开标准词汇整理，应用不包含任何侵犯第三方商业教材受保护版面的素材。
