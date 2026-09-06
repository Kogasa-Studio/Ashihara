# Ashihara Mod Development Guidelines

Working guidelines for this repository. Revised for a modern coding agent; earlier sections
that prescribed shell-escaping hacks or banned specific tools were workarounds for a legacy
model and have been removed. Choose the editing and verification workflow that is reliable
for the task at hand.

## Development Habits

- Decouple and abstract where it genuinely reduces complexity; never write specialized logic
  into a generic parent class.
- Prefer inner classes for small, one-off features or helpers.
- Prefer platform/framework-provided APIs over ad-hoc shell or string manipulation, and treat
  the Minecraft/NeoForge sources (`src-references`, decompiled jars under
  `build/moddev/artifacts`) as the source of truth for API behavior.
- If a decision is important and genuinely uncertain, ask the user before committing to it.

## Tool Usage

- Editing method is your choice: platform editing tools, `apply_patch`, or direct shell/Python
  writes are all acceptable as long as the result is verified and the diff stays clean.
- The Python helpers under `resource/tools` (`fread.py`, `fwrite.py`, `freplace.py`,
  `patch.py`) remain available and are convenient for bulk, repetitive, or CJK-heavy edits.
- MCP broken? Run `clean_mcp_zombies.ps1` from the desktop.

## Code Style

- Never use fully qualified class names. Add imports.
- Allman brace style: opening brace on its own line. Blocks with body < 20 chars may stay on
  one line: `{ stmt; }`.
- Every statement ending with `;` must be followed by a newline.
- Never break single-line statements arbitrarily. Keep lines under 192 characters on a single
  line. This rule targets chained calls and long parameter lists; it does NOT justify omitting
  braces or putting multiple `;` on one line.

## Verification & Workflow

- Compile after code changes:
  `.\gradlew.bat compileJava --rerun-tasks --no-build-cache --console=plain`.
- Keep changes scoped; check `git status` and `git diff` before finishing.
- Never silently revert the user's own changes or run destructive git operations. If something
  breaks, report it and let the user decide how to restore.
- Commit messages follow `#Fix:`, `#Addition:`, `#Modification:` prefixes.
