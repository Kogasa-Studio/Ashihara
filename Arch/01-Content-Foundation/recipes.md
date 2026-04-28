# 配方系统

> **标签**：[阶段 0~2]
> **状态**：📋 待规划

---

## 实现命名与风格约束

- 命名与格式必须遵循 ../00-Overview/naming-and-code-style.md。
- 接口统一使用 I 前缀；方块与物品类名分别使用 Block / Item 后缀。
- BlockEntity 渲染器统一使用 BER 后缀（历史术语 TER 仅用于兼容说明）。
- 大括号采用 Allman 风格；长参数和长调用链需按缩进展开。

---

## 概述

当前稻作链路的核心加工配方已存在（主要在 `src/generated/resources`），可支持“稻作中间产物 -> 糙米/白米/米粉”等基础流程。

## 水稻相关配方（已存在）

### 研钵/舂（Mortar）

- `src/generated/resources/data/ashihara/recipes/mortar/mortar_rice_unthreshing.json`
- `src/generated/resources/data/ashihara/recipes/mortar/mortar_rice_unthreshing_2x.json`
- `src/generated/resources/data/ashihara/recipes/mortar/mortar_rice_unthreshing_3x.json`

### 石磨（Mill）

- `src/generated/resources/data/ashihara/recipes/mill/mill_rice.json`
- `src/generated/resources/data/ashihara/recipes/mill/mill_rice_powder.json`

## 稻作配方链路状态

- 稻作相关物品链本体：✅ 已打通（稻谷/糙米/白米/米粉等节点已存在）。
- 与工作方块 UI 的完整交互体验：🚧 待 3D UI 库完成后收尾。
- 最终玩家可感知的引导流程：🚧 需在指南书章节中补齐步骤说明。

## 工作机器状态说明（阶段口径）

涉及的主要工作方块：

- `ashihara:mill`（石磨）
- `ashihara:mortar`（舂/研钵）
- `ashihara:dirt_cookstove`（土灶）
- `ashihara:pot`（土锅）

> 注：配方文件已具备基础数据，但“3D UI + 操作反馈 + 教程引导”尚未全部完成，因此在 PRD 中归类为“流程可验证、体验未完工”。
