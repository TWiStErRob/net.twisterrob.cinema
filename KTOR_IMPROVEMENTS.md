# Ktor Improvements Recommendations

## Overview
This document contains a comprehensive list of proposed improvements for the Ktor usage in this project. The project currently uses **Ktor 2.3.13** and could benefit from various optimizations, new features, and best practices.

## Current State Analysis

### Dependencies
- **Ktor Version**: 2.3.13
- **Server Engine**: Netty
- **Client Engine**: OkHttp
- **Serialization**: Jackson (for both server and client)
- **Key Server Plugins**: Authentication, Sessions, ContentNegotiation, CallLogging, CachingHeaders, Compression, DefaultHeaders, StatusPages, Resources
- **Key Client Plugins**: ContentNegotiation, Logging

### Architecture
- Multiple backend modules using Ktor client (quickbook, feed, network)
- Custom server logging plugin (`ServerLogging`)
- Custom header logging feature (`HeaderLoggingFeature`)
- OAuth 2.0 authentication with Google
- Type-safe routing with Resources plugin
- Uses `runBlocking` in several client service implementations

---

## Proposed Improvements

### 🚀 Major Improvements

#### 1. Upgrade to Ktor 3.0+
- [ ] **Upgrade Ktor from 2.3.13 to 3.0.x (or latest 3.x)**
  - **Benefits**: 
    - 90%+ performance improvements in I/O operations (kotlinx-io migration)
    - Server-Sent Events (SSE) support
    - Better structured concurrency
    - Improved stability and bug fixes
  - **Migration effort**: Medium
  - **Breaking changes**: 
    - `ApplicationEngine` relationship changes
    - Some deprecated APIs need updating
    - I/O classes (`Input`, `Output`, `ByteReadChannel`, `ByteWriteChannel`) migrated to kotlinx-io
  - **References**: 
    - https://ktor.io/docs/migrating-3.html
    - https://blog.jetbrains.com/kotlin/2024/10/ktor-3-0/
  - **Files to update**: `planner/gradle/libs.versions.toml`, potentially all Ktor-using files

#### 2. Replace Jackson with kotlinx-serialization
- [ ] **Migrate from Jackson to kotlinx-serialization for JSON content negotiation**
  - **Benefits**:
    - Native Kotlin support with first-class null safety
    - Compile-time safety (no reflection)
    - Better performance (faster startup, lower memory usage)
    - Multiplatform ready (if needed in future)
    - Better integration with Kotlin idioms (data classes, default parameters)
    - Smaller runtime footprint
  - **Migration effort**: Medium-High
  - **Considerations**: 
    - Need to migrate custom serializers (e.g., `OffsetDateTimeJsonSerializer`)
    - XML parsing still needs Jackson (for feed module)
    - Some Jackson-specific features may need alternatives
  - **References**: 
    - https://ktor.io/docs/server-serialization.html
    - https://kotlinlang.org/docs/serialization.html
  - **Files affected**: 
    - `configuration.kt`, `QuickbookServiceNetwork.kt`, `FeedServiceNetwork.kt`
    - All data classes need `@Serializable` annotation

#### 3. Remove `runBlocking` and use proper suspend functions
- [ ] **Refactor service methods to be proper suspend functions instead of using `runBlocking`**
  - **Benefits**:
    - Better structured concurrency
    - Proper cancellation support
    - More efficient resource usage
    - No thread blocking
    - Better testability
  - **Migration effort**: Medium
  - **Changes needed**:
    - Make service interface methods suspend
    - Update all callers to use coroutine context
    - Update tests to use proper coroutine test utilities
  - **References**: 
    - https://ktor.io/docs/server-http-request-lifecycle.html
    - https://kotlinlang.org/docs/coroutines-guide.html
  - **Files affected**:
    - `QuickbookServiceNetwork.kt`
    - `FeedServiceNetwork.kt`
    - All service implementations and callers

---

### 🔧 Medium Improvements

#### 4. Add HttpRequestRetry plugin for resilience
- [ ] **Add retry logic to HTTP clients for better resilience**
  - **Benefits**:
    - Automatic retries on transient failures
    - Exponential backoff support
    - Better handling of network issues
    - Configurable retry strategies
  - **Migration effort**: Low
  - **Configuration example**:
    ```kotlin
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        exponentialDelay()
    }
    ```
  - **References**: https://ktor.io/docs/client-request-retry.html
  - **Files affected**: `SyncAppModule.kt`, `GenerateAppModule.kt`, `App.kt` (where HttpClient is created)

#### 5. Use DefaultRequest plugin for base URLs
- [ ] **Consolidate common request configuration using DefaultRequest plugin**
  - **Benefits**:
    - DRY principle for common headers/parameters
    - Centralized base URL configuration
    - Cleaner request code
  - **Migration effort**: Low
  - **Configuration example**:
    ```kotlin
    install(DefaultRequest) {
        url("https://www.cineworld.co.uk/api/")
        header("User-Agent", "TWiStErRob Cinema App")
    }
    ```
  - **References**: https://ktor.io/docs/client-default-request.html
  - **Files affected**: `QuickbookServiceNetwork.kt`, `FeedDownloader.kt`

