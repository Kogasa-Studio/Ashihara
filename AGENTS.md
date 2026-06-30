# Ashihara Mod Development Guidelines

## Tool Usage

All tools and shell commands free to use. No permission needed. But please consider use Python scripts under resource/tools first, as they are more robust and cross-platform.
MCP broken? Run clean_mcp_zombies.ps1 from desktop.

## Code Style

- Never use fully qualified class names. Add imports.
- Allman brace style: opening brace on its own line. Blocks with body < 20 chars may stay on one line: { stmt; }.
- Every statement ending with ; must be followed by a newline.
- Never break single-line statements arbitrarily. Keep lines under 192 characters on a single line. This rule targets chained calls and long parameter lists; it does NOT justify omitting braces or putting multiple ; on one line.





## PowerShell Escaping Rules (2026-06-30 verified)

### Core Rules
PowerShell escape character is BACKTICK, not backslash.
No Linux-style \ or cmd-style ^ escaping exists in PowerShell.

### Passing Text Blocks to Shell
Use single-quoted here-string: @' ... '@ (each delimiter must be on its own line).
Content between delimiters is completely literal ? no escaping needed for quotes, angle brackets, parens, dollar signs, or backticks.
Pipe to the target program:

    $script = @'
    ...arbitrary code with "quotes", <brackets>, (parens)...
    '@
    $script | python

This is the ONLY way to pass Python/Java/JSON/CJK code without any escaping issues.
NEVER use python -c for long scripts ? both cmd and PS will truncate or mis-escape arguments.

### Known Broken Tool
apply_patch: confirmed bug. Modifying Java files corrupts encoding, causing compileJava errors.

### Verified Working Tools
- $script = @'...'@ ; $script | python ? batch file editing of any complexity
- fread.py ? read files
- fwrite.py ? overwrite simple files (only when args contain no quotes/brackets)
- freplace.py ? simple substring replacement
- patch.py ? insert lines into JSON/lang files (write .patch data via Python for CJK content)
