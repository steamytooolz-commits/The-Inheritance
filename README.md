# The Inheritance
*Your uncle died. The books are lying. You have 30 days.*

A narrative forensic-accounting game for Android. Inherit a failing business ("The Book Nook"), read its books, survive 30 days. The Game Master — the dead uncle living inside the ledger — is antagonist and ally.

- Platform: Android (min SDK 26, target SDK 36), Kotlin 2.4.10, Compose BOM 2026.08.00
- Architecture: Clean Architecture + MVVM + MVI, Hilt DI, Room, Version Catalog
- Template base: Android Production Template (CI/CD, keystore automation, Roborazzi kept intact)

## Packaging decision (single `:app` module)

The spec describes `core/`, `data/`, `ui/` Gradle modules. This repo implements them as **packages inside `:app`** (`com.theinheritance.accounting.*`, `.simulation`, `.gm`, `.data.*`, `.ui.*`) so the template's `ci.yml` / `release.yml` / Roborazzi / Secrets-plugin pipeline keeps working without multi-module Gradle overhead. Splitting into Gradle modules later is mechanical (each package is dependency-clean: `accounting` is pure Kotlin, `gm` never touches Room).

## Project map

```
app/src/main/java/com/theinheritance/
├── TheInheritanceApp.kt, MainActivity.kt
├── navigation/ (Routes, AppNavHost)
├── di/ (AppModule, DatabaseModule, LlmModule, RepositoryModule)
├── accounting/model/ (Account, AccountType, JournalEntry, JournalLine, Ledger, TrialBalance, IncomeStatement, BalanceSheet, CashFlowStatement)
├── accounting/engine/ (AccountingEngine, PostingValidator, Calculators, FinancialStatementGenerator, PeriodCloser)
├── accounting/fraud/ (Fraud, schemes/*)
├── accounting/money/ (Money Long-cents, MoneyFormatter BigDecimal)
├── simulation/ (BusinessState, EconomicSimulator, MarketEventGenerator, NpcAgentEngine, DailyTurnResolver, RunScorer)
├── gm/ (GameMaster, GmOrchestrator, PromptBuilder, ToolExecutor, GmMemoryStore + actions/*)
├── data/local/ (InheritanceDatabase, entity/*, dao/*, converter/*)
├── data/llm/ (LlmEngine, MediaPipe/LiteRT/llama.cpp/Remote/RuleBased, ModelManager+Worker, LlmBackendResolver)
├── data/repository/ (Accounting, GameState, Npc, GmMemory, Run)
└── ui/theme|dashboard|narrative|journal|ledger|statements|npc|market|modelmanager|settings
```

## Critical constraints (enforced)

1. Money is `Long` cents (`Money`), BigDecimal only for display.
2. `accounting/` is pure Kotlin — verified: no `android`/`androidx`/`dagger` imports.
3. GM never writes DB directly — goes through `ToolExecutor` → engine validates → repository persists.
4. Rejected actions are fed back (`recordRejection`).
5. Action budget 15/turn (`GmActionBudget(maxPerTurn = 15)`).
6. Model downloads `NetworkType.UNMETERED` only (`ModelManager`).
7. Offline-first: `RuleBasedEngine` default Hilt binding; MediaPipe when model present; remote optional.
8. Every statement line tappable → drills to ledger + GM explains.
9. Fraud procedurally generated per run (`FraudGenerator(seed)`).
10. 30-day runs (`maxDays = 30`), NG+ keeps GM memory.

## Build

| Command | Action |
|---|---|
| `gradle :app:assembleDebug` | Debug APK |
| `gradle :app:testDebugUnitTest` | JUnit + Robolectric (`PostingValidatorTest`, `FraudGeneratorTest`, `JournalScreenTest`) |
| `gradle :app:verifyRoborazziDebug` | Golden screenshots |
| `gradle :app:recordRoborazziDebug` | Update goldens |
| `gradle :app:lintDebug` | Lint |

Secrets: `.env.example` → `.env` (Secrets plugin → `BuildConfig`). Never commit `.env`/`.jks`.

## Phases 10–12

**Live Economy (10):** `MarketViewModel` drives a 7-day street forecast from the seeded
`MarketEventGenerator`; events persist via `RunRepository` and feed `DailyTurnResolver`
cash deltas. Deterministic per run seed.

**Monetization (11):** `monetization/` package —
`BillingManager` (one-time `the_inheritance_pro` SKU, acknowledge + entitlement refresh),
`ProUnlockRepository` (DataStore flag, offline-readable),
`AdsManager` (optional AdMob interstitial, initialized in `TheInheritanceApp`, game plays
without it), `AnalyticsLogger` (run/day/fraud/ending/pro events, never crashes).
Create the SKU in Play Console → Monetize → Products before release.

**Polish & Beta (12):** 3-page `TutorialScreen` (`TutorialRoute`, first-run flag in
DataStore, entry in Settings), zero-asset `SoundManager` (ToneGenerator beeps, no res
files), permission-free `Haptics` (Compose haptic channel).

### Closed beta (Play Console)

1. `Generate Release Keystore` workflow (or scripts) → set `KEYSTORE_BASE64`,
   `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS` secrets.
2. Tag `v1.0.0` → `release.yml` publishes signed APK + AAB.
3. Play Console → Testing → Closed testing → create track, upload the AAB,
   add tester emails, roll out. Promote to production after the 30-day
   content pass.

