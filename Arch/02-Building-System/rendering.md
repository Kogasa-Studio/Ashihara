# 建筑系统：渲染管线（WithLevelRenderer）

> **标签**：[阶段 2]
> **状态**：🚧 进行中
> **最后更新**：2026-06-01
> **相关文档**：[overview.md](overview.md), [block-entities.md](block-entities.md)

---

## 概述

建筑系统的渲染不走传统的每帧 BE render pass，而是通过 `WithLevelRenderer` 接口将自定义几何体注入 Minecraft 原版的 chunk buffer（区块网格）中。渲染结果作为 chunk mesh 的一部分，享受与普通方块同等的渲染优化——无 per-frame 开销，仅在 chunk rebuild 时执行。

## 核心机制：AddSectionGeometryEvent

NeoForge 提供了 `AddSectionGeometryEvent`，在每次 chunk section（16×16×16）重建时触发。这是插入自定义几何体的唯一钩子。

```
ChunkRenderer.RenderChunkRebuildTask
  └── AddSectionGeometryEvent 触发
        └── WLREventHandler.onWLR()
              └── WithLevelRendererAdditionalSectionRenderer.render()
                    └── 遍历 section 内所有 BlockPos
                          └── 检查 BE 的 renderer 是否为 WithLevelRenderer
                                └── renderStatic(context, modelRenderer)
```

## WithLevelRenderer 接口

**文件**: `client/render/WithLevelRenderer.java`

```java
public interface WithLevelRenderer<T extends BlockEntity>
{
    // 注入 chunk buffer 的核心方法 —— 只在 chunk rebuild 时调用
    void renderStatic(SectionRenderContext context, ModelRenderer renderer);

    // 将 PoseStack 重置到方块在 section 内的本地坐标 (pos & 15)
    static void resetToBlock000(BlockPos pos, PoseStack poseStack);

    // 获取方块位置的光照 packed coords，供 BER 使用
    default int getPackedLight(BlockEntity be);

    // 提交方块的独立 BlockStateModel（而非普通 blockstate model）
    interface ModelRenderer
    {
        void renderBlockStateModel(BlockStateModel model, PoseStack stack,
            int overlay, ModelData modelData);
    }
}
```

## SectionRenderContext

**文件**: `client/render/SectionRenderContext.java`

```java
public record SectionRenderContext(
    BlockAndTintGetter level,           // section 区域的 block access
    BlockPos pos,                       // BE 的绝对坐标
    BlockEntity blockEntity,            // BE 实例
    PoseStack poseStack,                // 全新的 identity PoseStack（未定位）
    Function<ChunkSectionLayer, VertexConsumer> consumerFunction  // 获取 chunk buffer
) {}
```

**关键**：
- `poseStack` 每次创建时是 identity 矩阵。BE 必须调用 `resetToBlock000()` 定位到 section 内的相对坐标 `(pos.x & 15, pos.y & 15, pos.z & 15)`。
- `consumerFunction` 提供按 layer 获取 `VertexConsumer` 的能力，用于写入 `putBakedQuad`。

## WLREventHandler：桥接层

**文件**: `event/WLREventHandler.java`

这是连接 `AddSectionGeometryEvent` 和 `WithLevelRenderer` 的关键代码。

```java
public static final class WithLevelRendererAdditionalSectionRenderer
    implements AddSectionGeometryEvent.AdditionalSectionRenderer
{
    @Override
    public void render(AddSectionGeometryEvent.SectionRenderingContext context)
    {
        BlockPos posSource = this.origin;           // section 原点
        BlockPos posTarget = posSource.offset(15, 15, 15);  // section 对角

        for (BlockPos pos : BlockPos.betweenClosed(posSource, posTarget))
        {
            BlockEntity be = region.getBlockEntity(pos);
            if (be == null) continue;

            BlockEntityRenderer<?, ?> renderer =
                Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be);

            if (renderer instanceof WithLevelRenderer<?> r)
            {
                r.renderStatic(
                    new SectionRenderContext(region, pos, be, new PoseStack(),
                        context::getOrCreateChunkBuffer),
                    (model, stack, overlay, modelData) ->
                        context.getBlockRenderer().tesselateBlock(
                            (x, y, z, quad, quadInstance) ->
                                context.getOrCreateChunkBuffer(ChunkSectionLayer.CUTOUT)
                                    .putBakedQuad(stack.last(), quad, quadInstance),
                            SectionPos.sectionRelative(pos.getX()),
                            SectionPos.sectionRelative(pos.getY()),
                            SectionPos.sectionRelative(pos.getZ()),
                            region, pos, region.getBlockState(pos), model, 42L
                        )
                );
            }
        }
    }
}
```

### `ModelRenderer` 的 lambda 实现

这是整个管线的核心。`ModelRenderer` 的 lambda 做了以下事情：

1. 接收 `(BlockStateModel model, PoseStack stack, int overlay, ModelData modelData)`
2. 调用 `context.getBlockRenderer().tesselateBlock(...)` 将 BlockStateModel 分解为 quad 列表
3. 对每个 quad，调用 `putBakedQuad(stack.last(), quad, quadInstance)` 写入 `ChunkSectionLayer.CUTOUT` 的 chunk buffer

`tesselateBlock` 的最后一个参数 `42L` 是随机数seed。

## MultiBuiltBlockRenderer 的 renderStatic 实现

**文件**: `client/render/ber/MultiBuiltBlockRenderer.java`

