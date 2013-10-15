package com.google.gwt.faintynmm.client.ui;

import com.google.gwt.user.client.ui.Button;

public class Piece extends Button {
	private int index, x, y, state;
	
	public Piece(int x, int y) {
		super();
		this.index = x * 7 + y;
		this.x = x;
		this.y = y;
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
	
	public void setCord(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
