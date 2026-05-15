# 3D UI 组件变换数据驱动（规范 v0.2）

> 状态：**待实现**（设计已确定，代码尚未开始）
> 最后更新：2026-05-15
> 范围：定义需求、约束与数据模型，不定义编辑器 UI
> 关联文档：[3d-ui-design.md](3d-ui-design.md) · [roadmap.md](roadmap.md)

---

## 0. 当前现状（代码锚点）

| 内容 | 位置 | 状态 |
|---|---|---|
| `ModelComponent.presetTransform` (Matrix4f) | `client/gui3d/components/ModelComponent.java` | ✅ 已存在 |
| `ModelComponent.applyPreset(Matrix4f)` | 同上 | ✅ 已存在 |
| `Screen3D.screenId` (ResourceLocation) | `client/gui3d/Screen3D.java` | ❌ 尚未添加 |
| `ModelComponent.componentId` (String) | `client/gui3d/components/ModelComponent.java` | ❌ 尚未添加 |
| `ModelComponent.transformPolicy` (enum) | 同上 | ❌ 尚未添加 |
| `Gui3dConfigIO`（JSON 读写工具类） | 待新建 `client/gui3d/config/` | ❌ 尚未实现 |

> 前提条件已满足：`presetTransform` 字段和 `applyPreset()` 入口已在 `ModelComponent` 就位。

---

## 1. 目标

为 `Screen3D` 中的 `ModelComponent` 提供**可配置的变换预设**（位置/缩放/旋转），并支持：
- 从 JSON 配置文件加载变换，应用到组件的 `presetTransform`
- 将当前 Screen 状态导出回 JSON（用于调试/编辑器工作流）
- 支持"绝对变换"和"相对父组件变换"两种模式

---

## 2. 标识体系

### 2.1 screenId

- 类型：`ResourceLocation`
- 位置：`Screen3D` 基类新增字段，由子类构造时传入
- `null` 表示该 Screen 不参与数据驱动配置，跳过全部加载逻辑
- 示例：`ashihara:pot_screen`

```java
// 子类构造时传入 screenId
public PotScreen(PotMenu menu, Inventory inv, Component title) {
    super(menu, Component.empty(), ResourceLocation.fromNamespaceAndPath("ashihara", "pot_screen"));
}
```

### 2.2 componentId

- 类型：`String`（可为 null；null 表示不参与配置）
- 位置：`ModelComponent` 新增字段，在注册/创建组件时显式赋值
- 示例：`"pot_main"`、`"recipe_bubble"`

### 2.3 transformPolicy（新增，核心约束）

`ModelComponent` 新增枚举字段 `TransformPolicy`，**在代码注册时声明**：

| 值 | 语义 | 典型用例 |
|---|---|---|
| `NONE` | 禁止预设，JSON 中的配置会被忽略并打 warn | 骨骼绑定子组件（`PotLidComponent`、`ItemSlotComponent`）——它们跟骨骼走，不应接受外部 preset |
| `ABSOLUTE` | 允许绝对预设（Screen 空间） | 独立主模型（`PotModelComponent`） |
| `RELATIVE_ONLY` | 只允许相对于父组件的预设 | 链接到主模型的附属组件（气泡、标签等） |

> **设计原则**：由代码声明边界，而不是由 JSON 控制是否允许预设。JSON 只提供数值，代码声明权限。

---

## 3. 配置路径规则

- 根目录：`config/ashihara/gui3d/`
- 每个 Screen 一个配置文件，文件名与 `screenId` 对应
- 命名规则：`{namespace}/{path}.json`
  - `ashihara:pot_screen` → `config/ashihara/gui3d/ashihara/pot_screen.json`

> 文件不存在：静默跳过，所有组件保持代码默认值，不报错。

---

## 4. JSON 结构（v1）

完整示例（含绝对组件 + 链接组件）：

