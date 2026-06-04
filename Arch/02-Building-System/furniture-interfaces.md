# 建筑系统：家具组件接口体系

> **标签**：[阶段 2]
> **状态**：📋 待规划（R4）
> **最后更新**：2026-06-04
> **相关文档**：[overview.md](overview.md), [container-system.md](container-system.md), [rendering.md](rendering.md)

---

## 概述

家具组件（FurnitureComponent）在现有建筑组件（BuildingComponent）基础上增加了**动态渲染**、**自定义数据持久化**和**覆盖层渲染**的能力。这些能力通过三个可选接口实现。接口由组件自愿实现，不需要时组件保持原有行为。

### 接口层次

```
FurnitureComponent (abstract, extends BuildingComponent)
├── rendererType: CHUNK_BUFFER / BER_DYNAMIC / GECKOLIB
│
├── [CHUNK_BUFFER]  → 使用现有 WithLevelRenderer 管线，无额外接口
│
├── [BER_DYNAMIC]   → 实现 ICustomRender
│   └── [可选的]    → 实现 ICustomData（需要持久化状态）
│       └── [可选的] → 实现 IRenderOverlayProvider（有内容物覆盖层）
│
└── [GECKOLIB]      → 实现 ICustomRender（未来，当前文档不涉及）
```

---

## ICustomRender — 自定义 BER 渲染

**文件**：`block/furniture/ICustomRender.java`（待创建）

```java
public interface ICustomRender
{
    /**
     * 是否应在当前帧渲染。若返回 false，BER pass 跳过此组件。
     * 常用于空容器不渲染的场景。
     */
    boolean doRender(MultiBuiltBlockEntity be);

    /**
     * 收集此组件的 BER 渲染状态。
     * 返回值注册到 FurnitureComponentDispatcher，供 MBER 在 submit()
     * 阶段统一分发。
     */
    FurnitureRenderState collectRenderState(ComponentStateDefinition def,
        MultiBuiltBlockEntity be);
}
```

### 与 FurnitureRendererType 的关系

- `CHUNK_BUFFER`：**不需要** ICustomRender。组件通过 `renderStatic()` 走 chunk buffer 渲染。
- `BER_DYNAMIC`：**必须**实现 ICustomRender。组件通过 BER per-frame pass 渲染。
- `GECKOLIB`：**必须**实现 ICustomRender（留待 R6 实现）。

### 注册与分发

组件实现 ICustomRender 后，在静态初始化中将自身类型注册到 `FurnitureComponentDispatcher`（见下文）。MBER 的 `submit()` 在每帧轮询 FURNITURE 列表时，通过 `instanceof ICustomRender` 识别并调用 `collectRenderState()`。

---

## ICustomData — 组件自定义数据持久化

**文件**：`block/furniture/ICustomData.java`（待创建）

```java
public interface ICustomData
{
    /** 将自定义数据写入 NBT（在 ComponentStateDefinition.serialize 之后调用）。 */
    void serializeCustom(ValueOutput output);

    /** 从 NBT 读取自定义数据（在 ComponentStateDefinition.deserializeNBT 之后调用）。 */
    void deserializeCustom(ValueInput input);
}
```

### 集成点

- `ComponentStateDefinition` 的 `serialize` 和 `deserializeNBT` 方法，在完成标准字段处理后，检查 `component instanceof ICustomData`，若为真则调用对应的 custom 方法。
- 自定义数据序列化在独立子标签 `custom` 下，不污染标准字段的读写。

### 与 Occupancy 系统无关

ICustomData 仅处理 content 状态，不影响碰撞体（shape）和占用（occupation）计算。

---

## IRenderOverlayProvider — 内容物覆盖层

**文件**：`block/furniture/IRenderOverlayProvider.java`（待创建）

```java
public interface IRenderOverlayProvider
{
    /**
     * 构建容器当前状态快照，供 MealRenderDispatch (MRD) 查询。
     * 若容器为空（无内容物），返回 null。
     */
    @Nullable
    ContainerState getContainerState();
}
```

### 与 ICustomRender 的分工

