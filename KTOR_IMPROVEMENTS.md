# Ktor Improvements - Evidence-Based Recommendations

## Overview
This document contains evidence-based recommendations for Ktor improvements, derived from official release notes from the [Ktor GitHub repository](https://github.com/ktorio/ktor/releases) and [Ktor documentation](https://github.com/ktorio/ktor-documentation).

**Current Version**: Ktor 2.3.13 (Released: November 20, 2024)  
**Latest Stable**: Ktor 3.4.0 (Released: January 23, 2026)  
**Recommendation**: Upgrade to Ktor 3.x to access 2+ years of improvements

---

## 🔴 Critical Issues in Current Code

### Current Anti-Patterns Found

1. **Using `runBlocking` in Service Methods**
   - **Location**: `QuickbookServiceNetwork.kt`, `FeedServiceNetwork.kt`
   - **Problem**: Blocks threads unnecessarily, prevents proper structured concurrency
   - **Solution**: Make service methods suspend functions (supported since Ktor 3.2.0)
   - **Reference**: [KTOR-8005](https://youtrack.jetbrains.com/issue/KTOR-8005) - "Allow suspend Ktor modules"

2. **Hardcoded Session Secret**
   - **Location**: `configuration.kt` line 107: `val secretSignKey = "twister".toByteArray()`
   - **Problem**: Security vulnerability, weak encryption
   - **Solution**: Load from environment variable or secure configuration
   - **Impact**: High security risk

3. **No Retry Logic for HTTP Requests**
   - **Problem**: Network failures cause immediate errors
   - **Solution**: Use `HttpRequestRetry` plugin (available in all Ktor 2.x)
   - **Reference**: [KTOR-6770](https://youtrack.jetbrains.com/issue/KTOR-6770) - Retry improvements

---

## 🚀 Major Improvements (High Priority)

### 1. Upgrade to Ktor 3.0+ ⭐⭐⭐⭐⭐
- [ ] **Upgrade from 2.3.13 to 3.4.0 (or latest 3.x)**
  
  **Benefits from Official Release Notes**:
  - **Performance**: 90%+ I/O performance improvements via kotlinx-io migration (Ktor 3.0.0)
  - **Server-Sent Events**: Built-in SSE support for both server and client (Ktor 3.0.0-beta-1)
  - **Zstd Compression**: Support for Zstandard compression algorithm ([KTOR-7075](https://youtrack.jetbrains.com/issue/KTOR-7075), Ktor 3.4.0)
  - **Jackson 3**: Support for latest Jackson version ([KTOR-9209](https://youtrack.jetbrains.com/issue/KTOR-9209), Ktor 3.4.0)
  - **HTTP/2**: Java engines now use HTTP/2 by default ([KTOR-9097](https://youtrack.jetbrains.com/issue/KTOR-9097), Ktor 3.4.0)
  - **OkHttp 5**: Upgraded from 4.12.0 to 5.1.0 ([KTOR-8652](https://youtrack.jetbrains.com/issue/KTOR-8652), Ktor 3.3.0)
  - **Jetty 12**: Server/client upgraded to Jetty 12 ([KTOR-6734](https://youtrack.jetbrains.com/issue/KTOR-6734), Ktor 3.3.0)
  - **Kotlin 2.3**: Latest Kotlin version support ([KTOR-9242](https://youtrack.jetbrains.com/issue/KTOR-9242), Ktor 3.4.0)
  
  **Migration Effort**: Medium  
  **Breaking Changes**:
  - Application/Engine relationship changes
  - kotlinx-io migration (old I/O APIs deprecated but still work)
  - Some API renames and deprecations
  
  **Migration Guide**: https://ktor.io/docs/migrating-3.html  
  **Files to Update**: `planner/gradle/libs.versions.toml`

### 2. Replace `runBlocking` with Suspend Functions ⭐⭐⭐⭐⭐
- [ ] **Convert service methods to proper suspend functions**
  
  **Ktor 3.2.0 Feature**: [Suspendable module functions](https://ktor.io/docs/whats-new-320.html#suspendable-module-functions)
  - **Reference**: [KTOR-8005](https://youtrack.jetbrains.com/issue/KTOR-8005)
  - **Problem Statement**: "Previously, adding asynchronous functions inside Ktor modules required the `runBlocking` block that could lead to a deadlock on server creation"
  - **Solution**: Use `suspend` keyword for async operations
  
  **Benefits**:
  - No thread blocking
  - Proper structured concurrency
  - Better cancellation support
  - Concurrent module loading support (Ktor 3.2.0+)
  
  **Migration Pattern**:
  ```kotlin
  // BEFORE (Current Code)
  fun cinemas(full: Boolean): List<QuickbookCinema> = runBlocking {
      client.getCinemasAsync(full) { /* ... */ }.await()
  }
  
  // AFTER (Recommended)
  suspend fun cinemas(full: Boolean): List<QuickbookCinema> {
      return client.getCinemasAsync(full) { /* ... */ }.await()
  }
  ```
  
  **Files Affected**:
  - `QuickbookServiceNetwork.kt` (4 methods)
  - `FeedServiceNetwork.kt` (2 methods)
  - All callers of these services
  - Service interface definitions

### 3. Add Typed Configuration Deserialization ⭐⭐⭐⭐
- [ ] **Use new `.property<T>()` extension for type-safe configuration**
  
  **Ktor 3.2.0 Feature**: [Configuration file deserialization](https://ktor.io/docs/whats-new-320.html#config-deserialization)
  - **Reference**: [KTOR-7874](https://youtrack.jetbrains.com/issue/KTOR-7874)
  - **Benefit**: "Significantly reduces boilerplate when working with nested or grouped settings"
  
  **Current Code Issue**:
  ```kotlin
  // configuration.kt line 64-65
  config: Map<String, Any?> = jacksonObjectMapper()
      .readValue(App::class.java.getResourceAsStream("/default-env.json")!!)
  ```
  
  **Recommended Approach**:
  ```kotlin
  @Serializable
  data class AppConfig(
      val googleClientId: String,
      val googleClientSecret: String,
      val neo4jUrl: String
  )
  
  val config: AppConfig = application.property("app")
  ```
  
  **Benefits**:
  - Type safety at compile time
  - Automatic validation
  - Better IDE support
  - Supports YAML and HOCON
  
  **Files Affected**: `configuration.kt`, `config.kt`, YAML/HOCON config files

### 4. Add OpenAPI Generation ⭐⭐⭐⭐
- [ ] **Use Ktor 3.3.0+ experimental OpenAPI generation**
  
  **Ktor 3.3.0 Feature**: [OpenAPI specification generation](https://ktor.io/docs/whats-new-330.html#openapi-spec-gen)
  - **References**: [KTOR-8316](https://youtrack.jetbrains.com/issue/KTOR-8316), [KTOR-8859](https://youtrack.jetbrains.com/issue/KTOR-8859)
  - **New in 3.4.0**: Runtime-generated specs ([KTOR-8993](https://youtrack.jetbrains.com/issue/KTOR-8993))
  
  **Capabilities**:
  - Analyze route definitions automatically
  - Parse KDoc for API documentation
  - Infer request/response types from code
  - Generate JSON schema for Jackson/Gson
  - Read security details from Authentication plugin
  
  **Usage**:
  ```bash
  ./gradlew buildOpenApi
  ```
  
  ```kotlin
  routing {
      openAPI("/docs", swaggerFile = "openapi/generated.json")
  }
  ```
  
  **Benefits**:
  - Auto-generated API documentation
  - Always in sync with code
  - No manual OpenAPI writing
  
  **Files Affected**: Add KDoc to controllers, update build configuration

---

## 🔧 High-Value Improvements (Medium Priority)

### 5. Add HttpRequestRetry Plugin ⭐⭐⭐⭐
- [ ] **Implement automatic retry logic for transient failures**
  
  **Available Since**: Ktor 2.0+  
  **Improvements**:
  - [KTOR-5850](https://youtrack.jetbrains.com/issue/KTOR-5850) - Fixed retry count issues (3.3.2)
  - [KTOR-6770](https://youtrack.jetbrains.com/issue/KTOR-6770) - Improved exception handling (3.2.0)
  
  **Configuration**:
  ```kotlin
  install(HttpRequestRetry) {
      retryOnServerErrors(maxRetries = 3)
      exponentialDelay()
      retryIf { _, response -> !response.status.isSuccess() }
  }
  ```
  
  **Files Affected**: `SyncAppModule.kt`, `GenerateAppModule.kt`, client creation sites

### 6. Use DefaultRequest Plugin ⭐⭐⭐⭐
- [ ] **Consolidate common request configuration**
  
  **Available Since**: Ktor 2.0+  
  **Fixed Issues**:
  - [KTOR-7162](https://youtrack.jetbrains.com/issue/KTOR-7162) - Configuration applied twice (fixed in 3.4.0)
  - [KTOR-9258](https://youtrack.jetbrains.com/issue/KTOR-9258) - Headers block collision (fixed in 3.4.0)
  
  **Configuration**:
  ```kotlin
  install(DefaultRequest) {
      url("https://www.cineworld.co.uk/api/")
      header("User-Agent", "TWiStErRob Cinema")
  }
  ```
  
  **Benefit**: DRY principle for common headers, base URLs
  
  **Files Affected**: `QuickbookServiceNetwork.kt`, `FeedDownloader.kt`

### 7. Add Dependency Injection ⭐⭐⭐⭐
- [ ] **Use Ktor 3.2.0 built-in DI support**
  
  **Ktor 3.2.0 Feature**: [Dependency Injection](https://ktor.io/docs/whats-new-320.html#dependency-injection)
  - **Reference**: [KTOR-8267](https://youtrack.jetbrains.com/issue/KTOR-8267)
  
  **Benefits Over Dagger**:
  - Native Ktor integration
  - Async/suspend support
  - Configuration-based registration
  - Automatic cleanup
  - Simpler than Dagger
  
  **Configuration**:
  ```yaml
  ktor:
    application:
      dependencies:
        - com.example.RepositoriesKt.provideDatabase
        - com.example.UserRepository
  ```
  
  ```kotlin
  val service: GreetingService by dependencies
  ```
  
  **Consideration**: Evaluate vs keeping Dagger - Ktor DI is simpler but less feature-rich
  
  **Files Affected**: All Dagger-related files, module configuration

### 8. Add CallId Plugin ⭐⭐⭐
- [ ] **Install CallId for request tracing**
  
  **Available Since**: Ktor 2.0+  
  
  **Configuration**:
  ```kotlin
  install(CallId) {
      header(HttpHeaders.XRequestId)
      generate { UUID.randomUUID().toString() }
      verify { it.isNotEmpty() }
  }
  ```
  
  **Benefits**:
  - Request correlation across services
  - Better log analysis
  - Easier debugging in production
  
  **Files Affected**: `configuration.kt`

### 9. Add DoubleReceive Plugin ⭐⭐⭐
- [ ] **Fix request body logging issues**
  
  **Available Since**: Ktor 2.0+  
  **Related Issue**: Already mentioned in code comments (`ServerLogging.kt` line 118, `FeedDownloader.kt`)
  
  **Configuration**:
  ```kotlin
  install(DoubleReceive) {
      cacheRawRequest = true
  }
  ```
  
  **Fixes**:
  - RequestAlreadyConsumedException
  - Request body logging
  
  **Files Affected**: `configuration.kt`, `ServerLogging.kt`

### 10. Enable HTTP/2 by Default ⭐⭐⭐
- [ ] **Enable HTTP/2 support (now default in Ktor 3.4.0)**
  
  **Ktor 3.4.0 Feature**: Java engines use HTTP/2 by default
  - **Reference**: [KTOR-9097](https://youtrack.jetbrains.com/issue/KTOR-9097)
  
  **For Netty (server)**:
  - **Ktor 3.3.0**: HTTP/2 cleartext (h2c) support ([KTOR-4750](https://youtrack.jetbrains.com/issue/KTOR-4750))
  
  **Configuration**:
  ```kotlin
  // For h2c (HTTP/2 without TLS)
  connector {
      enableH2c = true
  }
  ```
  
  **Benefits**:
  - Multiplexing
  - Header compression
  - Reduced latency
  
  **Files Affected**: Engine configuration in `configuration.kt`

### 11. Fix SaveBodyPlugin Deprecation ⭐⭐⭐
- [ ] **Update code to not use deprecated SaveBodyPlugin**
  
  **Deprecated**: Ktor 3.2.0+  
  **Reference**: [KTOR-8367](https://youtrack.jetbrains.com/issue/KTOR-8367)
  
  **Migration**:
  ```kotlin
  // OLD (deprecated)
  client.get("/file") { skipSavingBody() }
  
  // NEW (recommended)
  client.prepareGet("/file").execute { response ->
      saveFile(response.bodyAsChannel())
  }
  ```
  
  **Files Affected**: Client code if using `skipSavingBody()`

### 12. Use `.replaceResponse()` Instead of `.wrapWithContent()` ⭐⭐⭐
- [ ] **Update to new response wrapping API**
  
  **Deprecated**: Ktor 3.2.0+  
  **Reference**: Ktor 3.2.0 release notes
  
  **Problem**: Old APIs could only be read once, breaking plugin compatibility
  
  **Migration**:
  ```kotlin
  // OLD (deprecated)
  call.wrapWithContent(decodedBody).response
  
  // NEW
  call.replaceResponse { decode(response.rawContent) }
  ```
  
  **Files Affected**: Any custom plugins or response transformation

---

## 🎯 Medium-Value Improvements (Lower Priority)

### 13. Add Static Content Improvements ⭐⭐⭐
- [ ] **Use Ktor 3.3.0 static content enhancements**
  
  **Ktor 3.3.0 Features**:
  - Custom fallback mechanism ([KTOR-8496](https://youtrack.jetbrains.com/issue/KTOR-8496))
  - ETag based on SHA-256 ([KTOR-6700](https://youtrack.jetbrains.com/issue/KTOR-6700))
  - LastModified and ETag headers
  
  **Usage**:
  ```kotlin
  staticFiles("/") {
      // Custom fallback when file not found
      fallback { path, call ->
          if (path.endsWith(".html")) {
              call.respondText("Custom 404")
          }
      }
      
      // Strong ETags with SHA-256
      etag { file -> strongEtag(sha256(file)) }
  }
  ```
  
  **Files Affected**: `AppController.kt` (currently uses basic staticFiles)

### 14. Consider Replacing Jackson with kotlinx-serialization ⭐⭐⭐
- [ ] **Evaluate migrating to kotlinx-serialization**
  
  **Considerations**:
  - ✅ Native Kotlin support
  - ✅ Compile-time safety
  - ✅ Better performance
  - ✅ Ktor 3.4.0 supports Jackson 3 ([KTOR-9209](https://youtrack.jetbrains.com/issue/KTOR-9209))
  - ❌ Need to migrate custom serializers (e.g., `OffsetDateTimeJsonSerializer`)
  - ❌ XML parsing still needs Jackson (feed module)
  
  **Decision**: Can upgrade to Jackson 3 with Ktor 3.4.0, or evaluate migration later
  
  **Files Affected**: All content negotiation, data classes

### 15. Add HTTP Cache Clearing ⭐⭐
- [ ] **Use new cache management APIs**
  
  **Ktor 3.2.0 Feature**: [HTTP cache clearing](https://ktor.io/docs/whats-new-320.html#cache-clearing)
  - **Reference**: [KTOR-6653](https://youtrack.jetbrains.com/issue/KTOR-6653)
  
  **New APIs**:
  ```kotlin
  cacheStorage.removeAll(url)  // Remove all for URL
  cacheStorage.remove(url, varyKeys)  // Remove specific
  ```
  
  **Benefit**: Better cache invalidation control

### 16. Add Compression Support to Client ⭐⭐
- [ ] **Install ContentEncoding plugin**
  
  **Available Since**: Ktor 2.0+  
  **Ktor 3.4.0**: Added Zstd support ([KTOR-7075](https://youtrack.jetbrains.com/issue/KTOR-7075))
  
  **Configuration**:
  ```kotlin
  install(ContentEncoding) {
      gzip()
      deflate()
      zstd()  // New in 3.4.0
  }
  ```
  
  **Benefits**:
  - Reduced bandwidth
  - Faster transfers
  - Cost savings

### 17. Add Security Headers ⭐⭐
- [ ] **Install HSTS and security headers**
  
  **Available Since**: Ktor 2.0+  
  
  **Configuration**:
  ```kotlin
  install(HSTS) {
      includeSubDomains = true
      maxAgeInSeconds = 31536000
  }
  ```
  
  **Files Affected**: `configuration.kt`

### 18. Fix Session Security ⭐⭐⭐⭐⭐
- [ ] **Use secure session secret from environment**
  
  **Current Issue**: Hardcoded "twister" key (line 107 in `configuration.kt`)
  
  **Solution**:
  ```kotlin
  val secretSignKey = environment.config
      .propertyOrNull("ktor.security.sessionSecret")
      ?.getString()
      ?.toByteArray()
      ?: error("Session secret not configured")
  ```
  
  **Critical Security Fix**

### 19. Add MicrometerMetrics ⭐⭐
- [ ] **Add monitoring with Micrometer**
  
  **Available Since**: Ktor 2.0+  
  **Improvements**:
  - [KTOR-8276](https://youtrack.jetbrains.com/issue/KTOR-8276) - Fixed OOM issue (3.1.3)
  - [KTOR-7274](https://youtrack.jetbrains.com/issue/KTOR-7274) - Route label improvements (3.2.0)
  - [KTOR-8183](https://youtrack.jetbrains.com/issue/KTOR-8183) - Configurable route labels (3.2.0)
  
  **Files Affected**: `configuration.kt`, add dependencies

### 20. Review OAuth Security ⭐⭐
- [ ] **Update OAuth implementation**
  
  **Improvements in Recent Versions**:
  - [KTOR-2404](https://youtrack.jetbrains.com/issue/KTOR-2404) - Better 401 responses (3.4.0)
  - [KTOR-4420](https://youtrack.jetbrains.com/issue/KTOR-4420) - Fixed form-urlencoded POST (3.2.1)
  - Bearer token caching issues fixed in multiple releases
  
  **Files Affected**: `configuration.kt`, `AuthController.kt`

### 21. Add Bearer Auth Token Control ⭐⭐
- [ ] **Use new token control APIs**
  
  **Ktor 3.4.0 Feature**: Better token control
  - **Reference**: [KTOR-8180](https://youtrack.jetbrains.com/issue/KTOR-8180)
  - **Related**: [KTOR-4946](https://youtrack.jetbrains.com/issue/KTOR-4946), [KTOR-4759](https://youtrack.jetbrains.com/issue/KTOR-4759)
  
  **Benefit**: Better token refresh and invalidation control

### 22. Add Auth API Key Plugin ⭐⭐
- [ ] **Use new API key authentication plugin**
  
  **Ktor 3.4.0 Feature**: API key auth support
  - **Reference**: [KTOR-9162](https://youtrack.jetbrains.com/issue/KTOR-9162)
  
  **Benefit**: First-party API key auth (currently using custom solution with quickbook API key)

---

## 🧪 Testing Improvements

### 23. Use TestApplication Improvements ⭐⭐⭐
- [ ] **Use new testing features**
  
  **Ktor 3.2.0 Features**:
  - Configurable `client` property ([KTOR-8465](https://youtrack.jetbrains.com/issue/KTOR-8465))
  - Access to `Application` instance ([KTOR-8215](https://youtrack.jetbrains.com/issue/KTOR-8215))
  - Create `ApplicationCall` for testing ([KTOR-7607](https://youtrack.jetbrains.com/issue/KTOR-7607))
  
  **Example**:
  ```kotlin
  testApplication {
      // Configure reusable client
      client = createClient {
          install(ContentNegotiation) { json() }
      }
      
      // Access application instance
      val app: Application = application
      assertTrue(app.pluginOrNull(CORS) != null)
  }
  ```
  
  **Files Affected**: Test files using `testApplication`

### 24. Fix SSE Testing ⭐⭐
- [ ] **Use improved SSE test support**
  
  **Fixed in 3.2.2**: SSE acts as stream in test environment
  - **Reference**: [KTOR-7910](https://youtrack.jetbrains.com/issue/KTOR-7910)
  
  **Files Affected**: Any SSE-related tests

---

## 📦 Bug Fixes Available in Newer Versions

### Critical Bug Fixes

1. **[KTOR-4828](https://youtrack.jetbrains.com/issue/KTOR-4828)** - NumberFormatException with null bytes in Content-Length (Fixed in 3.3.1)
2. **[KTOR-8523](https://youtrack.jetbrains.com/issue/KTOR-8523)** - Multipart parsing race condition (Fixed in 3.2.3)
3. **[KTOR-8682](https://youtrack.jetbrains.com/issue/KTOR-8682)** - Infinite loop in ByteReadChannel.readFully (Fixed in 3.2.3)
4. **[KTOR-8770](https://youtrack.jetbrains.com/issue/KTOR-8770)** - Server shutdown blocking twice as long (Fixed in 3.3.1)
5. **[KTOR-6790](https://youtrack.jetbrains.com/issue/KTOR-6790)** - OkHttp MultiPart with onUpload (Fixed in 3.2.1)
6. **[KTOR-8916](https://youtrack.jetbrains.com/issue/KTOR-8916)** - Android VerifyError with Netty (Fixed in 3.3.2)

---

## 🎯 Implementation Priority

### Phase 1: Critical & High Priority (Do First)
1. ✅ **Fix session security** (#18) - Critical security issue
2. ✅ **Upgrade to Ktor 3.4.0** (#1) - Unlocks all other improvements
3. ✅ **Remove runBlocking** (#2) - Better concurrency
4. ✅ **Add HttpRequestRetry** (#5) - Better reliability
5. ✅ **Add DoubleReceive** (#9) - Fix existing logging issues

### Phase 2: High Value (Do Next)
6. **Add typed configuration** (#3) - Better type safety
7. **Add DefaultRequest** (#6) - Cleaner code
8. **Add CallId** (#8) - Better debugging
9. **Enable HTTP/2** (#10) - Performance
10. **Fix deprecated APIs** (#11, #12) - Future-proofing

### Phase 3: Nice to Have (Do Later)
11. **OpenAPI generation** (#4) - Better documentation
12. **Dependency Injection** (#7) - Evaluate vs Dagger
13. **Static content improvements** (#13)
14. **Testing improvements** (#23, #24)
15. All remaining improvements

---

## 📚 References

### Official Documentation
- **Migration Guide**: https://ktor.io/docs/migrating-3.html
- **Ktor 3.3.0 Release**: https://ktor.io/docs/whats-new-330.html
- **Ktor 3.2.0 Release**: https://ktor.io/docs/whats-new-320.html
- **Release Notes**: https://github.com/ktorio/ktor/releases
- **Changelog**: https://github.com/ktorio/ktor/blob/main/CHANGELOG.md

### Key YouTrack Issues Referenced
All issues listed are from https://youtrack.jetbrains.com/issues/KTOR

---

## 📝 Notes

- All recommendations are based on official Ktor release notes and GitHub releases
- Issue numbers (KTOR-XXXX) are directly from Ktor's YouTrack
- Version numbers indicate when features were introduced or bugs were fixed
- This project is on 2.3.13, missing 1+ years of improvements from 3.x line
- Breaking changes in 3.0 are well-documented and mostly affect low-level I/O

---

## ✅ Quick Wins (Can Do Immediately)

Even before upgrading, you can:
1. Fix session security (load from env)
2. Add HttpRequestRetry plugin (available in 2.3.13)
3. Add DefaultRequest plugin (available in 2.3.13)
4. Add CallId plugin (available in 2.3.13)
5. Add DoubleReceive plugin (available in 2.3.13)
6. Add compression to client (available in 2.3.13)

These don't require version upgrade!
