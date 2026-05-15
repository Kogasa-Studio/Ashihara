# 3D UI 框架架构设计

> 状态：架构已确定
> 最后更新：2026-05-15
> 相关文档：[pot-gui-spec.md](./pot-gui-spec.md)、[3d-ui-quickref.md](./3d-ui-quickref.md)、[decisions/](./decisions/)

## 四层架构

系统按职责严格分为四层，方向：logic → graphic（逻辑驱动表现）。

```
客户端
  渲染层   Screen.render()          只渲染，不处理输入，只接受组件层数据
  交互层   Screen.mouseClick/Drag() 接收用户输入，向组件层传递，自身对象是虚拟的
  组件层   Component                系统核心，储存数据、把关交互、触发动画
服务端
  逻辑层   BlockEntity / 其他        handle 网络、tick、方块实体
```

---

### 渲染层

- 存在于 `Screen.render()`
- **只做渲染**，不 handle 任何输入
- 渲染数据全部来自组件层
- 负责：GeckoLib 模型绘制、RenderType 管理、PoseStack 矩阵、粒子特效输出

用户在屏幕上看到的东西由渲染层负责，但它是哑的——它只是组件层数据的视觉表达。

---

### 交互层

- 存在于 `Screen.mouseClicked()`、`mouseDragged()`、`mouseScrolled()` 等
- **Handle 所有用户输入**：点击、拖拽、选择、悬停
- 交互层没有自己的"真实对象"，用户交互的空间是由**组件层数据构造的伪渲染层**（射线检测 + OBB 碰撞）
- 检测到交互后，向组件层派发，由组件层决策后续行为

关键点：交互层的对象是虚拟的。OBB 来自组件层，渲染结果来自渲染层，交互层只是"把鼠标事件翻译成组件命中"的管道。

---

### 组件层（核心）

- 存在于 `AbstractComponent` 及其子类
- **系统核心**，是渲染层和逻辑层之间的网关
- 职责：
  - 储存组件的所有状态数据（是否选中、内含物品/流体/能量、位置、包围盒映射等）
  - 把关交互层的输入：决定是否传递至逻辑层，或只更新渲染层
  - **所有组件动画都在本层触发**
  - 可从服务端逻辑层接收数据更新

**所有交互必须由组件处理**。哪怕是一个 cube 上的一个 quad 被分成四份，也要分别建四个组件，再依 parent-children 关系传给上级组件。不允许绕过组件层直接从交互层访问逻辑层（技术上可行，但不推荐）。

---

### 逻辑层

- 存在于 `BlockEntity` 或类似的服务端对象
- 实际上客户端也有，但以服务端为准
- 职责：handle 网络同步、方块实体 tick、配方计算等
- 接收来自组件层的数据
- 必要时可直接接收交互层数据（不推荐，跳过了组件层的把关）

---

## 数据流向

```
用户输入 (鼠标事件)
  → 交互层：射线检测 + OBB 命中哪个组件
  → 组件层：处理输入，更新内部状态
      ├─→ 渲染层：推送新数据（动画触发、位置更新）
      └─→ 逻辑层：需要持久化/服务端操作时发包

服务端数据变化
  → 逻辑层：发包到客户端
  → 组件层：接收更新，更新内部状态
  → 渲染层：自动同步（数据绑定 Property<T>）
```

---

## 组件层详解

### AbstractComponent 基类

组件是有状态的树节点。每个组件管理自己的生命周期、碰撞盒、子组件。

```java
public abstract class AbstractComponent {
    // 位置与变换
    Vector3f position, rotation, scale;
    int depth;  // z轴深度，影响渲染顺序和碰撞优先级

    // 碰撞
    OBB collider;  // 由 GeckoLib 骨骼矩阵驱动

    // 状态
    boolean visible, hovered, selected, dragging;

    // 子组件
    List<AbstractComponent> children;

    // 生命周期
    void init();
    void tick();
    void render(GuiGraphics guiGraphics, float partialTick);

    // 接收交互层的输入
    boolean mouseClicked(double x, double y, int button);
    boolean mouseReleased(double x, double y, int button);
    boolean mouseDragged(double x, double y, int button, double dx, double dy);
    void mouseMoved(double x, double y);

    // 碰撞查询
    OBB getCollisionBox();
    boolean isHovered();
}
```

### 具体组件

| 组件 | 职责 | 核心数据 |
|---|---|---|
| `ModelComponent` | 3D GeckoLib 模型 | 模型引用、动画状态 |
| `ItemSlotComponent` | 物品槽位显示和交互 | ItemStack、槽位索引 |
| `FluidDisplayComponent` | 流体渲染 | FluidStack、容量、填充度 |
| `Panel` | 容器，聚合子组件 | 子组件列表、背景 |
| `ProgressBarComponent` | 进度条 | 当前值、最大值 |

---

## Screen3D（交互层 + 渲染层的宿主）

`Screen3D` 继承 MC 的 `Screen`，是交互层和渲染层的物理载体，但它本身不包含业务逻辑——业务逻辑在组件层。

