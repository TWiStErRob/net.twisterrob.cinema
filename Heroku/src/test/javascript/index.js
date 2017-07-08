//var nodeunit = require('nodeunit');
//var reporter = nodeunit.reporters['default'];
var path = require('path');
var reporter = require('./index.reporter.junit');

var testRoot = 'src/test/javascript';

//reporter.run([path.join(testRoot, 'nodeunit.js')]);
reporter.run([path.join(testRoot, 'utils.js')]);
reporter.run([path.join(testRoot, 'underscore.js')]);
reporter.run([path.join(testRoot, 'neo4j.js')]);
//reporter.run([path.join(testRoot, 'neo4j.bugs.js')]);
