# 渲染系统：容器覆盖层渲染分发（MRD / SuBER）

> **标签**：[阶段 2]
> **状态**：📋 待规划（R4）
> **最后更新**：2026-06-04
> **相关文档**：[container-system.md](../02-Building-System/container-system.md), [furniture-interfaces.md](../02-Building-System/furniture-interfaces.md), [rendering.md](../02-Building-System/rendering.md)

---

## 概述

容器的内容物（米饭、流体、料理）需要独立的覆盖层渲染——这些内容物不能简单地用 chunk buffer 渲染，因为：
1. 内容物会随玩家交互动态变化（装饭→倒茶→吃完），chunk rebuild 代价太高。
2. 内容物的视觉表现可能是复杂的"菜品"外观（如茶泡饭上漂浮的木鱼花），不是单一的 BlockStateModel。

解决方案：**MealRenderDispatch (MRD)**——注册表驱动的覆盖层渲染分发系统。

---

## 1. ContainerState — 渲染上下文

**文件**：`block/furniture/content/ContainerState.java`（待创建）

```java
public record ContainerState(
    ContainerType containerType,   // BOWL, PLATE, CUP, ...
    ContainerSize size,            // SMALL, MID, LARGE
    ContentType contentType,       // EMPTY, ITEM, FLUID, MEAL
    int itemCount,                 // 物品堆叠数（仅 ITEM 时有意义）
    @Nullable Meal meal            // 料理数据（仅 MEAL 时有意义）
)
```

### ContainerType 与 ContainerSize

```java
public enum ContainerType { BOWL, PLATE, CUP, POT, ... }
public enum ContainerSize { SMALL, MID, LARGE }
```

**关键**：`(ContainerType, ContainerSize)` 二元组唯一标识一个容器种类。容器的 `FurnitureComponent.id`（如 `"wooden_bowl_mid"`）映射到 `(BOWL, MID)`。

### 构建时机

`ContainerComponent.getContainerState()` 被调用时从自身 `content` 字段构建：

```java
@Override
public ContainerState getContainerState()
{
    ContainerContent c = this.content;
    return switch (c)
    {
        case ContainerContent.Empty e ->
            new ContainerState(getContainerType(), getSize(), ContentType.EMPTY, 0, null);
        case ContainerContent.ItemContent i ->
            new ContainerState(getContainerType(), getSize(), ContentType.ITEM, i.stack().getCount(), null);
        case ContainerContent.FluidContent f ->
            new ContainerState(getContainerType(), getSize(), ContentType.FLUID, f.stack().getAmount(), null);
        case ContainerContent.MealContent m ->
            new ContainerState(getContainerType(), getSize(), ContentType.MEAL, 0, m.meal());
    };
}
```

---

## 2. MealRenderDispatch (MRD) — 覆盖层渲染分发

**文件**：`client/render/ber/dispatch/MealRenderDispatch.java`（待创建）

### 核心设计

MRD 是一个注册表，为每个 `(ContainerType, ContainerSize, ContentType, count)` 组合绑定一个 **渲染 lambda**。当容器状态变化时，MRD 查表得到对应的渲染逻辑，决定"覆盖层长什么样"。

```java
public final class MealRenderDispatch
{
    /** 注册 key：容器类型 + 尺寸 + 内容物类型 */
    private static final Map<String, MealRenderer> registry = new HashMap<>();
    private static final MealRenderer DEFAULT = (ctx, state) -> { /* 默认：用 item/fluid 的 standalone model 渲染 */ };

    /**
     * @param contentType 内容物类型 (ITEM, FLUID, MEAL)
     * @param containerType 容器种类
     * @param size 容器尺寸
     * @param count 物品/流体数量（ITEM/FLUID 时有意义，MEAL 时忽略）
     * @param renderer 渲染 lambda
     */
    public static void register(ContentType contentType, ContainerType containerType,
        ContainerSize size, int count, MealRenderer renderer);

    /**
     * 查询匹配的渲染器。
     * 优先精确匹配 (type, size, content, count)，无匹配时回退到 DEFAULT。
     */
    public static MealRenderer getRenderer(ContainerState state);

    /** 从内容物对象注册：自动从 ItemStack / FluidStack / Meal 推断参数。 */
    public static void registerFrom(ContainerType containerType, ContainerSize size,
        MealRenderer renderer, Object... contents);
}
```

### MealRenderer 函数接口

```java
@FunctionalInterface
public interface MealRenderer
{
    void render(MealRenderContext ctx, ContainerState state);
}
```

### MealRenderContext

```java
public record MealRenderContext(
    PoseStack poseStack,               // 已定位到容器的 inBlockPos + 旋转
    MultiBufferSource bufferSource,    // BER 的 bufferSource
    int packedLight,                   // 光照
    int packedOverlay                  // 覆盖层
) {}
```