```java
public abstract class Screen3D extends Screen {
    protected List<AbstractComponent> rootComponents;

    // 渲染层入口
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 按 depth 排序，调用各组件 render()
    }

    // 交互层入口
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 射线检测 → 找到命中组件 → 调用组件 mouseClicked()
    }

    // 坐标转换（屏幕 ↔ GUI空间）
    Vector3f screenToGuiSpace(double screenX, double screenY, float guiZ);
}
```

---

## 交互检测系统

### 射线检测

交互层用射线模拟"用户鼠标点了哪个组件"。

当前实现（屏幕空间射线，适用于当前 Screen3D 变换链）：
- origin = `(mouseX, mouseY, -2000)`
- direction = `(0, 0, 1)`
- 与各组件 OBB 做相交测试，取最近命中

### HitPolicy（命中策略）

当前主链已接入命中策略分流：
- `BLOCK`：命中后消费事件（点击/拖拽目标）
- `PENETRATE`：命中可参与 hover，但不消费点击，事件继续向后
- `MIXED`：运行时由组件状态决定阻挡或穿透

`Screen3D` 事件分发与 hover 更新都通过统一命中链路完成：
- 交互目标优先选最近 `BLOCK` 命中
- 若无 `BLOCK`，保留最近 `PENETRATE` 作为悬停目标

### OBB 碰撞检测

算法：分离轴定理（SAT）。支持任意旋转和缩放——这是 GeckoLib 骨骼在动画中会旋转时的必要条件。

OBB 的 pose 直接来自 GeckoLib 骨骼矩阵（由 BoneTracer 实时同步）。

### BoneTracer

通过 Mixin 注入 GeoRenderer，在骨骼渲染时同步矩阵到对应组件的 OBB。
- 用途：让 OBB 实时跟随动画骨骼，保证交互和视觉一致
- 无 BoneTracer 则碰撞盒停在初始位置，动画后命中会错位

### 深度管理

- 每个组件有 `depth` 属性
- 渲染：depth 从小到大（小的先渲染，在后面）
- 碰撞：depth 从大到小检测（大的在前，优先命中）
- 悬停时临时提升 depth

### 调试覆盖层（F9）

- 绘制各组件 OBB 的投影线框
- 显示鼠标射线与各 OBB 的命中距离 `t`
- 标示当前命中组件名和悬停状态
- 用于验证骨骼矩阵同步是否正确，区分"选框渲染错误"和"命中链未接入"

---

## 动画系统

动画在**组件层触发**（不在渲染层也不在逻辑层）。

### InternalAnimationBuilder

可以根据代码内变量生成动画，预制动画文件无法抓取内部变量：

```java
geoModel.triggerInternal(player, id,
    new InternalAnimationBuilder("open_lid", LoopType.HOLD_ON_LAST_FRAME)
        .startBone("lid")
            .lerpX(VarType.ROTATION, 20, 0, -90, EasingType.EASE_OUT_CUBIC)
        .endBone()
        .build()
);
```

动画驱动流程：
```
组件层：业务条件满足 → triggerInternal()
  → GeckoLib 播放动画
  → 骨骼实时变换
  → BoneTracer 同步矩阵到 OBB
  → 渲染层和碰撞检测同步更新
```

---

## 数据绑定

`Property<T>` 实现组件层到渲染层的自动同步：

```java
// 在组件层定义
Property<Integer> cookProgress = new Property<>(0);
cookProgress.addListener(v -> progressBarComponent.setValue(v));

// 逻辑层更新（通过网络包）→ 组件层接收 → 自动触发渲染层更新
cookProgress.set(newValue);
```

---

## 事件系统

组件间通信通过 EventBus 解耦（不直接调用）：

```java
// 组件层：物品槽被点击后发布事件
EventBus.publish(new ItemSlotClickedEvent(slotIndex, itemStack));

// 其他组件订阅
EventBus.subscribe(ItemSlotClickedEvent.class, e -> {
    recipeDisplay.checkRecipe(e.items);
});
```

---

## 包结构

```
com.ashihara.mod.ui
  core/
    Screen3D.java              交互层+渲染层宿主
    AbstractComponent.java     组件基类
  component/
    ModelComponent.java
    ItemSlotComponent.java
    FluidDisplayComponent.java
    Panel.java
    ProgressBarComponent.java
  interaction/
    Ray.java
    OBB.java
    ObbIntersector.java
    BoneTracer.java            (Mixin)
  animation/
    InternalControlGeoModel.java
    InternalAnimationBuilder.java
  event/
    EventBus.java
  property/
    Property.java
```

---

## 关键规则（实现时必须遵守）

1. 渲染层不处理输入，它只是组件层数据的视觉表达
2. 所有交互由组件处理，哪怕是一个 quad 也要建组件
3. 动画在组件层触发，不要在 `Screen.render()` 里触发动画
4. 逻辑层不直接操作渲染，通过组件层中转
5. OBB 必须由 BoneTracer 驱动，否则动画后碰撞会错位

---

更多细节：
- 土锅 GUI 完整实现：[pot-gui-spec.md](./pot-gui-spec.md)
- 技术决策：[decisions/INDEX.md](./decisions/INDEX.md)
- 快速参考：[3d-ui-quickref.md](./3d-ui-quickref.md)
