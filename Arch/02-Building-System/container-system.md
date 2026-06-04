# 建筑系统：容器组件系统

> **标签**：[阶段 2]
> **状态**：📋 待规划（R4~R5）
> **最后更新**：2026-06-04
> **相关文档**：[furniture-interfaces.md](furniture-interfaces.md), [food-system.md](../03-Gameplay-Systems/food-system.md)

---

## 概述

容器组件（ContainerComponent）是家具组件的子类，提供物品/流体/料理的存放、渲染和"料理调合"功能。它是连接建筑系统（Building System）和食物系统（Food System）的关键桥梁。

典型的容器：碗（盛饭/茶泡饭/丼）、碟（盛寿司/刺身）、杯（盛茶/酒）。

---

## 1. ContainerComponent 基类

**文件**：`block/furniture/ContainerComponent.java`（待创建）

```java
public abstract class ContainerComponent extends FurnitureComponent
    implements ICustomRender, ICustomData, IRenderOverlayProvider
{
    // rendererType 固定为 BER_DYNAMIC
    public ContainerComponent(String idIn, BuildingComponents.Type typeIn,
        List<ItemStack> dropsIn, Supplier<BaseMultiBuiltBlock> materialIn,
        SoundType soundIn)
    {
        super(idIn, typeIn, dropsIn, materialIn, soundIn,
            FurnitureRendererType.BER_DYNAMIC);
    }

    // --- 内容物状态 ---
    protected ContainerContent content = ContainerContent.EMPTY;

    // --- 实现 ICustomRender ---
    @Override public boolean doRender(MultiBuiltBlockEntity be) { return true; }
    @Override public abstract FurnitureRenderState collectRenderState(...);

    // --- 实现 ICustomData ---
    @Override public void serializeCustom(ValueOutput output) { /* 写入 content */ }
    @Override public void deserializeCustom(ValueInput input) { /* 读取 content */ }

    // --- 实现 IRenderOverlayProvider ---
    @Override @Nullable public ContainerState getContainerState() { /* ... */ }

    // --- 交互逻辑 ---
    /** 尝试向容器添加物品/流体。返回 true 表示成功。 */
    public abstract boolean tryAddContent(UseOnContext context, ItemStack held);

    /** 尝试从容器取出。返回 true 表示成功。 */
    public abstract boolean tryTakeContent(UseOnContext context, Player player);

    /** 尝试触发料理调合。参数为第二个原料。返回 true 表示调合成功。 */
    public abstract boolean tryCook(UseOnContext context, ItemStack ingredient);
}
```

---

## 2. 内容物状态机 — ContainerContent

**文件**：`block/furniture/content/ContainerContent.java`（待创建）

容器内容物有且仅有四种状态：

```
EMPTY ──(放入物品)──→ ITEM
EMPTY ──(放入流体)──→ FLUID
ITEM  ──(加入第二原料+调合)──→ MEAL（锁定）
FLUID ──(加入第二原料+调合)──→ MEAL（锁定）
```

### 状态定义

```java
public sealed interface ContainerContent
    permits ContainerContent.Empty, ContainerContent.ItemContent,
            ContainerContent.FluidContent, ContainerContent.MealContent
{
    record Empty() implements ContainerContent {}

    record ItemContent(ItemStack stack) implements ContainerContent {}

    record FluidContent(FluidStack stack) implements ContainerContent {}

    record MealContent(Meal meal) implements ContainerContent {}
}
```

### 状态说明

| 状态 | 允许操作 | 渲染方式 |
|------|---------|---------|
| EMPTY | 可放入物品或流体 | 仅渲染容器本体 |
| ITEM | 只能加入第二原料触发调合；不可移除已装填的物品 | 覆盖层渲染物品 |
| FLUID | 只能加入第二原料触发调合；不可移除已装填的流体 | 覆盖层渲染流体 |
| MEAL | **锁定**。不可再添加/移除任何内容物，直到料理被吃完或倒掉 | MRD 渲染完整料理 |

### 关键约束

- **调合最多接受 2 种原料**（因为内容物只能有一种：ITEM 或 FLUID + 第二个原料 → MEAL）。
- **MEAL 状态锁定**：一旦形成料理，不再接受任何内容物变更。
- **吃完倒掉**由 `tryTakeContent()` 处理——玩家每吃一口，减少一口计数；计数器归零时回到 EMPTY 状态。

---

## 3. 料理结构 — Meal

**文件**：`block/furniture/content/Meal.java`（待创建）

