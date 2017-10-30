import Group from './Group';

export default class FilmGroup extends Group {
	constructor(groupID, listID) {
		super(groupID, listID, 'film');
	}
}
