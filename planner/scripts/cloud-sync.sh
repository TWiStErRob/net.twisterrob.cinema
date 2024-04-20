#!/usr/bin/env bash
# Note: requires `gradlew :backend:sync:jar :backend:sync:copyJarDependencies`
java -jar backend/sync/build/libs/twisterrob-cinema-backend-sync.jar cinemas films performances
