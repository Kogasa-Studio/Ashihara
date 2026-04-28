# 命名规范与代码风格

> **标签**：[与阶段无关]
> **状态**：✅ 已生效
> **适用范围**：Ashihara 项目内所有可控源码（`src/main/java`、`src/generated` 中的可维护代码）

---

## 目标

- 统一命名语义，降低沟通和维护成本。
- 保证代码风格一致，提升可读性和审阅效率。
- 新增代码必须遵守本规范；历史遗留代码按迁移策略逐步重构。

---

## 命名规范

### 1) 接口命名必须使用 `I` 前缀

所有接口统一使用 `I` 前缀。

- 推荐形式：`I` + 语义名（PascalCase）
- 示例：`ICapableOfStorage`、`IPlaceable`、`IHasPreSwing`

### 2) 方块与物品类名必须带类型后缀

- 方块类名必须以 `Block` 结尾。
- 物品类名必须以 `Item` 结尾。

示例：

- `RiceCropBlock`
- `MortarBlock`
- `GuideBookItem`
- `PestleItem`

### 3) BlockEntity 渲染器统一使用 `BER` 后缀

Ashihara 内部约定：所有 BlockEntity 渲染器均命名为 `*BER`。

- 示例：`MortarBER`、`MillBER`、`CharlotteBER`

术语关系说明：

- `TER` 是历史生态中的旧称（Tile Entity Renderer）。
- `BEWLR` 指 `BlockEntityWithoutLevelRenderer`，语义上不等同于 BlockEntity 渲染器。
- 本项目文档和命名中，BlockEntity 渲染器统一使用 `BER`。

---

## 代码风格规范

### 1) 大括号风格：Allman

除特定可读性例外外，大括号独占一行。

```java
public class ExampleBlock extends Block
{
    public ExampleBlock(Properties properties)
    {
        super(properties);
    }

    public void runExample()
    {
        if (true)
        {
            doWork();
        }
    }
}
```

### 2) 长参数和长调用链必须展开

当小括号内内容过长或调用链较多时，按链式缩进换行，保持结构清晰。

```java
someBlock
{
    someMember.someMethod
    (
        foo
        .bar()
        .fooBar()
        .cat
        (
            meow(catField)
        )
    );
}
```

工程化建议：

- 优先保证“结构可读性”而不是“压缩行数”。
- 调用链断行后，点链和嵌套调用应与缩进层级一致。

### 3) 非必要不使用private或protected

本项目为完全开源项目，非常注重与其他mod的兼容性。因此，除非出于防止错误调用等优化意图，应尽量使用public作为类，方法和成员的修饰符。

---

## 历史代码迁移策略

项目中已有部分历史类不符合该规范，后续统一重构。

迁移原则：

1. 新代码严格执行，不再新增不规范命名。
2. 修改旧文件时，按“触达即清理”逐步重构。
3. 涉及对外 API 或跨模块引用时，先做兼容过渡，再删除旧名。

---

## 不适用范围

以下内容不强制按本规范重命名：

- 第三方库、Minecraft、NeoForge 的外部 API 名称。
- 与外部协议强绑定的字段名（如固定 NBT/JSON key）。

原则：内部可控代码严格统一，外部依赖保持原样。

