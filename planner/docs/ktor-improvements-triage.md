# Ktor modernization triage plan

Scope reviewed:

- `rg "io\.ktor" /home/runner/work/net.twisterrob.cinema/net.twisterrob.cinema`
- Planner server/client modules and the standalone `map/map.main.kts` script.

## Proposed improvements (for triage, not implemented)

1. **Session cookie signing secret should not be hardcoded**
   - Current: `secretSignKey = "twister".toByteArray()`
   - Proposal: load from environment/config and fail fast if missing in non-local environments.
   - Project refs: `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/configuration.kt:119`
   - Ktor refs: https://ktor.io/docs/server-sessions.html

2. **Replace reflection-based session serializer with kotlinx serialization**
   - Current: `serializer = reflectionSessionSerializer()`
   - Proposal: annotate session models with `@Serializable` and use default/session serializer path, reducing reflection usage and deprecated API reliance.
   - Project refs:
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/configuration.kt:118`
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/auth/data/AuthSession.kt:3`
   - Ktor refs:
     - https://ktor.io/docs/server-sessions.html
     - https://ktor.io/docs/releases.html

3. **Remove `runBlocking` from network services**
   - Current: synchronous wrappers around Ktor client calls in service layer.
   - Proposal: make service contracts suspendable (or isolate blocking at process edge) so Ktor client remains non-blocking end-to-end.
   - Project refs:
     - `backend/feed/src/main/kotlin/net/twisterrob/cinema/cineworld/sync/syndication/FeedServiceNetwork.kt:27`
     - `backend/quickbook/src/main/kotlin/net/twisterrob/cinema/cineworld/quickbook/QuickbookServiceNetwork.kt:50`
   - Ktor refs: https://ktor.io/docs/client-requests.html

4. **Migrate legacy `BaseApplicationPlugin` custom plugins to modern plugin APIs (or remove dead plugin)**
   - Current: old plugin style in `ServerLogging` / `HeaderLoggingFeature`; one is commented out in configuration.
   - Proposal: use modern custom plugin APIs and delete/retire code paths no longer installed.
   - Project refs:
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/ServerLogging.kt:175`
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/HeaderLoggingFeature.kt:13`
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/configuration.kt:88`
   - Ktor refs: https://ktor.io/docs/server-custom-plugins.html

5. **Centralize and harden HttpClient baseline configuration**
   - Current: multiple ad-hoc `HttpClient().config { ... }` blocks with repeated logging/`expectSuccess`.
   - Proposal: one shared factory/plugin setup for timeout, retry, default headers/user-agent, and common observability.
   - Project refs:
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/app/App.kt:44`
     - `backend/sync/src/main/kotlin/net/twisterrob/cinema/cineworld/sync/SyncAppModule.kt:76`
     - `backend/sync/src/main/kotlin/net/twisterrob/cinema/cineworld/generate/GenerateAppModule.kt:36`
   - Ktor refs:
     - https://ktor.io/docs/client-timeout.html
     - https://ktor.io/docs/client-request-retry.html

6. **Add explicit outbound resilience defaults (timeouts + retry policy)**
   - Current: no obvious global timeout/retry policy for external Cineworld/Google calls.
   - Proposal: configure sane connect/request/socket timeouts and idempotent retry behavior for transient failures.
   - Project refs:
     - `backend/quickbook/src/main/kotlin/net/twisterrob/cinema/cineworld/quickbook/QuickbookServiceNetwork.kt`
     - `backend/feed/src/main/kotlin/net/twisterrob/cinema/cineworld/sync/syndication/FeedServiceNetwork.kt`
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/configuration.kt`
   - Ktor refs:
     - https://ktor.io/docs/client-timeout.html
     - https://ktor.io/docs/client-request-retry.html
     - https://github.com/ktorio/ktor/releases (3.2.x/3.3.x client stability fixes)

7. **Evaluate CSRF protection for session-authenticated state-changing routes**
   - Current: session + OAuth flow present; state-changing endpoints exist.
   - Proposal: evaluate and likely enable first-party CSRF protection where session cookies are used.
   - Project refs:
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/configuration.kt`
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/auth/AuthController.kt`
   - Ktor refs:
     - https://ktor.io/docs/server-csrf.html
     - https://ktor.io/docs/releases.html

8. **Review static content handling against newer Ktor static features**
   - Current: static serving exists; custom behavior may overlap with newer first-party capabilities.
   - Proposal: verify if first-party static content ETag/cache features can replace custom logic and simplify code.
   - Project refs:
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/ktor/caching.kt`
     - `backend/endpoint/src/main/resources/application.conf`
   - Ktor refs:
     - https://github.com/ktorio/ktor/releases (3.3.0 static ETag/caching feature)
     - https://ktor.io/docs/server-static-content.html

9. **Assess OpenAPI generation for route/documentation simplification**
   - Current: routes are annotated with `@Resource`; no first-party OpenAPI generation usage.
   - Proposal: evaluate Ktor OpenAPI generation to reduce manual API docs drift and simplify endpoint documentation.
   - Project refs:
     - `backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/**`
   - Ktor refs:
     - https://github.com/ktorio/ktor/releases (3.4.0 OpenAPI support)
     - https://ktor.io/docs/openapi-spec-generation.html

10. **Small cleanup in standalone map script: close HttpClient and avoid per-call `runBlocking`**
    - Current: script builds `HttpClient` without explicit close and uses `runBlocking` in helper.
    - Proposal: use structured lifecycle (`use`) and reduce blocking wrappers for cleaner resource handling.
    - Project refs: `map/map.main.kts:98`, `map/map.main.kts:109`
    - Ktor refs: https://ktor.io/docs/client-create-and-configure.html
