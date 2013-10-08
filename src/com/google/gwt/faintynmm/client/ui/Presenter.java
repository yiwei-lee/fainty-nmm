package com.google.gwt.faintynmm.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.faintynmm.client.exception.InvalidMovementException;
import com.google.gwt.faintynmm.client.exception.InvalidPlacementException;
import com.google.gwt.faintynmm.client.exception.InvalidRemovalException;
import com.google.gwt.faintynmm.client.exception.WrongTurnException;
import com.google.gwt.faintynmm.client.game.Color;
import com.google.gwt.faintynmm.client.game.Game;
import com.google.gwt.user.client.ui.Widget;

public class Presenter {
	public interface View {
		/**
		 * Renders the piece at this position. If piece is null then the
		 * position is empty.
		 * 
		 * @param color
		 *            the color to render
		 * @param row
		 *            the row number
		 * @param col
		 *            the col number
		 * @return void
		 * 
		 */
		void setPiece(Color color, int row, int col);

		/**
		 * Turns the highlighting on or off at this cell. Cells that can be
		 * clicked should be highlighted.
		 * 
		 * @param row
		 *            the row number
		 * @param col
		 *            the col number
		 * @param highlighted
		 *            whether the place should be hightlighted
		 * @return void
		 * 
		 */
		void setHighlighted(int row, int col, boolean highlighted);

		/**
		 * Indicate whose turn it is.
		 * 
		 * @param color
		 *            the one who should take the turn
		 * @return void
		 * 
		 */
		void setTurn(Color color);

		/**
		 * Indicate which phase it is.
		 * 
		 * @param phase
		 *            show which phase the game is in
		 * @return void
		 */
		void setPhase(int phase);

		/**
		 * Indicate whether the game is in progress or over.
		 * 
		 * @param gameResult
		 *            show the result of the game
		 * @return void
		 */
		void setResult(Color gameResult);
	}

	private Graphics graphics;
	private Game game;
	private int lastX, lastY;

	public Presenter(Graphics graphics) {
		this.graphics = graphics;
		game = new Game();
		lastX = lastY = -1;
	}

	/**
	 * Handles the event when player click on a button. Then call Graphics's
	 * methods to set the view.
	 * 
	 * @param x
	 *            the row number
	 * @param y
	 *            the col number
	 * @param event
	 *            the click event triggered by the button
	 * @return void
	 */
	public void clickOn(int x, int y, ClickEvent event) {
		int phase = game.getPhase();
		Color turn = game.getRemovalTurn();
		Widget source = (Widget) event.getSource();
		int left = source.getAbsoluteLeft() + 10;
		int top = source.getAbsoluteTop() + 10;
		if (turn == null)
			turn = game.getTurn();
		else {
			try {
				game.removeMan(x, y);
				graphics.setPiece(null, x, y);
			} catch (WrongTurnException e) {
				graphics.sendWarning(e.getMessage(), left, top);
			} catch (InvalidRemovalException e) {
				graphics.sendWarning(e.getMessage(), left, top);
			}
			return;
		}
		if (phase == 1) {
			try {
				game.placeMan(turn, x, y);
				graphics.setPiece(turn, x, y);
				graphics.setTurn(game.getTurn());
			} catch (WrongTurnException e) {
				graphics.sendWarning(e.getMessage(), left, top);
			} catch (InvalidPlacementException e) {
				graphics.sendWarning(e.getMessage(), left, top);
			}
		} else {
			if (lastX == -1) {
				if (game.getMan(x, y) == turn) {
					lastX = x;
					lastY = y;
				}
			} else {
				if (lastX != x || lastY != y) {
					try {
						game.moveMan(turn, lastX, lastY, x, y);
						graphics.setPiece(null, lastX, lastY);
						graphics.setPiece(turn, x, y);
						graphics.setTurn(game.getTurn());
					} catch (WrongTurnException e) {
						lastX = lastY = -1;
						graphics.sendWarning(e.getMessage(), left, top);
					} catch (InvalidMovementException e) {
						lastX = lastY = -1;
						graphics.sendWarning(e.getMessage(), left, top);
					}
				}
				lastX = lastY = -1;
			}
		}
		graphics.setPhase(game.getPhase());
	}

	public void reset() {
		game = new Game();
		lastX = lastY = -1;
	}

	public Color getTurn() {
		return game.getTurn();
	}

	public int getPhase() {
		return game.getPhase();
	}

	public Color getRemovalTurn() {
		return game.getRemovalTurn();
	}

	/**
	 * Parse the given string which indicates a specific game state, and
	 * correspondingly set the view.
	 * 
	 * @param stateString
	 *            the string of state of the game, showing each button's status
	 * @return void
	 */
	public void parseStateString(String stateString) {
		assert (stateString.length() == 31);
		char[] states = stateString.toCharArray();
		int phase = Character.digit(states[0], 10);
		Color turn = charToColor(states[1]);
		Color removal = charToColor(states[2]);
		int[] board = new int[49];
		for (int i = 0; i < 24; i++) {
			int state = Character.digit(states[i + 7], 10);
			Piece piece = graphics.getPiece(i);
			piece.setStatus(state);
			piece.setEnabled(true);
			int index = piece.getIndex();
			board[index] = state;
			Color color = charToColor(states[i + 7]);
			if (color == null) {
				piece.getElement().getStyle()
						.setProperty("background", "OrangeRed");
			} else {
				piece.getElement().getStyle()
						.setProperty("background", color.name());
			}
		}
		lastX = lastY = -1;
		game = new Game(board, turn, removal, phase);
		String pieceStat = stateString.substring(3, 7);
		game.setPieceStat(pieceStat.toCharArray());
		graphics.setPhase(phase);
		graphics.setTurn(turn);
		graphics.setPieceStat(pieceStat);
	}

	private Color charToColor(char state) {
		if (state == '1')
			return Color.BLACK;
		else if (state == '2')
			return Color.WHITE;
		else
			return null;
	}

	public String getPieceStat() {
		return game.getPieceStat();
	}
}
