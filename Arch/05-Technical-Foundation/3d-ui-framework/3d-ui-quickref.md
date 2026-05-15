# 3D UI 框架快速参考

> 标签：[快速参考、代码模板、API查询]
> 用途：AI实现者的快速查询手册
> 相关文档：[3d-ui-design.md](./3d-ui-design.md)、[pot-gui-spec.md](./pot-gui-spec.md)
> 最后更新：2026-05-15（更新：HitPolicy 主链接入、DebugOverlay 分层、里程碑文档同步）

## 核心API速查

### Screen3D - UI容器

```java
// 基本结构
public abstract class Screen3D extends Screen {
    protected List<AbstractComponent> components = new ArrayList<>();
    
    // 坐标转换
    Vector3f screenToGuiSpace(double screenX, double screenY, float guiZ);
    Vector2f guiSpaceToScreen(float guiX, float guiY, float guiZ);
    
    // 事件处理
    boolean handleMouseClick(double screenX, double screenY, int button);
    boolean handleMouseScroll(double screenX, double screenY, double delta);
    
    // 组件管理
    void addComponent(AbstractComponent component);
    void removeComponent(AbstractComponent component);
}
```

**职责**：
- 管理组件树生命周期
- 屏幕坐标↔UI空间坐标转换
- 事件分发和路由

### AbstractComponent - 组件基类

```java
public abstract class AbstractComponent {
    protected List<AbstractComponent> children = new ArrayList<>();
    
    // 生命周期
    void init();
    void tick();
    void render(GuiGraphics graphics, float partialTick);
    
    // 事件处理
    boolean mouseClicked(double x, double y, int button);
    boolean mouseScrolled(double x, double y, double delta);
    void mouseMoved(double x, double y);
    boolean mouseReleased(double x, double y, int button);
    
    // 交互数据
    OBB getCollisionBox();
    List<OBB> getCollisionBoxes();
    float rayHitDistance(Ray ray);
    boolean isHovered();
    boolean isDragging();
    
    // 组件树
    void addChild(AbstractComponent child);
    void removeChild(AbstractComponent child);
}
```

**职责**：
- 渲染自身内容
- 处理鼠标事件
- 管理子组件

### Ray - 射线检测

```java
// 射线定义
public class Ray {
    Vector3f origin;      // 起点
    Vector3f direction;   // 方向（单位向量）
    
    static Ray screenToRay(double screenX, double screenY, PoseStack poseStack);
}
```

### OBB - 有向包围盒

```java
// OBB定义
public class OBB {
    Vector3f center;        // 中心
    Vector3f[] axes;        // 3个正交轴（已归一化）
    float[] halfExtents;    // 3个轴方向的半长度
    
    static OBB fromModel(BakedModel model, Matrix4f transform);
}

// SAT碰撞检测
public class ObbInterSector {
    static boolean rayIntersectsOBB(Ray ray, OBB obb);
    static boolean obbIntersectsOBB(OBB obb1, OBB obb2);
}
```

### BoneTracer - 骨骼矩阵同步

```java
// 实时同步GeckoLib骨骼矩阵
public class BoneTracer {
    boolean testBone(GeoBone bone);
    void syncFromBone(GeoBone bone, Matrix4f matrix);
    Matrix4f matrix();
    List<OBB> collisionBoxes();
}
```

### GeoCubeObbExtractor - 自动提取 cube OBB

```java
public final class GeoCubeObbExtractor {
    // 将 bone pose + cube 几何转换为单个 OBB
    static OBB extract(Matrix4f bonePose, GeoCube cube);
}
```

## 代码模板库

### 模板1：创建简单3D UI Screen

```java
public class MyScreen extends Screen3D {
    public MyScreen() {
        super(Component.literal("My 3D Screen"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 添加一个简单的面板
        MyPanel panel = new MyPanel(0, 0, 256, 256);
        this.addComponent(panel);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.fillGradient(graphics, 0, 0, this.width, this.height, 0xFF000000, 0xFF000000);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
```

### 模板2：创建自定义组件

```java
public class CustomComponent extends AbstractComponent {
    private float x, y, z;
    private float width, height, depth;
    
    public CustomComponent(float x, float y, float z, float w, float h, float d) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = w;
        this.height = h;
        this.depth = d;
    }
    
    @Override
    public void tick() {
        // 更新逻辑
    }
    
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        // 渲染逻辑
    }
    
    @Override
    public OBB getCollisionBox() {
        // 返回包围盒用于交互检测
        Vector3f center = new Vector3f(x + width/2, y + height/2, z + depth/2);
        float[] halfExtents = {width/2, height/2, depth/2};
        return new OBB(center, new Vector3f[]{
            new Vector3f(1, 0, 0),
            new Vector3f(0, 1, 0),
            new Vector3f(0, 0, 1)
        }, halfExtents);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {  // 左键
            // 处理点击
            return true;
        }
        return false;
    }
}
```

### 模板3：GeckoLib模型渲染

