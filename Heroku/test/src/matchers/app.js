import { toHaveClass } from './generic';

/**
 * Matches the element to have a Bootstrap glyphicon element inside with the given icon name
 * @param {ElementFinder} actual
 * @param {string} iconName the end of `glyphicon-name`
 * @returns {{message: string, pass: boolean|Promise<boolean>}}
 */
export function toHaveIcon(actual, iconName) {
	const iconElement = actual.element(by.className('glyphicon'));
	const iconClass = 'glyphicon-' + iconName;
	return toHaveClass(iconElement, iconClass);
}

export default {
	toHaveIcon: () => ({ compare: toHaveIcon }),
};
