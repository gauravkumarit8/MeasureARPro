# MeasureAR Pro — Phase 0 Scaffold

Multi-module Android project scaffold for **MeasureAR Pro: AR Tape Measure & Room Planner**,
generated from the v2.0 PRD. This is Phase 0 of the 7-phase build sequence: project
structure, module boundaries, and the navigation graph are wired; feature logic is stubbed
with `TODO`-style comments pointing at the PRD section that specifies it.

## What's here

- `:app` — nav host, bottom tab bar, manifest with `com.google.ar.core = "optional"`
- `:domain` — pure Kotlin models & formulas (Measurement, RoomPlan, CostEstimator) — no Android deps, unit-testable without an emulator
- `:core-ar`, `:core-billing`, `:core-ads`, `:core-database`, `:core-export` — infrastructure modules, one per external dependency (ARCore/SceneView, Play Billing, AdMob, Room, PDF/QR export)
- `:feature-*` — one module per screen (measure, level, ruler, converter, roomplan, fitchecker, templates, paywall), each a thin Compose layer over `:domain` + the `:core-*` modules it needs

## Important: this won't build in the Claude sandbox

This scaffold was written in a container without network access to Google's Maven
repository or the Gradle distribution servers, so it has **not been compiled or run**.
There may be small API-surface mismatches once you sync against real ARCore/SceneView/
Compose versions — treat this as a structurally-correct starting point, not a verified build.

## Getting it running

1. Open this folder in **Android Studio** (simplest path — it bundles the Android SDK and
   generates the Gradle wrapper for you), or in **GitHub Codespaces** using the included
   `.devcontainer/devcontainer.json`, which installs Gradle 8.9 (via the official
   `devcontainers/features/java` feature) and the Android SDK command-line tools
   (via `.devcontainer/setup-android-sdk.sh` — there's no reliable official "android-sdk"
   devcontainer feature, so this installs it manually from Google's distribution).
2. In Codespaces, generate the Gradle wrapper once the container is built —
   `gradle wrapper --gradle-version 8.9` — this repo does not include a pre-built
   `gradle-wrapper.jar`.
3. Sync Gradle. Dependency versions live in `gradle/libs.versions.toml` — bump ARCore/
   SceneView/Compose BOM to whatever's current at the time you build.
4. Run on an ARCore-capable device or emulator for AR features; non-AR devices should
   fall back to the Screen Ruler automatically once `ArSessionManager` is implemented (Phase 1).

## Build sequence (see PRD Section "Technical Notes" for full detail)

| Phase | Scope |
|---|---|
| 0 | This scaffold |
| 1 | Free-tier MVP: AR Distance + confidence score, Level, Ruler + calibration, Converter |
| 2 | Play Billing + EntitlementState, AdMob + UMP consent, Paywall |
| 3 | Room Planner (Pro) |
| 4 | Fit Checker |
| 5 | Guided Templates, Cost Estimator, QR Plan Sharing |
| 6 | Localization (en, es, pt, fr, hi, de) & polish |
| 7 | Play Store submission prep |

## Key architectural decisions baked into this scaffold

- **SceneView, not Sceneform** — Sceneform is deprecated; `:core-ar` depends on
  `io.github.sceneview:arsceneview` for AR 3D rendering (Fit Checker box, Room Planner walls).
- **`com.google.ar.core = "optional"`** in the manifest — keeps the app visible to all
  devices on Play; AR-dependent screens check `ArSessionManager.isArSupported()` and fall
  back to Screen Ruler rather than being filtered out of the store listing entirely.
- **Single `EntitlementState`** (`:core-billing`) — every Pro feature and every ad request
  gates on this one `StateFlow<Boolean>`, so ad-removal and feature-unlocks can't drift
  out of sync with each other.
- **`:domain` has zero Android dependencies** — cost/confidence/geometry formulas are
  plain Kotlin, so they can be unit tested on the JVM without an emulator.
