/**
 * Matches any element in {@param elementList} that has the same text as the resolved value of {@param textPromise}.
 * @param {ElementArrayFinder} elementList
 * @param {Promise<string>} textPromise
 * @returns {Promise<boolean>}
 */
export function anyWithText(elementList, textPromise) {
	//noinspection JSValidateTypes reduce will resolve correctly: `false` + `||` -> boolean
	return textPromise.then(function (resolvedText) {
		return elementList.reduce(function (acc, elem) {
			return elem.getText().then(function (elemName) {
				return acc || resolvedText === elemName;
			});
		}, false);
	});
}

/**
 * Checks if there are no elements in {@param elementList} that have the same text as the resolved value of {@param textPromise}.
 * @param {ElementArrayFinder} elementList
 * @param {Promise<string>} textPromise
 * @returns {Promise<boolean>}
 */
export function noneWithText(elementList, textPromise) {
	return anyWithText(elementList, textPromise).then((result) => !result);
}
