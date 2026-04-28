# 土锅 GUI 实现规范

> 标签：[土锅功能、完整实现示例、玩法UI]
> 状态：🔨 Day 1 进行中 → Day 2 开发
> 相关文档：[3d-ui-design.md](./3d-ui-design.md)、[3d-ui-quickref.md](./3d-ui-quickref.md)、[decisions/DECISION-003-3d-selection-frame.md](./decisions/DECISION-003-3d-selection-frame.md)
> 最后更新：2026-04-18（更新：立体选框、物品骨骼绑定、锅子渲染方向修复、F9 调试覆盖层、GeoBone/GeoCube 自动 OBB、SelectionFrame 的 scale_sim 重构）

## 概述

土锅GUI是3D UI框架的首个完整应用，展示如何将架构设计转化为实际代码。

**核心功能**：
- GeckoLib骨骼动画（锅盖打开/关闭）
- 流体渲染（液体高度、颜色）
- 物品槽位与交互（8个进料口）
- 配方匹配系统
- 进度条和特效系统

## 用户交互流程

### 阶段1：打开土锅GUI

操作：玩家右键土锅方块

效果：
1. Screen3D初始化
2. 土锅模型动画飞入（通过InternalAnimationBuilder）
3. 界面完全加载

代码入口：
```java
public class PotScreen extends Screen3D {
    @Override
    protected void init() {
        super.init();
        // 初始化组件树
        this.potPanel = new PotPanel(this);
        this.addComponent(potPanel);
        
        // 触发打开动画
        this.playOpeningAnimation();
    }
}
```

### 阶段2：空锅交互

玩家看到：
- 完整的土锅3D模型
- 锅盖可见
- 4个物品槽位空置

交互：
- **鼠标悬停锅盖**：显示**立体选中框**（从 `cubic_selection_frame.geo.json` 的边骨骼生成，QUAD EASING IN 动画平滑变形）
- **点击锅盖**：触发移除动画（锅盖上升旋转）
- **锅盖打开后**：鼠标悬停槽位区域 → 槽位上显示**立体选框**（绑定到 `item_slot_0..3` 骨骼）

代码：
```java
// 锅盖组件（包含立体选框）
public class PotLidComponent extends AbstractComponent {
    private PotModelComponent modelComponent;
    private SelectionFrameComponent frameComponent;  // ← 新增：立体选框
    private boolean lidRemoved = false;
    
    @Override
    public void init() {
        super.init();
        // 初始化立体选框组件
        frameComponent = new SelectionFrameComponent(
            "geo/assistance/cubic_selection_frame.geo.json"
        );
        this.addChild(frameComponent);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {  // 左键
            this.playRemovalAnimation();
            this.lidRemoved = true;
            frameComponent.setVisible(false);  // 锅盖打开后隐藏锅盖框
            return true;
        }
        return false;
    }
    
    @Override
    protected void updateHoverState(double mouseX, double mouseY) {
        // 只在锅盖还在时显示框
        if (!lidRemoved && containsPoint(mouseX, mouseY)) {
            frameComponent.showWithAnimation();
        } else {
            frameComponent.hide();
        }
    }
    
    private void playRemovalAnimation() {
        // 锅盖向上移动 + 旋转
        modelComponent.getModel().triggerInternal(null, 1,
            new InternalAnimationBuilder("lid_removal", LoopType.HOLD_ON_LAST_FRAME)
                .startBone("lid")
                    .lerpY(VarType.POSITION, currentY, currentY + 8, 10, EasingType.EASE_OUT_CUBIC)
                    .lerpZ(VarType.ROTATION, 0, -20, 10, EasingType.EASE_OUT_CUBIC)
                .endBone()
                .build()
        );
    }
}

// 立体选框组件（重构后：scale_sim 驱动）
public class SelectionFrameComponent extends AbstractComponent {
    private SelectionFrameModel frameModel;
    private Matrix4f targetMatrix = new Matrix4f();
    private Vector3f minBounds = new Vector3f();
    private Vector3f maxBounds = new Vector3f();

    public void showForObb(String key, OBB box) {
        this.visible = true;
        this.targetMatrix.set(box.pose());
        this.minBounds.set(box.minXYZ());
        this.maxBounds.set(box.maxXYZ());

        // 由 GeckoLib 动画机统一驱动：scale_sim + 12条边同步动画
        this.frameModel.triggerResize(this.minBounds, this.maxBounds, 8.0d, EasingType.EASE_IN_QUAD);
    }

    @Override
    protected void renderSelf(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;
        graphics.pose().pushPose();
        graphics.pose().mulPose(this.targetMatrix);
        graphics.pose().translate(-0.5f, -0.51f, -0.5f);
        this.frameModel.render(graphics.pose(), graphics.bufferSource(), 15728880, 0);
        graphics.pose().popPose();
    }
}
```

