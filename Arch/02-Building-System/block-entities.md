# 建筑系统：方块实体规范

> **标签**：[阶段 2]
> **状态**：🚧 进行中
> **最后更新**：2026-06-01
> **相关文档**：[overview.md](overview.md), [rendering.md](rendering.md)

---

## 概述

`MultiBuiltBlockEntity` 是建筑系统的唯一 BE，承载所有子方块组件（`BuildingComponent`）的状态。它继承 `AshiharaCommonBE` 并实现 `IMultiBuiltBlock` 接口，无 tick 方法——所有操作均由事件驱动。

## MultiBuiltBlockEntity

**文件**: `block/blockentity/MultiBuiltBlockEntity.java`
**类层级**: `BlockEntity → AshiharaCommonBE → MultiBuiltBlockEntity implements IMultiBuiltBlock`

### 核心数据

```java
// 操作码（用于 getComponents 筛选和序列化分片）
public static final int OPCODE_COMPONENT = 0;    // 结构组件
public static final int OPCODE_ADDITIONAL = 1;   // 装饰/附加组件
public static final int OPCODE_READALL = 2;      // 两者合并

// 组件状态列表
public List<ComponentStateDefinition> COMPONENTS = new ArrayList<>();           // 结构组件
public List<ComponentStateDefinition> ADDITIONAL_COMPONENTS = new ArrayList<>(); // 装饰组件

// 碰撞网格缓存（3×4×3 网格中已被占用的单元）
public List<Occupation> occupationCache = new ArrayList<>();
```

### 结构组件 vs 附加组件

| 属性 | COMPONENTS (OPCODE=0) | ADDITIONAL_COMPONENTS (OPCODE=1) |
|------|----------------------|----------------------------------|
| 作用 | 建筑的承重/主体结构 | 装饰、附加品（如金具、悬鱼） |
| 碰撞检测 | 参与 Occupation 检查 | 不参与，仅检查重复 |
| 破坏工具 | WOODEN_HAMMER（木槌） | CHISEL（凿子） |
| 典型实例 | Column, Beam, Wall, Floor | HangingFish, GoldPinFin |

### 关键方法

#### tryPlace — 放置组件

```java
public boolean tryPlace(BuildingComponent component, Vec3 target, Direction facing, Player player)
```

1. 校验 `component` 是否实现了 `Connectable` 且 `checkConnection` 通过
2. 若非 `AdditionalComponent`：调用 `Occupation.join(occupationCache, component.occupation)` 检查碰撞；冲突则返回 false
3. 若是 `AdditionalComponent`：检查列表中是否已有同 id 的装饰（不可重复）
4. 新增 `ComponentStateDefinition`，添加到对应列表
5. 触发 `Connectable.applyConnection()`、`reloadShape()`、`checkMaterial()`、`setChanged()`

#### tryBreak — 破坏组件

```java
public boolean tryBreak(ItemStack tool, Vec3 target, Player player)
```

1. 通过木槌/凿子判断破坏 COMPONENTS 还是 ADDITIONAL_COMPONENTS
2. 调用 `breakComponent(index, list, listName)`

#### breakComponent

```java
private void breakComponent(int index, List<ComponentStateDefinition> list, String listName)
```

1. 生成粒子效果、播放破坏音效
2. 从组件 `ModelBake` 列表中获取对应物品并生成 world drop
3. 将 `index` 从列表中移除并更新 occupationCache
4. 若列表为空（所有组件被移除）：销毁方块自身 → 掉落 MultiBuiltBlock 方块物品
5. 调用 `refresh()` → `reloadShape()` + `checkConnection()` + `checkMaterial()` + `setChanged()` + `sync()`

#### reloadShape — 重建碰撞形状

```java
public void reloadShape()
```

遍历所有 `COMPONENTS`，将每个 `ComponentStateDefinition.shape()` OR 叠加，然后按 `FACING` 旋转，得到方块最终的复合 `VoxelShape`。该 shape 被 `BaseMultiBuiltBlock.getShape()` 查询。

#### checkConnection — 自动连接

```java
public void checkConnection(BlockPos neighborPos)
```

遍历所有实现了 `Connectable` 的组件，若有与 `neighborPos` 相关的连接——例如 Beam 组件需要延伸到相邻的 MultiBuiltBlock——则更新组件的属性（如 `Beam.applyConnection()` 在相邻方块存在同方向 Beam 时变为 connected 状态）。

连接检测在 `BaseMultiBuiltBlock.neighborChanged` 触发（即相邻方块更新时）。

#### checkMaterial — 材料自动升级

```java
public void checkMaterial()
```

