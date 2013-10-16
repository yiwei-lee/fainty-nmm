package com.google.gwt.faintynmm.client.ui;

import com.google.gwt.animation.client.Animation;

public class PieceColorAnimation extends Animation {
	private Piece piece;
	private int r1, r2, g1, g2, b1, b2;

	public PieceColorAnimation(Piece piece, String fromColor, String toColor) {
		this.piece = piece;
		parseColor(fromColor.toLowerCase(), toColor.toLowerCase());
	}

	@Override
	protected void onUpdate(double progress) {
		int r, g, b;
		r = (int) ((1.0 - progress) * r1 + progress * r2);
		g = (int) ((1.0 - progress) * g1 + progress * g2);
		b = (int) ((1.0 - progress) * b1 + progress * b2);
		piece.getElement().getStyle().setBackgroundColor(rgbToHexString(r, g, b));
	}

	private void parseColor(String color1, String color2) {
		if (color1.equals("black")) {
			r1 = g1 = b1 = 0;
		} else if (color1.equals("white")) {
			r1 = g1 = b1 = 255;
		} else {
			r1 = 255;
			g1 = 69;
			b1 = 0;
		}
		if (color2.equals("black")) {
			r2 = g2 = b2 = 0;
		} else if (color2.equals("white")) {
			r2 = g2 = b2 = 255;
		} else {
			r2 = 255;
			g2 = 69;
			b2 = 0;
		}
	}

	private static String rgbToHexString(final int r, final int g, final int b) {
		return "#" + (r < 16 ? "0" : "") + Integer.toHexString(r)
				+ (g < 16 ? "0" : "") + Integer.toHexString(g)
				+ (b < 16 ? "0" : "") + Integer.toHexString(b);
	}
}
