class Ut {
	public prettyDate(date: string) {
		return date.substring(2, 10);
	}

	public prettyDateTime(date: string) {
		return date.substring(2, 16).replace('T', ' ');
	}
}

const ut = new Ut();

export default ut;
