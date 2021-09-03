## Environment setup
Since the dependencies are old some trickery is necessary. In the package.json there are hardcoded version numbers.

`--versions.standalone 3.141.59` is the latest available Selenium driver that's compatible with this runner.

`--versions.chrome 92.0.4515.107` is a compatible Chrome Driver version which cannot be installed automatically, so need to do it manually:
 * `Heroku\test$ npm install`
 * Working directory: `Heroku\test\node_modules\webdriver-manager\selenium`
 * Go to Chrome > Help > About and find version number: `x`
 * Download the ZIP file as described here from `x`:
   https://chromedriver.chromium.org/downloads/version-selection  
   `win32` will do, let's say it's version `y`.
 * Rename it to `chromedriver_$y.zip`
 * Extract and rename `chromedriver.exe` inside it to `chromedriver_$y.exe`
 * Replace `package.json`'s Chrome version numbers with `y`.

This should help webdriver-manager's up-to-date check happy.

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
