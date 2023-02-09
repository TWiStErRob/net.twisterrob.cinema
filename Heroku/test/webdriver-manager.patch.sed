#!/bin/sed -r -f webdriver-manager.patch.sed -i node_modules/.bin/webdriver-manager.*
# sed script (of a single replace command) that
# * uses extended regex syntax (-r)
# * is in a file so it can be documented (-f)
# * replaces the input file in place (-i)

### For Windows
# Expected input in webdriver-manager.*:
#```cmd
#endLocal & goto #_undefined_# 2>NUL || title %COMSPEC% & "%_prog%"  "%dp0%\..\protractor\bin\webdriver-manager" %*
#```
#```ps
#& "node$exe"  "$basedir/../protractor/bin/webdriver-manager" $args
#```
#```shell
#exec node  "$basedir/../protractor/bin/webdriver-manager" "$@"
#```
# The script will replace all the paths to point to `../webdriver-manager/bin/webdriver-manager`.
#
### For Linux
# Expected input in webdriver-manager:
#```js
##!/usr/bin/env node
#require('webdriver-manager');
#```
# We need to replace the module name with a specific folder inside, so that the CLI is invoked.
# Otherwise it just parses the manager package and exists.
s/protractor/webdriver-manager/g ; s/require\('webdriver-manager'\)/require('webdriver-manager\/dist\/lib\/cli')/
