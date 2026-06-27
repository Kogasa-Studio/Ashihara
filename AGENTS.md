# Ashihara Mod Development Guidelines

## Tool Usage

All tools and shell commands free to use. No permission needed. But please consider use Python scripts under resource/tools first, as they are more robust and cross-platform.
MCP broken? Run clean_mcp_zombies.ps1 from desktop.

## Code Style

- Never use fully qualified class names. Add imports.
- Allman brace style: opening brace on its own line. Blocks with body < 20 chars may stay on one line: { stmt; }.
- Every statement ending with ; must be followed by a newline.
- Never break single-line statements arbitrarily. Keep lines under 192 characters on a single line. This rule targets chained calls and long parameter lists; it does NOT justify omitting braces or putting multiple ; on one line.
