# 3D UI 框架系统

> 标签：[渲染系统、UI框架、核心基础设施]
> 状态：🚧 已进入实现期（主链已落地，处于迁移收口）
> 相关文档：见下方导航
> 最后更新：2026-05-15

## 概述

3D UI框架是Ashihara Mod的核心UI系统。四层架构：渲染层（只渲染）/ 交互层（处理输入）/ 组件层（系统核心，储存数据、把关交互）/ 逻辑层（BlockEntity等）。

**首个应用案例**：土锅GUI

## 文档导航

### 核心文档

| 文档 | 用途 | 学习时间 | 对象 |
|---|---|---|---|
| [3d-ui-design.md](./3d-ui-design.md) | 系统架构、6大核心模块详解 | 2-3小时 | AI实现 + 用户审查 |
| [pot-gui-spec.md](./pot-gui-spec.md) | 土锅GUI从需求到实现的完整规范 | 1-2小时 | AI实现 |
| [3d-ui-quickref.md](./3d-ui-quickref.md) | API速查、代码模板、常见问题 | 按需查询 | 实现时参考 |

### 技术决策与规划

| 文档 | 用途 | 对象 |
|---|---|---|
| [decisions/](./decisions/) | 10项核心技术决策记录 | 理解设计选择 |
| [roadmap.md](./roadmap.md) | 快速交付路线图（1-3天） | 项目管理 |


## 快速开始（开发者视角）

### 第1天：理解架构（2小时）

1. 阅读本README（10分钟）
2. 浏览 [3d-ui-design.md](./3d-ui-design.md) 前3个核心模块（45分钟）
3. 查看 [pot-gui-spec.md](./pot-gui-spec.md) 的组件结构（25分钟）

### 第2-3天：开始实现

1. 参考 [3d-ui-design.md](./3d-ui-design.md) 实现基础框架
2. 使用 [3d-ui-quickref.md](./3d-ui-quickref.md) 的代码模板
3. 根据 [pot-gui-spec.md](./pot-gui-spec.md) 实现具体组件

## 文件结构

```
3d-ui-framework/
├── README.md                    (本文件)
├── 3d-ui-design.md             (架构设计)
├── pot-gui-spec.md             (土锅GUI规范)
├── 3d-ui-quickref.md           (快速参考)
├── roadmap.md                  (交付路线图)
├── decisions/                  (技术决策)
│   ├── 001-screen-inheritance.md
│   ├── 002-component-tree.md
│   ├── 003-collision-detection.md
│   ├── 004-geckolib-integration.md
│   ├── 005-data-binding.md
│   ├── 006-render-batching.md
│   ├── 007-network-sync.md
│   ├── 008-animation-driver.md
│   ├── 009-memory-management.md
│   └── 010-error-handling.md
└── examples/                   (代码示例)
    ├── basic-screen.java
    ├── component-impl.java
    └── interaction-example.java
```

## 核心概念速览

### 四层架构

```
客户端
  渲染层   Screen.render()           只渲染，不处理输入
  交互层   Screen.mouseClick/Drag()  处理用户输入，对象是虚拟的
  组件层   AbstractComponent         系统核心，储存数据、把关交互、触发动画
服务端
  逻辑层   BlockEntity / 其他         网络、tick、配方
```

### 数据流

```
用户点击
  → 交互层射线检测 → 命中组件
  → 组件层处理 → 更新状态
      ├─→ 渲染层（动画/视觉更新）
      └─→ 逻辑层（发包，需要服务端操作时）

服务端变化 → 逻辑层 → 组件层 → 渲染层（Property<T>自动同步）
```

## 关键技术简介

**射线检测**：屏幕鼠标坐标转3D射线，用于点击检测

**OBB碰撞检测**：分离轴定理(SAT)，支持任意旋转/缩放模型

**GeckoLib集成**：InternalControlGeoModel在GUI中使用GeckoLib的完整功能

**骨骼追踪**：BoneTracer实时同步GeckoLib骨骼矩阵到UI系统

**事件总线**：组件间通过发布-订阅解耦通信

**数据绑定**：Property<T>实现UI和业务逻辑自动同步

## 快速FAQ

**Q：这个框架能做什么？**  
A：创建任意复杂的3D UI，支持动画、交互、多组件协作。土锅GUI是完整示例。

**Q：如何添加新的组件类型？**  
A：继承AbstractComponent，参考[3d-ui-design.md](./3d-ui-design.md)的组件化架构章节。

**Q：如何处理复杂的交互？**  
A：使用事件总线解耦，参考[pot-gui-spec.md](./pot-gui-spec.md)的组件交互示例。

**Q：这会很慢吗？**  
A：设计充分考虑性能，参考[decisions/006-render-batching.md](./decisions/006-render-batching.md)的批渲染优化。

**Q：为什么要用GeckoLib？**  
A：原生支持骨骼动画，在GUI中使用和在世界中一样流畅。参考[decisions/004-geckolib-integration.md](./decisions/004-geckolib-integration.md)。

## 使用指南

### 针对AI实现者

1. 先完整阅读 [3d-ui-design.md](./3d-ui-design.md)
2. 查看相关的技术决策理解"为什么"
3. 按照 [pot-gui-spec.md](./pot-gui-spec.md) 实现组件
4. 遇到问题时查询 [3d-ui-quickref.md](./3d-ui-quickref.md)

### 针对用户校对

1. 浏览本README了解系统概述
2. 查看 [pot-gui-spec.md](./pot-gui-spec.md) 验证实现是否符合需求
3. 运行代码检查功能和性能
4. 对照 [roadmap.md](./roadmap.md) 检查交付物

## 交付承诺

设计完全：架构、接口、代码示例都已齐全  
可执行性强：不是概念设计，是可直接编码实现的规范  
风险可控：充分的技术决策记录和缓解方案  
质量保证：框架支持长期维护和扩展  

## 后续步骤

- [x] 文档状态与代码事实对齐（README/STATUS/规格）→ 已完成（2026-05-15 P0）
- [x] 统一 Screen3D 命中与坐标约定，减少双路径 → 已完成（2026-05-15 P1，HitPolicy 接入主链）
- [x] 将临时迁移记录整合为单一正式状态板 → 已完成（temp-* 已清理，状态板见 `Plans/3d-ui-current-state-and-rectification-plan.md`）
- [ ] 收口 GuideBook 阶段性兼容路径（移除 `renderCompat`）→ 计划于 Stage 2.2 完成

---

**有问题？** 查看对应章节或相关的技术决策文档。

**有改进意见？** 参考 [DOCUMENTATION-RULES.md](../DOCUMENTATION-RULES.md)，然后提出改进方案。


