package com.google.gwt.faintynmm.client.ui;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.faintynmm.client.FaintyNMMMessages;
import com.google.gwt.faintynmm.client.GameServiceAsync;
import com.google.gwt.faintynmm.client.exception.InvalidMovementException;
import com.google.gwt.faintynmm.client.exception.InvalidPlacementException;
import com.google.gwt.faintynmm.client.exception.InvalidRemovalException;
import com.google.gwt.faintynmm.client.exception.WrongTurnException;
import com.google.gwt.faintynmm.client.game.Color;
import com.google.gwt.faintynmm.client.game.Game;
import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
		 *            whether the place should be highlighted
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
	}

	private final Audio moveSound = Audio.createIfSupported();
	private final Audio killSound = Audio.createIfSupported();
	private final MatchCell matchCell;
	private Graphics graphics;
	private Game game;
	private Color playerColor;
	private GameServiceAsync gameService;
	private int lastX, lastY;
	private boolean practiceMode;
	private String playerId, opponentId, matchId;
	private ArrayList<Match> matchList;
	private AsyncCallback<Void> voidCallBack = new AsyncCallback<Void>() {
		@Override
		public void onFailure(Throwable caught) {
			// Do nothing.
		}

		@Override
		public void onSuccess(Void result) {
			// Do nothing
		}
	};

	private AsyncCallback<ArrayList<Match>> getMatchListCallback = new AsyncCallback<ArrayList<Match>>() {

		@Override
		public void onFailure(Throwable caught) {
			GWT.log("Match list update callback error: " + caught.getMessage());
		}

		@Override
		public void onSuccess(ArrayList<Match> result) {
			matchList = result;
			graphics.showMatchListDialog(matchCell, matchList);
		}
	};

	public Presenter(GameServiceAsync gameService, String playerId) {
		this.graphics = new Graphics(this);
		this.gameService = gameService;
		this.playerId = playerId;
		this.game = new Game();
		this.matchCell = new MatchCell(playerId, this);
		lastX = lastY = -1;
		practiceMode = false;

		//
		// Initialize Audio objects. Different type of sources added to support
		// different browsers.
		//
		moveSound.addSource("sound/move.wav", AudioElement.TYPE_WAV);
		moveSound.addSource("sound/move.mp3", AudioElement.TYPE_MP3);
		moveSound.setVolume(1.0);
		moveSound.setPreload(MediaElement.PRELOAD_AUTO);
		moveSound.setControls(false);
		killSound.addSource("sound/kill.wav", AudioElement.TYPE_WAV);
		killSound.addSource("sound/kill.mp3", AudioElement.TYPE_MP3);
		killSound.setVolume(1.0);
		killSound.setPreload(MediaElement.PRELOAD_AUTO);
		killSound.setControls(false);

		//
		// Load matches from datastore.
		//
		// gameService.getMatchList(channelId, getMatchListCallback);
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
	public void clickOn(int x, int y, ClickEvent event, boolean isAiTurn) {
		if (!isMyTurn() && !isAiTurn)
			return;
		boolean succeed = false;
		int phase = game.getPhase();
		Color turn = game.getRemovalTurn();
		// Widget source = (Widget) event.getSource();
		// int left = source.getAbsoluteLeft() + 10;
		// int top = source.getAbsoluteTop() + 10;
		if (turn == null) {
			turn = game.getTurn();
		} else {
			try {
				game.removeMan(x, y);
				graphics.setPiece(null, x, y);
				graphics.setTurn(game.getTurn());
				moveSound.pause();
				moveSound.setCurrentTime(0.0);
				killSound.pause();
				killSound.setCurrentTime(0.0);
				killSound.play();
				succeed = true;
				if (phase == 1 && game.getPhase() == 2) {
					Piece piece;
					for (int i = 0; i < 24; i++) {
						piece = graphics.getPiece(i);
						if (piece.getStatus() != 0)
							piece.getElement()
									.setAttribute("draggable", "true");
					}
				}
				updateGraphicInfo();
				if (game.getWinner() != null && !practiceMode) {
					if (game.getWinner() == playerColor) {
						finishMatch(playerId, opponentId);
					} else {
						finishMatch(opponentId, playerId);
					}
				}
				if (!practiceMode) {
					writeToChannel();
				} else if (!isAiTurn && game.getWinner() == null) {
					aiMakeMove();
				}
			} catch (WrongTurnException e) {
				GWT.log(e.getMessage());
			} catch (InvalidRemovalException e) {
				GWT.log(e.getMessage());
			}
			return;
		}
		Color removalTurn = null;
		if (phase == 1) {
			try {
				game.placeMan(turn, x, y);
				graphics.setPiece(turn, x, y);
				killSound.pause();
				killSound.setCurrentTime(0.0);
				moveSound.pause();
				moveSound.setCurrentTime(0.0);
				moveSound.play();
				removalTurn = game.getRemovalTurn();
				if (removalTurn != null) {
					graphics.setRemovalTurn(removalTurn);
				} else {
					graphics.setTurn(game.getTurn());
				}
				succeed = true;
			} catch (WrongTurnException e) {
				GWT.log(e.getMessage());
				// Graphics.showWarning(e.getMessage(), left, top);
			} catch (InvalidPlacementException e) {
				GWT.log(e.getMessage());
				// Graphics.showWarning(e.getMessage(), left, top);
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
						killSound.pause();
						killSound.setCurrentTime(0.0);
						moveSound.pause();
						moveSound.setCurrentTime(0.0);
						moveSound.play();
						removalTurn = game.getRemovalTurn();
						if (removalTurn != null) {
							graphics.setRemovalTurn(removalTurn);
						} else {
							graphics.setTurn(game.getTurn());
						}
						succeed = true;
					} catch (WrongTurnException e) {
						lastX = lastY = -1;
						GWT.log(e.getMessage());
						// Graphics.showWarning(e.getMessage(), left, top);
					} catch (InvalidMovementException e) {
						lastX = lastY = -1;
						GWT.log(e.getMessage());
						// Graphics.showWarning(e.getMessage(), left, top);
					}
				}
				lastX = lastY = -1;
			}
		}
		graphics.setPhase(game.getPhase());
		if (succeed) {
			updateGraphicInfo();
			if (!practiceMode) {
				writeToChannel();
			} else if (removalTurn == null && !isAiTurn) {
				aiMakeMove();
			}
		}
		return;
	}

	private void aiMakeMove() {
		//
		// Make a random move!
		//
		new Timer(){
			@Override
			public void run() {
				int phase = getPhase();
				int[] move;
				if (phase == 1) {
					move = game.getBoard().getRandomPlace();
					if (move[0] != -1) {
						clickOn(move[0], move[1], null, true);
					} else {
						graphics.setResult(Color.BLACK);
						return;
					}
				} else if (phase == 2) {
					move = game.getBoard().getRandomMove();
					if (move[0] != -1) {
						moveMan(move[0], move[1], move[2], move[3], null, true);
					} else {
						graphics.setResult(Color.BLACK);
						return;
					}
				} else {
					if (game.getPieceStat().charAt(3) == '3')
						move = game.getBoard().getRandomFly();
					else {
						move = game.getBoard().getRandomMove();
					}
					if (move[0] != -1) {
						moveMan(move[0], move[1], move[2], move[3], null, true);
					} else {
						graphics.setResult(Color.BLACK);
						return;
					}
				}
			}
		}.schedule(500);
		//
		// Remove is possible.
		//
		new Timer(){
			@Override
			public void run() {
				int[] move;
				if (game.getRemovalTurn() != null) {
					move = game.getBoard().getRandomRemove();
					clickOn(move[0], move[1], null, true);
				}
			}
		}.schedule(1000);
	}

	public void moveMan(int fromX, int fromY, int toX, int toY,
			DropEvent event, boolean isAiTurn) {
		if (!isMyTurn() && !practiceMode)
			return;
		boolean succeed = false;
		// Widget source = (Widget) event.getSource();
		// int left = source.getAbsoluteLeft() + 10;
		// int top = source.getAbsoluteTop() + 10;
		Color removalTurn = null;
		try {
			Color from = game.getMan(fromX, fromY);
			game.moveMan(from, fromX, fromY, toX, toY);
			graphics.setPiece(null, fromX, fromY);
			graphics.setPiece(from, toX, toY);
			killSound.pause();
			killSound.setCurrentTime(0.0);
			moveSound.pause();
			moveSound.setCurrentTime(0.0);
			moveSound.play();
			removalTurn = game.getRemovalTurn();
			if (removalTurn != null) {
				graphics.setRemovalTurn(removalTurn);
			} else {
				graphics.setTurn(game.getTurn());
			}
			succeed = true;
		} catch (WrongTurnException e) {
			GWT.log(e.getMessage());
			// Graphics.showWarning(e.getMessage(), left, top);
		} catch (InvalidMovementException e) {
			GWT.log(e.getMessage());
			// Graphics.showWarning(e.getMessage(), left, top);
		}
		if (succeed) {
			updateGraphicInfo();
			if (!practiceMode) {
				writeToChannel();
			} else if (removalTurn == null && !isAiTurn) {
				aiMakeMove();
			}
		}
		return;
	}

	public void reset() {
		game = new Game();
		lastX = lastY = -1;
		Piece piece;
		for (int i = 0; i < 24; i++) {
			piece = graphics.getPiece(i);
			piece.getElement().getStyle().setBackgroundColor("OrangeRed");
			piece.getElement().setAttribute("draggable", "false");
			piece.setEnabled(false);
			piece.setStatus(0);
		}
		graphics.setTurn(null);
		graphics.setPhase(0);
		graphics.setPieceStat("9999");
		opponentId = matchId = null;
		playerColor = null;
	}

	public void start() {
		Piece piece;
		for (int i = 0; i < 24; i++) {
			piece = graphics.getPiece(i);
			piece.setEnabled(true);
		}
		graphics.setTurn(Color.BLACK);
		graphics.setPhase(1);
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

	public Color getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(Color playerColor) {
		this.playerColor = playerColor;
	}

	/**
	 * Parse the given string which indicates a specific game state, and
	 * correspondingly set the view.
	 * 
	 * @param newState
	 *            the string of state of the game, showing each button's status
	 * @return void
	 */
	public void parseStateString(String matchId, String stateString) {
		if (!matchId.equals(this.matchId)) {
			System.out.println("Not current game: ignore.");
			return;
		} else {
			System.out.println("Parsing: " + stateString);
		}
		char[] states = stateString.toCharArray();
		int phase = Character.digit(states[0], 10);
		Color turn = charToColor(states[1]);
		Color removal = charToColor(states[2]);
		int[] board = new int[49];
		for (int i = 0; i < 24; i++) {
			int state = Character.digit(states[i + 7], 10);
			Piece piece = graphics.getPiece(i);
			piece.getElement().setAttribute("draggable", "false");
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
				if (phase != 1) {
					piece.getElement().setAttribute("draggable", "true");
				}
			}
		}
		lastX = lastY = -1;
		game = new Game(board, turn, removal, phase);
		String pieceStat = stateString.substring(3, 7);
		game.setPieceStat(pieceStat.toCharArray());
		graphics.setPhase(phase);
		if (removal != null) {
			graphics.setRemovalTurn(removal);
		} else {
			graphics.setTurn(game.getTurn());
		}
		graphics.setPieceStat(pieceStat);
	}

	public String getPieceStat() {
		return game.getPieceStat();
	}

	public void loadMatchList() {
		gameService.getMatchList(playerId, getMatchListCallback);
	}

	public Graphics getGraphics() {
		return graphics;
	}

	private boolean isMyTurn() {
		Color removal = game.getRemovalTurn();
		Color turn = game.getTurn();
		if (removal != null) {
			return removal == playerColor;
		} else {
			return turn == playerColor;
		}
	}

	private Color charToColor(char state) {
		if (state == '1')
			return Color.BLACK;
		else if (state == '2')
			return Color.WHITE;
		else
			return null;
	}

	private int colorToInt(Color color) {
		if (color == null)
			return 0;
		else if (color == Color.BLACK)
			return 1;
		else
			return 2;
	}

	private String getStateString() {
		StringBuilder stateString = new StringBuilder();
		stateString.append(game.getPhase());
		stateString.append(colorToInt(game.getTurn()));
		stateString.append(colorToInt(game.getRemovalTurn()));
		stateString.append(game.getPieceStat());
		for (int i = 0; i < 24; i++) {
			stateString.append(graphics.getPiece(i).getStatus());
		}
		return stateString.toString();
	}

	private void updateGraphicInfo() {
		String stateString = getStateString();
		char[] states = stateString.toCharArray();
		int phase = Character.digit(states[0], 10);
		Color turn = charToColor(states[1]);
		Color removal = charToColor(states[2]);
		String pieceStat = stateString.substring(3, 7);
		graphics.setPhase(phase);
		if (removal != null) {
			graphics.setRemovalTurn(removal);
		} else {
			graphics.setTurn(turn);
		}
		graphics.setPieceStat(pieceStat);
	}

	private void writeToChannel() {
		String newState = getStateString();
		gameService.changeState(newState, matchId, playerId, opponentId,
				voidCallBack);
	}

	public void startNewMatchGivenEmail(String opponentId) {
		practiceMode = false;
		gameService.startNewMatch(playerId, opponentId, voidCallBack);
	}

	public void startNewMatchWithAutoMatch() {
		practiceMode = false;
		gameService.startAutoMatch(playerId, voidCallBack);
	}

	public void startNewMatchWithAI() {
		playerColor = Color.BLACK;
		practiceMode = true;
		updateMatchInfo("robot-89757", "Match with AI");
		parseStateString("Match with AI", "1109999000000000000000000000000");
	}

	public void loadMatch(Match match) {
		practiceMode = false;
		if (match.getBlackPlayerId().equals(playerId)
				|| match.getWhitePlayerId().equals(playerId)) {
			gameService.loadMatch(playerId, match.getMatchId(),
					new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Load match async callback error: "
									+ caught.getMessage());
						}

						@Override
						public void onSuccess(Void result) {
							hideMatchList();
						}
					});
		} else {
			GWT.log("It's not your match?!");
		}
	}

	public void hideMatchList() {
		graphics.hideMatchListDialog();
	}

	public void deleteMatch(String matchId) {
		gameService.deleteMatch(matchId, playerId, voidCallBack);
		graphics.hideMatchListDialog();
		if (matchId.equals(this.matchId)) {
			// Clean up graphics because the match is deleted from this side.
			reset();
			graphics.resetMatchInfo();
		}
	}

	public void updateMatchInfo(String opponentId, String matchId) {
		this.opponentId = opponentId;
		this.matchId = matchId;
		graphics.updateMatchInfo(opponentId, matchId);
	}

	public FaintyNMMMessages getMessages() {
		return graphics.getMessages();
	}

	public void surrender() {
		if (Window.confirm(getMessages().surrenderMsg())) {
			if (!practiceMode) {
				finishMatch(opponentId, playerId);
			}
			if (playerColor == Color.BLACK)
				graphics.setResult(Color.WHITE);
			else
				graphics.setResult(Color.BLACK);
		}
	}

	private void finishMatch(String winnerId, String loserId) {
		gameService.finishMatch(matchId, winnerId, loserId, voidCallBack);
	}

	public void setPracticeMode(boolean practiceMode) {
		this.practiceMode = practiceMode;
	}
}