```json
{
  "schemaVersion": 1,
  "screenId": "ashihara:pot_screen",
  "components": [
    {
      "id": "pot_main",
      "policy": "ABSOLUTE",
      "transform": {
        "pos": [0.0, 0.0, 0.0],
        "scale": [1.0, 1.0, 1.0],
        "rot": [0.0, 0.0, 0.0]
      }
    },
    {
      "id": "recipe_bubble",
      "policy": "RELATIVE_ONLY",
      "link": {
        "parent": "pot_main",
        "space": "parent",
        "transform": {
          "pos": [38.0, -26.0, 8.0],
          "scale": [0.45, 0.45, 0.45],
          "rot": [0.0, 0.0, 0.0]
        }
      }
    }
  ]
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|---|---|---|
| `schemaVersion` | int | 当前为 `1`；解析器先读版本，再分发 |
| `screenId` | string | ResourceLocation 字符串，用于校验与日志，非必须匹配才能加载 |
| `components[].id` | string | 对应 `ModelComponent.componentId` |
| `components[].policy` | string | `"ABSOLUTE"` 或 `"RELATIVE_ONLY"`；与代码声明的 `TransformPolicy` 必须一致 |
| `components[].transform` | object | `policy=ABSOLUTE` 时使用 |
| `components[].link` | object | `policy=RELATIVE_ONLY` 时使用 |
| `link.parent` | string | 父组件的 `componentId` |
| `link.space` | string | 当前只支持 `"parent"`（后续可扩展 `"screen"` / `"bone"`） |
| `link.transform` | object | 相对于父组件原点的偏移变换 |
| `transform.pos` | `[x,y,z]` | float，平移 |
| `transform.scale` | `[x,y,z]` | float，`1.0` = 原始大小 |
| `transform.rot` | `[x,y,z]` | float，**角度制（度）**，顺序 X→Y→Z（ZYX 内旋） |

> 未知字段一律忽略（forward-compatible）。

---

## 5. 加载行为（两阶段）

在 `Screen3D.init()` 末尾（`super.init()` 调用后）触发 `Gui3dConfigIO.load(this)`：

### 阶段一：应用所有 ABSOLUTE 组件

1. 若 `screenId == null`，跳过整个加载
2. 定位并读取 JSON；文件不存在 → 静默返回
3. 遍历 `components` 数组，筛选 `policy == "ABSOLUTE"` 的条目：
   - 在 `this.components` 中找 `componentId` 匹配的 `ModelComponent`
   - 组件不存在：`LOGGER.warn(...)` 并继续
   - 组件的 `transformPolicy == NONE`：`LOGGER.warn("policy conflict: ...")` 并跳过
   - 组件 `transformPolicy != ABSOLUTE`：`LOGGER.warn(...)` 并跳过
   - 匹配且策略兼容：构建 `Matrix4f`，调用 `component.applyPreset(matrix)`
   - 单条解析出错：`LOGGER.error(...)` 并跳过，不影响其余

### 阶段二：应用所有 RELATIVE_ONLY 组件（拓扑排序）

4. 筛选 `policy == "RELATIVE_ONLY"` 的条目，按 `link.parent` 依赖关系做拓扑排序
5. 按序应用各链接组件：
   - 父组件不存在（或未完成阶段一）：`LOGGER.warn(...)` 并跳过
   - 组件 `transformPolicy != RELATIVE_ONLY`：`LOGGER.warn("policy conflict: ...")` 并跳过
   - 正常：`M_final = M_parent × M_linkLocal`，调用 `component.applyPreset(M_final)`

### 异常处理汇总

| 情况 | 处理 |
|---|---|
| JSON 中 `parent` 不存在 | `LOGGER.warn` + 跳过该条 |
| 循环依赖（A→B→A） | `LOGGER.error` + 跳过整个循环链 |
| JSON `policy` 与代码 `TransformPolicy` 冲突 | `LOGGER.warn("policy conflict")` + 跳过 |
| 单条字段解析出错 | `LOGGER.error` + 跳过该条，继续其余 |
| `snapshots` 缺失 / 结构异常 | `LOGGER.error` + 整体放弃该 JSON |

---

## 6. 保存行为

```java
Gui3dConfigIO.save(Screen3D screen)
```

1. 遍历 `screen.components`，深度遍历所有 `ModelComponent`（含子树）
2. 过滤：`componentId != null && transformPolicy != NONE`
3. 按 `transformPolicy` 生成对应 JSON 结构：
   - `ABSOLUTE` → 输出 `transform` 块，从 `presetTransform` 反解 `pos/scale/rot`
   - `RELATIVE_ONLY` → 输出 `link` 块（含 parent id），`link.transform` 为局部变换
4. 写入对应路径（覆盖写入），`schemaVersion: 1`

> 保存触发时机**由调用方决定**，不自动保存。

---

## 7. Matrix4f 构建规则

**从 JSON 构建变换**（pos/scale/rot → Matrix4f）：

```java
Matrix4f m = new Matrix4f()
    .translate(pos.x, pos.y, pos.z)
    .rotateZ((float) Math.toRadians(rot.z))
    .rotateY((float) Math.toRadians(rot.y))
    .rotateX((float) Math.toRadians(rot.x))
    .scale(scale.x, scale.y, scale.z);
