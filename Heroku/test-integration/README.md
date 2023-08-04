## Run all tests

### Command line

Start up everything:
```shell
# Set the DB NEO4J_URL and frontend PORT
Heroku$ scripts\env.bat

# Make sure the database has up to date data
Heroku$ gradlew :backend:sync:run

# Start up everything
Heroku$ npm run devTest
```
Then separately run the tests:
```shell
test$ gradlew :test-integration:integrationTest
```
