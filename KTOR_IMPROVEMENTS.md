# Ktor Improvements - Now on 3.4.0!

## Overview
You've successfully upgraded to **Ktor 3.4.0** 🎉! This document contains evidence-based recommendations for improvements you can now make, derived from official release notes from the [Ktor GitHub repository](https://github.com/ktorio/ktor/releases).

**Current Version**: Ktor 3.4.0 (Released: January 23, 2026)  
**Status**: ✅ On latest stable version

---

## 🔴 Critical Issues to Fix Immediately

### 1. Security: Hardcoded Session Secret ⚠️ CRITICAL
- **Location**: `configuration.kt` line 119
- **Problem**: Security vulnerability - anyone can forge session tokens
- **Impact**: **CRITICAL** - Users can impersonate other users
- **Solution**: Load from environment variable
- **Fix**:
  ```kotlin
  val secretSignKey = environment.config
      .propertyOrNull("ktor.security.sessionSecret")
      ?.getString()
      ?.toByteArray()
      ?: error("Session secret must be configured")
  ```

### 2. Deprecated: reflectionSessionSerializer()
- **Location**: `configuration.kt` line 118
- **Problem**: Deprecated, Ktor 3.0+ uses kotlinx-serialization
- **Fix**: Remove the line, add `@Serializable` to `AuthSession`
- **Reference**: Ktor 3.0 migration

