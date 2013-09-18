package org.shared.nmm.game;

import org.shared.nmm.exception.InvalidMovementException;
import org.shared.nmm.exception.InvalidPlacementException;
import org.shared.nmm.exception.InvalidRemovalException;
import org.shared.nmm.exception.WrongTurnException;

public class Game {
	Color turn, removalTurn, winner;
	int phase;
	Board board;
	int blackUnplacedMen, whiteUnplacedMen;
	public Game(Color color){
		turn = color;
		phase = 1;
		board = new Board();
		blackUnplacedMen = whiteUnplacedMen = 9;
	}
	public Game(int board[], Color color, int phase){
		turn = color;
		this.board = new Board(board);
		this.phase = phase;
		for (int i = 0 ; i < 48 ; i++){
			
		}
	}
	public void placeMan(Color color, int x, int y) throws WrongTurnException, InvalidPlacementException{
		if (removalTurn != null){
			System.out.println("Not a turn to place man, time to remove!");
			throw new WrongTurnException("Not a turn to place man, time to remove!");
		}
		if (turn != color){
			System.out.println("Not player: "+color+"'s turn to place man!");
			throw new WrongTurnException("Not player: "+color+"'s turn to place man!");
		}
		if (phase != 1){
			System.out.println("Phase 1 has ended!");
			throw new InvalidPlacementException("Phase 1 has ended!");
		}
		board.placeMan(color, x, y);
		if (checkNewMill(color, x, y)){
			removalTurn = color;
		}
		if (color == Color.BLACK) blackUnplacedMen--;
		else whiteUnplacedMen--;
		if (blackUnplacedMen == 0 && whiteUnplacedMen == 0) phase = 2;
		if (turn == Color.BLACK) turn = Color.WHITE;
		else turn = Color.BLACK;
	}
	public void moveMan(Color color, int x1, int y1, int x2, int y2) throws WrongTurnException, InvalidMovementException{
		assert(phase == 2);
	}
	public void removeMan(int x, int y) throws WrongTurnException, InvalidRemovalException{
		if (removalTurn == null){
			System.out.println("Not the time to remove!");
			throw new WrongTurnException("Not the turn to remove!");
		}
		board.removeMan(removalTurn, x, y);
		removalTurn = null;
	}
	public void printCurrentBoard(){
		
	}
	private boolean checkNewMill(Color color, int x, int y){
		boolean newMill = false;
		return newMill;
	}
	public Color getTurn(){
		return turn;
	}
	public Color getRemovalTurn(){
		return removalTurn;
	}
	public int getPhase(){
		return phase;
	}
	public Color getWinner(){
		return winner;
	}
}
