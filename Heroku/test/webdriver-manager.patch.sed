#!/bin/sed -r -f webdriver-manager.patch.sed -i node_modules/.bin/webdriver-manager.*
# sed script (of a single replace command) that
# * uses extended regex syntax (-r)
# * is in a file so it can be documented (-f)
# * replaces the input file in place (-i)

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
s/protractor/webdriver-manager/g