重构规则：

| 项目 | 要求 |
|---|---|
| 动画入口 | 必须使用 `triggerInternal`，禁止 tick 中手动推进骨骼状态 |
| 驱动骨骼 | `scale_sim` 作为包围盒主驱动，12 条边骨骼由模型层统一同步 |
| 组件职责 | `SelectionFrameComponent` 只负责目标矩阵/边界变化检测与触发 |
| 模型职责 | `SelectionFrameModel` 负责边骨骼吸附公式与内部动画构造 |

### 阶段3：添加材料

玩家操作：
- 从背包拖拽物品到槽位
- 或点击槽位选择物品

显示：
- 流体面片（高度反映体积）
- 8个物品显示位置（基于GeckoLib骨骼）
- 物品数量标签

调试：
- 按 `F9` 打开 OBB / 射线调试覆盖层
- 左上角显示当前鼠标射线命中的组件名和 `t` 值
- 线框颜色：绿色=被命中，黄色=当前 hovered，灰色=仅显示 OBB

### GeoBone 绑定与去硬编码规则（新增）

为避免 `PotLidComponent` / `ItemSlotComponent` 使用硬编码数值，碰撞盒必须从 GeckoLib 实际模型数据生成：

| 规则 | 要求 |
|---|---|
| 数据来源 | 从 `GeoBone.getCubes()` 读取 `GeoCube` 顶点与变换，不手写 `min/max` 常量 |
| 绑定方式 | 组件通过 bone id 绑定（如 `lid`、`item_slot_0`） |
| 多 OBB | 一个组件允许对应多个 OBB（同 bone 多 cube 或多 bone 聚合） |
| 追踪时机 | 在 GeckoLib `renderRecursively` 流程中同步骨骼矩阵与 cube OBB |
| 退化策略 | 若追踪数据尚未准备好，允许临时回退到 2D OBB，避免交互失效 |

实现接口建议：

```java
// 完整示例
public class PotModelComponent extends AbstractComponent {
    public BoneTracer bindBone(String boneName);
    public List<OBB> getBoneCollisionBoxes(String boneName);
    public List<OBB> getBoneCollisionBoxes(Collection<String> boneNames);
}

public class ItemSlotComponent extends AbstractComponent {
    public ItemSlotComponent setBoundBones(List<String> boneNames);

    @Override
    public List<OBB> getCollisionBoxes() {
        return potModelComponent.getBoneCollisionBoxes(boundBones);
    }
}
```

代码：
```java
// 物品槽位
public class ItemSlotComponent extends AbstractComponent {
    private ItemStack item = ItemStack.EMPTY;
    private int slotIndex = 0;
    
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        // 渲染槽位背景
        fill(graphics, x, y, x+18, y+18, 0xFF8B8B8B);
        
        // 渲染物品
        if (!item.isEmpty()) {
            graphics.renderItem(item, (int)x, (int)y);
            // 渲染数量
            graphics.drawString(font, String.valueOf(item.getCount()),
                (int)(x+10), (int)(y+10), 0xFFFFFF);
        }
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            // 触发物品选择
            EventBus.publish(new ItemSlotClickedEvent(slotIndex));
            return true;
        }
        return false;
    }
}

// 流体显示
public class FluidDisplayComponent extends AbstractComponent {
    private FluidStack fluid = FluidStack.EMPTY;
    private int maxCapacity = 1000;
    
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        float fillRatio = (float)fluid.getAmount() / maxCapacity;
        float filledHeight = height * fillRatio;
        
        // 渲染流体矩形
        int color = getFluidColor(fluid.getFluid());
        fill(graphics, x, y + height - (int)filledHeight, 
             x + width, y + height, color | 0xFF000000);
    }
}
```

### 阶段4：配方匹配

系统自动检查：
- 输入物品是否满足配方
- 输入流体量是否正确
- 热源等级是否满足

若匹配失败，显示错误提示：
```java
// 配方验证
public class RecipeValidator {
    public ValidationResult validate(PotRecipe recipe, PotBlockEntity entity) {
        // 检查物品
        for (ItemStack required : recipe.getInputItems()) {
            if (!hasItem(entity, required)) {
                return ValidationResult.MISSING_ITEM(required);
            }
        }
        
        // 检查流体
        if (!matchesFluid(entity.fluidTank, recipe.getInputFluid())) {
            return ValidationResult.FLUID_MISMATCH(recipe.getInputFluid());
        }
        
        // 检查热源
        if (!hasHeatSource(entity, recipe.getRequiredHeat())) {
            return ValidationResult.INSUFFICIENT_HEAT();
        }
        
        return ValidationResult.VALID();
    }
}
```

