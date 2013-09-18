package org.shared.nmm.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.shared.nmm.exception.InvalidMovementException;
import org.shared.nmm.exception.InvalidPlacementException;
import org.shared.nmm.exception.InvalidRemovalException;
import org.shared.nmm.exception.WrongTurnException;
import org.shared.nmm.game.Color;
import org.shared.nmm.game.Game;

public class PhaseTwoStateTest {
	//#13
	@Test
	public void intoPhaseTwo() throws WrongTurnException, InvalidPlacementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,2,0,
			           0,0,1,2,1,0,0,
			           2,1,2,0,1,2,1,
			           0,0,2,1,0,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,0};
		Game game = new Game(board, Color.WHITE, 1);
		game.placeMan(Color.WHITE, 4, 4);
		assertEquals("Into phase 2.", 1, game.getPhase());
	}
	//#14
	@Test
	public void toMoveMan() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,2,0,
			           0,0,1,2,1,0,0,
			           2,1,2,0,1,2,1,
			           0,0,2,1,2,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 2);
		assertEquals("Black's turn.", Color.BLACK, game.getTurn());
		game.moveMan(Color.BLACK, 2, 2, 2, 1);
		assertEquals("White's turn.", Color.WHITE, game.getTurn());
	}
	//#15
	@Test(expected = InvalidMovementException.class)
	public void toMoveToInvalidPlace() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,2,0,
			           0,0,1,2,1,0,0,
			           2,1,2,0,1,2,1,
			           0,0,2,1,2,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 2);
		assertEquals("Black's turn.", Color.BLACK, game.getTurn());
		game.moveMan(Color.BLACK, 0, 0, 1, 0);
	}
	//#16
	@Test(expected = InvalidMovementException.class)
	public void toMoveMoreThanOneBlock() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,2,0,
			           0,0,1,2,1,0,0,
			           2,1,2,0,1,2,1,
			           0,0,2,1,2,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 2);
		game.moveMan(Color.BLACK, 6, 3, 3, 6);
	}
	//#17
	@Test(expected = InvalidMovementException.class)
	public void toMoveToOccupiedPlace() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,2,0,
			           0,0,1,2,1,0,0,
			           2,1,2,0,1,2,1,
			           0,0,2,1,2,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 2);
		assertEquals("Black's turn.", Color.BLACK, game.getTurn());
		game.moveMan(Color.BLACK, 2, 2, 3, 2);
	}
	//#18
	@Test(expected = WrongTurnException.class)
	public void consequentMovement() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,2,0,
			           0,0,1,2,1,0,0,
			           2,1,2,0,1,2,1,
			           0,0,2,1,2,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 2);
		game.moveMan(Color.BLACK, 3, 4, 3, 5);
		game.moveMan(Color.BLACK, 3, 5, 3, 6);
	}
	//#19
	@Test(expected = WrongTurnException.class)
	public void emptyMovement() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,2,0,
			           0,0,1,2,1,0,0,
			           2,1,2,0,1,2,1,
			           0,0,2,1,2,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,0};
		Game game = new Game(board, Color.BLACK, 2);
		game.moveMan(Color.BLACK, 3, 5, 3, 6);
	}
	//#20
	@Test
	public void toRemoveMan() throws WrongTurnException, InvalidRemovalException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
				       0,2,0,1,0,2,0,
				       0,0,1,2,1,0,0,
				       2,1,2,0,1,2,2,
				       0,0,2,1,0,0,0,
				       0,0,0,0,0,0,0,
				       0,0,0,0,0,0,1};
		Game game = new Game(board, Color.BLACK, 2);
		game.moveMan(Color.BLACK, 3, 4, 4, 4);
		assertEquals("Black's turn to removl.", Color.BLACK, game.getRemovalTurn());
		game.removeMan(3, 0);
		assertEquals("White's turn to move.", Color.WHITE, game.getTurn());
	}
	//#21
	@Test(expected = WrongTurnException.class)
	public void toMoveAtRemovalTime() throws WrongTurnException, InvalidRemovalException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
				       0,2,0,1,0,2,0,
				       0,0,1,2,1,0,0,
				       2,1,2,0,1,2,2,
				       0,0,2,1,0,0,0,
				       0,0,0,0,0,0,0,
				       0,0,0,0,0,0,1};
		Game game = new Game(board, Color.BLACK, 2);
		game.moveMan(Color.BLACK, 3, 4, 4, 4);
		game.moveMan(Color.WHITE, 0, 3, 0, 6);
	}
}
