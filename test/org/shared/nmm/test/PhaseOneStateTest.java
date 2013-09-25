package org.shared.nmm.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.shared.nmm.exception.InvalidPlacementException;
import org.shared.nmm.exception.InvalidRemovalException;
import org.shared.nmm.exception.WrongTurnException;
import org.shared.nmm.game.Color;
import org.shared.nmm.game.Game;

public class PhaseOneStateTest {
	//#0
	@Test
	public void toPlaceMan() throws InvalidPlacementException, WrongTurnException{
		Game game = new Game(Color.BLACK);
		assertEquals("Now black's turn.", Color.BLACK, game.getTurn());
		game.placeMan(Color.BLACK, 0, 0);
		assertEquals("Now white's turn.", Color.WHITE, game.getTurn());
	}
	//#1
	@Test(expected = InvalidPlacementException.class)
	public void toPlaceAtInvalidPlace() throws InvalidPlacementException, WrongTurnException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 3, 3);
	}
	//#2
	@Test(expected = InvalidPlacementException.class)
	public void toPlaceAtOccupiedPlace() throws InvalidPlacementException, WrongTurnException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.placeMan(Color.WHITE, 0, 0);
	}
	//#3
	@Test(expected = InvalidPlacementException.class)
	public void overPlacement() throws InvalidPlacementException, WrongTurnException{
		int board[] = {1,0,0,2,0,0,1,
				       0,2,0,1,0,2,0,
				       0,0,1,2,1,0,0,
				       2,1,2,0,1,2,1,
				       0,0,2,0,2,0,0,
				       0,0,0,0,0,0,0,
				       0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 1);
		game.placeMan(Color.BLACK, 4, 3);
		game.placeMan(Color.WHITE, 6, 0);
	}
	//#4
	@Test(expected = WrongTurnException.class)
	public void consequentPlacement() throws InvalidPlacementException, WrongTurnException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.placeMan(Color.BLACK, 1, 1);
	}
	//#5
	@Test
	public void toRemoveMan() throws WrongTurnException, InvalidPlacementException, InvalidRemovalException{
		int board[] = {1,0,0,1,0,0,0,
					   0,2,0,2,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 1);
		game.placeMan(Color.BLACK, 0, 6);
		assertEquals("Black's turn to removl.", Color.BLACK, game.getRemovalTurn());
	}
	//#6
	@Test(expected = WrongTurnException.class)
	public void toPlaceInRemovealTime1() throws InvalidPlacementException, WrongTurnException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.placeMan(Color.WHITE, 1, 1);
		game.placeMan(Color.BLACK, 3, 0);
		game.placeMan(Color.WHITE, 1, 3);
		game.placeMan(Color.BLACK, 6, 0);
		game.placeMan(Color.WHITE, 5, 1);
	}
	//#7
	@Test(expected = WrongTurnException.class)
	public void toPlaceInRemovealTime2() throws InvalidPlacementException, WrongTurnException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.placeMan(Color.WHITE, 1, 1);
		game.placeMan(Color.BLACK, 3, 0);
		game.placeMan(Color.WHITE, 1, 3);
		game.placeMan(Color.BLACK, 6, 0);
		game.placeMan(Color.BLACK, 5, 1);
	}
	//#8
	@Test(expected = WrongTurnException.class)
	public void toRemoveAtWrongTime() throws WrongTurnException, InvalidPlacementException, InvalidRemovalException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.removeMan(0, 0);
	}
	//#9
	@Test(expected = InvalidRemovalException.class)
	public void removeOwnMan() throws WrongTurnException, InvalidPlacementException, InvalidRemovalException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.placeMan(Color.WHITE, 1, 1);
		game.placeMan(Color.BLACK, 3, 0);
		game.placeMan(Color.WHITE, 1, 3);
		game.placeMan(Color.BLACK, 6, 0);
		game.removeMan(0, 0);
	}
	//#10
	@Test(expected = InvalidRemovalException.class)
	public void emptyRemoval() throws WrongTurnException, InvalidPlacementException, InvalidRemovalException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.placeMan(Color.WHITE, 1, 1);
		game.placeMan(Color.BLACK, 3, 0);
		game.placeMan(Color.WHITE, 1, 3);
		game.placeMan(Color.BLACK, 6, 0);
		game.removeMan(1, 5);
	}
	//#11
	@Test(expected = InvalidRemovalException.class)
	public void removeManInMill() throws WrongTurnException, InvalidPlacementException, InvalidRemovalException{
		Game game = new Game(Color.BLACK);
		game.placeMan(Color.BLACK, 0, 0);
		game.placeMan(Color.WHITE, 1, 1);
		game.placeMan(Color.BLACK, 3, 0);
		game.placeMan(Color.WHITE, 1, 3);
		game.placeMan(Color.BLACK, 6, 0);
		game.removeMan(1, 3);
		game.placeMan(Color.WHITE, 3, 1);
		game.placeMan(Color.BLACK, 1, 3);
		game.placeMan(Color.WHITE, 5, 1);
		game.removeMan(1, 1);
	}
	//#12
	@Test
	public void removeManInMillWhenNoOther() throws WrongTurnException, InvalidPlacementException, InvalidRemovalException{
		int board[] = {1,0,0,1,0,0,1,
					   0,2,0,2,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0,
					   0,0,0,0,0,0,0};
		Game game = new Game(board, Color.WHITE, 1);
		game.placeMan(Color.WHITE, 1, 5);
		assertEquals("White's turn to removl.", Color.WHITE, game.getRemovalTurn());
		game.removeMan(0,0);
		assertEquals("White's removal completed.", null, game.getRemovalTurn());
		assertEquals("Now black's turn.", Color.BLACK, game.getTurn());
	}
}
