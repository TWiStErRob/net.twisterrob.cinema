# Warmup
./gradlew --stacktrace --version

# Pure outputs
echo 'allDependencies'
./gradlew --stacktrace :allDependencies --write-locks >dependencies.txt 2>&1

echo ':dependencies'
./gradlew --stacktrace :dependencies >dependencies-root.txt 2>&1

echo ':backend:dependencies'
./gradlew --stacktrace :backend:dependencies >dependencies-backend.txt 2>&1

echo ':backend:sync:dependencies'
./gradlew --stacktrace :backend:sync:dependencies >dependencies-backend-sync.txt 2>&1

echo ':backend:feed:dependencies'
./gradlew --stacktrace :backend:feed:dependencies >dependencies-backend-feed.txt 2>&1

echo ':backend:quickbook:dependencies'
./gradlew --stacktrace :backend:quickbook:dependencies >dependencies-backend-quickbook.txt 2>&1

echo ':backend:database:dependencies'
./gradlew --stacktrace :backend:database:dependencies >dependencies-backend-database.txt 2>&1

echo ':backend:network:dependencies'
./gradlew --stacktrace :backend:network:dependencies >dependencies-backend-network.txt 2>&1

echo ':backend:endpoint:dependencies'
./gradlew --stacktrace :backend:endpoint:dependencies >dependencies-backend-endpoint.txt 2>&1

echo ':test-helpers:dependencies'
./gradlew --stacktrace :test-helpers:dependencies >dependencies-test-helpers.txt 2>&1

# Teardown
echo 'Stopping daemon'
./gradlew --stop
