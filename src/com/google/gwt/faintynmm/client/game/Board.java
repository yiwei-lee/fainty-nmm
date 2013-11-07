package com.google.gwt.faintynmm.client.game;

import com.google.gwt.faintynmm.client.exception.InvalidMovementException;
import com.google.gwt.faintynmm.client.exception.InvalidPlacementException;

import com.google.gwt.faintynmm.client.exception.InvalidRemovalException;

public class Board {
	private Color board[][];

	public Board(int board[]) {
		this.board = new Color[7][7];
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				if (board[i * 7 + j] == 1)
					this.board[i][j] = Color.BLACK;
				else if (board[i * 7 + j] == 2)
					this.board[i][j] = Color.WHITE;
			}
		}
	}

	public Board() {
		board = new Color[7][7];
	}

	public void placeMan(Color color, int x, int y)
			throws InvalidPlacementException {
		if (board[x][y] != null) {
			// System.out.println("Position occupied! Trying to placed at: "+x+", "+y);
			throw new InvalidPlacementException(
					"Position occupied! Trying to placed at: " + x + ", " + y);
		}
		if (!isValid(x, y)) {
			// System.out.println("Invalid postion! Trying to placed at: "+x+", "+y);
			throw new InvalidPlacementException(
					"Invalid postion! Trying to placed at: " + x + ", " + y);
		}
		board[x][y] = color;
	}

	public void removeMan(Color color, int x, int y)
			throws InvalidRemovalException {
		if (color == board[x][y]) {
			// System.out.println("Trying to remove own man!");
			throw new InvalidRemovalException("Trying to remove own man!");
		}
		if (board[x][y] == null) {
			// System.out.println("Trying to remove at empty place!");
			throw new InvalidRemovalException(
					"Trying to remove at empty place!");
		}
		board[x][y] = null;
	}

	public boolean checkNewMill(Color color, int x, int y) {
		boolean newMill = false;
		if (x == 0 || x == 6) {
			if (board[x][0] == color && board[x][3] == color
					&& board[x][6] == color)
				newMill = true;
		} else if (x == 1 || x == 5) {
			if (board[x][1] == color && board[x][3] == color
					&& board[x][5] == color)
				newMill = true;
		} else if (x == 2 || x == 4) {
			if (board[x][2] == color && board[x][3] == color
					&& board[x][4] == color)
				newMill = true;
		} else {
			if (y <= 2 && board[3][0] == color && board[3][1] == color
					&& board[3][2] == color)
				newMill = true;
			else if (y >= 4 && board[3][4] == color && board[3][5] == color
					&& board[3][6] == color)
				newMill = true;
		}
		if (newMill)
			return true;
		if (y == 0 || y == 6) {
			if (board[0][y] == color && board[3][y] == color
					&& board[6][y] == color)
				newMill = true;
		} else if (y == 1 || y == 5) {
			if (board[1][y] == color && board[3][y] == color
					&& board[5][y] == color)
				newMill = true;
		} else if (y == 2 || y == 4) {
			if (board[2][y] == color && board[3][y] == color
					&& board[4][y] == color)
				newMill = true;
		} else {
			if (x <= 2 && board[0][3] == color && board[1][3] == color
					&& board[2][3] == color)
				newMill = true;
			else if (x >= 4 && board[4][3] == color && board[5][3] == color
					&& board[6][3] == color)
				newMill = true;
		}
		return newMill;
	}

	public void moveMan(Color color, int x1, int y1, int x2, int y2)
			throws InvalidMovementException {
		if (board[x1][y1] != color) {
			// System.out.println("Invalid postion! Trying to move man at ("+x1+","+y1+").");
			throw new InvalidMovementException(
					"Invalid postion! Trying to move man at (" + x1 + "," + y1
							+ ").");
		}
		if (board[x2][y2] != null) {
			// System.out.println("Position occupied! Trying to move to ("+x2+","+y2+").");
			throw new InvalidMovementException(
					"Position occupied! Trying to move to (" + x2 + "," + y2
							+ ").");
		}
		if (!isValidMovement(x1, y1, x2, y2)) {
			// System.out.println("Invalid movement! Trying to move from ("+x1+","+y1+") to ("+x2+","+y2+").");
			throw new InvalidMovementException(
					"Invalid movement! Trying to move from (" + x1 + "," + y1
							+ ") to (" + x2 + "," + y2 + ").");
		}
		board[x1][y1] = null;
		board[x2][y2] = color;
	}

	public void flyMan(Color color, int x1, int y1, int x2, int y2)
			throws InvalidMovementException {
		if (board[x1][y1] != color) {
			// System.out.println("Invalid postion! Trying to fly man at ("+x1+","+y1+").");
			throw new InvalidMovementException(
					"Invalid postion! Trying to fly man at (" + x1 + "," + y1
							+ ").");
		}
		if (board[x2][y2] != null) {
			// System.out.println("Position occupied! Trying to fly to ("+x2+","+y2+").");
			throw new InvalidMovementException(
					"Position occupied! Trying to fly to (" + x2 + "," + y2
							+ ").");
		}
		board[x1][y1] = null;
		board[x2][y2] = color;
	}

	private boolean isValid(int x, int y) {
		x -= 3;
		y -= 3;
		if (x == 0 && y == 0)
			return false;
		if (x > 3 || x < -3 || y > 3 || y < -3)
			return false;
		if (x != 0 && y != 0 && x != y && x != -y)
			return false;
		return true;
	}

	private boolean isValidMovement(int x1, int y1, int x2, int y2) {
		if (!isValid(x2, y2))
			return false;
		if (x1 == x2 && y1 == y2)
			return false;
		if (x1 != x2 && y1 != y2)
			return false;
		int distance = (x1 - x2) + (y1 - y2);
		if (distance < 0)
			distance = -distance;
		if ((x1 == 0 && x2 == 0) || (y1 == 0 && y2 == 0)
				|| (x1 == 6 && x2 == 6) || (y1 == 6 && y2 == 6)) {
			if (distance != 3)
				return false;
		}
		if ((x1 == 1 && x2 == 1) || (y1 == 1 && y2 == 1)
				|| (x1 == 5 && x2 == 5) || (y1 == 5 && y2 == 5)) {
			if (distance != 2)
				return false;
		}
		if ((x1 == 2 && x2 == 2) || (y1 == 2 && y2 == 2)
				|| (x1 == 4 && x2 == 4) || (y1 == 4 && y2 == 4)) {
			if (distance != 1)
				return false;
		}
		if ((x1 == 3 && x2 == 3) || (y1 == 3 && y2 == 3)) {
			if (distance != 1)
				return false;
		}
		return true;
	}

	public boolean canPlace(int x, int y){
		if (isValid(x, y)){
			return board[x][y]==null;
		} else {
			return false;
		}
	}
	
	public boolean canMove(int x1, int y1, int x2, int y2){
		if (isValid(x1, y1)){
			return board[x1][y1]==null;
		} else {
			return false;
		}
	}
	
	public Color getMan(int x, int y) {
		return board[x][y];
	}
}
