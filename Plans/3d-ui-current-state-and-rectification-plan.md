# 3D UI 现状盘点与整改计划（含 Arch + Plans）

> 日期：2026-05-15
> 范围：`src/main`、`Arch/05-Technical-Foundation/3d-ui-framework`、`Plans/temp-*`（用户确认纳入）
> 目标：沉淀一份可执行整改清单，解决“现有资产、冗余与优化、文档过时”三类问题。

---

## 1. 现有资产（我们已经有的东西）

### 1.1 核心运行链路（已落地）

- **3D Screen 基座**：`src/main/java/kogasastudio/ashihara/client/gui3d/Screen3D.java`
  - 组件树生命周期（`init/tick`）
  - 鼠标事件分发（`mouseClicked/mouseReleased/mouseDragged/mouseScrolled/mouseMoved`）
  - PiP 提交渲染状态（`extractRenderState` -> `Screen3DPiPRenderState`）
  - F9 调试覆盖层开关（`Gui3dDebugOverlay`）

- **容器3D基类**：`src/main/java/kogasastudio/ashihara/client/gui3d/ContainerScreen3D.java`
  - 持有 `AbstractContainerMenu`
  - 统一槽位点击发包（`ContainerSlotClickPacket`）
  - carried-item 跟随鼠标渲染

- **土锅首个业务屏幕**：`src/main/java/kogasastudio/ashihara/client/gui3d/PotScreen.java`
  - 组装 `PotModelComponent`、`PotLidComponent`、4个 `ItemSlotComponent`
  - 已接入 `MenuTypes.POT_MENU` 与菜单注册

- **组件系统骨架**：`src/main/java/kogasastudio/ashihara/client/gui3d/components/AbstractComponent.java`
  - 树结构、命中检测、悬停状态、优先级排序
  - 通过 `OBB + ObbInterSector` 做拾取
  - 支持 hover transition 钩子（`onHoverEnter/onHoverExit/fireHoverTransitions`）

### 1.2 几何与交互能力（已落地）

- **Ray/OBB/相交计算**：
  - `src/main/java/kogasastudio/ashihara/client/gui3d/util/Ray.java`
  - `src/main/java/kogasastudio/ashihara/client/gui3d/util/OBB.java`
  - `src/main/java/kogasastudio/ashihara/client/gui3d/util/ObbInterSector.java`

- **骨骼追踪到 OBB**：
  - `src/main/java/kogasastudio/ashihara/client/gui3d/util/BoneTracer.java`
  - `src/main/java/kogasastudio/ashihara/client/gui3d/util/GeoCubeObbExtractor.java`
  - Mixin 注入链：`MixinGeoObjectRenderer` + `MixinGeoBone` + `GeoRendererPoseSyncProvider`
  - Mixin 配置：`src/main/resources/ashihara.mixins.json`

- **选框反馈系统（部分可用）**：
  - `SelectionFrameComponent`（状态机字段与驱动接口存在）
  - `PotLidComponent` / `ItemSlotComponent` 触发选框显示/隐藏

### 1.3 PiP 渲染主链（已落地）

- **PiP renderer**：`src/main/java/kogasastudio/ashihara/client/render/geo/pip/Screen3DPiPRenderer.java`
- **渲染状态**：
  - `src/main/java/kogasastudio/ashihara/client/render/state/Screen3DPiPRenderState.java`
  - `src/main/java/kogasastudio/ashihara/client/render/state/GUI3DComponentRenderState.java`
- **客户端注册**：`ClientEventSubscribeHandler.onRegisterPiPRenderers`

### 1.4 GuideBook 迁移桥接（阶段性）

- `GuideBookScreen` 已继承 `Screen3D`，并通过 PiP 提交书本渲染状态。
- `GuideBookRenderer` 保留 `renderCompat` 兼容入口（明确标注“临时 shim / fallback”）。

---

## 2. 冗余代码与可优化空间

## 2.1 冗余/未接入（优先处理）

1. **未被主链使用的交互抽象（已完成）**
   - 文件：
     - `src/main/java/kogasastudio/ashihara/client/gui3d/interaction/HitPolicy.java`
     - `src/main/java/kogasastudio/ashihara/client/gui3d/interaction/HitResult.java`
   - 现状：已接入主链（`Screen3D` / `AbstractComponent`），支持 `BLOCK/PENETRATE/MIXED` 命中决策。
   - 收益：避免“定义存在但未生效”的认知偏差。

2. **2D/旧渲染残留方法体（注释化，已完成主要清理）**
   - 典型文件：`Panel.java`、`PotLidComponent.java`、`SelectionFrameComponent.java`、`ItemSlotComponent.java`
   - 现状：已清理主要组件中的注释化旧渲染逻辑。
   - 收益：降低阅读成本，减少后续改动误判。

