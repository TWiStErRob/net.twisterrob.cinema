# Warmup
./gradlew --no-daemon --no-build-cache --stacktrace --version

# Pure outputs
echo 'allDependencies'
./gradlew --no-daemon --no-build-cache --stacktrace allDependencies > dependencies.txt

echo ':dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :dependencies > dependencies-root.txt

echo ':backend:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :backend:dependencies > dependencies-backend.txt

echo ':backend:sync:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :backend:sync:dependencies > dependencies-backend-sync.txt

echo ':backend:feed:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :backend:feed:dependencies > dependencies-backend-feed.txt

echo ':backend:quickbook:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :backend:quickbook:dependencies > dependencies-backend-quickbook.txt

echo ':backend:database:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :backend:database:dependencies > dependencies-backend-database.txt

echo ':backend:network:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :backend:network:dependencies > dependencies-backend-network.txt

echo ':backend:endpoint:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :backend:endpoint:dependencies > dependencies-backend-endpoint.txt

echo ':test-helpers:dependencies'
./gradlew --no-daemon --no-build-cache --stacktrace :test-helpers:dependencies > dependencies-test-helpers.txt