```java
@Override
public void renderStatic(SectionRenderContext context, ModelRenderer modelRenderer)
{
    MultiBuiltBlockEntity be = (MultiBuiltBlockEntity) context.blockEntity();
    PoseStack stack = context.poseStack();

    for (ComponentStateDefinition def : be.getComponents(OPCODE_READALL))
    {
        if (def.component().type != Type.BAKED_MODEL) continue;

        // 1) 归零到 section 内的方块相对坐标
        resetToBlock000(be, stack);

        // 2) FACING 方向旋转（绕方块中心）
        translateCoordinateSystem(be, stack);

        // 3) 平移到组件在方块内的位置
        Vec3 pos = def.inBlockPos();
        stack.translate(pos.x, pos.y, pos.z);

        // 4) 组件自身旋转（绕组件局部中心）
        stack.translate(0.5, 0, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(def.rotationY()));
        stack.mulPose(Axis.XP.rotationDegrees(def.rotationX()));
        stack.mulPose(Axis.ZP.rotationDegrees(def.rotationZ()));
        stack.translate(-0.5, 0, -0.5);

        // 5) 获取独立 BlockStateModel 并提交
        BlockStateModel model = Minecraft.getInstance().getModelManager()
            .getStandaloneModel(ClientEventSubscribeHandler.getOrCreateKey(def.model().id()));
        modelRenderer.renderBlockStateModel(model, stack, OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
    }
}
```

### 变换链总结

每个组件的最终世界矩阵：

```
Final = resetToBlock000                    // → section 内相对坐标 (x&15, y&15, z&15)
      × translate(0.5, 0, 0.5)            // → 移到方块中心（FACING 旋转用）
      × rotateY(FACING)                    // → 朝向旋转
      × translate(-0.5, 0, -0.5)          // → 移回方块角
      × translate(inBlockPos)             // → 移到组件 inBlock 位置
      × translate(0.5, 0, 0.5)            // → 移到组件中心（自旋用）
      × rotateY(rotationY)                // → 组件自旋 Y
      × rotateX(rotationX)                // → 组件自旋 X
      × rotateZ(rotationZ)                // → 组件自旋 Z
      × translate(-0.5, 0, -0.5)          // → 移回组件角
```

**`resetToBlock000` 的 `pos & 15` 位运算**：Minecraft 的 chunk section 是 16×16×16，方块在该 section 内的坐标是 `pos.x % 16` 到 `pos.x % 16 + 1`。`pos & 15` 等价于 `pos % 16`（对正数），将绝对世界坐标映射到 section 相对坐标。

**`tesselateBlock` 的 `SectionPos.sectionRelative(pos)` 参数**：与 `resetToBlock000` 互相配合——`resetToBlock000` 将 PoseStack 定位到 section 相对位置，`tesselateBlock` 以该原点为基础将 quad 顶点偏移到正确位置。

## 模型注册

### 独立模型注册（Standalone Models）

建筑组件模型作为独立模型注册，而非 blockstate model：

```java
// ClientEventSubscribeHandler.onRegisterAdditionalModels()
@SubscribeEvent
public static void onRegisterAdditionalModels(ModelEvent.RegisterStandalone event)
{
    AdditionalModels.getModels().forEach(location ->
        event.register(
            getOrCreateKey(location.id()),
            SimpleUnbakedStandaloneModel.blockStateModel(location.id())
        )
    );
}
```

所有组件模型在 `AdditionalModels.java` 中集中登记。渲染时通过 `Minecraft.getInstance().getModelManager().getStandaloneModel(key)` 获取已烘焙的 `BlockStateModel`。

## CandleBER：另一个 WithLevelRenderer 实现

**文件**: `client/render/ber/CandleBER.java`

CandleBER 展示了另一种用法：渲染多个**普通 blockstate model**实例。

```java
BlockStateModel model = Minecraft.getInstance().getModelManager()
    .getBlockStateModelSet().get(Blocks.CANDLE.get().defaultBlockState());

for (double[] d : be.getPosList()) {
    stack.pushPose();
    stack.translate(-0.5, 0, -0.5);
    stack.translate(d[0], d[1], d[2]);
    renderer.renderBlockStateModel(model, stack, OverlayTexture.NO_OVERLAY, ModelData.EMPTY);
    stack.popPose();
}
```

关键区别：CandleER 使用 `getBlockStateModelSet().get(BlockState)`（普通 blockstate model），MultiBuiltBlockRenderer 使用 `getStandaloneModel(key)`（独立 standalone model）。

## 渲染类型

当前所有建筑组件通过 `ChunkSectionLayer.CUTOUT` 渲染。不支持半透明或 cutout_mipped 层。如未来需要支持半透明组件（如玻璃障子），需在 `ModelRenderer` lambda 中增加 layer 参数。

## 性能特征

| 维度 | 特征 |
|------|------|
| 调用频率 | 仅在 chunk rebuild 时（区块加载、方块更新），不在每帧 |
| 几何体处理 | quad 直接写入 chunk buffer → 编译进 section mesh → GPU 高效绘制 |
| 实例数量 | 不受 BE count 限制（几何体已 baked into mesh） |
| 内存 | 同原版 chunk——不额外占用 per-frame 分配 |
| 限制 | 动态动画不支持（可通过叠加传统 BER pass 实现） |

## 文件索引

| 文件 | 角色 |
|------|------|
| `client/render/WithLevelRenderer.java` | 接口定义 |
| `client/render/SectionRenderContext.java` | 渲染上下文 record |
| `event/WLREventHandler.java` | AddSectionGeometryEvent → WithLevelRenderer 桥接 |
| `client/render/ber/MultiBuiltBlockRenderer.java` | 建筑组件渲染器 |
| `client/render/ber/CandleBER.java` | 蜡烛渲染器（另一个实现） |
| `event/ClientEventSubscribeHandler.java` | BER 注册 + StandaloneModel 注册 |
| `registry/AdditionalModels.java` | 所有独立模型定义 |
