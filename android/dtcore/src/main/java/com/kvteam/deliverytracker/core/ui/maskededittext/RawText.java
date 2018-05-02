package com.kvteam.deliverytracker.core.ui.maskededittext;

/**
 * Raw text, another words TextWithout mask characters
 */
class RawText {
	private String text;

	RawText() {
		text = "";
	}

	/**
	 * text = 012345678, range = 123 =&gt; text = 0456789
	 *
	 * @param range given range
	 */
	void subtractFromString(Range range) {
		String firstPart = "";
		String lastPart = "";

		if (range.getStart() > 0 && range.getStart() <= text.length()) {
			firstPart = text.substring(0, range.getStart());
		}
		if (range.getEnd() >= 0 && range.getEnd() < text.length()) {
			lastPart = text.substring(range.getEnd(), text.length());
		}
		text = firstPart.concat(lastPart);
	}

	/**
	 * @param newString New String to be added
	 * @param start     Position to insert newString
	 * @param maxLength Maximum raw text length
	 * @return Number of added characters
	 */
	int addToString(
			String newString,
			int start,
			int maxLength) {
		String firstPart = "";
		String lastPart = "";

		if (newString == null || newString.equals("")) {
			return 0;
		} else if (start < 0) {
			throw new IllegalArgumentException("Start position must be non-negative");
		} else if (start > text.length()) {
			throw new IllegalArgumentException("Start position must be less than the actual text length");
		}

		int count = newString.length();

		if (start > 0) {
			firstPart = text.substring(0, start);
		}
		if (start < text.length()) {
			lastPart = text.substring(start, text.length());
		}
		if (text.length() + newString.length() > maxLength) {
			count = maxLength - text.length();
			newString = newString.substring(0, count);
		}
		text = firstPart.concat(newString).concat(lastPart);
		return count;
	}

	String getText() {
		return text;
	}

	int length() {
		return text.length();
	}

	char charAt(int position) {
		return text.charAt(position);
	}
}
