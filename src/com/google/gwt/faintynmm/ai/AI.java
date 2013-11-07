package com.google.gwt.faintynmm.ai;

import com.google.gwt.faintynmm.client.game.Board;
import com.google.gwt.faintynmm.client.game.Color;

public class AI {
	public static final double MAX = 1000000.0;
	public static final double MIN = -1000000.0;
	public static final int xs[] = { 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3,
			3, 4, 4, 4, 5, 5, 5, 6, 6, 6 };
	public static final int ys[] = { 0, 3, 6, 1, 3, 5, 2, 3, 4, 0, 1, 2, 4, 5,
			6, 2, 3, 4, 1, 3, 5, 0, 3, 6 };

	public static int[] placeMan(Board board, Color turn) {
		int best = -1;
		double bestFitness = MIN;
		for (int i = 0; i < 24; i++) {
			double fitness = alphaBeta(board, new int[] { xs[i], ys[i] }, 0,
					MAX, MIN);
			if (fitness > bestFitness) {
				bestFitness = fitness;
				best = i;
			}
		}
		return new int[] { xs[best], ys[best] };
	}

	public static int[] moveMan(Board board, Color turn) {
		int[] position = { -1, -1, -1, -1 };
		return position;
	}

	private static double alphaBeta(Board board, int[] position,
			int currentDepth, double maxAlpha, double minBeta) {
		double fitness = 0.0;
		return fitness;
	}
}
