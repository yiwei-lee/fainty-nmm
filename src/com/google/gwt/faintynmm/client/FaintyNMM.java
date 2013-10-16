package com.google.gwt.faintynmm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.faintynmm.client.ui.Graphics;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FaintyNMM implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Graphics graphics = new Graphics();
		graphics.getPresenter().loadGame();
		RootPanel.get("gameContainer").add(graphics);
		if (!History.getToken().equals(""))
			History.fireCurrentHistoryState();
	}
}