3. **GuideBook 迁移临时桥接未收口**
   - 文件：`src/main/java/kogasastudio/ashihara/client/render/geo/GuideBookRenderer.java`
   - 现状：`renderCompat` 仍保留，Plans 里也写明后续应删除。
   - 问题：桥接层长期存在会增加双路径维护成本。

## 2.2 结构性优化（中期）

1. **命中空间统一（已完成）**
   - `GuideBookScreen.mouseClicked(...)` 已复用 `Screen3D.createMouseRay(...)`，并保留当前 GUI 空间 OBB 判定一致性。

2. **ModelComponent tracer 生命周期（已完成）**
   - 已在 `ModelComponent.dispose()` 与 `Screen3D` 的移除/关闭链路补齐清理路径。

3. **OBB 投影与调试代码分层**
   - `Gui3dDebugOverlay` 内含投影、线段绘制、命中收集。
   - 优化：拆分为 `debug/collector/projector/draw` 小模块，提高可测性。

4. **渲染状态类型统一**
   - `CommonGeoRenderState` 与 `GUI3DComponentRenderState` 的 dataMap 思路重叠。
   - 优化：评估统一为一套 state builder，减少重复心智模型。

---

## 3. 哪些文档已经过时（Arch + Plans）

## 3.1 明显过时（建议立即修）

1. **`Arch/05-Technical-Foundation/3d-ui-framework/README.md`**
   - 当前写法："状态：架构已确定，待代码实现"。
   - 实际：核心代码已存在（`Screen3D`、`AbstractComponent`、`PotScreen`、PiP链、Mixin骨骼追踪）。
   - 结论：状态描述过时。

2. **`Arch/05-Technical-Foundation/3d-ui-framework/3D-UI-FRAMEWORK-STATUS.md`**
   - 当前强调“准备启动实现 session”。
   - 实际：实现已进行并含阶段迁移桥接。
   - 结论：定位从“验收准备”应升级为“实施中状态板”。

3. **`Arch/05-Technical-Foundation/3d-ui-framework/roadmap.md`**
   - 当前是 1-3 天冲刺计划（Day1/Day2/Day3）。
   - 实际：项目进入多阶段迁移（含 stage 2.x、stage3、stage4）。
   - 结论：路线图已失去时间语义真实性。

4. **`Arch/05-Technical-Foundation/3d-ui-framework/pot-gui-spec.md`**
   - 文档写 8 个槽位；代码 `PotMenu.INGREDIENT_SLOTS` 目前接入 4 槽（`ItemSlotComponent` 循环）。
   - 结论：规格与实现不一致（需确认目标是 4 还是 8）。

5. **`Arch/05-Technical-Foundation/3d-ui-framework/decisions/INDEX.md`**
   - 引用旧路径：`Ashihara_1.21`。
   - 结论：路径已失效，属于直接可见的过时引用。

## 3.2 阶段性过时/临时文档（应清理）

以下临时文档已完成清理（2026-05-15）：
- `Plans/temp-3d-ui-geckolib-migration-split-plan.md`
- `Plans/temp-guidebookrenderer-legacy-reference.md`
- `Plans/temp-virtual-space-interaction-plan.md`
- `Plans/temp-stage2-execution-plan.md`
- `Plans/temp-stage0-baseline-lock.md`

当前仅保留正式状态文档，避免多人并行时版本分叉。

---

## 4. 整改计划（按优先级执行）

## P0（本周内）

- [x] **统一状态事实**：更新 `Arch/.../README.md`、`3D-UI-FRAMEWORK-STATUS.md` 顶部状态字段（从“待实现/准备验收”改为“实施中 + 阶段进度”）。
- [x] **修正关键不一致**：明确土锅槽位规格（4/8），同步 `pot-gui-spec.md`。
- [x] **清理过时引用**：修复 `decisions/INDEX.md` 中 `Ashihara_1.21` 旧路径。
- [x] **桥接收口计划冻结**：给 `GuideBookRenderer.renderCompat` 增加明确移除里程碑（例如：Stage 2.2 完成后删除）。

### P0 执行记录（2026-05-15）

- 已更新 `Arch/05-Technical-Foundation/3d-ui-framework/README.md` 状态与后续步骤。
- 已更新 `Arch/05-Technical-Foundation/3d-ui-framework/3D-UI-FRAMEWORK-STATUS.md` 为实施期口径。
- 已更新 `Arch/05-Technical-Foundation/3d-ui-framework/pot-gui-spec.md` 的槽位数量描述为当前 4 槽实现。
- 已更新 `Arch/05-Technical-Foundation/3d-ui-framework/decisions/INDEX.md` 的旧工作区路径引用。
- 已在 `src/main/java/kogasastudio/ashihara/client/render/geo/GuideBookRenderer.java` 标注兼容入口移除里程碑（Stage 2.2）。

