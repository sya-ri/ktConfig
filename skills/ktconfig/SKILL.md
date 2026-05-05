---
name: ktconfig
description: Use when the user wants to build, explain, or review Kotlin Bukkit/Spigot/Paper configuration code that uses ktConfig. Covers dependency setup, `@KtConfig` models, generated loader usage, comments, `@SerialName`, `hasDefault`, sealed hierarchies, custom serializers, supported types, and common KSP/version pitfalls.
license: MIT
---

# ktconfig

Guidance for writing and explaining code that uses `dev.s7a:ktConfig`.

## When to use this skill

Use this skill when the task involves:

- adding or fixing ktConfig-based config classes in a Bukkit/Spigot/Paper plugin
- wiring generated `*Loader` classes into plugin startup code
- choosing between default values, nullable values, sealed configs, and custom serializers
- diagnosing ktConfig KSP generation errors or Kotlin/KSP version mismatches

## Workflow

1. Read [`references/ktconfig-reference.md`](references/ktconfig-reference.md) before making non-trivial ktConfig changes.
2. Prefer the simplest working pattern:
   - `@KtConfig` on a Kotlin `data class`
   - generated `<ClassName>Loader`
   - `load`, `loadAndSave`, or `loadAndSaveIfNotExists` depending on the desired file creation behavior
3. If the user asks for plugin code, include:
   - Gradle dependencies for both runtime and KSP
   - the annotated config class
   - plugin-side loader usage with a concrete file path such as `dataFolder.resolve("config.yml")`
4. If the task involves advanced modeling, load the relevant reference section:
   - defaults: `Default values`
   - union-like configs: `Sealed classes and interfaces`
   - unsupported domain types: `Custom serializers`
   - compiler/build failures: `Troubleshooting`

## Output expectations

- Prefer concrete Kotlin code over abstract explanation.
- Keep YAML keys stable; use `@SerialName` instead of deprecated `@PathName`.
- When `hasDefault = true`, ensure every property has a static default value.
- When using sealed hierarchies, annotate both the parent and every subclass with `@KtConfig`.
- When using a custom serializer, declare it as a Kotlin `object`, not a `class`.

## Scope note

This skill is about **using** ktConfig in plugin/application code. If the task is about changing ktConfig internals, inspect the library source directly instead of relying only on the reference.