#### 6. Add CallId plugin for request tracing
- [ ] **Install CallId plugin for better request tracing and debugging**
  - **Benefits**:
    - Unique request ID for correlation
    - Better log analysis
    - Easier debugging in production
    - Can propagate to downstream services
  - **Migration effort**: Low
  - **Configuration example**:
    ```kotlin
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { UUID.randomUUID().toString() }
        verify { it.isNotEmpty() }
    }
    ```
  - **References**: https://ktor.io/docs/server-call-id.html
  - **Files affected**: `configuration.kt`

#### 7. Add DoubleReceive plugin for request logging
- [ ] **Install DoubleReceive plugin to fix request body logging**
  - **Benefits**:
    - Proper request body logging
    - No "RequestAlreadyConsumedException" errors
    - Better debugging capabilities
  - **Migration effort**: Low
  - **Note**: Already referenced in TODO comment in `ServerLogging.kt`
  - **Configuration example**:
    ```kotlin
    install(DoubleReceive) {
        cacheRawRequest = true
    }
    ```
  - **References**: https://ktor.io/docs/server-double-receive.html
  - **Files affected**: `configuration.kt`, `ServerLogging.kt`, `FeedDownloader.kt`

#### 8. Consider migrating to Ktor's built-in plugins
- [ ] **Evaluate migrating custom `ServerLogging` to CallLogging with custom formatting**
  - **Benefits**:
    - Less custom code to maintain
    - Better integration with Ktor ecosystem
    - Standard patterns
  - **Migration effort**: Low-Medium
  - **Note**: Current CallLogging is already installed, but custom ServerLogging could be merged
  - **Files affected**: `ServerLogging.kt`, `configuration.kt`

#### 9. Add client-side Resources plugin
- [ ] **Use Resources plugin for type-safe client requests**
  - **Benefits**:
    - Type-safe URLs on client side
    - Shared resource definitions between client/server
    - Compile-time safety for API contracts
    - Auto-completion in IDE
  - **Migration effort**: Medium
  - **References**: https://ktor.io/docs/client-resources.html
  - **Files affected**: `QuickbookServiceNetwork.kt`, client request code

---

### 🎯 Minor Improvements / Optimizations

#### 10. Enable HTTP/2 support
- [ ] **Enable HTTP/2 support in Netty engine**
  - **Benefits**:
    - Better performance (multiplexing, header compression)
    - Reduced latency
    - Modern protocol support
  - **Migration effort**: Low
  - **Configuration**: Add to engine configuration in `build.gradle.kts` or programmatically
  - **References**: https://ktor.io/docs/server-engines.html#http2
  - **Files affected**: `configuration.kt` or engine configuration

#### 11. Configure client connection pooling
- [ ] **Explicitly configure OkHttp connection pooling for better resource management**
  - **Benefits**:
    - Better control over connections
    - Potential performance improvements
    - Explicit resource limits
  - **Migration effort**: Low
  - **Configuration**: Configure OkHttp engine settings
  - **Files affected**: HttpClient creation sites

#### 12. Add request/response validation
- [ ] **Consider adding request validation plugin for API endpoints**
  - **Benefits**:
    - Automatic validation
    - Better error messages
    - API contract enforcement
  - **Migration effort**: Medium
  - **Note**: Could use kotlinx-serialization validation or custom validation
  - **Files affected**: Controller classes, data classes

#### 13. Optimize caching configuration
- [ ] **Review and optimize CachingHeaders configuration**
  - **Benefits**:
    - Better cache control
    - Reduced bandwidth
    - Improved performance
  - **Migration effort**: Low
  - **Current state**: Only default configuration in `configuration.kt`
  - **Files affected**: `configuration.kt`, `caching.kt`

#### 14. Add request throttling/rate limiting
- [ ] **Add rate limiting for external API calls**
  - **Benefits**:
    - Avoid API quota exhaustion
    - Better resilience
    - Predictable resource usage
  - **Migration effort**: Medium
  - **Note**: Could use custom plugin or third-party solution
  - **Files affected**: Client creation in modules

#### 15. Improve error handling with StatusPages
- [ ] **Enhance StatusPages configuration with more specific error handlers**
  - **Benefits**:
    - Better error responses
    - Consistent error format
    - Better client experience
  - **Migration effort**: Low
  - **Current state**: Only catches Throwable
  - **Files affected**: `configuration.kt`

#### 16. Add metrics/monitoring support
- [ ] **Add MicrometerMetrics plugin for application monitoring**
  - **Benefits**:
    - Production monitoring
    - Performance insights
    - Resource usage tracking
  - **Migration effort**: Medium
  - **References**: https://ktor.io/docs/server-metrics-micrometer.html
  - **Files affected**: `configuration.kt`, dependencies in `build.gradle.kts`