```java
public class ModelComponent extends AbstractComponent {
    private InternalControlGeoModel<Pot> geoModel;
    private Pot entityData;
    
    public ModelComponent(float x, float y, float z) {
        this.geoModel = new InternalControlGeoModel<>(new PotModel());
        this.entityData = new Pot(null);
    }
    
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        
        poseStack.translate(x, y, z);
        poseStack.scale(scale, scale, scale);
        
        this.geoModel.render(graphics, entityData, 0, 0, partialTick);
        
        poseStack.popPose();
    }
    
    // 触发动画
    public void playAnimation(String animationName) {
        this.geoModel.triggerInternal(null, 1,
            new InternalAnimationBuilder(animationName, LoopType.HOLD_ON_LAST_FRAME)
                .startBone("bone_name")
                    .lerpX(VarType.POSITION, 20, 0, 5, EasingType.EASE_OUT_CUBIC)
                .endBone()
                .build()
        );
    }
}
```

### 模板4：流体显示组件

```java
public class FluidDisplayComponent extends AbstractComponent {
    private FluidStack fluid;
    private float maxCapacity = 1000;
    private float x, y, z;
    private float width, height;
    
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        float fillLevel = fluid.getAmount() / maxCapacity;
        float filledHeight = height * fillLevel;
        
        // 渲染流体矩形
        fill(graphics, (int)(x), (int)(y + height - filledHeight), 
             (int)(x + width), (int)(y + height), 
             getFluidColor(fluid));
    }
}
```

### 模板5：物品槽位组件

```java
public class ItemSlotComponent extends AbstractComponent {
    private ItemStack item = ItemStack.EMPTY;
    private float x, y, z;
    private static final float SLOT_SIZE = 18;
    
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        // 渲染槽位背景
        fill(graphics, (int)x, (int)y, (int)(x+SLOT_SIZE), (int)(y+SLOT_SIZE), 0xFF8B8B8B);
        
        // 渲染物品
        if (!item.isEmpty()) {
            graphics.renderItem(item, (int)x, (int)y);
        }
    }
    
    @Override
    public OBB getCollisionBox() {
        Vector3f center = new Vector3f(x + SLOT_SIZE/2, y + SLOT_SIZE/2, z);
        float[] halfExtents = {SLOT_SIZE/2, SLOT_SIZE/2, 1};
        return new OBB(center, getStandardAxes(), halfExtents);
    }
    
    @Override
    public boolean mouseClicked(double clickX, double clickY, int button) {
        if (button == 0) {
            // 处理物品交互
            return true;
        }
        return false;
    }
}
```

### 模板6：事件系统集成

```java
// 定义事件
public class ItemAddedToSlotEvent {
    public final ItemStack item;
    public final int slotIndex;
    
    public ItemAddedToSlotEvent(ItemStack item, int slotIndex) {
        this.item = item;
        this.slotIndex = slotIndex;
    }
}

// 发布事件
EventBus.publish(new ItemAddedToSlotEvent(item, 0));

// 订阅事件
EventBus.subscribe(ItemAddedToSlotEvent.class, event -> {
    System.out.println("Item added: " + event.item);
});
```

### 模板7：立体选框组件（3D Selection Frame）

```java
public class SelectionFrameComponent extends AbstractComponent {
    private static final double DURATION = 8.0d;
    private final SelectionFrameModel frameModel;
    private final Matrix4f targetMatrix = new Matrix4f();
    private final Vector3f minBounds = new Vector3f();
    private final Vector3f maxBounds = new Vector3f();

    public SelectionFrameComponent(String modelPath, String texturePath) {
        this.frameModel = new SelectionFrameModel(modelPath, texturePath, Minecraft.getInstance().player);
        this.visible = false;
        this.enabled = false;
    }

    public void showForObb(String key, OBB obb) {
        this.visible = true;
        this.targetMatrix.set(obb.pose());
        this.minBounds.set(obb.minXYZ());
        this.maxBounds.set(obb.maxXYZ());
        this.frameModel.triggerResize(this.minBounds, this.maxBounds, DURATION, EasingType.EASE_IN_QUAD);
    }

    public void hide() {
        this.visible = false;
    }

    @Override
    protected void renderSelf(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.mulPose(this.targetMatrix);
        pose.translate(-0.5f, -0.51f, -0.5f);
        this.frameModel.render(pose, guiGraphics.bufferSource(), 15728880, 0);
        pose.popPose();
    }
}
```

## 常用工具类与方法

### 矩阵变换

```java
// PoseStack操作
PoseStack poseStack = graphics.pose();

// 平移
poseStack.translate(x, y, z);

// 缩放
poseStack.scale(sx, sy, sz);

// 旋转（四元数）
Quaternionf q = new Quaternionf().rotationXYZ(pitch, yaw, roll);
poseStack.mulPose(q);

// 保存和恢复
poseStack.pushPose();
// ... 修改
poseStack.popPose();
```

### Easing函数速查

