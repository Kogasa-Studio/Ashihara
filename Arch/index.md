# Ashihara Mod — 设计文档总索引

> **项目**：Ashihara (`kogasastudio.ashihara`)
> **平台**：Minecraft 1.21 · NeoForge 21.1.125 · Java 21
> **版本**：`0.0.0.15`（早期开发）
> **许可证**：GPL 3.0
> **作者**：KogasaStudio
> **文档更新**：2026-04-17

---

## 关于本文档体系

本目录下的所有文件构成 Ashihara Mod 的**产品需求文档（PRD）**与**技术设计文档**体系。文档面向两类读者：

- **设计者**：了解整个 mod 的功能目标、游戏设计意图和内容规划
- **开发者（含 AI）**：获取足够精确的规范，以便在无额外说明的情况下直接实现功能

### 文档组织原则

1. **功能模块为主轴**：一级目录按功能模块划分，便于查找与维护
2. **阶段信息在模块中标注**：每份文档中用 `[阶段 N]` 标签说明该内容归属的开发阶段
3. **跨阶段内容单独标注**：若某功能跨越多个阶段，在其文档中注明 `[与阶段无关]` 或 `[阶段 N~M]`
4. **阶段文档作为总纲**：`07-Development-Phases/` 中的文档是每个阶段的内容全览，指向各模块文档

---

## 📁 目录总览

### [00 · 项目总览](./00-Overview/)
项目愿景、整体架构与术语表。**建议首先阅读。**

| 文档 | 说明 |
|------|------|
| [vision.md](./00-Overview/vision.md) | 项目愿景、核心理念、五阶段总览 |
| [architecture.md](./00-Overview/architecture.md) | 代码架构、模块依赖关系图、技术选型 |
| [glossary.md](./00-Overview/glossary.md) | 术语表（中英日对照） |
| [naming-and-code-style.md](./00-Overview/naming-and-code-style.md) | 命名规范、代码风格、历史代码迁移规则 |

---

### [01 · 内容基础系统](./01-Content-Foundation/)
物品、方块、配方、音效与世界生成等基础游戏内容的规范。

| 文档 | 说明 | 主要阶段 |
|------|------|---------|
| [items.md](./01-Content-Foundation/items.md) | 所有物品的规范（材料、食物、工具、特殊物品） | 0~3 |
| [blocks.md](./01-Content-Foundation/blocks.md) | 所有方块的规范（装饰、功能、农业方块） | 0~2 |
| [recipes.md](./01-Content-Foundation/recipes.md) | 原版与自定义配方系统总览 | 0~2 |
| [sounds.md](./01-Content-Foundation/sounds.md) | 音效注册与使用规范 | 0~3 |
| [worldgen.md](./01-Content-Foundation/worldgen.md) | 世界生成特性、生物群系、树木、野生作物 | 0, 2 |

---

### [02 · 建筑系统](./02-Building-System/)
Ashihara 的核心特色系统之一。涵盖全新建造机制、特殊方块实体、渲染方案与模组兼容。内容体量大，独立成目录。

| 文档 | 说明 | 主要阶段 |
|------|------|---------|
| [overview.md](./02-Building-System/overview.md) | 建筑系统设计哲学、总体架构与目标 | 1~2 |
| [building-blocks.md](./02-Building-System/building-blocks.md) | 建筑专用方块规范（梁、柱、墙、窗等） | 1~2 |
| [building-mechanics.md](./02-Building-System/building-mechanics.md) | 建造交互机制、多方块结构、拼合逻辑 | 2 |
| [block-entities.md](./02-Building-System/block-entities.md) | 建筑系统涉及的方块实体规范 | 1~2 |
| [rendering.md](./02-Building-System/rendering.md) | 建筑方块的渲染策略、自定义模型、Render Type | 与阶段无关 |
| [mod-compatibility.md](./02-Building-System/mod-compatibility.md) | 与 Sodium / Iris / WorldEdit 的兼容方案 | 与阶段无关 |

---

### [03 · 玩法系统](./03-Gameplay-Systems/)
驱动核心游戏体验的各类机制，从农业到神道研究。

| 文档 | 说明 | 主要阶段 |
|------|------|---------|
| [agriculture.md](./03-Gameplay-Systems/agriculture.md) | 水稻与农业种植机制、稻田、灌溉 | 0 |
| [crafting-stations.md](./03-Gameplay-Systems/crafting-stations.md) | 工作台系统：切菜板、研钵、磨粉机、灶台等 | 0~1 |
| [food-system.md](./03-Gameplay-Systems/food-system.md) | 食物制作、食用效果与料理系统 | 0~1 |
| [combat.md](./03-Gameplay-Systems/combat.md) | 战斗系统扩展：武器类型、攻击前摇/后摇动作 | 3 |
| [shinto-research.md](./03-Gameplay-Systems/shinto-research.md) | 神道研究玩法：产灵系统、研究树、剧情触发 | 3~4 |

---

### [04 · 玩家交互系统](./04-Player-Interaction/)
玩家与 mod 内容交互的界面层：指南书、UI 与动画。

