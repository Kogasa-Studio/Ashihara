# 物品系统

> **标签**：[阶段 0~3]
> **状态**：🚧 编写中

---

## 实现命名与风格约束

- 命名与格式必须遵循 ../00-Overview/naming-and-code-style.md。
- 接口统一使用 I 前缀；方块与物品类名分别使用 Block / Item 后缀。
- BlockEntity 渲染器统一使用 BER 后缀（历史术语 TER 仅用于兼容说明）。
- 大括号采用 Allman 风格；长参数和长调用链需按缩进展开。

---

## 概述

Phase 0 的稻作物品链已从“稻谷原料”打通到“白米/熟米食物”。

## 水稻物品链（已完成）

| 阶段 | 注册ID | 代码符号/类 | 资源文件（示例） | 状态 |
|------|------|------|------|------|
| 种源 | `ashihara:rice_seedling` | `Items.RICE_SEEDLING` -> `RiceSeedling` | `src/main/resources/assets/ashihara/models/item/rice_seedling.json`<br>`src/main/resources/assets/ashihara/textures/item/rice_seedling.png` | ✅ 已实现 |
| 收获物 | `ashihara:rice_crop_item` | `Items.RICE_CROP` -> `AshiharaItem` | `src/main/resources/assets/ashihara/models/item/rice_crop_item.json`<br>`src/main/resources/assets/ashihara/textures/item/rice_crop_item.png` | ✅ 已实现 |
| 晾晒产物 | `ashihara:dried_rice_crop` | `Items.DRIED_RICE_CROP` -> `AshiharaItem` | `src/main/resources/assets/ashihara/models/item/dried_rice_crop.json`<br>`src/main/resources/assets/ashihara/textures/item/dried_rice_crop.png` | ✅ 已实现 |
| 稻谷 | `ashihara:paddy` | `Items.PADDY` -> `Paddy` | `src/main/resources/assets/ashihara/models/item/paddy.json`<br>`src/main/resources/assets/ashihara/textures/item/paddy.png` | ✅ 已实现 |
| 稻草 | `ashihara:straw` | `Items.STRAW` -> `AshiharaItem` | `src/main/resources/assets/ashihara/models/item/straw.json`<br>`src/main/resources/assets/ashihara/textures/item/straw.png` | ✅ 已实现 |
| 稻堆中间品 | `ashihara:paddy_pile` | `Items.PADDY_PILE` -> `AshiharaItem` | `src/main/resources/assets/ashihara/models/item/paddy_pile.json`<br>`src/main/resources/assets/ashihara/textures/item/paddy_pile.png` | ✅ 已实现 |
| 糙米 | `ashihara:brown_rice` | `Items.BROWN_RICE` -> `AshiharaItem` | `src/main/resources/assets/ashihara/models/item/brown_rice.json`<br>`src/main/resources/assets/ashihara/textures/item/brown_rice.png` | ✅ 已实现 |
| 白米 | `ashihara:rice` | `Items.RICE` -> `AshiharaItem` | `src/main/resources/assets/ashihara/models/item/rice.json`<br>`src/main/resources/assets/ashihara/textures/item/rice.png` | ✅ 已实现 |
| 熟米（食物） | `ashihara:cooked_rice` | `Items.COOKED_RICE` -> `EasyFood` | `src/main/resources/assets/ashihara/models/item/cooked_rice.json`<br>`src/main/resources/assets/ashihara/textures/item/cooked_rice.png` | ✅ 已实现 |

## 当前命名偏差（已记录）

以下历史命名不符合 `*Item` 规范，后续在不破坏存档/兼容前提下重构：

- `Paddy`（建议目标：`PaddyItem`）
- `RiceSeedling`（建议目标：`RiceSeedlingItem`）

## 其他已添加作物物品（暂未形成完整玩法用途）

- 大豆：`ashihara:soy_bean`（`Items.SOY_BEAN`）
- 山芋：`ashihara:sweet_potato`（`Items.SWEET_POTATO`）
- 黄瓜：`ashihara:cucumber`（`Items.CUCUMBER`）

资源文件（示例）：

- `src/main/resources/assets/ashihara/models/item/soy_bean.json`
- `src/main/resources/assets/ashihara/models/item/sweet_potato.json`
- `src/main/resources/assets/ashihara/models/item/cucumber.json`

> 注：以上作物当前“可种植/可采集”已具备，后续需要通过加工配方和料理系统绑定实际价值。
