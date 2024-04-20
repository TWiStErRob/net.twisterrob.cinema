## Run all tests

### Command line

Start up everything:
```shell
# Set the DB NEO4J_URL and frontend PORT
planner$ scripts\env.bat

# Make sure the database has up to date data
planner$ gradlew :backend:sync:run

# Start up everything
planner$ npm run devTest
```
Then separately run the tests:
```shell
test$ gradlew :test-integration:integrationExternalTest
```