若匹配成功，显示配方面板：
```java
// 配方显示面板
public class RecipeDisplayPanel extends Panel {
    private PotRecipe recipe;
    private float alpha = 0;  // 淡入动画
    
    @Override
    public void tick() {
        if (recipe != null && alpha < 1.0f) {
            alpha = Math.min(1.0f, alpha + 0.05f);  // 淡入
        }
    }
    
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        // 根据alpha调整透明度
        graphics.pose().pushPose();
        graphics.setColor(1, 1, 1, alpha);
        
        // 显示输入/输出信息
        graphics.drawString(font, "配方信息", x+10, y+10, 0xFFFFFF);
        graphics.drawString(font, "烹饪时间: " + recipe.getCookTime(), 
            x+10, y+30, 0xFFFFFF);
        
        graphics.pose().popPose();
    }
}
```

### 阶段5：烹饪开始

触发条件：配方完全匹配

效果：
1. 播放锅子工作动画
2. 显示土灶模型
3. 渲染火焰粒子
4. 显示进度条

代码：
```java
public class PotScreen extends Screen3D {
    public void startCooking(PotRecipe recipe) {
        // 触发锅子工作动画
        potModelComponent.playAnimation("cooking_loop");
        
        // 显示土灶
        cookstoveComponent.setVisible(true);
        
        // 启动火焰粒子
        flameEmitter.start();
        
        // 显示进度条
        progressBar.startAnimation(recipe.getCookTime());
        
        // 发布事件给服务端
        EventBus.publish(new CookingStartedEvent(recipe.getId()));
    }
}
```

### 阶段6：烹饪过程

显示：
- 物品翻腾动画（如果锅盖打开）
- 气泡粒子（流体内）
- 烟雾粒子（往上）
- 进度条更新

代码：
```java
// 粒子效果容器
public class CookingEffectContainer extends AbstractComponent {
    private ParticleEmitter bubbleEmitter;
    private ParticleEmitter smokeEmitter;
    private boolean lidRemoved;
    
    @Override
    public void tick() {
        if (lidRemoved) {
            // 物品翻腾动画
            itemsComponent.playFlipAnimation();
        }
        
        // 气泡粒子
        if (bubbleEmitter.isActive()) {
            bubbleEmitter.tick();
            bubbleEmitter.emit(3, 5);  // 每帧3-5个气泡
        }
        
        // 烟雾粒子
        if (smokeEmitter.isActive()) {
            smokeEmitter.emit(2, 3);
        }
    }
}

// 进度条（基于GeckoLib骨骼）
public class ProgressBarComponent extends AbstractComponent {
    private float progress = 0;
    private float maxProgress;
    
    @Override
    public void tick() {
        if (progress < maxProgress) {
            progress += 1;
            
            // 动态调整骨骼缩放来表现进度
            float scale = progress / maxProgress;
            geoModel.triggerInternal(null, 1,
                new InternalAnimationBuilder("progress_update", LoopType.HOLD_ON_LAST_FRAME)
                    .startBone("progress_bar")
                        .lerpX(VarType.SCALE, 0, scale, 1, EasingType.LINEAR)
                    .endBone()
                    .build()
            );
        }
    }
}
```

### 阶段7：烹饪完成

效果：
1. 进度条完成
2. 完成特效（星星爆出）
3. 音效播放
4. 物品/流体更新

代码：
```java
private void onCookingComplete(PotRecipe recipe) {
    // 完成特效
    playCompletionEffect();
    
    // 音效
    this.minecraft.getSoundManager().play(
        SimpleSoundInstance.forUI(SoundEvents.ITEM_PICKUP, 1.0f));
    
    // 更新物品和流体（根据配方输出）
    for (ItemStack output : recipe.getOutputItems()) {
        entity.addItem(output);
    }
    entity.setFluid(recipe.getOutputFluid());
    
    // 返回初始阶段
    this.resetToCooking();
}

private void playCompletionEffect() {
    // 星星粒子
    ParticleEmitter starEmitter = new ParticleEmitter(
        new Vector3f(0, 0, 0),
        Particles.STAR,
        100  // 总数
    );
    starEmitter.setVelocityRange(
        new Vector3f(-1, 0.5f, -1),
        new Vector3f(1, 2, 1)
    );
    starEmitter.emit();
}
```

## 组件树结构