| 函数 | 特征 | 使用场景 |
|---|---|---|
| LINEAR | 线性 | 均匀运动 |
| EASE_OUT_CUBIC | 快速开始，逐渐减速 | 大多数UI动画 |
| EASE_IN_CUBIC | 缓慢开始，逐渐加速 | 需要缓和的开始 |
| EASE_IN_OUT_CUBIC | 两端缓和 | 来回运动 |
| EASE_OUT_ELASTIC | 弹性反弹 | 碰撞效果 |

### RenderType速查

| 类型 | 用途 |
|---|---|
| `3D_GUI_SOLID` | 标准3D不透明物体 |
| `3D_GUI_TRANSLUCENT` | 半透明（如玻璃） |
| `3D_GUI_ADDITIVE` | 加法混合（发光、特效） |
| `2D_GUI_OVERLAY` | 屏幕空间2D叠加 |
| `GLOW_OUTLINE` | 发光选中框 |

## FAQ与常见陷阱

### F9 调试覆盖层

| 项目 | 说明 |
|---|---|
| 开关 | `F9` |
| 射线 | `origin=(mouseX, mouseY, -2000)`，`direction=(0,0,1)` |
| 线框颜色 | 绿色=被射线命中，黄色=当前 hovered，灰色=仅显示 OBB |
| 文本输出 | 左上角显示命中组件名与 `t` 值 |
| 主要用途 | 验证锅盖 / 锅内槽位的骨骼矩阵与 OBB 是否对齐 |

#### 当前分层结构（P2.1）

```text
Screen3D.extractRenderState()
  -> Gui3dDebugOverlay.render(...)        // 编排与文本输出
      -> Gui3dDebugCollector.collect(...) // 命中与调试数据采集
      -> Gui3dDebugDrawer.drawObb(...)    // 线框/顶点绘制
          -> Gui3dDebugProjector.projectCorners(...) // OBB 投影
```

#### 对应代码入口

- 开关与入口：`src/main/java/kogasastudio/ashihara/client/gui3d/Screen3D.java`
- 编排层：`src/main/java/kogasastudio/ashihara/client/gui3d/Gui3dDebugOverlay.java`
- 采集层：`src/main/java/kogasastudio/ashihara/client/gui3d/debug/Gui3dDebugCollector.java`
- 投影层：`src/main/java/kogasastudio/ashihara/client/gui3d/debug/Gui3dDebugProjector.java`
- 绘制层：`src/main/java/kogasastudio/ashihara/client/gui3d/debug/Gui3dDebugDrawer.java`

#### 维护约定

- 新增调试统计优先放在 `Gui3dDebugCollector`，避免把业务逻辑塞回 Overlay。
- 新增投影策略放在 `Gui3dDebugProjector`，Overlay 不做矩阵计算。
- `Gui3dDebugOverlay` 保持轻量，只负责编排与文本展示。

### Q：为什么我的射线检测不工作？

A：常见原因：
1. 射线方向未归一化 - 确保`direction.normalize()`
2. 模型没有正确变换到世界空间 - 检查矩阵计算
3. OBB包围盒太小 - 增加debugger查看实际大小

### Q：GeckoLib动画播放不动？

A：检查清单：
1. 骨骼名称是否正确？
2. 动画时长设置是否太短？
3. 是否有其他动画覆盖？

### Q：为什么很卡？

A：常见原因：
1. 过度创建OBB对象 - 使用对象池
2. 没有批渲染 - 按RenderType分组
3. 过度排序 - 使用深度缓存

### Q：如何调试复杂交互？

A：建议：
1. 添加碰撞盒可视化（临时绘制边框）
2. 打印事件日志追踪事件流
3. 使用Minecraft的调试渲染功能

## 常用数值参考

| 参数 | 推荐值 | 说明 |
|---|---|---|
| 动画总时长 | 20-60 ticks | 1-3秒 |
| 物品槽位大小 | 18 pixels | Minecraft标准 |
| UI缩放比例 | 0.5-2.0 | 通常为1.0 |
| 流体显示高度精度 | 0.01 | 1%精度 |
| 粒子发射率 | 5-20 /tick | 根据效果强度 |

## 打包和导出

### 生成的主要代码文件位置

```
com.ashihara.mod.ui.
├── core/
│   ├── Screen3D.java
│   ├── AbstractComponent.java
│   └── ...基础框架
├── component/
│   ├── ModelComponent.java
│   ├── FluidDisplayComponent.java
│   ├── ItemSlotComponent.java
│   └── ...具体组件
├── interaction/
│   ├── Ray.java
│   ├── OBB.java
│   └── ObbInterSector.java
├── animation/
│   ├── InternalControlGeoModel.java
│   ├── InternalAnimationBuilder.java
│   └── ...GeckoLib集成
└── event/
    ├── EventBus.java
    └── ...事件系统
```

## 相关资源链接

- 完整系统设计：[3d-ui-design.md](./3d-ui-design.md)
- 土锅GUI规范：[pot-gui-spec.md](./pot-gui-spec.md)
- 技术决策：[decisions/INDEX.md](./decisions/INDEX.md)
- GeckoLib官方文档：https://github.com/bernie-g/geckolib

---

**更新快速参考**：在实现过程中遇到常见问题或有新的有用模板，应更新此文档以积累知识库。

