# 方块系统

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

当前农业方块中，水稻主链已具备完整可用性（稻田 -> 秧苗/野生稻 -> 成熟稻作 -> 后处理方块）。

## 水稻相关方块（已完成）

| 设计名 | 注册ID | 代码符号/类 | 资源文件（示例） | 状态 |
|------|------|------|------|------|
| WaterField（稻田） | `ashihara:water_field` | `Blocks.WATER_FIELD` -> `PaddyFieldBlock` | `src/main/resources/assets/ashihara/blockstates/water_field.json`<br>`src/main/resources/assets/ashihara/models/block/water_field.json`<br>`src/main/resources/assets/ashihara/textures/block/water_field.png` | ✅ 已实现 |
| WildRiceCrop（野生稻） | `ashihara:wild_rice` | `Blocks.WILD_RICE` -> 匿名 `ChrysanthemumBushBlock` 子类 | `src/main/resources/assets/ashihara/blockstates/wild_rice.json`<br>`src/main/resources/assets/ashihara/models/block/wild_rice.json`<br>`src/main/resources/assets/ashihara/textures/block/wild_rice.png` | ✅ 已实现 |
| RiceSeedling（秧苗作物方块） | `ashihara:immature_rice` | `Blocks.IMMATURE_RICE` -> `ImmatureRiceCropBlock` | `src/main/resources/assets/ashihara/blockstates/immature_rice.json`<br>`src/main/resources/assets/ashihara/models/block/rice_seedlings_age0_1.json`（及同系列）<br>`src/main/resources/assets/ashihara/textures/block/crops/rice_seedlings_age0.png` | ✅ 已实现 |
| RiceCrop（水稻作物方块） | `ashihara:rice_crop` | `Blocks.RICE_CROP` -> `RiceCropBlock` | `src/main/resources/assets/ashihara/blockstates/rice_crop.json`<br>`src/main/resources/assets/ashihara/models/block/rice_crop_age0_1.json`（及同系列）<br>`src/main/resources/assets/ashihara/textures/block/crops/rice_crop_age0.png` | ✅ 已实现 |
| RiceDryingSticks（晾晒架） | `ashihara:rice_drying_sticks` | `Blocks.RICE_DRYING_STICKS` -> `RiceDryingSticksBlock` | `src/main/resources/assets/ashihara/blockstates/rice_drying_sticks.json`<br>`src/main/resources/assets/ashihara/models/block/rice_drying_sticks_single.json`<br>`src/main/resources/assets/ashihara/textures/block/rice_drying_sticks.png` | ✅ 已实现 |

## 农业工作方块（功能未完工）

以下方块已注册并有模型资源，但完整交互依赖 3D UI 库完成后推进：

- 石磨：`ashihara:mill`（`Blocks.MILL` -> `MillBlock`）
- 舂/研钵：`ashihara:mortar`（`Blocks.MORTAR` -> `MortarBlock`）
- 土灶：`ashihara:dirt_cookstove`（`Blocks.DIRT_COOKSTOVE` -> `DirtCookStoveBlock`）
- 土锅：`ashihara:pot`（`Blocks.POT` -> `PotBlock`）

对应资源（示例）：

- `src/main/resources/assets/ashihara/blockstates/mill.json`
- `src/main/resources/assets/ashihara/blockstates/mortar.json`
- `src/main/resources/assets/ashihara/blockstates/dirt_cookstove.json`
- `src/main/resources/assets/ashihara/blockstates/pot.json`

## 其他已添加作物方块（暂未形成用途链）

- 大豆：`ashihara:soy_beans`（`Blocks.SOY_BEANS`）
- 山芋：`ashihara:sweet_potatoes`（`Blocks.SWEET_POTATOES`）
- 黄瓜：`ashihara:cucumbers`（`Blocks.CUCUMBERS`）

资源文件（示例）：

- `src/main/resources/assets/ashihara/blockstates/soy_beans.json`
- `src/main/resources/assets/ashihara/blockstates/sweet_potatoes.json`
- `src/main/resources/assets/ashihara/blockstates/cucumbers.json`

> 注：作物方块资源齐全，当前主要缺口为配方与加工链路的玩法闭环设计。
