# Ktor modernization review plan (planning only)

## Scope reviewed

- Ktor server usage in `planner/backend/endpoint/**`
- Ktor client usage in `planner/backend/{network,quickbook,feed,sync}/**`
- Shared test usage in `planner/test-helpers/**`
- Current version is already up-to-date: `ktor = "3.4.0"` in `planner/gradle/libs.versions.toml`

## Candidate improvements

> These are intentionally **not implemented yet**. Each item below has references and a short rationale.

1. **Migrate legacy custom plugins to modern plugin API (`createApplicationPlugin`).**  
   - Project usage: `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/{HeaderLoggingFeature.kt,ServerLogging.kt}`  
   - Why: these still use `BaseApplicationPlugin`; modern API is simpler and more explicit for request/response hooks.  
   - References:  
     - https://ktor.io/docs/server-custom-plugins.html  
     - https://ktor.io/docs/migration-to-20x.html

2. **Harden session security (serializer + secret management).**  
   - Project usage: `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/configuration.kt` (`reflectionSessionSerializer()`, hardcoded `"twister"` signing key).  
   - Why: move away from reflection serializer and avoid hardcoded signing material; use config/env sourced keys and stricter cookie settings.  
   - References:  
     - https://ktor.io/docs/server-sessions.html  
     - https://github.com/ktorio/ktor/releases/tag/2.3.13

3. **Centralize and modernize `HttpClient` construction.**  
   - Project usage: duplicated `HttpClient().config { ... }` in `backend/endpoint/.../App.kt`, `backend/sync/.../SyncAppModule.kt`, plus per-service reconfiguration in `QuickbookServiceNetwork`, `FeedServiceNetwork`, `AuthController`.  
   - Why: reduce duplication, ensure consistent logging/timeouts/retry/security behavior, and make lifecycle ownership clearer.  
   - References:  
     - https://ktor.io/docs/client-create-and-configure.html  
     - https://github.com/ktorio/ktor/releases/tag/3.1.0

4. **Replace blocking client usage (`runBlocking`) with suspend-first flow.**  
   - Project usage: `backend/quickbook/.../QuickbookServiceNetwork.kt`, `backend/feed/.../FeedServiceNetwork.kt`.  
   - Why: avoid blocking in service layer and align with Ktor coroutine-first APIs; this also opens the door for cleaner startup/runtime behavior.  
   - References:  
     - https://ktor.io/docs/whats-new-320.html  
     - https://ktor.io/docs/server-modules.html#concurrent-modules

5. **Evaluate replacing manual XML/Jackson wiring with first-party XML serialization.**  
   - Project usage: `backend/feed/.../FeedServiceNetwork.kt` currently uses `JacksonConverter` for `ContentType.Application.Xml`.  
   - Why: first-party Ktor XML serialization can reduce custom converter wiring and improve consistency with other serialization config.  
   - References:  
     - https://ktor.io/docs/client-serialization.html  
     - https://github.com/ktorio/ktor/releases/tag/3.0.0

6. **Use typed configuration loading for server secrets/settings.**  
   - Project usage: mixed JSON/env/manual map access in `backend/endpoint/.../configuration.kt` and startup wiring.  
   - Why: typed config (`Application.property<T>()`) reduces boilerplate and invalid config risks.  
   - References:  
     - https://ktor.io/docs/whats-new-320.html  
     - https://ktor.io/docs/server-configuration-file.html

7. **Review OAuth flow for current best practices and safer defaults.**  
   - Project usage: `backend/endpoint/.../configuration.kt`, `backend/endpoint/.../auth/AuthController.kt`, `backend/endpoint/.../ktor/KtorExtensions.kt`.  
   - Why: tighten redirect/session handling and align with current Ktor OAuth guidance (state/fallback/error handling and URL construction).  
   - References:  
     - https://ktor.io/docs/server-oauth.html  
     - https://ktor.io/docs/releases.html

8. **Adopt OpenAPI generation for `Resources` routes (optional/experimental).**  
   - Project usage: endpoint routing already uses `Resources`, e.g. `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/**`.  
   - Why: Ktor now offers runtime/build-time OpenAPI generation that could replace manual API documentation drift.  
   - References:  
     - https://ktor.io/docs/openapi-spec-generation.html  
     - https://ktor.io/docs/whats-new-330.html

9. **Revisit low-level request/response body logging implementation.**  
   - Project usage: `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/ServerLogging.kt` uses low-level channel operations (`receiveChannel`, `readRemaining`) and manual response extraction.  
   - Why: simplify maintenance and reduce risk of consuming streams unexpectedly; confirm whether newer plugin hooks can provide equivalent diagnostics with less custom IO code.  
   - References:  
     - https://ktor.io/docs/server-custom-plugins.html  
     - https://github.com/ktorio/ktor/releases/tag/3.2.0

## Notes for implementation phase (later)

- Keep each selected item as a small, isolated PR step (one behavior change at a time).
- Prioritize security and non-invasive refactors first: session key handling, plugin API migration, typed config.
- Only then consider larger API-shape changes (`suspend` propagation, serialization model changes).
