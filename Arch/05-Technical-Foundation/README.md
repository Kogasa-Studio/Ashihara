# 3D UI 库系统 - 技术文档导航

## 重要通知：文档系统已优化重组

⚠️ **从2026-04-18起，3D UI库的文档已重新组织**

原有的文档系统已根据实际开发需求（快速交付、AI实现、用户校对）进行了全面优化。

### 快速导航（新系统）

**[进入优化后的3D UI框架文档系统](./3d-ui-framework/README.md)**

这是最新、最高效的版本。

### 完整参考文档

以下完整版本文档保留作为详细参考资源：
- `3d-ui-system.md` - 完整架构设计（原始版，包含所有细节）
- `3d-ui-technical-decisions.md` - 完整决策论证（原始版，包含所有权衡分析）

**注**：已被优化版本替代的旧文档已删除：
- ~~3d-ui-documentation-index.md~~ → 新: `3d-ui-framework/README.md`
- ~~3d-ui-quick-reference.md~~ → 新: `3d-ui-framework/3d-ui-quickref.md`
- ~~3d-ui-implementation-roadmap.md~~ → 新: `3d-ui-framework/roadmap.md`
- ~~pot-gui-implementation.md~~ → 新: `3d-ui-framework/pot-gui-spec.md`（位置：03-Gameplay-Systems中）

---

## 📖 Ashihara mod 的 3D UI 库系统

该项目旨在突破 Minecraft mod 开发中持续近 1 年的技术瓶颈，创建一个**高可读性、高可扩展性、与 GeckoLib 深度结合的现代化 3D UI 框架**。

## 🎯 目标与愿景

### 主要目标

1. **解决架构问题**：打破原版 Minecraft UI 充满硬编码、轮子一次性、缺乏复用的局面
2. **建立通用框架**：创建一个能被 mod 内多个工作台 UI 复用的系统
3. **完整实现案例**：以**土锅 GUI** 为首个完整参考实现
4. **长期支持**：为后续的其他工作台、库存系统、地图界面等 UI 提供基础

### 核心特性

- ✅ **组件化设计**：所有 UI 元素都是可复用的独立组件
- ✅ **GeckoLib 优先**：充分利用骨骼动画能力实现流畅动画
- ✅ **交互完整**：射线检测、包围盒检测、z 轴深度排序
- ✅ **现代化架构**：事件总线、数据绑定、拖拽框架
- ✅ **兼容性保证**：与原版、NeoForge 生态完全兼容

## 📚 文档结构

```
3d-ui-system/                          # 3D UI 库文档集合
├── 3d-ui-documentation-index.md       # 📍 导航索引（从这里开始）
├── 3d-ui-system.md                    # 🌟 核心架构设计（必读）
├── 3d-ui-quick-reference.md           # 快速参考与代码模板
├── 3d-ui-implementation-roadmap.md    # 实现路线图与时间表
├── 3d-ui-technical-decisions.md       # 技术决策与权衡分析
└── ../03-Gameplay-Systems/
    └── pot-gui-implementation.md      # 🔥 土锅 GUI 实现规范
```

## 🚀 快速导航

### 🔴 第一次接触 - 我应该读什么？

**如果你是...**

- **架构师**：`3d-ui-system.md` → `3d-ui-technical-decisions.md` → `3d-ui-implementation-roadmap.md`
- **开发者**：`3d-ui-documentation-index.md` → `pot-gui-implementation.md` → `3d-ui-quick-reference.md`
- **美术/设计**：`pot-gui-implementation.md` (用户交互流程章节)
- **项目经理**：`3d-ui-implementation-roadmap.md` (时间表、风险、资源)

### 🟡 快速查找

| 我想了解... | 查看文档 | 章节 |
|---|---|---|
| 系统如何工作 | `3d-ui-system.md` | 概述 + 核心技术模块 |
| 土锅 GUI 怎么做 | `pot-gui-implementation.md` | 详细实现指南 |
| 如何写代码 | `3d-ui-quick-reference.md` | 代码模板 |
| 为什么这样设计 | `3d-ui-technical-decisions.md` | 全部内容 |
| 什么时候完成 | `3d-ui-implementation-roadmap.md` | 时间表与阶段 |
| API 查询 | `3d-ui-quick-reference.md` | 常用类与方法 |

## 📋 文档详解

### 1. 3D UI 系统设计 (`3d-ui-system.md`)

**内容**：完整的架构设计和技术规范

**关键章节**：
- ✅ 系统概述与设计哲学（为什么要做这个）
- ✅ 6 大核心技术模块详解
  - 基础渲染层（Screen3D + GeckoLib）
  - 交互检测系统（射线 + OBB 碰撞）
  - 组件化架构（树形组件管理）
  - UI-逻辑连接（事件总线）
  - 可拖拽框架（交互状态机）
  - 粒子特效系统
- ✅ 土锅 GUI 应用案例（完整组件树）
- ✅ 文件结构规划

**代码量**：~2000 行 markdown  
**学习时间**：2-3 小时

---

### 2. 土锅 GUI 实现规范 (`pot-gui-implementation.md`)

