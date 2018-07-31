## Run all tests

### Command line

Start up everything:
```console
Heroku$ npm run devTest
```

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
