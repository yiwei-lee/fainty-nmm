package org.shared.nmm.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.shared.nmm.exception.InvalidMovementException;
import org.shared.nmm.exception.InvalidRemovalException;
import org.shared.nmm.exception.WrongTurnException;
import org.shared.nmm.game.Color;
import org.shared.nmm.game.Game;

public class PhaseThreeStateTest {
	//#22
	@Test
	public void intoPhaseThree() throws WrongTurnException, InvalidRemovalException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
				       0,2,0,1,0,0,0,
				       0,0,1,2,1,0,0,
				       2,1,0,0,1,0,0,
				       0,0,0,1,0,0,0,
				       0,0,0,0,0,0,0,
				       0,0,0,0,0,0,1};
		Game game = new Game(board, Color.BLACK, 2);
		game.moveMan(Color.BLACK, 4, 3, 4, 4);
		game.removeMan(3, 0);
		assertEquals("Into phase 3.", 3, game.getPhase());
	}
	//#23
	@Test
	public void toFlyMan() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,0,0,
			           0,0,1,2,1,0,0,
			           0,1,0,0,1,0,0,
			           0,0,0,0,1,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,1};
		Game game = new Game(board, Color.WHITE, 3);
		game.flyMan(Color.WHITE, 1, 1, 1, 5);
		assertEquals("Black's turn to move.", Color.BLACK, game.getTurn());
	}
	//#24
	@Test(expected = InvalidMovementException.class)
	public void toFlyManOfStrongerSide() throws WrongTurnException, InvalidMovementException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,0,0,
			           0,0,1,2,1,0,0,
			           0,1,0,0,1,0,0,
			           0,0,0,0,1,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,1};
		Game game = new Game(board, Color.BLACK, 3);
		game.flyMan(Color.BLACK, 0, 0, 3, 6);
	}
	//#25
	@Test
	public void toWin() throws WrongTurnException, InvalidMovementException, InvalidRemovalException{
		int board[] = {1,0,0,2,0,0,1,
			           0,2,0,1,0,0,0,
			           0,0,1,2,1,0,0,
			           0,1,0,0,0,1,0,
			           0,0,0,0,1,0,0,
			           0,0,0,0,0,0,0,
			           0,0,0,0,0,0,1};
		Game game = new Game(board, Color.BLACK, 3);
		game.moveMan(Color.BLACK, 3, 5, 3, 4);
		game.removeMan(0, 3);
		assertEquals("Black is the winner.", Color.BLACK, game.getWinner());
	}
}
