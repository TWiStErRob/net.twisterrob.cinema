# Warmup
./gradlew --stacktrace --version

# Pure outputs
echo ':dependencies'
./gradlew --stacktrace :dependencies --write-locks >dependencies-root.txt 2>&1

echo ':backend:dependencies'
./gradlew --stacktrace :backend:dependencies --write-locks >dependencies-backend.txt 2>&1

echo ':backend:sync:dependencies'
./gradlew --stacktrace :backend:sync:dependencies --write-locks >dependencies-backend-sync.txt 2>&1

echo ':backend:feed:dependencies'
./gradlew --stacktrace :backend:feed:dependencies --write-locks >dependencies-backend-feed.txt 2>&1

echo ':backend:quickbook:dependencies'
./gradlew --stacktrace :backend:quickbook:dependencies --write-locks >dependencies-backend-quickbook.txt 2>&1

echo ':backend:database:dependencies'
./gradlew --stacktrace :backend:database:dependencies --write-locks >dependencies-backend-database.txt 2>&1

echo ':backend:network:dependencies'
./gradlew --stacktrace :backend:network:dependencies --write-locks >dependencies-backend-network.txt 2>&1

echo ':backend:endpoint:dependencies'
./gradlew --stacktrace :backend:endpoint:dependencies --write-locks >dependencies-backend-endpoint.txt 2>&1

echo ':test-helpers:dependencies'
./gradlew --stacktrace :test-helpers:dependencies --write-locks >dependencies-test-helpers.txt 2>&1

echo 'allDependencies'
./gradlew --stacktrace :allDependencies >dependencies.txt 2>&1

# Teardown
echo 'Stopping daemon'
./gradlew --stop
