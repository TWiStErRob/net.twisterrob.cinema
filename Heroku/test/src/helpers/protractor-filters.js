/**
 * Matches the element to have a Bootstrap glyphicon element inside with the given icon name
 * @param {ElementArrayFinder} elementList
 * @param {Promise<string>} textPromise the end of `glyphicon-name`
 * @returns {Promise<boolean>}
 */
export function anyWithText(elementList, textPromise) {
	//noinspection JSValidateTypes reduce will resolve correctly: `false` + `||` -> boolean
	return elementList.reduce(function (acc, elem) {
		return textPromise.then(function (resolvedText) {
			return elem.getText().then(function (elemName) {
				return acc || resolvedText === elemName;
			});
		});
	}, false);
}

/**
 * Matches the element to have a Bootstrap glyphicon element inside with the given icon name
 * @param {ElementArrayFinder} elementList
 * @param {Promise<string>} text the end of `glyphicon-name`
 * @returns {Promise<boolean>}
 */
export function noneWithText(elementList, text) {
	return anyWithText(elementList, text).then((result) => !result);
}
