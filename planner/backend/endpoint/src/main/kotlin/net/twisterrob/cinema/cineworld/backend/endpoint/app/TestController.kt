commit 38ac73d60727dc3f56300b670a02da1f88491283
Author: copilot-swe-agent[bot] <198982749+Copilot@users.noreply.github.com>
Date:   Sun Feb 15 13:02:47 2026 +0000

    fixup! Replace deprecated ApplicationCallPipeline.Call with plugin in TestController

diff --git a/planner/backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/app/TestController.kt b/planner/backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/app/TestController.kt
index 308b6a7..c880312 100644
--- a/planner/backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/app/TestController.kt
+++ b/planner/backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/endpoint/app/TestController.kt
@@ -3,8 +3,6 @@ package net.twisterrob.cinema.cineworld.backend.endpoint.app
 import io.ktor.http.HttpHeaders
 import io.ktor.http.encodeURLPath
 import io.ktor.server.application.Application
-import io.ktor.server.application.ApplicationCallPipeline
-import io.ktor.server.application.BaseApplicationPlugin
 import io.ktor.server.application.call
 import io.ktor.server.application.createApplicationPlugin
 import io.ktor.server.application.install
@@ -14,7 +12,6 @@ import io.ktor.server.request.uri
 import io.ktor.server.response.header
 import io.ktor.server.response.respondFile
 import io.ktor.server.routing.Routing
-import io.ktor.util.AttributeKey
 import net.twisterrob.cinema.cineworld.backend.ktor.Env
 import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
 import net.twisterrob.cinema.cineworld.backend.ktor.environment
@@ -28,34 +25,17 @@ class TestController @Inject constructor(
 
 	override val order: Int get() = Int.MIN_VALUE
 
-	@Suppress("LabeledExpression") // https://github.com/detekt/detekt/issues/5132
-	override fun Routing.registerRoutes() {
-		if (application.environment.config.environment == Env.PRODUCTION) return
-
-		val root = application.environment.config.fakeRootFolder
-		application.log.debug(
-			"""
-				Running fake content from ${root}
-				  -> ${root.absolutePath}
-				  -> ${root.canonicalPath}
-			""".trimIndent()
-		)
-		
-		application.install(FakeContentPlugin) {
-			this.root = root
-		}
-	}
-	
 	private class FakeContentConfiguration {
 		var root: File = File(".")
 	}
-	
-	private val FakeContentPlugin = createApplicationPlugin(
+
+	@Suppress("VariableNaming") // Plugin name conventionally uses PascalCase
+	private val fakeContentPlugin = createApplicationPlugin(
 		name = "FakeContentPlugin",
 		createConfiguration = ::FakeContentConfiguration
 	) {
 		val root = pluginConfig.root
-		
+
 		onCall { call ->
 			val fullPathAndQuery = call.request.uri.ending
 			val fakeFullPathAndQueryFile = root.resolve(fullPathAndQuery)
@@ -78,6 +58,24 @@ class TestController @Inject constructor(
 			// no fake found, proceed normally
 		}
 	}
+
+	@Suppress("LabeledExpression") // https://github.com/detekt/detekt/issues/5132
+	override fun Routing.registerRoutes() {
+		if (application.environment.config.environment == Env.PRODUCTION) return
+
+		val root = application.environment.config.fakeRootFolder
+		application.log.debug(
+			"""
+				Running fake content from ${root}
+				  -> ${root.absolutePath}
+				  -> ${root.canonicalPath}
+			""".trimIndent()
+		)
+
+		application.install(fakeContentPlugin) {
+			this.root = root
+		}
+	}
 }
 
 private val String.ending: String
