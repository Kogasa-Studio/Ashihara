
# 技术决策 - 3D 立体选框设计与实现

> 决策ID：003
> 相关系统：3D UI 框架 → 交互反馈
> 决策日期：2026-04-18
> 更新日期：2026-04-27（重构：状态机驱动，hover 钩子集成，骨骼轴约定修正）
> 状态：🔨 重构中

## 问题

在土锅GUI中，当鼠标悬停在物品槽位上时，需要显示一个**立体的、与目标骨骼位置和旋转完全相同**的高亮框，以清晰表示选中目标。

### 具体需求
1. 框应该是立体的（3D 网格），而非平面高亮框
2. 框的位置、旋转、尺寸应由目标组件的骨骼变换决定
3. 框应该只有在锅盖打开时才显示（取决于 `lidRemoved` 状态）
4. 框应使用动画平滑地调整尺寸和位置（QUAD EASING IN）

### 为什么不能用简单的2D框
- 土锅是3D旋转显示的，简单2D框与3D模型不匹配
- 用户在3D空间中交互时需要3D反馈
- 能直观看到具体的选中范围（特别是物品槽位的空间位置）

## 候选方案

| 方案 | 优势 | 劣势 | 成本 |
|---|---|---|---|
| A: 使用立体框模型 + 骨骼绑定 | ✅ 真实3D效果；✅ 与模型完全匹配；✅ 可视性最佳 | ⚠️ 需要新组件；⚠️ 动画配置较复杂 | 中等 |
| B: 动态网格渲染 | ✅ 完全自由的形状；⚠️ 性能好 | ❌ 实现复杂；❌ 没有参考模型 | 高 |
| C: 2D屏幕空间框 | ✅ 实现简单 | ❌ 与3D模型不匹配；❌ 用户体验差 | 低 |

## 最终选择

**方案 A：使用立体框模型 + 骨骼绑定**

### 理由

1. **模型已准备**：`cubic_selection_frame.geo.json` 已有 12 条边的骨骼结构
2. **集成成本低**：复用现有的 GeckoLib 渲染管线，无需自写渲染器
3. **用户体验最佳**：完全3D效果，与模型视角一致
4. **扩展性强**：后续可复用该框架用于其他3D UI高亮反馈

## 权衡分析

### 放弃的方案及其原因

**方案B（动态网格渲染）**
- 实现难度高，但相比用户体验提升不大
- 没有参考设计，风险较高
- 优先级不足以支撑该成本

**方案C（2D框）**  
- 快速但不符合3D UI的设计哲学
- 用户体验差，后期修改成本高

## 详细设计

### SelectionFrameComponent 结构

```
SelectionFrameComponent（作为子组件附加到 ISelectable 的实现类上）
├── 持有一个 SelectionFrameModel（cubic_selection_frame）
├── 状态机：FrameState { HIDDEN, EXPANDING, EXPANDED, CONTRACTING }
├── 关键属性：
│   ├── targetMatrix: Matrix4f（来自触发时的 OBB pose）
│   ├── minBounds / maxBounds: Vector3f（来自 OBB 局部坐标）
│   ├── animDuration: double（默认 8 ticks，可配置）
│   ├── expandEasing / contractEasing: EasingType
│   └── state: FrameState
└── 核心方法：
    ├── onHoverEnter(List<OBB> obbs) / onHoverEnter(List<OBB>, double duration)
    ├── onHoverExit() / onHoverExit(double duration)
    └── renderSelf()：检测 CONTRACTING 是否结束以触发 HIDDEN
```

ISelectable 的实现组件（如 PotLidComponent）覆写 AbstractComponent 的
`onHoverEnter()` / `onHoverExit()` 钩子来驱动 SelectionFrameComponent。

### 骨骼命名规则（立体框）

立方体 12 条边按"平行轴 + 其余两轴的正负角点"命名。
**命名约定**：第一轴为平行轴（该轴做 scale 动画），其余两轴按 xyz 去除平行轴后的顺序对应 p/n。

| 骨骼 | 语义 | posX | posY | posZ | 动画轴 |
|---|---|---|---|---|---|
| x_pp | x 平行，y+, z+ | min.x | max.y | max.z | ScaleX |
| x_pn | x 平行，y+, z- | min.x | max.y | min.z | ScaleX |
| x_np | x 平行，y-, z+ | min.x | min.y | max.z | ScaleX |
| x_nn | x 平行，y-, z- | min.x | min.y | min.z | ScaleX |
| y_pp | y 平行，x+, z+ | max.x | min.y | max.z | ScaleY |
| y_pn | y 平行，x+, z- | max.x | min.y | min.z | ScaleY |
| y_np | y 平行，x-, z+ | min.x | min.y | max.z | ScaleY |
| y_nn | y 平行，x-, z- | min.x | min.y | min.z | ScaleY |
| z_pp | z 平行，x+, y+ | max.x | max.y | min.z | ScaleZ |
| z_pn | z 平行，x+, y- | max.x | min.y | min.z | ScaleZ |
| z_np | z 平行，x-, y+ | min.x | max.y | min.z | ScaleZ |
| z_nn | z 平行，x-, y- | min.x | min.y | min.z | ScaleZ |