| 接口 | 职责 |
|------|------|
| ICustomRender | 渲染"容器本体"（碗、碟、杯的模型） |
| IRenderOverlayProvider | 渲染"内容物覆盖层"（米饭、茶、酱汁等） |

`IRenderOverlayProvider` 是可选的——一个容器组件可以仅实现 ICustomRender（本体由 chunk buffer 渲染），也可同时实现两者（本体 + 内容物都走 BER）。

---

## FurnitureComponentDispatcher — BER 渲染器注册表

**文件**：`client/render/ber/dispatch/FurnitureComponentDispatcher.java`（待创建）

### 职责

维护 FurnitureComponent 类型到其 BER 渲染逻辑的映射。在 MBER 的 `submit()` 中用于分发渲染调用。

### 设计

```java
public final class FurnitureComponentDispatcher
{
    /**
     * 注册表：component 类 → 渲染 lambda 工厂。
     * key: Class<? extends FurnitureComponent>
     * value: (component, definition, be) → FurnitureRenderState
     *
     * 注册方式支持：
     *   - 按类注册：register(ContainerComponent.class, ...)
     *   - 按 ID 注册：registerById("wooden_bowl_mid", ...)（委托至类查找）
     */
    private static final Map<Class<?>, IFurnitureBER> registry = new HashMap<>();

    public static <T extends FurnitureComponent & ICustomRender>
    void register(Class<T> clazz, IFurnitureBER<T> renderer);

    @Nullable
    public static IFurnitureBER<?> getRenderer(FurnitureComponent component);
}
```

### IFurnitureBER

```java
@FunctionalInterface
public interface IFurnitureBER<T extends FurnitureComponent & ICustomRender>
{
    FurnitureRenderState collect(T component, ComponentStateDefinition def,
        MultiBuiltBlockEntity be);
}
```

### FurnitureRenderState

```java
public record FurnitureRenderState(
    FurnitureComponent component,
    ComponentStateDefinition definition,
    PoseStack pose,
    @Nullable ContainerState containerState
) {}
```

`FurnitureRenderState` 是 BER pass 中传递给渲染队列的不可变快照。`containerState` 为非 null 时，渲染逻辑进一步查询 MRD 获取覆盖层 renderer。

---

## 组件 → BER 映射类型图

```
FurnitureComponent
├── WoodenBowlComponent (CHUNK_BUFFER, 不实现任何新接口)
│   └── 当前行为不变：碗本体通过 chunk buffer 渲染
│
├── ContainerComponent (BER_DYNAMIC)
│   ├── implements ICustomRender   ← 本体走 BER
│   ├── implements ICustomData     ← 持久化内容物状态
│   └── implements IRenderOverlayProvider  ← 提供覆盖层入口
│
└── 未来扩展...
    └── AnimatedFurnitureComponent (GECKOLIB)
        └── implements ICustomRender
```

**设计决策**：WoodenBowlComponent 保持 CHUNK_BUFFER，不升级为 ContainerComponent。当需要容器功能时，创建新的 ContainerComponent 子类（如 `BowlContainerComponent`），将碗体模型复用但 rendererType 设为 BER_DYNAMIC。这样两种碗共存：普通装饰碗（CHUNK_BUFFER，零 per-frame 开销）和交互容器碗（BER_DYNAMIC，有 per-frame 渲染）。

---

## 文件清单

| 文件 | 状态 | 说明 |
|------|------|------|
| `block/furniture/ICustomRender.java` | 📋 待创建 | 自定义 BER 渲染接口 |
| `block/furniture/ICustomData.java` | 📋 待创建 | 自定义数据持久化接口 |
| `block/furniture/IRenderOverlayProvider.java` | 📋 待创建 | 内容物覆盖层接口 |
| `block/furniture/ContainerComponent.java` | 📋 待创建 | 容器家具基类 |
| `client/render/ber/dispatch/FurnitureComponentDispatcher.java` | 📋 待创建 | BER 渲染注册表 |
| `client/render/ber/dispatch/IFurnitureBER.java` | 📋 待创建 | BER 渲染函数接口 |
| `client/render/ber/dispatch/FurnitureRenderState.java` | 📋 待创建 | BER 渲染状态 record |