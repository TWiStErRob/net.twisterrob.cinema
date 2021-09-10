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

Create a Node.js run configuration with:
 * Working directory: `test` folder
 * Javascript file: `node_modules\protractor\built\cli.js`
 * Application parameters: `protractor.config.js`
 * Node parameters: `--trace-warnings`

Debug attach from IntelliJ IDEA is not working with the following message:

> I/BlockingProxy - Starting BlockingProxy with args: --fork,--seleniumAddress,http://localhost:4444/wd/hub,--logDir,logs
> E/BlockingProxy - Error: listen EADDRINUSE :::64617

to fix it, change `bpRunner.js` this way:

```diff
+var execArgv = process.execArgv.filter(function(arg) {
+    return arg.indexOf('--debug-brk=') !== 0 && arg.indexOf('--inspect') !== 0; });
-this.bpProcess = child_process_1.fork(BP_PATH, args, { silent: true });
+this.bpProcess = child_process_1.fork(BP_PATH, args, { silent: true, execArgv });
```

## Upgrade to new Babel

These may be helpful:

 * https://github.com/antonybudianto/angular-webpack-starter/commit/bdd3567ed7c60347d0f8df96c641e4cf33d2c06b
 * https://github.com/TypeStrong/ts-node/issues/51