**内容**：从用户交互到代码实现的完整指南

**关键章节**：
- ✅ 用户交互流程（7 个完整阶段）
  - 打开阶段、空锅交互、添加材料、配方匹配、烹饪开始、烹饪过程、完成
- ✅ 组件树详细结构
- ✅ 关键接口定义
  - `ICustomUIRenderable` - 自定义物品渲染
  - `IPotRecipe` - 配方接口
  - `IHeatSource` - 热源接口
- ✅ 核心组件实现（完整代码示例）
  - PotScreen + PotPanel
  - PotModelComponent（骨骼追踪）
  - FluidDisplayComponent（流体渲染）
  - ItemSlotComponent（物品槽位）
  - RecipeDisplayPanel（配方预览）
  - CookingEffectContainer（烹饪特效）
- ✅ ItemStackHandler 兼容性
- ✅ 网络同步设计
- ✅ 性能考虑

**代码量**：~1500 行（包含完整代码示例）  
**学习时间**：2-3 小时

---

### 3. 实现路线图 (`3d-ui-implementation-roadmap.md`)

**内容**：具体的开发时间表和里程碑规划

**关键章节**：
- ✅ 5 个实现阶段（共 10 周）
  1. 基础框架（2 周）
  2. GeckoLib 集成（2 周）
  3. 标准组件库（2 周）
  4. 事件系统（1 周）
  5. 土锅 GUI 实现（3 周）
- ✅ 每个阶段的详细子任务和检查点
- ✅ 风险评估（4 项主要风险 + 缓解策略）
- ✅ 质量保障计划（单元测试、集成测试、性能基准）
- ✅ 依赖关系图和资源需求

**学习时间**：1 小时（用于理解项目计划）

---

### 4. 技术决策记录 (`3d-ui-technical-decisions.md`)

**内容**：关键架构决策的论证和权衡

**10 项核心决策**：
1. Screen 继承架构
2. 组件树 vs 直接渲染
3. 碰撞检测算法（OBB + SAT）
4. GeckoLib 集成方式
5. 数据绑定策略
6. 渲染批处理
7. 网络同步方式
8. 动画驱动方式
9. 内存管理与缓存
10. 错误处理与日志

**每个决策包含**：
- 问题陈述
- 3+ 个备选方案对比表
- 最终选择及论证
- 权衡分析
- 风险缓解策略

**学习时间**：1-2 小时（用于理解设计选择）

---

### 5. 快速参考 (`3d-ui-quick-reference.md`)

**内容**：开发者快速查询的代码模板和 API

**关键部分**：
- ✅ 核心类层级图
- ✅ 关键工具类速查表
- ✅ 代码模板（创建 Screen、组件、动画等）
- ✅ 常用数值（超时、速度、大小）
- ✅ Easing 函数速查
- ✅ RenderType 速查
- ✅ 调试技巧和常见问题解答
- ✅ 文件结构快速查找

**学习时间**：需要时查询

---

### 6. 导航索引 (`3d-ui-documentation-index.md`)

**内容**：所有文档的总览和相互链接

**用途**：
- 📍 找到正确的文档
- 📍 理解文档之间的关系
- 📍 了解项目总体结构

---

## 💡 核心设计概念

### 层级架构

```
Screen3D
  ├─ 管理组件树
  ├─ 事件分发
  ├─ 坐标转换
  └─ 生命周期管理

AbstractComponent (树节点)
  ├─ render()：渲染
  ├─ tick()：更新
  ├─ mouseClicked()：事件处理
  └─ 子组件管理

具体组件
  ├─ ModelComponent：3D 模型
  ├─ FluidDisplayComponent：流体
  ├─ ItemSlotComponent：物品
  ├─ TooltipComponent：提示
  └─ Panel：容器
```

### 交互流程

```
用户输入 (鼠标点击)
  ↓
屏幕坐标转换为射线
  ↓
射线 vs OBB 碰撞检测
  ↓
事件分发给被点击的组件
  ↓
组件处理事件（更新数据）
  ↓
数据绑定触发 UI 更新
  ↓
事件发送到服务端（网络同步）
```

### 动画驱动

```
业务逻辑（如烹饪开始）
  ↓
通过 InternalAnimationBuilder 生成动画
  ↓
通过 triggerInternal() 触发 GeckoLib 动画
  ↓
动画执行，骨骼变换
  ↓
BoneTracer 实时同步矩阵
  ↓
用于碰撞检测、UI 渲染等
```

## 🛠️ 核心技术

### 1. 射线检测（Ray Casting）

将屏幕鼠标坐标转换为 3D 射线，用于检测点击目标。

```java
Ray ray = buildMouseRay(mouseX, mouseY);
boolean hit = ObbInterSector.rayIntersectsOBB(ray, targetOBB);
```

### 2. OBB 碰撞检测（Object-Oriented Bounding Box）

使用分离轴定理（SAT）进行任意方向的包围盒相交判定，支持旋转和缩放。

### 3. GeckoLib 动画

利用 GeckoLib 的骨骼动画系统，通过代码动态生成和触发动画。

