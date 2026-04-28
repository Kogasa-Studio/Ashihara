# Day 2 实现总结 - 立体选框和骨骼绑定

> 日期：2026-04-18
> 状态：🔨 进行中
> 相关决策：[DECISION-003-3d-selection-frame.md](./decisions/DECISION-003-3d-selection-frame.md)

## 本次更新内容

### 1. 修复锅子渲染方向 ✅

**问题**：锅子在GUI中倒着显示

**修复**：在 `PotModelComponent.renderSelf()` 中的Y轴缩放取反
```java
// 原来
poseStack.scale(this.modelScale, this.modelScale, this.modelScale);

// 修改为
poseStack.scale(this.modelScale, -this.modelScale, this.modelScale);
```

### 2. 新建 SelectionFrameComponent ✅

**文件**：`src/main/java/kogasastudio/ashihara/client/gui3d/components/SelectionFrameComponent.java`

**功能**：
- 使用 `cubic_selection_frame.geo.json` 模型的12条边骨骼
- 支持 QUAD EASING IN 动画平滑变形
- 自动同步目标组件的位置、旋转、尺寸
- 通过 `BoneTracer` 实时跟踪目标骨骼的变换矩阵

**核心API**：
```java
// 绑定目标并显示
selectionFrame.bindToAndShow(component, width, height, depth);

// 或绑定到骨骼矩阵
selectionFrame.bindToMatrixAndShow(component, matrix, width, height, depth);

// 隐藏
selectionFrame.hide();
```

### 3. 修改 PotLidComponent ✅

**变更**：
- 添加 `SelectionFrameComponent selectionFrame` 成员变量
- 在 `init()` 中初始化并添加为子组件
- 在 `setHoveredChain()` 时显示框
- 在 `clearHoverState()` 时隐藏框
- 锅盖打开后自动隐藏框

### 4. 修改 ItemSlotComponent ✅

**新增字段**：
- `SelectionFrameComponent selectionFrame`
- `PotModelComponent potModelComponent`
- `BoneTracer slotBoneTracer`（追踪 `item_slot_0..3` 骨骼）

**新增方法**：
```java
public ItemSlotComponent setPotModelComponent(PotModelComponent potModel) {
    this.potModelComponent = potModel;
    // 注册骨骼追踪器
    return this;
}
```

**交互逻辑**：
- 只有当**锅盖已打开**（`lidRemoved == true`）时，才显示槽位的选框
- 悬停时：获取骨骼矩阵并显示选框
- 鼠标移开时：隐藏选框

### 5. 修改 PotScreen ✅

**变更**：在初始化 `ItemSlotComponent` 时，调用 `.setPotModelComponent(this.potModelComponent)` 建立关联

### 6. 修改 PotModelComponent ✅

**新增方法**：
```java
public SimpleInternalControlGeoModel getModel() {
    return this.model;
}
```

用于其他组件访问底层模型和注册骨骼追踪器。

## 文档更新

### 1. 新建决策文档
- **文件**：`Arch/05-Technical-Foundation/3d-ui-framework/decisions/DECISION-003-3d-selection-frame.md`
- **内容**：详细的设计决策、权衡分析、风险评估

### 2. 更新 pot-gui-spec.md
- 补充**阶段2：空锅交互**的完整代码示例
- 详细描述立体选框的动画机制
- 说明骨骼绑定的工作流程

### 3. 更新 3d-ui-quickref.md
- 新增**模板7：立体选框组件**
- 完整的 SelectionFrameComponent 使用示例

## 编译状态

✅ **BUILD SUCCESSFUL**

## 待验证（运行时）

当游戏启动后，需要检查：

- [ ] 土锅模型现在朝向正确（不倒）
- [ ] 鼠标悬停锅盖时，出现3D立体框
- [ ] 点击锅盖移除后，框消失
- [ ] 锅盖打开后，鼠标悬停槽位时出现3D框
- [ ] 框的旋转与模型旋转相同（改变视角确认）
- [ ] 框的动画平滑（QUAD EASING IN）
- [ ] 无崩溃/错误日志

## 性能考虑

- SelectionFrameComponent 使用骨骼追踪，避免频繁计算变换矩阵
- 只在悬停时激活动画，不显示时完全隐藏
- 复用 SimpleInternalControlGeoModel 的渲染管线，无额外GPU开销

## 下一步（Day 3）

1. 调试并优化渲染效果
2. 若有问题，调整骨骼追踪或矩阵变换
3. 完整功能测试和文档补充
4. 性能基准测试

---

**所有改动已符合 DOCUMENTATION-RULES.md 规范**