#### 17. Add CORS configuration
- [ ] **Explicitly configure CORS if needed for frontend**
  - **Benefits**:
    - Secure cross-origin access
    - Clear API boundaries
    - Better security posture
  - **Migration effort**: Low
  - **References**: https://ktor.io/docs/server-cors.html
  - **Files affected**: `configuration.kt`

#### 18. Optimize OAuth configuration
- [ ] **Review OAuth implementation for security best practices**
  - **Benefits**:
    - Enhanced security
    - Better token management
    - Updated OAuth patterns
  - **Migration effort**: Low-Medium
  - **Current state**: Using OAuth with Google, session-based
  - **Files affected**: `configuration.kt`, `AuthController.kt`

#### 19. Review and update deprecated API usage
- [ ] **Search for and update any deprecated Ktor APIs**
  - **Benefits**:
    - Future-proof code
    - Better performance
    - Use of latest features
  - **Migration effort**: Low
  - **Note**: Especially relevant after upgrading to Ktor 3.0
  - **Files affected**: All Ktor-using files

#### 20. Add compression configuration for client
- [ ] **Add compression support to HTTP clients**
  - **Benefits**:
    - Reduced bandwidth usage
    - Faster transfers
    - Lower costs (especially on mobile)
  - **Migration effort**: Low
  - **Configuration**: Install ContentEncoding plugin
  - **References**: https://ktor.io/docs/client-content-encoding.html
  - **Files affected**: Client creation sites

---

### 🧪 Testing & Quality

#### 21. Improve test infrastructure
- [ ] **Use Ktor's TestApplication for more realistic integration tests**
  - **Benefits**:
    - Better test coverage
    - More realistic tests
    - Easier to write tests
  - **Migration effort**: Low-Medium
  - **Current state**: Uses test helpers but could be enhanced
  - **Files affected**: Test files in `integrationTest` source sets

#### 22. Add client testing with MockEngine improvements
- [ ] **Enhance MockEngine usage with better stubs and verification**
  - **Benefits**:
    - Better client tests
    - Easier mocking
    - More reliable tests
  - **Migration effort**: Low
  - **Current state**: Already has some mock support in `Ktor.kt`
  - **Files affected**: `Ktor.kt`, test files

---

### 🔐 Security

#### 23. Review session security
- [ ] **Enhance session security with stronger encryption**
  - **Benefits**:
    - Better security
    - Protection against tampering
    - Compliance with best practices
  - **Migration effort**: Low
  - **Current issue**: Uses "twister" as secret key (hardcoded)
  - **Files affected**: `configuration.kt`

#### 24. Add security headers plugin
- [ ] **Install HSTS, XSS protection, and other security headers**
  - **Benefits**:
    - Better security posture
    - Protection against common attacks
    - Security best practices
  - **Migration effort**: Low
  - **References**: https://ktor.io/docs/server-hsts.html
  - **Files affected**: `configuration.kt`

---

### 📦 Dependency Management

#### 25. Review and update all Ktor dependencies
- [ ] **Ensure all Ktor artifacts are on the same version**
  - **Benefits**:
    - Consistency
    - Avoid version conflicts
    - Easier upgrades
  - **Migration effort**: Low
  - **Current state**: All using same version ref in TOML
  - **Files affected**: `libs.versions.toml`

#### 26. Consider removing unused dependencies
- [ ] **Audit Ktor-related dependencies for unused artifacts**
  - **Benefits**:
    - Smaller artifact size
    - Fewer vulnerabilities
    - Cleaner dependency tree
  - **Migration effort**: Low
  - **Files affected**: `build.gradle.kts` files

---

## Implementation Priority Recommendations

### High Priority (Highest Impact)
1. ✅ Upgrade to Ktor 3.0+ (#1)
2. ✅ Remove `runBlocking` and use proper suspend functions (#3)
3. ✅ Add HttpRequestRetry plugin (#4)
4. ✅ Fix session security (#23)

### Medium Priority (Good Impact/Effort Ratio)
5. Replace Jackson with kotlinx-serialization (#2)
6. Add CallId plugin (#6)
7. Add DoubleReceive plugin (#7)
8. Use DefaultRequest plugin (#5)
9. Add security headers (#24)

### Low Priority (Nice to Have)
10. All remaining improvements as time/need permits

---

## Testing Strategy

For each improvement:
1. Update code
2. Run existing tests
3. Add new tests if needed
4. Test manually if applicable
5. Review for security implications
6. Update documentation

---

## Notes

- Some improvements are dependent on others (e.g., #2 should be done after #1)
- Breaking changes should be carefully tested
- Consider feature flags for gradual rollout
- Monitor performance before and after changes
- Keep backward compatibility where possible

---

## References

- [Ktor Official Documentation](https://ktor.io/docs/)
- [Ktor 3.0 Migration Guide](https://ktor.io/docs/migrating-3.html)
- [Ktor Changelog](https://ktor.io/changelog/)
- [Ktor GitHub Repository](https://github.com/ktorio/ktor)
- [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html)