```
PotScreen extends Screen3D
  ├─ PotPanel extends Panel
  │   ├─ PotModelComponent (3D模型)
  │   │   └─ 骨骼：lid, pot_body, 8个item_slot
  │   │
  │   ├─ FluidDisplayComponent (液体面片)
  │   │   └─ 高度 = fluid.getAmount() / maxCapacity * height
  │   │
  │   ├─ ItemSlotContainer extends Panel
  │   │   ├─ ItemSlotComponent[0] 
  │   │   ├─ ItemSlotComponent[1]
  │   │   ├─ ItemSlotComponent[2]
  │   │   ├─ ItemSlotComponent[3]
  │   │   ├─ ItemSlotComponent[4]
  │   │   ├─ ItemSlotComponent[5]
  │   │   ├─ ItemSlotComponent[6]
  │   │   └─ ItemSlotComponent[7]
  │   │
  │   ├─ RecipeDisplayPanel (配方信息，淡入/淡出)
  │   │   ├─ 输入物品列表
  │   │   ├─ 输入流体显示
  │   │   ├─ 输出物品列表
  │   │   └─ 烹饪时间
  │   │
  │   ├─ CookstoveComponent (土灶模型，烹饪时显示)
  │   │   └─ 火焰粒子发射器
  │   │
  │   ├─ ProgressBarComponent (进度条，烹饪时显示)
  │   │   └─ 基于GeckoLib骨骼的动态缩放
  │   │
  │   └─ CookingEffectContainer (特效容器，烹饪时活跃)
  │       ├─ BubbleParticleEmitter
  │       ├─ SmokeParticleEmitter
  │       ├─ StarParticleEmitter (完成时)
  │       └─ ItemFlipAnimation (开盖时)
```

## 核心接口定义

### IPotRecipe - 配方接口

```java
public interface IPotRecipe {
    List<ItemStack> getInputItems();
    FluidStack getInputFluid();
    List<ItemStack> getOutputItems();
    FluidStack getOutputFluid();
    int getCookTime();
    int getRequiredHeatLevel();
    ResourceLocation getId();
}
```

### ICustomUIRenderable - 自定义物品渲染

物品可通过此接口自定义在GUI中的显示方式：

```java
public interface ICustomUIRenderable {
    void renderInUI(GuiGraphics graphics, int x, int y);
}
```

### IHeatSource - 热源接口

检测土灶下方是否有足够的热源：

```java
public interface IHeatSource {
    int getHeatLevel();  // 0=无热源，1-10=不同热度
}
```

## ItemStackHandler兼容性

土锅使用NeoForge的`ItemStackHandler`作为内部库存：

```java
public class PotBlockEntity extends BlockEntity {
    private ItemStackHandler itemHandler = new ItemStackHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            // 当物品改变时更新UI
            EventBus.publish(new SlotContentsChangedEvent(slot));
        }
    };
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
```

UI交互与ItemStackHandler同步：

```java
// 添加物品到槽位
public void addItemToSlot(int slotIndex, ItemStack item) {
    ItemStack result = itemHandler.insertItem(slotIndex, item, false);
    // result为无法插入的部分
}

// 移除物品
public void removeItemFromSlot(int slotIndex, int count) {
    itemHandler.extractItem(slotIndex, count, false);
}
```

## 网络同步

客户端操作通过事件发送到服务端，服务端验证后广播：

```java
// 客户端发送事件
public void onClientItemAdded(ItemStack item, int slotIndex) {
    Networking.sendToServer(new PotAddItemPacket(entity.getBlockPos(), item, slotIndex));
}

// 服务端处理
public class PotAddItemPacket {
    public void handle(ServerPlayer player) {
        PotBlockEntity entity = getEntity(player.level, pos);
        entity.itemHandler.insertItem(slotIndex, item, false);
        
        // 广播给所有观察者
        Networking.broadcastToClients(
            new PotItemAddedPacket(entity.getBlockPos(), item, slotIndex)
        );
    }
}

// 客户端接收更新
public class PotItemAddedPacket {
    public void handle(ClientPlayer player) {
        // 更新UI显示
        EventBus.publish(new SlotContentsChangedEvent(slotIndex));
    }
}
```

## 测试检查清单

实现完成后的验证：

- [ ] 打开GUI时流畅无卡顿
- [ ] 锅盖打开/关闭动画正常
- [ ] 物品可以添加/移除
- [ ] 流体高度随容量变化
- [ ] 配方匹配逻辑正确
- [ ] 烹饪动画播放
- [ ] 进度条更新准确
- [ ] 粒子特效可见
- [ ] 完成提示正常
- [ ] 客户端/服务端同步正常
- [ ] 关闭GUI后无内存泄漏
- [ ] 打开其他GUI时无冲突

---

**相关文档**：
- 架构设计：[3d-ui-design.md](./3d-ui-design.md)
- 快速参考：[3d-ui-quickref.md](./3d-ui-quickref.md)
- 实现路线图：[roadmap.md](./roadmap.md)

