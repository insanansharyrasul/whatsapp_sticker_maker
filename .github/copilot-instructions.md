# GitHub Copilot Instructions — WhatsApp Sticker Maker (Android / Kotlin)

## Project Overview

This is a production-grade Android application written in **Kotlin** that allows users to create, manage, and share custom WhatsApp sticker packs. It conforms to the [WhatsApp Stickers API](https://github.com/WhatsApp/stickers) specification.

---

## Architecture: Vertical Slice Architecture + Domain-Driven Design

Every feature is a **self-contained vertical slice** that owns all of its layers (Domain → Application → Infrastructure → Presentation). No horizontal layers exist at the top level. Cross-cutting concerns live in a shared `core` module.

### Top-Level Package Structure

```
com.example.whatsapp_sticker_maker
├── core/                        # Shared infrastructure & utilities
│   ├── di/                      # Hilt modules for core components
│   ├── navigation/              # NavGraph, Destinations sealed class
│   ├── ui/                      # Shared Compose components, theme, typography
│   ├── util/                    # Extensions, Result<T>, Either<L,R>
│   └── domain/                  # Shared domain primitives (ValueObject base, DomainEvent)
│
├── features/
│   ├── sticker_pack/            # Bounded Context: Sticker Pack management
│   │   ├── domain/
│   │   │   ├── model/           # Aggregate roots, Entities, Value Objects
│   │   │   ├── repository/      # Repository interfaces (port)
│   │   │   ├── service/         # Domain services
│   │   │   └── event/           # Domain events
│   │   ├── application/
│   │   │   ├── usecase/         # One class per use-case
│   │   │   └── dto/             # Input/Output DTOs for use-cases
│   │   ├── infrastructure/
│   │   │   ├── repository/      # Room-backed repository implementations (adapter)
│   │   │   ├── local/           # Room DAOs, Entities, Database
│   │   │   └── mapper/          # Domain ↔ Room Entity mappers
│   │   └── presentation/
│   │       ├── viewmodel/       # Hilt ViewModels (one per screen)
│   │       ├── ui/              # Composable screens & components
│   │       └── state/           # UiState, UiEvent sealed classes
│   │
│   ├── sticker_creation/        # Bounded Context: creating individual stickers
│   │   └── ...                  # same internal structure
│   │
│   ├── sticker_export/          # Bounded Context: packaging & sending to WhatsApp
│   │   └── ...
│   │
│   └── settings/                # Bounded Context: user preferences
│       └── ...
│
└── app/
    ├── MainActivity.kt          # Single-activity host
    └── App.kt                   # Hilt Application class
```

---

## Domain-Driven Design Rules

### Aggregates & Entities

- Every **Aggregate Root** must be a Kotlin `data class` or plain `class` annotated with `// AggregateRoot`.
- Aggregate roots protect their invariants — all mutation goes through methods on the root, never direct field assignment from outside.
- Entities have identity (`id: EntityId`). Value Objects have no identity and must be immutable (`data class` with `val` fields only).
- Use **typed IDs** (value objects) instead of raw `String`/`Long`:

```kotlin
@JvmInline
value class StickerPackId(val value: String)

@JvmInline
value class StickerId(val value: String)
```

### Value Objects

- Must be Kotlin `data class` or `@JvmInline value class`.
- Must validate on construction; throw `DomainException` for invalid state.
- Examples: `StickerPackName`, `StickerFile`, `Tray`, `Emoji`.

### Domain Events

- Implement the `DomainEvent` marker interface from `core/domain`.
- Raised inside the aggregate and dispatched via an `EventBus` in the Application layer.

```kotlin
data class StickerPackPublished(
    val packId: StickerPackId,
    val occurredOn: Instant = Clock.System.now()
) : DomainEvent
```

### Repository Interfaces (Ports)

- Located in `features/<feature>/domain/repository/`.
- Pure Kotlin interfaces — zero Android, Room, or framework imports.
- Return `Flow<List<T>>` for reactive queries and `Result<T>` for commands.

### Domain Services

- Used only when logic does not belong to a single aggregate.
- Stateless; injected via the Application layer.

---

## Application Layer Rules

- One **Use Case class** per user action (Single Responsibility Principle).
- Use Cases are `suspend fun` operators (`operator fun invoke`) or return `Flow`.
- They orchestrate domain objects but do not contain business rules.
- Receive DTOs as input; return `Result<OutputDto>`.

```kotlin
class CreateStickerPackUseCase @Inject constructor(
    private val stickerPackRepository: StickerPackRepository
) {
    suspend operator fun invoke(input: CreateStickerPackInput): Result<StickerPackId> {
        val pack = StickerPack.create(input.name, input.publisher)
        return stickerPackRepository.save(pack)
    }
}
```

---

## Infrastructure Layer Rules

- Repository implementations must be suffixed `Impl` and annotated with `@Singleton`.
- Use **Room** for local persistence. Room entities are separate from domain models; use mappers.
- All file I/O operates through a `FileStorage` abstraction in `core`.
- Use `kotlinx.coroutines.Dispatchers.IO` for all I/O; never block the main thread.

---

## Presentation Layer Rules

- **Jetpack Compose** only — no XML layouts.
- One `ViewModel` per screen. ViewModels expose:
  - `uiState: StateFlow<ScreenUiState>` — immutable snapshot of screen state.
  - `uiEvent: SharedFlow<UiEvent>` — one-shot events (navigation, toasts).
- ViewModels call Use Cases; they do not reference repositories or domain objects directly.
- Follow **Unidirectional Data Flow (UDF)**:

```
User Action → ViewModel.onIntent() → UseCase → Domain → Repository
                                                         ↓
                                             uiState updated via StateFlow
```

---

## Tech Stack

| Concern | Library |
|---|---|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Async | Coroutines + Flow |
| Local DB | Room |
| Navigation | Compose Navigation (type-safe routes) |
| Image Loading | Coil 3 |
| Image Processing | Android Bitmap API + `androidx.core` |
| Serialization | `kotlinx.serialization` |
| Date/Time | `kotlinx.datetime` |
| Testing | JUnit5, MockK, Turbine, Compose UI Test |
| Build | Gradle Version Catalogs (`libs.versions.toml`) |

---

## WhatsApp Sticker API Constraints (Domain Rules)

Encode the following as domain invariants (enforced in Value Objects / Aggregates):

- A sticker pack must have **3–30 stickers**.
- Each sticker image must be exactly **512×512 px** WebP format.
- Each sticker file must be **≤ 100 KB**.
- Each sticker must have **1–3 emojis** associated with it.
- The tray (preview) icon must be **96×96 px** WebP.
- The sticker pack `identifier` must be unique and contain only `[a-zA-Z0-9_-]`.
- The sticker pack must be added to WhatsApp via the `Intent` with action `com.whatsapp.intent.action.ENABLE_STICKER_PACK`.
- Expose sticker data via a `ContentProvider` as required by the WhatsApp Stickers SDK.

---

## Coding Conventions

- **No `null`** in domain or application layers; use `Option<T>` (arrow-kt) or Kotlin sealed classes.
- Prefer **sealed interfaces** over sealed classes for domain events and UI state.
- All `suspend` functions in repositories/use-cases must be wrapped in `runCatching` at the ViewModel boundary.
- Use **`Result<T>`** from Kotlin stdlib for error propagation across layer boundaries.
- Name booleans as questions: `isLoading`, `hasError`, `canPublish`.
- Every public API (domain interfaces, use-cases) must have **KDoc**.
- No business logic in Composables — they only read state and emit intents.
- Feature packages are **closed to outside layers**; only the Application layer's Use Cases are accessible by the Presentation layer.

---

## Testing Strategy

| Layer | Tool | Rule |
|---|---|---|
| Domain (unit) | JUnit5 + MockK | No Android dependencies. 100% pure Kotlin. |
| Application (unit) | JUnit5 + MockK | Mock repository interfaces. Test each use-case in isolation. |
| ViewModel | JUnit5 + Turbine | Use `TestCoroutineDispatcher`. Assert `uiState` flows. |
| Compose UI | Compose Test | Test user interactions; mock ViewModels. |
| Integration | Room in-memory DB | Test repository implementations against real Room. |

---

## File Naming Convention

| Type | Naming Pattern | Example |
|---|---|---|
| AggregateRoot | `<Name>.kt` | `StickerPack.kt` |
| Value Object | `<Name>.kt` | `StickerPackName.kt` |
| Repository Interface | `<Name>Repository.kt` | `StickerPackRepository.kt` |
| Repository Impl | `<Name>RepositoryImpl.kt` | `StickerPackRepositoryImpl.kt` |
| Use Case | `<Verb><Noun>UseCase.kt` | `CreateStickerPackUseCase.kt` |
| ViewModel | `<Screen>ViewModel.kt` | `PackListViewModel.kt` |
| Screen Composable | `<Screen>Screen.kt` | `PackListScreen.kt` |
| Room Entity | `<Name>Entity.kt` | `StickerPackEntity.kt` |
| DAO | `<Name>Dao.kt` | `StickerPackDao.kt` |
| Mapper | `<Name>Mapper.kt` | `StickerPackMapper.kt` |
| Hilt Module | `<Feature>Module.kt` | `StickerPackModule.kt` |

---

## Slice Dependency Rules

```
Presentation  →  Application
Application   →  Domain
Infrastructure  →  Domain
Domain        →  (nothing — pure Kotlin)
core          ←  all slices (one-directional)
```

- **Never** import from another feature's `presentation`, `application`, or `infrastructure` package.
- Cross-feature communication uses **Domain Events** published on the shared `EventBus`.

---

## APK Size Optimization (Non-Negotiable)

Every build decision must consider binary size. Apply the following rules unconditionally.

### Build Configuration (`app/build.gradle.kts`)

```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true          // R8 full-mode shrinking + obfuscation
            isShrinkResources = true        // Remove unused resources
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Produce per-ABI APKs or use AAB (preferred)
    bundle {
        language { enableSplit = true }
        density  { enableSplit = true }
        abi      { enableSplit = true }
    }

    // Strip unused native debug symbols
    packagingOptions {
        resources.excludes += setOf(
            "META-INF/*.version",
            "META-INF/proguard/**",
            "kotlin/**",
            "DebugProbesKt.bin"
        )
    }

    // Target a single language for stubs (add more as needed)
    defaultConfig {
        resourceConfigurations += listOf("en")
    }
}
```

### Dependency Rules

- **Always publish AAB** (Android App Bundle) to the Play Store — never a fat APK. AABs reduce download size by ~40% via dynamic delivery.
- Prefer libraries that support **R8 keep rules** out of the box (most AndroidX libs do).
- **Never** add a dependency that pulls in a large transitive tree for a small utility (e.g. use `kotlin.stdlib` string extensions instead of Apache Commons).
- Use **Coil 3** (`io.coil-kt.coil3`) — it is significantly smaller than Glide/Picasso.
- Avoid `kotlin-reflect`; use `kotlin-stdlib` only. Enable R8 to strip unused stdlib code.
- Do **not** include debug-only libraries (LeakCanary, Chucker) in the release build type; gate them with `debugImplementation`.

### Image & Asset Rules

- All sticker and tray images are already **WebP** (mandated by the WhatsApp API) — do not include PNG/JPEG equivalents.
- Use **vector drawables** (`VectorDrawable`) for all UI icons; never bundle raster icons at multiple densities.
- Run `./gradlew lint` and resolve all `UnusedResources` warnings before each release.
- Compress any raw asset (JSON, config) with `assets/` + runtime decompression rather than embedding large static data.

### R8 / ProGuard

- Enable **R8 full mode** by adding to `gradle.properties`:
  ```properties
  android.enableR8.fullMode=true
  ```
- Keep rules must be **minimal and explicit** — never use `-keep class com.example.**` blanket rules.
- Add keep rules only for classes accessed via reflection (Room, Hilt, `kotlinx.serialization`). The build system auto-generates rules for Hilt and Room; do not duplicate them.
- Review `build/outputs/mapping/release/usage.txt` after each release build to confirm dead code is stripped.

### Baseline Profiles

- Generate a **Baseline Profile** (`app/src/main/baseline-prof.txt`) using the Macrobenchmark library to improve startup time without increasing APK size:
  ```
  ./gradlew :app:generateBaselineProfile
  ```
- Include the profile in the release AAB; it is compiled AOT on install and does not bloat the download size.

### Size Budgets

Enforce these hard limits in CI:

| Artifact | Budget |
|---|---|
| Release AAB | ≤ 10 MB |
| Per-ABI split APK (arm64-v8a) | ≤ 6 MB |
| Any single feature module | ≤ 2 MB |

Add an `apkanalyzer` step to the CI pipeline that fails the build if the AAB exceeds the budget:
```bash
$ANDROID_HOME/cmdline-tools/latest/bin/apkanalyzer apk summary app/build/outputs/bundle/release/app-release.aab
```

---

## Code Generation Guidance for Copilot

When generating any new feature or class:

1. **Start with the Domain** — define Value Objects, the Aggregate, and the Repository interface first.
2. **Write Use Cases** — one class per action, no framework dependencies.
3. **Implement Infrastructure** — Room entities, DAOs, mapper, and repository implementation.
4. **Build the Presentation** — UiState sealed interface, UiEvent sealed interface, ViewModel, then Composable screen.
5. **Write tests** — domain unit tests first, then use-case tests, then ViewModel tests.
6. Always tie new DI bindings in a `<Feature>Module.kt` Hilt module within the feature's `infrastructure/` package.
7. Register new screens in `core/navigation/NavGraph.kt` using type-safe Compose Navigation destinations.
