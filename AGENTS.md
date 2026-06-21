# Ashihara Mod Development Guidelines

## 工具使用规范

进行任何代码编辑时，必须优先使用平台提供的编辑工具（如 `edit_file`、`apply_patch` 等），而非通过 shell 命令写入文件。Shell 写入容易被 IDE 自动保存覆盖，且难以追溯改动内容。如确实遇到工具无法完成的编辑任务，必须先向用户说明情况并获得许可，方可使用 shell。

## 代码风格

- 除非存在类名冲突，禁止使用全限定类名（如 `kogasastudio.ashihara.registry.SoundEvents.CUT.get()`）。应当添加 import 语句，在代码中使用短名称。
- 能在单行内自然书写的语句，不要为了凑行数而随意折行。只有在存在多重链式调用或复杂的嵌套括号结构时才进行换行。短行代码打骨折会严重影响可读性。