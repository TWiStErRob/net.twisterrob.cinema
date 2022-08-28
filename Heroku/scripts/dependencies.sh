# Warmup
./gradlew --stacktrace --version

# Pure outputs
echo 'allDependencies'
./gradlew --stacktrace :allDependencies --write-locks >all.dependencies 2>&1

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

echo ':test-helpers:dependencies'
./gradlew --stacktrace :test-helpers:dependencies >test-helpers.dependencies 2>&1

# Teardown
echo 'Stopping daemon'
./gradlew --stop
