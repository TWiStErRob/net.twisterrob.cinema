# Warmup
./gradlew --stacktrace --version

# Pure outputs
echo 'allDependencies'
./gradlew --stacktrace :allDependencies > dependencies.txt

echo ':dependencies'
./gradlew --stacktrace :dependencies > dependencies-root.txt

echo ':backend:dependencies'
./gradlew --stacktrace :backend:dependencies > dependencies-backend.txt

echo ':backend:sync:dependencies'
./gradlew --stacktrace :backend:sync:dependencies > dependencies-backend-sync.txt

echo ':backend:feed:dependencies'
./gradlew --stacktrace :backend:feed:dependencies > dependencies-backend-feed.txt

echo ':backend:quickbook:dependencies'
./gradlew --stacktrace :backend:quickbook:dependencies > dependencies-backend-quickbook.txt

echo ':backend:database:dependencies'
./gradlew --stacktrace :backend:database:dependencies > dependencies-backend-database.txt

echo ':backend:network:dependencies'
./gradlew --stacktrace :backend:network:dependencies > dependencies-backend-network.txt

echo ':backend:endpoint:dependencies'
./gradlew --stacktrace :backend:endpoint:dependencies > dependencies-backend-endpoint.txt

echo ':test-helpers:dependencies'
./gradlew --stacktrace :test-helpers:dependencies > dependencies-test-helpers.txt

# Teardown
echo 'Stopping daemon'
./gradlew --stop