骨骼沿其平行轴方向从角点位置向正方向延伸（scale = OBB 对应轴长度）。

### 悬停动画状态机

```
HIDDEN
  →（onHoverEnter）→ EXPANDING（triggerScaleAnimation: 0 → targetScale）
EXPANDING
  →（动画完成）→ EXPANDED
  →（onHoverExit 打断）→ CONTRACTING（triggerScaleAnimation: currentScale → 0）
EXPANDED
  →（onHoverExit）→ CONTRACTING（triggerScaleAnimation: targetScale → 0）
CONTRACTING
  →（动画完成，isInternalAnimFinished）→ HIDDEN（visible=false, 重置骨骼）
  →（onHoverEnter 打断）→ EXPANDING（triggerScaleAnimation: currentScale → targetScale）
```

每次触发新动画时读取当前实时骨骼 scale（`readCurrentEdgeScales()`），实现无缝打断。

### 动画合成

动画帧同时包含 POSITION 和 SCALE 两类 keyframe：
- **位置**：与持续时间等长的常量 keyframe，锁定各棱骨骼到 OBB 角点，防止 GeckoLib 帧间 reset
- **缩放**：目标轴（x/y/z）从 fromScale → toScale，另外两轴保持 1.0

使用 `Animation.LoopType.HOLD_ON_LAST_FRAME`，动画完成后骨骼值保持不变。

### AbstractComponent 悬停钩子

`AbstractComponent` 新增通用生命周期钩子（非 ISelectable 专用）：
- `protected void onHoverEnter()` / `onHoverExit()`：默认空实现
- `public void fireHoverTransitions()`：比较 `wasHovered` vs `hovered`，按需调用钩子，递归 children
- `clearHoverState()` 在清除前保存 `wasHovered = this.hovered`
- `Screen3D.updateHoverState()` 在 setHoveredChain 之后调用 `fireHoverTransitions()`

## 风险与缓解

| 风险 | 概率 | 缓解 |
|---|---|---|
| 骨骼追踪不准确（旋转错误） | 中 | 验证 BoneTracer 的矩阵同步逻辑；测试不同旋转角度 |
| 框与目标位置错开 | 中 | 确保框的 pivot 与目标的 pivot 完全对齐 |
| 动画卡顿 | 低 | 使用对象池避免频繁分配；缓存框模型 |
| 选框被模型遮挡 | 低 | 使用透明度和特殊渲染类型（TRANSLUCENT）|

## 如何验证

### 功能验证

- [ ] 打开土锅GUI，锅盖还有时，鼠标移入槽位区域
- [ ] 验证立体框出现在正确的3D位置
- [ ] 验证框的旋转与模型旋转一致
- [ ] 验证框的尺寸与目标槽位的骨骼尺寸匹配
- [ ] 验证转动视角，框跟随模型正确旋转

### 动画验证

- [ ] 框从隐藏到显示的过程平滑
- [ ] 在目标改变时（鼠标移到另一个槽位），框平滑地变换

### 性能验证

- [ ] FPS 不下降（60fps 基准）
- [ ] 内存占用无明显增加

## 实现检查清单

- [x] 创建 `SelectionFrameComponent` 类（骨架完成）
- [ ] 重构 `SelectionFrameModel`：修正骨骼轴约定和位置逻辑，新增 triggerFrameAnimation / readCurrentEdgeScales / isInternalAnimFinished
- [ ] 重构 `SelectionFrameComponent`：实现状态机，onHoverEnter/Exit
- [ ] 在 `AbstractComponent` 中添加悬停钩子（onHoverEnter/Exit/fireHoverTransitions）
- [ ] 在 `Screen3D.updateHoverState()` 中调用 fireHoverTransitions
- [ ] 在 `PotLidComponent` 中覆写钩子，移除手动 show/hide 调用
- [ ] 完整测试用例
- [ ] 更新文档

## 后续扩展

此设计可用于：
1. 其他物品槽位的高亮
2. 区域选择框（扩展骨骼结构）
3. 其他3D UI的交互反馈

---

**相关文档**：
- [pot-gui-spec.md](../pot-gui-spec.md) - 土锅GUI完整规范
- [3d-ui-design.md](../3d-ui-design.md) - 3D UI 框架架构
- [3d-ui-quickref.md](../3d-ui-quickref.md) - 快速参考