```

**RELATIVE_ONLY 最终矩阵**：

```java
Matrix4f mFinal = new Matrix4f(parentPresetTransform).mul(mLinkLocal);
component.applyPreset(mFinal);
```

**反解（保存时）**：
- `pos` = `m.getTranslation(new Vector3f())`
- `scale` = `m.getScale(new Vector3f())`
- `rot` = 从 `m` 提取 Euler 角（ZYX 内旋，转角度制）

---

## 8. 扩展性要求（强约束）

- `schemaVersion` 先读，再分发给对应版本解析器；不跨版本强解析
- `link.space` 字段当前只支持 `"parent"`，后续可扩展为 `"screen"` / `"bone"` / `"world"` 而不破坏 v1 数据
- `TransformPolicy` 枚举后续可增加值（如 `BONE_RELATIVE`），不影响现有 `NONE/ABSOLUTE/RELATIVE_ONLY`
- JSON 中任何未知字段均忽略，不报错（forward-compatible）

---

## 9. 非目标（本阶段不做）

- 不定义编辑器 UI（编辑器可基于此数据模型构建，但不在本阶段）
- 不定义网络同步策略（仅客户端 config）
- 不自动侦测保存时机
- `link.space` 仅支持 `"parent"`，不实现 `"bone"` 空间

---

## 10. 实现检查清单

### v1：基础绝对变换（当前阶段目标）

- [ ] `TransformPolicy` 枚举（`NONE / ABSOLUTE / RELATIVE_ONLY`）新建在 `client/gui3d/components/`
- [ ] `ModelComponent` 新增 `componentId: String` 和 `transformPolicy: TransformPolicy` 字段
- [ ] `Screen3D` 新增 `screenId: ResourceLocation` 字段及对应构造重载
- [ ] `Gui3dConfigIO.load(Screen3D)` 实现阶段一（ABSOLUTE 加载，含策略冲突检测）
- [ ] `Gui3dConfigIO.save(Screen3D)` 实现 ABSOLUTE 组件序列化
- [ ] `PotScreen` 传入 `screenId = ashihara:pot_screen`
- [ ] `PotModelComponent` 设置 `componentId = "pot_main"`, `policy = ABSOLUTE`
- [ ] `PotLidComponent` / `ItemSlotComponent` 设置 `policy = NONE`
- [ ] 手动测试：配置存在时变换生效；文件不存在无报错；策略冲突打 warn 且不影响其余组件

### v1.1：链接组件（后续阶段）

- [ ] `Gui3dConfigIO.load` 实现阶段二（RELATIVE_ONLY 加载、拓扑排序、循环依赖检测）
- [ ] `Gui3dConfigIO.save` 支持输出 `link` 块（含局部变换反解）
- [ ] 手动测试：`recipe_bubble` 链接 `pot_main` 后位置正确；父组件不存在时 warn + 跳过；循环依赖 error + 跳过

### v2：可扩展（后续阶段，当前不实现）

- [ ] `link.space = "bone"` 支持：`M_final = boneMatrix × M_linkLocal`（需从 BoneTracer 读目标矩阵）
- [ ] `defaults` 节点（组件级 fallbackTransform）
- [ ] 编辑器 UI 入口（可按 `policy` 限制可编辑项）
