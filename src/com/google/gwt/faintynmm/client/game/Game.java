package com.google.gwt.faintynmm.client.game;

import com.google.gwt.faintynmm.client.exception.InvalidMovementException;
import com.google.gwt.faintynmm.client.exception.InvalidPlacementException;
import com.google.gwt.faintynmm.client.exception.InvalidRemovalException;
import com.google.gwt.faintynmm.client.exception.WrongTurnException;

public class Game {
	private Color turn, removalTurn, winner;
	private int phase;
	private Board board;
	private int blackUnplacedMen, whiteUnplacedMen;
	private int blackLeftMen, whiteLeftMen;
	public Game(){
		turn = Color.BLACK;
		removalTurn = winner = null;
		phase = 1;
		board = new Board();
		blackUnplacedMen = whiteUnplacedMen = 9;
		blackLeftMen = whiteLeftMen = 9;
	}
	public Game(Color color){
		turn = color;
		phase = 1;
		board = new Board();
		blackUnplacedMen = whiteUnplacedMen = 9;
		blackLeftMen = whiteLeftMen = 9;
	}
	public Game(int board[], Color color, int phase){
		turn = color;
		this.board = new Board(board);
		this.phase = phase;
		blackUnplacedMen = whiteUnplacedMen = 9;
		blackLeftMen = whiteLeftMen = 9;
		if (phase == 1){
			for (int i = 0 ; i < 48 ; i++){
				if (board[i] == 1) blackUnplacedMen--;
				else if (board[i] == 2) whiteUnplacedMen--;
			}
		}else{
			blackUnplacedMen = whiteUnplacedMen = 0;
			blackLeftMen = whiteLeftMen = 0;
			for (int i = 0 ; i < 48 ; i++){
				if (board[i] == 1) blackLeftMen++;
				else if (board[i] == 2) whiteLeftMen++;
			}
		}
	}
	public Game(int board[], Color color, Color removal, int phase){
		turn = color;
		removalTurn = removal;
		this.board = new Board(board);
		this.phase = phase;
		blackUnplacedMen = whiteUnplacedMen = 9;
		blackLeftMen = whiteLeftMen = 9;
		if (phase == 1){
			for (int i = 0 ; i < 48 ; i++){
				if (board[i] == 1) blackUnplacedMen--;
				else if (board[i] == 2) whiteUnplacedMen--;
			}
		}else{
			blackUnplacedMen = whiteUnplacedMen = 0;
			blackLeftMen = whiteLeftMen = 0;
			for (int i = 0 ; i < 48 ; i++){
				if (board[i] == 1) blackLeftMen++;
				else if (board[i] == 2) whiteLeftMen++;
			}
		}
	}
	public void placeMan(Color color, int x, int y) throws WrongTurnException, InvalidPlacementException{
		if (removalTurn != null){
//			System.out.println("Not a turn to place man, time to remove!");
			throw new WrongTurnException("Not a turn to place man, time to remove!");
		}
		if (turn != color){
//			System.out.println("Not player "+color+"'s turn to place man!");
			throw new WrongTurnException("Not player "+color+"'s turn to place man!");
		}
		if (phase != 1){
//			System.out.println("Phase 1 has ended!");
			throw new InvalidPlacementException("Phase 1 has ended!");
		}
		board.placeMan(color, x, y);
		if (checkNewMill(color, x, y)){
			removalTurn = color;
		}
		if (color == Color.BLACK) blackUnplacedMen--;
		else whiteUnplacedMen--;
		if (phase == 1 && blackUnplacedMen == 0 && whiteUnplacedMen == 0) phase = 2;
		if (turn == Color.BLACK) turn = Color.WHITE;
		else turn = Color.BLACK;
	}
	public void moveMan(Color color, int x1, int y1, int x2, int y2) throws WrongTurnException, InvalidMovementException{
		if (removalTurn != null){
//			System.out.println("Not a turn to move man, time to remove!");
			throw new WrongTurnException("Not a turn to move man, time to remove!");
		}
		if (turn != color){
//			System.out.println("Not player "+color+"'s turn to move man!");
			throw new WrongTurnException("Not player "+color+"'s turn to move man!");
		}
		if (phase == 1){
//			System.out.println("Still in phase 1!");
			throw new InvalidMovementException("Still in phase 1!");
		}
		if ((color == Color.BLACK && blackLeftMen <= 3) || (color == Color.WHITE && whiteLeftMen <= 3)){
			board.flyMan(color, x1, y1, x2, y2);
		}else{
			board.moveMan(color, x1, y1, x2, y2);
		}
		if (checkNewMill(color, x2, y2)){
			removalTurn = color;
		}
		turnSwitched();
	}
	public void flyMan(Color color, int x1, int y1, int x2, int y2) throws WrongTurnException, InvalidMovementException {
		if (removalTurn != null){
//			System.out.println("Not a turn to fly man, time to remove!");
			throw new WrongTurnException("Not a turn to fly man, time to remove!");
		}
		if (turn != color){
//			System.out.println("Not player "+color+"'s turn to move man!");
			throw new WrongTurnException("Not player "+color+"'s turn to move man!");
		}
		if (phase != 3){
//			System.out.println("Not in phase 3!");
			throw new InvalidMovementException("Not in phase 3!");
		}
		if ((color == Color.BLACK && blackLeftMen <= 3) || (color == Color.WHITE && whiteLeftMen <= 3)){
			board.flyMan(color, x1, y1, x2, y2);
		}else{
//			System.out.println("Player "+color+" can only move man!");
			throw new InvalidMovementException("Player "+color+" can only move man!");
		}
		if (checkNewMill(color, x2, y2)){
			removalTurn = color;
		}
		turnSwitched();
	}
	public void removeMan(int x, int y) throws WrongTurnException, InvalidRemovalException{
		if (removalTurn == null){
//			System.out.println("Not the time to remove!");
			throw new WrongTurnException("Not the turn to remove!");
		}
		board.removeMan(removalTurn, x, y);
		if (removalTurn == Color.BLACK) whiteLeftMen--;
		else blackLeftMen--;
		if (phase == 2 && (blackLeftMen == 3 || whiteLeftMen == 3)) phase = 3;
		if (whiteLeftMen == 2) winner = Color.BLACK;
		if (blackLeftMen == 2) winner = Color.WHITE;
		removalTurn = null;
	}
	private boolean checkNewMill(Color color, int x, int y){
		return board.checkNewMill(color, x, y);
	}
	private void turnSwitched(){
		if (turn == Color.BLACK) turn = Color.WHITE;
		else turn = Color.BLACK;
	}
	public void printCurrentBoard(){
		
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
	public Color getMan(int x, int y){
		return board.getMan(x, y);
	}
}