```java
public record Meal(
    Identifier mealId,             // e.g., "ashihara:chazuke", "ashihara:donburi"
    List<MealPrefix> prefixes,     // 修饰/配料列表
    int remainingBites             // 剩余口数（≥1）
)
```

### Meal：不可变记录

- `mealId` 标识料理的"菜名"（茶泡饭、丼、拉面等），决定基础食效。
- `prefixes` 是修饰项列表（见下文），决定具体配料和渲染样式。
- `remainingBites` 从食谱定义中初始化，每次 eaten 后递减；归零后容器回到 EMPTY。

### 料理调合产物

料理调合（Cooking）的产物在概念上**不是普通物品**，而是一个 Meal 记录——它存在于碗的 ContainerContent 中，不可提取为物品放进背包。

---

## 4. 修饰项 — MealPrefix

**文件**：`block/furniture/content/MealPrefix.java`（待创建）

```java
public record MealPrefix(
    String id,                      // "tea_green", "sauce_soy", "nori", "bonito_flakes"
    Component displayName,          // 显示名称
    Supplier<MealPropertyModifier> effects  // 食效修正（饱和/状态效果）
)
```

### 职责

- **渲染**：prefix 决定 MRD 中对应的覆盖层模型/纹理。
  例如 `"bonito_flakes"` 触发在米饭上渲染木鱼花薄片纹理。
- **食效**：prefix 可调整食用后的饱食度、附加状态效果。
  例如 `"tea_green"` 使茶泡饭附加急迫效果。

### 与 Meal 的关系

```
Meal("ashihara:donburi")
├── MealPrefix("rice")           ← 米饭（base）
├── MealPrefix("sauce_soy")      ← 酱油
├── MealPrefix("nori")           ← 海苔
└── MealPrefix("bonito_flakes")  ← 木鱼花
```

`rice` 是料理的"底料"prefix，源于容器中已存在的 ITEM 状态（米饭物品）。

---

## 5. 料理调合系统

### 调合配方

调合配方定义在 `data/ashihara/recipe/` 下，使用自定义 RecipeType（或工作台配方）。

#### 配方示例

**茶泡饭（Chazuke）**：
```
输入 1: BowlContainerComponent 中含有 ItemContent(RICE)
输入 2: 流体：茶（FluidStack）
产物: Meal("ashihara:chazuke", [rice, tea_green], bites=4)
```

**丼（Donburi）**：
```
输入 1: BowlContainerComponent 中含有 ItemContent(RICE)
输入 2: 任意带有 don_material 标签的物品（e.g., 炸虾、牛肉）
产物: Meal("ashihara:donburi", [rice, don_material], bites=3)
```

### 调合触发方式

| 方式 | 说明 |
|------|------|
| **工作台合成** | 将装有内容的碗放入合成格 + 第二原料 → 输出带有 Meal 的碗 |
| **世界内右键** | 手持第二原料，对着放置在地上的碗右键 → 触发 `tryCook()` |

`tryCook()` 的逻辑：
1. 检查当前 content 状态是否为 ITEM 或 FLUID
2. 与手持物品匹配调合配方
3. 若匹配成功 → 创建 MealContent 替换原 content → 锁定 → 返 true
4. 若不匹配 → 不做任何事 → 返回 false

---

## 6. 玩家交互流程

```
1. 放置碗 → MBE 的 FURNITURE 表中添加 ContainerComponent
2. 手持米饭右键碗 → tryAddContent() → content = ItemContent(RICE)
   同步：BlockEntity updated + BER 覆盖层渲染米饭
3. 手持茶壶右键碗 → tryCook() → content = MealContent(Chazuke)
   同步：BER 覆盖层变为 MRD 渲染的茶泡饭外观
4. 手持筷子右键碗 → tryTakeContent() → remainingBites -= 1
   第4次一口后 → remainingBites = 0 → content = EMPTY
   同步：BER 覆盖层消失，仅剩空碗本体
```

---

## 7. 文件清单

| 文件 | 状态 | 说明 |
|------|------|------|
| `block/furniture/ContainerComponent.java` | 📋 待创建 | 容器基类 |
| `block/furniture/content/ContainerContent.java` | 📋 待创建 | 内容物状态机 |
| `block/furniture/content/Meal.java` | 📋 待创建 | 料理 record |
| `block/furniture/content/MealPrefix.java` | 📋 待创建 | 修饰项 record |
| `block/furniture/content/ContainerState.java` | 📋 待创建 | 渲染状态快照 |
| `block/furniture/content/ContainerType.java` | 📋 待创建 | 容器种类枚举 |