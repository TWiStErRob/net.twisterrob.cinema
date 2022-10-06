import Group from './Group';

export default class FilmGroup extends Group {
	constructor(groupCSS, listCSS) {
		super(groupCSS, listCSS, '.film');
	}
}
