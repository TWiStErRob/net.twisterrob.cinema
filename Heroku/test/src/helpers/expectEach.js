/**
 * @param {ElementArrayFinder} list
 * @param {function(ElementFinder, number?): boolean|wdpromise.Promise<boolean>} [filter]
 * @returns {jasmine.Matchers}
 */
export default function expectEach(list, filter = undefined) {
	list = filter ? list.filter(filter) : list;
	return proxy(expect);

	function proxy(expect) {
		return new Proxy(expect(element(by.id('dummy'))), {
			get(target, key) {
				if (key === 'not') {
					return proxy(function (arg) {
						return expect(arg).not;
					});
				}
				if (target[key]) {
					return function (...args) {
						list.each(function (item) {
							expect(item)[key](args);
						});
					};
				}
			},
		});
	}
}