## P1（1~2 周）

- [x] **接入或移除未用交互抽象**：二选一
  - A：把 `HitPolicy`/`HitResult` 接入 `Screen3D` 命中主链；
  - B：若暂不启用，迁移到 `experimental` 并在文档明确未启用。
- [x] **统一射线入口**：`GuideBookScreen` 改为复用 `Screen3D.createMouseRay(...)`。
- [x] **生命周期补全**：补齐 tracer / bone modifier 的 detach 清理路径。
- [x] **删除注释化死代码**：清理主要组件中的旧渲染注释块，保留必要历史可转移到 Plans 历史记录。

### P1 执行记录（2026-05-15，本轮）

- 已在 `AbstractComponent` / `Screen3D` 接入 `HitPolicy`、`HitResult` 的命中主链。
- 已将 `GuideBookScreen.mouseClicked` 命中射线改为复用 `Screen3D.createMouseRay(...)`（并保持当前页面 OBB 的 GUI 空间判定）。
- 已在 `ModelComponent.dispose()` 增加 tracer 与 bone modifier 清理，并在 `Screen3D` 的 `removeComponent/clearComponents/onClose` 中触发释放。
- 已清理 `Panel`、`PotLidComponent`、`SelectionFrameComponent`、`ItemSlotComponent` 的注释化旧渲染路径，并将 `SelectionFrame` 的状态收敛逻辑迁移到 `tick()`。

## P2（中期）

- [x] **调试模块化**：重构 `Gui3dDebugOverlay` 的投影、绘制、命中采集。
- [x] **渲染状态抽象统一**：收敛 `CommonGeoRenderState` / `GUI3DComponentRenderState` 的重复用途。
- [x] **文档体系升级**：把 `roadmap.md` 从“3天计划”改为“阶段里程碑 + 完成定义（DoD）”。

### P2 执行记录（2026-05-15，本轮）

- 新增 `src/main/java/kogasastudio/ashihara/client/gui3d/debug/Gui3dDebugCollector.java`，负责命中与调试文本采集。
- 新增 `src/main/java/kogasastudio/ashihara/client/gui3d/debug/Gui3dDebugProjector.java`，负责 OBB 到 GUI 角点投影。
- 新增 `src/main/java/kogasastudio/ashihara/client/gui3d/debug/Gui3dDebugDrawer.java`，负责线框与顶点绘制。
- `src/main/java/kogasastudio/ashihara/client/gui3d/Gui3dDebugOverlay.java` 降为编排层，只负责调用上述模块并输出统计文本。
- `src/main/java/kogasastudio/ashihara/client/render/state/GUI3DComponentRenderState.java` 新增 `newDataMap()` 工厂，`CommonGeoRenderState` 与 `GuideBookRenderer` 已复用。
- `Arch/05-Technical-Foundation/3d-ui-framework/roadmap.md` 已升级为里程碑 + DoD 主视图，并保留 Day1~Day3 历史内容作为参考。
- `Arch/05-Technical-Foundation/3d-ui-framework/3d-ui-quickref.md` 已补充 DebugOverlay 分层结构图、入口路径与维护约定（P2.1 对齐完成）。

---

## 5. 建议的"一次性交付物"

为避免继续堆临时文档，建议后续只维护一份正式状态板：

- `Plans/3d-ui-current-state-and-rectification-plan.md`（本文件，正式状态板，替代多个 `temp-*`）
  - 模块维度：GuideBook / Pot / Tracer / PiP / Docs
  - 每项包含：当前状态、阻塞、下一步、回滚点、预计完成时间

### 文档更新记录（2026-05-15 本轮末）

以下文档已同步至整改后的事实状态：

| 文件 | 更新内容 |
|---|---|
| `Arch/05-Technical-Foundation/3d-ui-framework/README.md` | 后续步骤检查清单打勾，保留待完成项（GuideBook 桥接收口） |
| `Arch/05-Technical-Foundation/3d-ui-framework/3D-UI-FRAMEWORK-STATUS.md` | 当前执行重点检查清单全部打勾，补充 P1/P2 已完成项，更新"下一步" |
| `Arch/05-Technical-Foundation/3d-ui-framework/roadmap.md` | DoD 检查清单全部打勾，M2/M3 里程碑状态更新（M3 ✅，M2 后半待完成） |

> 注：本次按你的要求，仅输出本文件，不新增其他文件。

---

## 6. 当前结论（简版）

- 3D UI 不是“待实现”，而是“**主链已建，正处于迁移收口期**”。
- 主要问题不是“没有功能”，而是“**双路径并存 + 文档状态滞后 + 临时计划分散**”。
- 最优策略：先做 **P0 文档与事实对齐**，再做 **P1 代码收口**，最后推进 **P2 结构优化**。