---

## 3. 注册示例：碗 + 米饭

```java
// 在 mod 初始化时注册：
// "碗（中）+ 1 份米饭" → 渲染饱满的白饭效果

MealRenderDispatch.register(
    ContentType.ITEM,
    ContainerType.BOWL,
    ContainerSize.MID,
    1,
    (ctx, state) ->
    {
        PoseStack stack = ctx.poseStack();
        stack.pushPose();
        // 米饭模型：在碗内 0.16 高度处放置椭圆体
        stack.translate(0.5, 0.16, 0.5);
        // 获取米饭的 standalone BlockStateModel 并 tesselate
        BlockStateModel riceModel = Minecraft.getInstance().getModelManager()
            .getStandaloneModel(RICE_BOWL_CONTENT_KEY);
        ModelBlockRenderer.getInstance().tesselateBlock(
            (x, y, z, quad, instance) ->
                ctx.bufferSource().getBuffer(RenderTypes.CUTOUT)
                    .putBakedQuad(stack.last(), quad, instance),
            0, 0, 0, /* level, pos, state, model, seed */ ...);
        stack.popPose();
    }
);
```

---

## 4. SuBER — 容器自定义 BER

**文件**：`client/render/ber/ContainerBER.java`（待创建，在 ContainerComponent 的 `collectRenderState()` 中调用）

### 渲染流程

```
submit() 轮询 FURNITURE 表
  └── 发现 ContainerComponent implements ICustomRender
        └── collectRenderState()
              ├── 1) 构建本体渲染的 PoseStack：
              │       world pos → inBlockPos → 容器模型变换
              │       渲染碗/碟/杯的 BlockStateModel
              │
              ├── 2) 若 IRenderOverlayProvider.getContainerState() != null：
              │       构建 ContainerState
              │       查询 MRD.getRenderer(state)
              │       传入 MealRenderContext，执行覆盖层渲染
              │
              └── 3) 返回 FurnitureRenderState（含 containerState）
```

### 伪代码

```java
@Override
public FurnitureRenderState collectRenderState(ComponentStateDefinition def,
    MultiBuiltBlockEntity be)
{
    PoseStack bodyStack = new PoseStack();
    transformToContainer(bodyStack, def);  // inBlockPos + rotation

    ContainerState cs = getContainerState();
    return new FurnitureRenderState(this, def, bodyStack, cs);
}

// 在 MBER submit() 中：
FurnitureRenderState frs = component.collectRenderState(def, be);
renderContainerBody(frs.pose(), frs.definition());  // 渲染容器本体
if (frs.containerState() != null)
{
    MealRenderer overlay = MealRenderDispatch.getRenderer(frs.containerState());
    MealRenderContext ctx = new MealRenderContext(frs.pose(), bufferSource, packedLight, overlay);
    overlay.render(ctx, frs.containerState());
}
```

---

## 5. 多渲染器分发架构（R6 预览）

当前阶段（R4）关注容器 BER 的实现。R6 会将 BER 分发扩展到所有 FurnitureRendererType：

```
MultiBuiltBlockRenderer
├── renderStatic(SectionRenderContext)
│   └── 遍历 OPCODE_READALL
│       ├── BAKED_MODEL 类型 → 现有 chunk buffer 管线
│       └── (跳过 BER_DYNAMIC / GECKOLIB)
│
└── submit(BlockEntityRenderState, PoseStack, SubmitNodeCollector)
    └── 遍历 OPCODE_FURNITURE
        ├── CHUNK_BUFFER → 跳过（已在 renderStatic 处理）
        ├── BER_DYNAMIC → FurnitureComponentDispatcher.getRenderer() → IFurnitureBER.collect()
        └── GECKOLIB → (R6 实现)
```

---

## 6. 文件清单

| 文件 | 状态 | 说明 |
|------|------|------|
| `client/render/ber/dispatch/MealRenderDispatch.java` | 📋 待创建 | 覆盖层渲染分发 |
| `client/render/ber/dispatch/MealRenderer.java` | 📋 待创建 | 渲染函数接口 |
| `client/render/ber/dispatch/MealRenderContext.java` | 📋 待创建 | 渲染上下文 record |
| `block/furniture/content/ContainerState.java` | 📋 待创建 | 容器状态快照 |
| `block/furniture/content/ContainerType.java` | 📋 待创建 | 容器种类枚举 |
| `block/furniture/content/ContainerSize.java` | 📋 待创建 | 容器尺寸枚举 |
| `block/furniture/content/ContentType.java` | 📋 待创建 | 内容物类型枚举 |
| `client/render/ber/ContainerBER.java` | 📋 待创建 | 容器 BER 实现示例 |