## Environment setup
In the [package.json](package.json) there are hardcoded version number for everything for reproducibility. In case Chrome moves ahead a lot update the version number to match installed Chrome. To download a manual driver version, see https://chromedriver.chromium.org/downloads/version-selection.

## Run all tests

### Command line

Start up everything:
```console
# Set the DB NEO4J_URL and frontend PORT
Heroku$ scripts\env.bat
# Make sure the database has up to date data
Heroku$ gradlew :backend:sync:run
# Start up everything
Heroku$ npm run devTest
```
Then separately run the tests:
```console
test$ npm run protractor
```

### IntelliJ IDEA

Create a Protractor run configuration in Ultimate.
Or in all versions: create a Node.js run configuration with:
 * Working directory: `test` folder
 * Javascript file: `node_modules\protractor\built\cli.js`
 * Application parameters: `protractor.config.js`
 * Node parameters: `--trace-warnings`

Debug attach from IntelliJ IDEA is not working with the following message:
```
I/launcher - Running 1 instances of WebDriver
I/hosted - Using the selenium server at http://localhost:4444/wd/hub
I/BlockingProxy - Starting BlockingProxy with args: --fork,--seleniumAddress,http://localhost:4444/wd/hub,--logDir,logs
E/BlockingProxy - Starting inspector on 127.0.0.1:54285 failed: address already in use
E/BlockingProxy - Exited with 12
E/BlockingProxy - signal null
E/launcher - Error: Error: BP exited with 12
    at ChildProcess.bpProcess.on.on.on (test/node_modules/protractor/built/bpRunner.js:37:24)
```

to fix it, change `bpRunner.js` as described in [patch](bpRunner.js.patch.sed). It should run automatically on `npm install`.

## Upgrade to new Babel

These may be helpful:

 * https://github.com/antonybudianto/angular-webpack-starter/commit/bdd3567ed7c60347d0f8df96c641e4cf33d2c06b
 * https://github.com/TypeStrong/ts-node/issues/51
