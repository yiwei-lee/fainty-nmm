package org.shared.nmm.game;

import org.shared.nmm.exception.InvalidPlacementException;
import org.shared.nmm.exception.InvalidRemovalException;

public class Board {
	private Color board[][];
	public Board(int board[]){
		this.board = new Color[7][7];
		for (int i = 0 ; i < 7 ; i++){
			for (int j = 0; j < 7; j++) {
				if (board[i*7+j] == 1) this.board[i][j] = Color.BLACK;
				if (board[i*7+j] == 2) this.board[i][j] = Color.WHITE;
			}
		} 
	}
	public Board(){
		board = new Color[7][7];
	}
	public void placeMan(Color color, int x, int y) throws InvalidPlacementException{
		if (!isValid(x, y)){
			System.out.println("Invalid postion! Trying to placed at: "+x+", "+y);
			throw new InvalidPlacementException("Invalid postion! Trying to placed at: "+x+", "+y);
		}
		if (board[x][y] != null){
			System.out.println("Position occupied! Trying to placed at: "+x+", "+y);
			throw new InvalidPlacementException("Position occupied! Trying to placed at: "+x+", "+y);
		}
		board[x][y] = color;
	}
	public void removeMan(Color color, int x, int y) throws InvalidRemovalException{
		if (color == board[x][y]){
			System.out.println("");
			throw new InvalidRemovalException("");
		}
	}
	public boolean isValid(int x, int y){
		x -= 3;
		y -= 3;
		if (x == 0 && y == 0) return false;
		if (x > 3 || x < -3 || y > 3 || y < -3) return false;
		if (x != 0 && y != 0 && x != y && x != -y) return false;
		return true;
	}
}