| 文档 | 说明 | 主要阶段 |
|------|------|---------|
| [guidebook-system.md](./04-Player-Interaction/guidebook-system.md) | 指南书（大百科）系统：数据格式、页面类型、渲染 | 与阶段无关（内容跨 0~3） |
| [ui-systems.md](./04-Player-Interaction/ui-systems.md) | 容器 UI、提示框、Screen 规范 | 与阶段无关 |
| [animations.md](./04-Player-Interaction/animations.md) | 玩家动画系统：持握、挥砍前摇、GeckoLib 集成 | 与阶段无关 |

---

### [05 · 技术基础设施](./05-Technical-Foundation/)
支撑整个 mod 的底层技术模块。

| 文档 | 说明 |
|------|------|
| [registry-system.md](./05-Technical-Foundation/registry-system.md) | 注册系统规范（方块、物品、BE、配方等） |
| [network.md](./05-Technical-Foundation/network.md) | 网络包系统规范（Payload、服务端/客户端处理） |
| [capabilities.md](./05-Technical-Foundation/capabilities.md) | NeoForge Capability 系统使用规范 |
| [data-components.md](./05-Technical-Foundation/data-components.md) | 物品 DataComponent 定义与使用规范 |
| [mixin-system.md](./05-Technical-Foundation/mixin-system.md) | Mixin 使用策略、现有 Mixin 清单与说明 |
| [config-system.md](./05-Technical-Foundation/config-system.md) | ⚠️ **待定**：在 Phase 1 完成后细化 |

---

### [06 · 渲染系统](./06-Rendering-Systems/)
独立于建筑系统的通用渲染基础设施。

| 文档 | 说明 |
|------|------|
| [render-types.md](./06-Rendering-Systems/render-types.md) | 自定义 Render Type 与 Atlas 管理 |
| [particles.md](./06-Rendering-Systems/particles.md) | 粒子系统：樱花、枫叶、稻米粒子 |
| [shaders.md](./06-Rendering-Systems/shaders.md) | 着色器注册与使用规范 |
| [geo-rendering.md](./06-Rendering-Systems/geo-rendering.md) | GeckoLib 模型渲染集成（指南书、特殊物品） |

---

### [07 · 开发阶段规划](./07-Development-Phases/)
以五阶段开发周期为视角，汇总每阶段的内容目标与各模块文档的对应关系。

| 文档 | 阶段 | 核心主题 |
|------|------|---------|
| [phase-0-agriculture.md](./07-Development-Phases/phase-0-agriculture.md) | Phase 0 | 古代日本农业：水稻、基础生存、指南书引入 |
| [phase-1-craftsmanship.md](./07-Development-Phases/phase-1-craftsmanship.md) | Phase 1 | 手工业与民生：食物系统、工作台、木工建筑 |
| [phase-2-exploration.md](./07-Development-Phases/phase-2-exploration.md) | Phase 2 | 世界探索：建筑系统、生物群系、村庄与城郭生成 |
| [phase-3-investigation.md](./07-Development-Phases/phase-3-investigation.md) | Phase 3 | 探索与战斗：剧情主线、神道研究引入 |
| [phase-4-conclusion.md](./07-Development-Phases/phase-4-conclusion.md) | Phase 4 | 神道研究深入：产灵系统、剧情结局 |

---

### [08 · 质量与支持](./08-Quality-Support/)

| 文档 | 说明 |
|------|------|
| [performance-requirements.md](./08-Quality-Support/performance-requirements.md) | 性能指标与基准 |
| [testing-strategy.md](./08-Quality-Support/testing-strategy.md) | 测试规范与覆盖目标 |
| [release-roadmap.md](./08-Quality-Support/release-roadmap.md) | 发布路线图与版本命名规则 |

---

## 🏷️ 标签约定

文档中使用以下标签标注内容状态：

| 标签 | 含义 |
|------|------|
| `[阶段 N]` | 该内容仅属于第 N 开发阶段 |
| `[阶段 N~M]` | 该内容跨越第 N 至第 M 阶段 |
| `[与阶段无关]` | 该内容是基础设施，不归属于特定阶段 |
| `✅ 已实现` | 代码中已有基本实现 |
| `🚧 进行中` | 代码中有框架但未完成 |
| `📋 待规划` | 尚未开始实现，需设计 |
| `⚠️ 待定` | 设计方向未确定，需进一步讨论 |

---

## 当前完成状态速览

| 模块 | 文档状态 | 代码状态 |
|------|---------|---------|
| 内容基础 | 🚧 编写中 | 🚧 进行中 |
| 建筑系统 | 🚧 编写中 | 🚧 进行中 |
| 农业系统 | 🚧 编写中 | ✅ 基本实现 |
| 工作台系统 | 🚧 编写中 | ✅ 基本实现 |
| 指南书系统 | 🚧 编写中 | 🚧 进行中 |
| 渲染系统 | 🚧 编写中 | 🚧 进行中 |
| 神道研究 | 📋 待规划 | 📋 待开始 |
| 配置系统 | ⚠️ 待定 | 📋 待开始 |