遍历所有已放置组件，找出 `component.material().priority` 最高的值。若高于当前方块的 `ComponentMaterial.priority`，则调用 `applyMaterial()` 替换方块材质。

典型场景：在 `OAK_WOOD (priority=21)` 组件方块中放置 `GOLD_DECO (priority=31)` 组件 → 整个方块升级为 `GOLD_DECO_COMPONENT`。

**目的**：让小地图、Distant Horizons 等依赖 MapColor 测绘缩略图的 mod 识别方块外观并生成正确的缩略图颜色，行为对玩家不可见。

#### tryInteract — 组件交互

```java
public boolean tryInteract(Vec3 target, Player player)
```

遍历所有实现了 `Interactable` 的组件。若组件实现了 `Decoratable`，则还检查玩家手持物品是否匹配该组件的装饰要求——如 `Wall` 组件接受特定物品填入障子。

### Occupation 碰撞系统

**文件**: `block/building/component/Occupation.java`

每个方块内部被划分为 3×4×3 = 36 个网格单元（X=3, Y=4层, Z=3）。每个 `BuildingComponent` 声明它占用的 `List<Occupation>` 单元。

```java
// 3×4×3 网格 —— X 为红轴，Z 为蓝轴，Y 分 4 层
public enum Occupation
{
    X1_Y1_Z1, X1_Y1_Z2, /* ... 全部 36 个单元 */
    X3_Y4_Z3;

    public static List<Occupation> join(List<Occupation> existing, List<Occupation> incoming)
    {
        // 检测 incoming 中的任意单元是否与 existing 冲突
        // 若冲突返回 null；不冲突返回合并后的新列表
    }
}
```

组件放置时调用 `Occupation.join(occupationCache, component.occupation)`：若返回 null（冲突），放置被拒绝；否则以合并后的列表更新 `occupationCache`。

### 序列化

```java
@Override
protected void saveAdditional(ValueOutput output)
{
    super.saveAdditional(output);
    output.putChild("components", ...);   // 序列化 COMPONENTS
    output.putChild("additionals", ...);  // 序列化 ADDITIONAL_COMPONENTS
}

@Override
protected void loadAdditional(ValueInput input)
{
    super.loadAdditional(input);
    COMPONENTS = input.getChild("components", ...);
    ADDITIONAL_COMPONENTS = input.getChild("additionals", ...);
    // 随后在 onLoad 或渲染触发时重建 occupationCache
}
```

使用 NeoForge 26.1 的 `ValueInput`/`ValueOutput` API，替代旧的 `CompoundTag` 模式。

### BE 类型注册

**文件**: `registry/BlockEntities.java`

```java
public static final Supplier<BlockEntityType<MultiBuiltBlockEntity>> MULTI_BUILT_BLOCKENTITY =
    BLOCK_ENTITIES.register("multi_built_blockentity",
        () -> new BlockEntityType<>(MultiBuiltBlockEntity::new,
            Blocks.BAMBOO_BONES_COMPONENT.get(),
            Blocks.RAMMED_SOIL_COMPONENT.get(),
            // ... 全部 13 种 BaseMultiBuiltBlock 子类 ...
            Blocks.TERRACOTTA_TILE_COMPONENT.get()
        )
    );
```

`BlockEntityType` 必须包含所有可作为该 BE 宿主的方块类型。当前 13 种 `ComponentMaterial` 均有自己的 `BaseMultiBuiltBlock` 子类，全部需要在构造 `BlockEntityType` 时声明。

## IMultiBuiltBlock 接口

**文件**: `block/building/IMultiBuiltBlock.java`

```java
public interface IMultiBuiltBlock
{
    List<ComponentStateDefinition> getComponents(int opcode);
}
```

供外部代码（如 BER、neighbor detection）统一获取组件列表的轻量接口。

## 生命周期总结

```
方块放置 (BuildingComponentItem.place)
  └── 创建 MultiBuiltBlockEntity
        └── tryPlace(component, ...) → COMPONENTS.add(def)
              └── Occupation.join → reloadShape → checkConnection → checkMaterial

右键放置组件 (BaseMultiBuiltBlock.useItemOn)
  └── be.tryPlace(component, ...)

右键破坏组件
  └── be.tryBreak(tool, ...)
        └── breakComponent(index, list)
              └── spawn drops + particles + 从列表移除
              └── 若列表为空 → 销毁方块
              └── refresh()

neighborChanged (相邻方块更新)
  └── be.checkConnection(neighborPos)
  └── be.checkMaterial()

chunk rebuild (渲染)
  └── WLREventHandler → renderStatic → 遍历所有 ComponentStateDefinition → 渲染模型
```
