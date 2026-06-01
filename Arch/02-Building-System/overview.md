# 建筑系统：总览

> **标签**：[阶段 1~2]
> **状态**：🚧 进行中
> **最后更新**：2026-06-01

---

## 实现命名与风格约束

- 命名与格式必须遵循 ../00-Overview/naming-and-code-style.md。
- 接口统一使用 `I` 前缀；方块与物品类名分别使用 `Block` / `Item` 后缀。
- BlockEntity 渲染器统一使用 `BER` 后缀。
- 大括号采用 Allman 风格；长参数和长调用链需按缩进展开。

---

## 概述

建筑系统是 Ashihara 最核心的特色模块，目标是在 Minecraft 中实现高还原度的日本传统建筑构件与建造体验。

### 核心设计理念

**子方块组件系统**：摒弃「一个方块一个模型」的传统方式，改为在单个 `BaseMultiBuiltBlock` 容器方块内组合 80+ 种 `BuildingComponent`，实现传统木结构建筑中「柱、梁、肘木、斗、垂木、壁、床」等构件在亚方块级别的自由组装。

### 架构总览

```
BuildingComponent（抽象基类）
  ├── 定义: id, Type, shape, occupation (3×4×3 网格), model
  ├── 子类 30+ 种: Column, Beam, Clamp, Hijiki, Tou, Wall, Floor, Rafter...
  └── 接口: Connectable（自动连接）, Interactable（右键交互）, Decoratable（装饰）

BaseMultiBuiltBlock extends Block implements EntityBlock, SimpleWaterloggedBlock
  ├── ComponentMaterial 内部枚举（13 种材质，优先级制）
  ├── 属性: FACING, WATERLOGGED
  ├── 外观: 空模型（仅粒子贴图）→ 渲染全部委托给 BER
  └── 交互: useItemOn → tryPlace / tryBreak

MultiBuiltBlockEntity extends AshiharaCommonBE
  ├── COMPONENTS / ADDITIONAL_COMPONENTS: List<ComponentStateDefinition>
  ├── occupationCache: 碰撞网格缓存
  ├── 核心方法: tryPlace, tryBreak, reloadShape, checkConnection, checkMaterial
  └── 序列化: loadAdditional / saveAdditional (ValueInput/ValueOutput)

MultiBuiltBlockRenderer (BER, implements WithLevelRenderer)
  ├── renderStatic: 遍历所有 ComponentStateDefinition
  ├── 变换链: resetToBlock000 → FACING旋转 → inBlockPos平移 → 组件自旋
  └── 提交: 获取独立 BlockStateModel → 输入 chunk buffer (CUTOUT layer)

BuildingComponentItem extends BlockItem
  ├── canPlace: 检测已有 BaseMultiBuiltBlock → 返回 false → 阻塞新方块放置
  └── place: 先创建方块再 tryPlace 第一个组件
```

### 关键设计决策

| 决策 | 理由 |
|------|------|
| 方块模型为空，渲染全委托 BER | BER 将几何体注入 chunk buffer，获得原版级渲染性能——不逐帧计算，几何体编译进 chunk mesh |
| 13 种材质带优先级 | 低优先级材质放置高优先级组件时自动升级方块材质；用于小地图/Distant Horizons 等依赖 MapColor 测绘缩略图的 mod 正确识别建筑外观 |
| 3×4×3 Occupation 网格 | 单方块内 36 个碰撞单元，组件通过 `Occupation.join()` 做碰撞检测——防止穿模，无需额外碰撞计算 |
| Connectable / Interactable / Decoratable 接口 | 组件可声明连接行为（如 Beam 间自动对接）、右键交互（如 Wall 开窗）、装饰接受（如加装金具） |
| ComponentStateDefinition 用 Vec3 存 inBlockPos | 支持任意位置自由放置，为后续容器/家具系统的灵活定位预留 |

### 模块关系

```
建筑系统 (02-Building-System)
  ├── 依赖: 01-Content-Foundation (Blocks, Items, BlockEntities 注册)
  ├── 依赖: 05-Technical-Foundation (AshiharaCommonBE, WithLevelRenderer)
  ├── 通过 BuildingComponents.java 注册所有组件定义
  ├── 通过 AdditionalModels.java 注册独立模型 → ModelEvent.RegisterStandalone
  └── 渲染: 06-Rendering-Systems → MultiBuiltBlockRenderer → WLREventHandler → AddSectionGeometryEvent
```

### 相关文档

- [block-entities.md](block-entities.md) — MultiBuiltBlockEntity 详细设计
- [rendering.md](rendering.md) — WithLevelRenderer 渲染管线
