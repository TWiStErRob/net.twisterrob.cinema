var nodeunit = require('nodeunit');
//var reporter = nodeunit.reporters['default'];
var reporter = require('./test/reporter.junit');

process.chdir(__dirname);

//reporter.run(['test/nodeunit.js']);
//reporter.run(['test/underscore.js']);
reporter.run(['test/neo4j.js']);
