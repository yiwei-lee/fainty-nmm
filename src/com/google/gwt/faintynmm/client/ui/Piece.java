package com.google.gwt.faintynmm.client.ui;

import com.google.gwt.user.client.ui.Button;

public class Piece extends Button {
	private int index, state;

	public Piece(int index) {
		super();
		this.index = index;
		state = 0;
	}

	public int getStatus() {
		return state;
	}

	public void setStatus(int status) {
		this.state = status;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
