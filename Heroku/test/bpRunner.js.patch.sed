#!/bin/sed -z -r -f bpRunner.js.patch.sed -i node_modules/protractor/built/bpRunner.js
# sed script (of a single replace command) that
# * works on the whole input at once (-z)
# * uses extended regex syntax (-r)
# * is in a file so it can be documented (-f)
# * replaces the input file in place (-i)

# Expected input in bpRunner.js:
#```javascript
#class BlockingProxyRunner {
#    ...
#            this.bpProcess = child_process_1.fork(BP_PATH, args, { silent: true });
#```

# The script will replace the first and last lines into
#```javascript
#            // HACKED -- begin
#            var execArgv = process.execArgv.filter(function(arg) {
#                return arg.indexOf('--debug-brk=') !== 0 && arg.indexOf('--inspect') !== 0;
#            });
#            this.bpProcess = child_process_1.fork(BP_PATH, args, { silent: true, execArgv });
#            // HACKED -- end
#```

# Note: Each line end has to be unterminated with an escape, so sed handles multiline script correctly.
# Note: s/ has to be on one line, because otherwise it'll not match.
# Note: programming () {} . has to be escaped in search.
# Note: programming && and // has to be escaped in replacement.
s/this\.bpProcess = child_process_1\.fork\(BP_PATH, args, \{ silent: true \}\);/\
            \/\/ HACKED -- begin\
            var execArgv = process.execArgv.filter(function(arg) {\
                return arg.indexOf('--debug-brk=') !== 0 \&\& arg.indexOf('--inspect') !== 0;\
            });\
            this.bpProcess = child_process_1.fork(BP_PATH, args, { silent: true, execArgv });\
            \/\/ HACKED -- end\
/
