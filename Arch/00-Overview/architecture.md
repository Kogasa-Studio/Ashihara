# 整体架构与技术选型

> **标签**：[与阶段无关]
> **状态**：🚧 编写中

---

## 技术栈

| 项目 | 版本 | 用途 |
|------|------|------|
| Minecraft | 1.21 | 宿主游戏 |
| NeoForge | 21.1.125 | Mod 加载器与 API |
| Java | 21 | 开发语言 |
| GeckoLib | latest | 3D 动画模型（指南书、特殊物品） |
| Parchment | 1.20.6 / 2024.05.01 | 参数名映射 |

**运行时兼容目标**（非强制依赖，但需保证不崩溃）：

| 模组 | 版本 | 兼容优先级 |
|------|------|-----------|
| Sodium | 0.6.0-beta.1 | 🔴 高（自定义渲染涉及） |
| Iris | 1.8.0-beta.2 | 🔴 高（着色器管线涉及） |
| JEI | 19.5.0.31 | 🟠 中（配方展示） |
| WorldEdit | 7.3.6+ | 🟡 低（建筑辅助工具） |

---

## 模块依赖关系

```
┌─────────────────────────────────────────────────────────┐
│                    Ashihara Mod                          │
│                                                         │
│  ┌──────────┐   ┌──────────┐   ┌──────────────────────┐│
│  │ 注册系统  │◄──│ 内容系统  │──►│    建筑系统           ││
│  │(Registry)│   │(Items/   │   │(BuildingComponent/   ││
│  └────┬─────┘   │Blocks/   │   │ MultiBuiltBlock)      ││
│       │         │Recipes)  │   └──────────┬───────────┘│
│  ┌────▼─────┐   └────┬─────┘              │            │
│  │ 技术基础  │        │              ┌─────▼──────┐      │
│  │(Network/ │   ┌────▼─────┐       │  渲染系统   │      │
│  │Capability│   │ 玩法系统  │       │(RenderType/│      │
│  │DataComp) │   │(农业/工作 │       │ GeckoLib/  │      │
│  └────┬─────┘   │台/神道)  │       │ Shader)    │      │
│       │         └────┬─────┘       └────────────┘      │
│  ┌────▼─────┐        │                                  │
│  │  Mixin   │   ┌────▼─────┐                           │
│  │  系统    │   │玩家交互系统│                           │
│  └──────────┘   │(指南书/UI│                           │
│                 │/动画)    │                           │
│                 └──────────┘                           │
└─────────────────────────────────────────────────────────┘
```

---

## 代码包结构

```
kogasastudio.ashihara
├── Ashihara.java                    # 模组入口
├── CreativeModeTabsRegistryHandler  # 创意模式标签
│
├── block/                           # 方块实现
│   ├── building/                    # 建筑系统方块
│   ├── blockentity/                 # 方块实体
│   ├── trees/                       # 树木生成器
│   └── woodcraft/                   # 木工系列方块
│
├── client/                          # 纯客户端代码
│   ├── particles/                   # 粒子效果
│   └── render/                      # 渲染相关
│       ├── geo/                     # GeckoLib 渲染
│       ├── ter/                     # TileEntityRenderer
│       └── ister/                   # ISTER
│
├── compat/                          # 模组兼容
│   ├── jei/                         # JEI 集成
│   └── sodium/                      # Sodium 兼容
│
├── event/                           # 事件处理器
├── fluid/                           # 流体注册
├── helper/                          # 通用辅助函数
├── interaction/recipes/             # 自定义配方
├── inventory/                       # 容器与物品堆处理
├── item/                            # 物品实现
│   ├── block/                       # 方块物品
│   └── food/                        # 食物物品
├── loading/                         # 资源加载（指南书）
├── mixin/                           # Mixin 注入
│   ├── geckolib/
│   ├── iris/
│   └── sodium/
├── network/                         # 网络包
├── registry/                        # 注册器
├── sounds/                          # 音效
├── utils/                           # 工具类
│   ├── json/                        # JSON 序列化
│   ├── mixin/                       # Mixin 接口
│   └── shape/                       # VoxelShape 工具
└── world/                           # 世界生成
    ├── configuration/
    ├── feature/
    └── tree/
```

---

## 关键设计原则

1. **数据驱动优先**：配方、指南书内容、世界生成参数尽量通过 JSON 定义，减少硬编码
2. **Mixin 谨慎使用**：Mixin 仅用于无法通过 NeoForge API 实现的功能，并在 `05-Technical-Foundation/mixin-system.md` 中登记
3. **兼容性隔离**：Sodium/Iris 相关代码严格限制在 `compat/` 和对应 `mixin/` 子包中，通过条件加载避免强依赖
4. **客户端/服务端分离**：客户端代码严格限制在 `client/` 包和 `@OnlyIn(Dist.CLIENT)` 标注中

