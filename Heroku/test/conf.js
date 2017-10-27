exports.config = {
	framework: 'jasmine',
	seleniumAddress: 'http://localhost:4444/wd/hub',
	multiCapabilities: [
		{ browserName: 'chrome' },
	],
	specs: ['src/*.spec.js'],
};
