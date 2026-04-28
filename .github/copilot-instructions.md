# Ashihara Mod - AI Agent 工作区指引

本文件由 GitHub Copilot 自动加载，适用于此工作区内所有 session。

## 工作区结构

```
Ashihara_1.21/
├── src/                        本项目源码（主要编辑区）
├── src-references/             只读参考源码（禁止修改）
│   ├── neoforge-21.1.125-sources/
│   │   ├── net/minecraft/      Minecraft 原版源码
│   │   └── net/neoforged/      NeoForge API 源码
│   └── geckolib-1.21.1/        GeckoLib 源码
└── Arch/                       设计文档和规范（实现前必读）
    ├── index.md                文档总导航
    ├── 00-Overview/
    ├── 01-Content-Foundation/
    ├── 02-Building-System/
    ├── 03-Gameplay-Systems/
    ├── 04-Player-Interaction/
    ├── 05-Technical-Foundation/
    │   └── 3d-ui-framework/    3D UI 框架核心文档
    ├── 06-Rendering-Systems/
    ├── 07-Development-Phases/
    └── 08-Quality-Support/
```

## 必须遵守的查阅规则

### 规则1：实现任何功能前，先查阅 `E:\WORKSPACE\Ashihara_1.21\Arch\` 文档

在开始编写代码之前，必须执行以下步骤：
1. 搜索 `E:\WORKSPACE\Ashihara_1.21\Arch\` 目录，确认是否有与当前任务相关的设计文档
2. 如果有，完整阅读对应文档后再实现
3. 实现必须符合文档中的设计决策和接口定义

快速入口：
- 总导航：`E:\WORKSPACE\Ashihara_1.21\Arch\index.md`
- 3D UI 框架：`E:\WORKSPACE\Ashihara_1.21\Arch\05-Technical-Foundation\3d-ui-framework\README.md`
- 文档规则：`E:\WORKSPACE\Ashihara_1.21\Arch\05-Technical-Foundation\DOCUMENTATION-RULES.md`

### 规则2：查阅源码时，到 `E:\WORKSPACE\Ashihara_1.21\src-references\` 找，而不是猜

需要了解 Minecraft / NeoForge / GeckoLib 的 API 时：

| 要查什么 | 去哪里找 |
|---|---|
| Minecraft 原版类（如 Block、Entity、Screen） | `E:\WORKSPACE\Ashihara_1.21\src-references\neoforge-21.1.125-sources\net\minecraft\` |
| NeoForge API（如 Capability、Event、PacketHandler） | `E:\WORKSPACE\Ashihara_1.21\src-references\neoforge-21.1.125-sources\net\neoforged\` |
| GeckoLib（如 GeoModel、GeoRenderer、GeoBone） | `E:\WORKSPACE\Ashihara_1.21\src-references\geckolib-1.21.1\` |

**不要凭记忆猜测 API**，实际读取源码文件后再编写调用代码。

### 规则3：src-references/ 只读

- 不得修改 `src-references/` 下的任何文件
- 仅用于阅读和理解 API

## 项目技术栈

- Minecraft: 1.21.1
- 模组加载器: NeoForge 21.1.125
- 动画库: GeckoLib 4.x（1.21.1 分支）
- 语言: Java
- 构建工具: Gradle

## 代码风格

参考：`E:\WORKSPACE\Ashihara_1.21\Arch\00-Overview\naming-and-code-style.md`

## 开始任务时的检查清单

1. [ ] 搜索 `E:\WORKSPACE\Ashihara_1.21\Arch\` 中是否有相关文档
2. [ ] 如有文档，阅读完毕后再实现
3. [ ] 需要查 API 时，打开 `E:\WORKSPACE\Ashihara_1.21\src-references\` 中对应的源码文件
4. [ ] 实现完成后，验证与 Arch 文档的设计一致性

