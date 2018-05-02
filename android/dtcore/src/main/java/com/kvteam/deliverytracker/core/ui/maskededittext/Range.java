package com.kvteam.deliverytracker.core.ui.maskededittext;

class Range {
	private int start;
	private int end;

	Range() {
		start = -1;
		end = -1;
	}
	
	int getStart() {
		return start;
	}
	
	void setStart(int start) {
		this.start = start;
	}
	
	int getEnd() {
		return end;
	}
	
	void setEnd(int end) {
		this.end = end;
	}
}
