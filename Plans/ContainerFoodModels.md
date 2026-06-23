# Custom Container Food Models — 设计文档

## 状态总览

| # | 需求 | 状态 |
|---|------|------|
| 2.1-2.5 | 模型目录结构 + 注册流程 | ✅ 已完成 |
| 3.1 | chopLeft DataComponent (CHOP_LEFT) | ✅ 已完成 |
| 3.1 | ContainerContent.chopLeft | ✅ 已完成 |
| 3.2 | 数据流向（放置/拿起碗） | ✅ 已完成 |
| 3.3 | 交互逻辑（筷子/空手/拿起碗） | ✅ 已完成 |
| 3.3 | 碗放置时 CHOP_LEFT/MAX_BITES 回传 BE | ✅ 已完成 |
| 3.4 | maxBites() | ✅ 已完成 |
| 3.4 | MAX_BITES DataComponent | ✅ 已完成 |
| 4.1 | 渲染查找 FoodModelRegistry | ✅ 已完成 |
| 4.1 | standalone BER 渲染 | ✅ 已完成 |
| 4.1 | BowlContentSpecialRenderer standalone | ✅ 已完成 |
| 4.2 | 筷子模型 open 变种 | ❌ 未实现 |
| 5 | FoodModelRegistry 自动扫描 | ✅ 已完成 |
| 6.1 | HUD 文本 | ✅ 已完成 |
| 6.2 | 碗 Tooltip chopLeft | ✅ 已完成 |
| 6.3 | 筷子 Tooltip | ✅ 已完成（文字） |
| 6.4 | ChopsticksItem 完整机制 | ✅ 已完成 |
| - | BowlFoodHelper 按 fraction 缩放 | ✅ 已完成 |
| - | FurnitureComponentItem 阻止 chopLeft 存取 | ✅ 已完成 |
| - | 中日英语言文件 | ✅ 已完成 |
| - | 筷子 SpecialRenderer | ❌ 未实现 |
| - | 创建模式物品栏注册 | ❌ 遗漏 |
| - | 大碗等新容器注册 | ❌ 未实现 |