```java
model.triggerInternal(player, id,
    new InternalAnimationBuilder("anim_name", LoopType.HOLD_ON_LAST_FRAME)
        .startBone("bone_name")
            .lerpX(VarType.POSITION, 20, 0, 5, EasingType.EASE_OUT_CUBIC)
        .endBone()
        .build()
);
```

### 4. 骨骼追踪（BoneTracer）

实时同步 GeckoLib 骨骼的变换矩阵到 UI 系统，用于物品槽位、碰撞检测等。

### 5. 事件总线

组件间通过发布-订阅的事件系统通信，实现解耦。

### 6. 数据绑定

UI 组件与业务逻辑通过 `Property<T>` 自动同步。

## 🎮 土锅 GUI 示例

土锅 GUI 是系统的首个完整应用，展示了所有核心功能：

**功能示例**：
- ✅ GeckoLib 控制的锅盖移除动画
- ✅ 流体高度动态渲染
- ✅ 物品槽位与 ItemStackHandler 兼容
- ✅ 配方匹配与错误提示
- ✅ 进度条动画
- ✅ 粒子特效（火焰、气泡、烟雾）
- ✅ 网络同步

**组件树**：
```
PotPanel
├─ PotModelComponent (3D 锅模型)
├─ FluidDisplayComponent (流体)
├─ ItemSlotContainer (物品槽位 x8)
├─ RecipeDisplayPanel (配方预览)
├─ CookstoveComponent (土灶)
├─ ProgressBarComponent (进度条)
└─ CookingEffectContainer (特效)
```

## 📊 规模与工作量

| 方面 | 数据 |
|---|---|
| 总文档字数 | ~7500 行 markdown |
| 代码示例 | ~500 行 Java |
| 核心类数 | ~20 个 |
| 接口定义 | ~8 个 |
| 预计实现时间 | 10 周 |
| 开发人数 | 1-2 人 |

## ✅ 文档完整性清单

- [x] 架构设计与系统概述
- [x] 6 大核心技术模块详解
- [x] 土锅 GUI 完整实现规范
- [x] 5 阶段实现路线图
- [x] 10 项关键技术决策
- [x] 代码示例和最佳实践
- [x] 快速参考和 API 文档
- [x] 风险评估和缓解策略
- [x] 质量保障计划
- [x] 后续扩展规划

## 🚦 使用建议

### 第 1 天：理解整体架构
- [ ] 阅读 `3d-ui-system.md` 核心部分（30 分钟）
- [ ] 查看土锅 GUI 用户交互流程（20 分钟）
- [ ] 浏览 `3d-ui-quick-reference.md` 了解 API（15 分钟）

### 第 2-3 天：深入学习
- [ ] 完整阅读 `3d-ui-system.md`（2 小时）
- [ ] 研究 `pot-gui-implementation.md` 中的代码示例（2 小时）
- [ ] 理解 `3d-ui-technical-decisions.md` 中的设计选择（1 小时）

### 第 4 天：制定计划
- [ ] 学习 `3d-ui-implementation-roadmap.md`（1 小时）
- [ ] 制定团队的具体实现计划
- [ ] 分配任务到各阶段

### 第 5+ 天：开始实现
- [ ] 按阶段推进开发
- [ ] 随时参考 `3d-ui-quick-reference.md` 和代码示例
- [ ] 定期审查 `3d-ui-technical-decisions.md` 确保不偏离设计

## 🤝 贡献与反馈

### 报告问题

如果发现文档中的：
- 错误或不准确
- 遗漏或不清楚
- 与代码实现不符

请创建 Issue 并描述具体情况。

### 改进建议

有更好的设计想法？
1. 创建讨论话题
2. 参考相关的技术决策文档
3. 提供论证和权衡分析

### 代码贡献

参与实现前：
1. 充分阅读相关文档
2. 理解设计决策和权衡
3. 遵循代码规范（见 `naming-and-code-style.md`）
4. 在相应 Issue 中报名

## 📞 联系方式

- **维护者**：Ashihara Mod Team
- **项目状态**：📋 设计完成，待代码实现
- **最后更新**：2026-04-18

## 📄 许可证

本文档集合遵循项目 LICENSE 证书。代码示例可自由使用和修改。

---

## 🎯 最后的话

这套完整的文档系统代表了对 3D UI 库设计的深度思考。它不仅是实现指南，更是团队共同的"蓝图"和"契约"。

**关键承诺**：
- ✅ 系统设计经过充分论证，已考虑主要风险
- ✅ 每项决策都有明确的权衡分析
- ✅ 实现路线图提供了清晰的里程碑
- ✅ 代码示例和最佳实践已准备就绪

**期待**：
- 🚀 按照文档指导的实现能显著提高代码质量
- 🚀 组件化架构能让团队成员高效并行开发
- 🚀 充分的文档能降低新成员的学习曲线
- 🚀 最终交付一个现代化、可维护的 3D UI 系统

---

**让我们一起突破这个近 1 年的技术瓶颈，创建 Ashihara mod 的现代化 3D UI 系统！** 🎉


