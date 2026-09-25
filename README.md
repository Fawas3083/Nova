# Nova Android

An early Android implementation of the Nova experimental cognitive architecture.

## Current scope

This build contains a small executable local core with:
- attention selection
- workspace state
- persistent memory
- self-state variables
- confidence/uncertainty updates
- simple prediction/novelty handling
- persistent local state across app launches

It is a research prototype. It does **not** establish subjective consciousness.

## Build

The included GitHub Actions workflow builds a debug APK on a GitHub-hosted runner.

1. Upload the project to a GitHub repository.
2. Push to `main`, or open **Actions → Build Nova APK → Run workflow**.
3. Download the `Nova-debug-apk` artifact.
