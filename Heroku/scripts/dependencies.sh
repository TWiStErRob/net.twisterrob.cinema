# Start with a clean slate.
rm ./gradle/dependency-locks/*.lockfile
rm ./*.dependencies

# Warmup
./gradlew --stacktrace --version

# Pure outputs

echo ':dependencies'
./gradlew --stacktrace :dependencies >root.dependencies 2>&1

echo ':backend:dependencies'
./gradlew --stacktrace :backend:dependencies >backend.dependencies 2>&1

echo ':backend:sync:dependencies'
./gradlew --stacktrace :backend:sync:dependencies >backend-sync.dependencies 2>&1

echo ':backend:feed:dependencies'
./gradlew --stacktrace :backend:feed:dependencies >backend-feed.dependencies 2>&1

echo ':backend:quickbook:dependencies'
./gradlew --stacktrace :backend:quickbook:dependencies >backend-quickbook.dependencies 2>&1

echo ':backend:database:dependencies'
./gradlew --stacktrace :backend:database:dependencies >backend-database.dependencies 2>&1

echo ':backend:network:dependencies'
./gradlew --stacktrace :backend:network:dependencies >backend-network.dependencies 2>&1

echo ':backend:endpoint:dependencies'
./gradlew --stacktrace :backend:endpoint:dependencies >backend-endpoint.dependencies 2>&1

echo ':deploy:dependencies'
./gradlew --stacktrace :deploy:dependencies >deploy.dependencies 2>&1

echo ':deploy:appengine:dependencies'
./gradlew --stacktrace :deploy:appengine:dependencies >deploy-appengine.dependencies 2>&1

echo ':shared:dependencies'
./gradlew --stacktrace :shared:dependencies >shared.dependencies 2>&1

echo ':test-helpers:dependencies'
./gradlew --stacktrace :test-helpers:dependencies >test-helpers.dependencies 2>&1

echo ':test-integration:dependencies'
./gradlew --stacktrace :test-integration:dependencies >test-integration.dependencies 2>&1

echo ':plugins:dependencies'
./gradlew -p gradle/plugins --stacktrace :dependencies >plugins.dependencies 2>&1

# Last, so it can generate lockfiles without affecting other outputs.
# Having a lockfile adds extra "constraints" to the dependency tree like this:
# +--- group:module:{strictly 0.0} -> 0.0 (c)
# Note: while allDependencies doesn't depend on includedBuild("plugin")'s dependencies task,
# it will still resolve the configurations and so --write-locks will have a side effect of dumping those lockfiles.
echo 'allDependencies'
./gradlew --stacktrace :allDependencies --write-locks >all.dependencies 2>&1

# Teardown
echo 'Stopping daemon'
./gradlew --stop