### 3. Anti-Pattern: runBlocking in Services
- **Location**: `QuickbookServiceNetwork.kt`, `FeedServiceNetwork.kt`
- **Problem**: Blocks threads, prevents structured concurrency
- **Reference**: [KTOR-8005](https://youtrack.jetbrains.com/issue/KTOR-8005)
- **Fix**: Convert to suspend functions

---

## 🚀 Quick Wins (High Priority, Low Effort)

### 4. Add HttpRequestRetry Plugin
- **Effort**: 5 minutes
- **Benefit**: Automatic retries on failures
- **Fixed in 3.4.0**: [KTOR-9129](https://youtrack.jetbrains.com/issue/KTOR-9129)
- **Where**: HttpClient in `SyncAppModule.kt`

### 5. Add DefaultRequest Plugin
- **Effort**: 5 minutes
- **Benefit**: DRY for base URLs
- **Fixed in 3.4.0**: [KTOR-7162](https://youtrack.jetbrains.com/issue/KTOR-7162), [KTOR-9258](https://youtrack.jetbrains.com/issue/KTOR-9258)
- **Where**: `QuickbookServiceNetwork.kt`

### 6. Add CallId Plugin
- **Effort**: 5 minutes
- **Benefit**: Request tracing
- **Where**: `configuration.kt`

### 7. Add DoubleReceive Plugin
- **Effort**: 5 minutes
- **Benefit**: Fixes logging (already in TODO comments!)
- **Where**: `configuration.kt`

### 8. Add Client Compression
- **Effort**: 2 minutes
- **New in 3.4.0**: Zstd support ([KTOR-7075](https://youtrack.jetbrains.com/issue/KTOR-7075))
- **Where**: HttpClient creation

---

## 🎯 New Features in 3.4.0

### 9. OpenAPI Generation ⭐
- **Reference**: [KTOR-8316](https://youtrack.jetbrains.com/issue/KTOR-8316), [KTOR-8993](https://youtrack.jetbrains.com/issue/KTOR-8993)
- **Benefit**: Auto-generated API docs from code
- **Command**: `./gradlew buildOpenApi`

### 10. API Key Auth Plugin ⭐
- **Reference**: [KTOR-9162](https://youtrack.jetbrains.com/issue/KTOR-9162)
- **Use Case**: Replace custom quickbook API key handling

### 11. Typed Configuration
- **Ktor 3.2.0**: `.property<T>()` extension
- **Reference**: [KTOR-7874](https://youtrack.jetbrains.com/issue/KTOR-7874)
- **Benefit**: Type-safe config loading

### 12. Bearer Token Control ⭐
- **Reference**: [KTOR-8180](https://youtrack.jetbrains.com/issue/KTOR-8180)
- **Fixes**: Token caching issues
- **Relevant**: OAuth implementation

---

## 🔧 Medium Priority

### 13. HTTP/2 (Now Default!)
- **Reference**: [KTOR-9097](https://youtrack.jetbrains.com/issue/KTOR-9097)
- **Status**: Already enabled with OkHttp 5

### 14. Static Content Enhancements
- **Ktor 3.3.0**: Custom fallback, SHA-256 ETags
- **References**: [KTOR-8496](https://youtrack.jetbrains.com/issue/KTOR-8496), [KTOR-6700](https://youtrack.jetbrains.com/issue/KTOR-6700)

### 15. Security Headers (HSTS)
- **Benefit**: Better security posture
- **Effort**: Low

### 16. MicrometerMetrics
- **Benefit**: Production monitoring
- **Improvements**: [KTOR-8276](https://youtrack.jetbrains.com/issue/KTOR-8276), [KTOR-7274](https://youtrack.jetbrains.com/issue/KTOR-7274)

### 17. Jackson 3 Support ⭐
- **Reference**: [KTOR-9209](https://youtrack.jetbrains.com/issue/KTOR-9209)
- **Alternative**: Consider kotlinx-serialization

### 18. Ktor DI
- **Ktor 3.2.0**: Native dependency injection
- **Reference**: [KTOR-8267](https://youtrack.jetbrains.com/issue/KTOR-8267)
- **Consideration**: Evaluate vs keeping Dagger

---

## 🧪 Testing

### 19. TestApplication Improvements
- **Ktor 3.2.0**: Configurable client, access to Application
- **References**: [KTOR-8465](https://youtrack.jetbrains.com/issue/KTOR-8465), [KTOR-8215](https://youtrack.jetbrains.com/issue/KTOR-8215)

### 20. SSE Testing Fixed
- **Reference**: [KTOR-7910](https://youtrack.jetbrains.com/issue/KTOR-7910)

---

## 📦 Additional Features

### 21. respondResource Helper ⭐
- **Reference**: [KTOR-8927](https://youtrack.jetbrains.com/issue/KTOR-8927)

### 22. Partial HTML Response ⭐
- **Reference**: [KTOR-8195](https://youtrack.jetbrains.com/issue/KTOR-8195)

### 23. Trust Store Config ⭐
- **Reference**: [KTOR-8985](https://youtrack.jetbrains.com/issue/KTOR-8985)

### 24. HTTP Cache Clearing
- **Ktor 3.2.0**: `removeAll()`, `remove()` APIs
- **Reference**: [KTOR-6653](https://youtrack.jetbrains.com/issue/KTOR-6653)

---

## 🎯 Implementation Priority

### Phase 1: This Week (1-2 hours)
1. ⚠️ Fix session secret (Item 1)
2. ✅ Remove reflectionSessionSerializer (Item 2)
3. ✅ Add HttpRequestRetry (Item 4)
4. ✅ Add DefaultRequest (Item 5)
5. ✅ Add CallId (Item 6)
6. ✅ Add DoubleReceive (Item 7)
7. ✅ Add compression (Item 8)

### Phase 2: Next Sprint (1-2 days)
8. Remove runBlocking (Item 3)
9. OpenAPI generation (Item 9)
10. Typed configuration (Item 11)
11. Static content (Item 14)

### Phase 3: Backlog
12-24. All remaining items

---

## 📚 References

- **Release Notes**: https://github.com/ktorio/ktor/releases/tag/3.4.0
- **Ktor 3.3.0**: https://ktor.io/docs/whats-new-330.html
- **Ktor 3.2.0**: https://ktor.io/docs/whats-new-320.html
- **All YouTrack Issues**: https://youtrack.jetbrains.com/issues/KTOR

---

## ✅ Summary

**Status**: Ktor 3.4.0 ✅  
**Critical**: 3 issues to fix  
**Quick Wins**: 5 plugins (< 30 min)  
**New Features**: OpenAPI, API Key, Bearer tokens, Jackson 3
