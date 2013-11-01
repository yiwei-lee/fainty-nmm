package com.google.gwt.faintynmm.client.ui;

import java.util.Date;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;

public class MatchCell extends AbstractCell<Match> {
	// This can be compared to UiBinder --> here where all magic is done
	public interface Renderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String opponentId,
				String currentPlayerId, String pieceColor, String lastUpdateDate);

		void onBrowserEvent(MatchCell o, NativeEvent e, Element p, Match n);
	}

	private final Renderer uiRenderer = GWT.create(Renderer.class);
	private Presenter presenter;
	private String playerId;

	public MatchCell(String playerId, Presenter presenter) {
		super(BrowserEvents.DBLCLICK, BrowserEvents.CLICK);
		this.presenter = presenter;
		this.playerId = playerId;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onBrowserEvent(Context context, Element parent, Match value,
			NativeEvent event, ValueUpdater valueUpdater) {
		uiRenderer.onBrowserEvent(this, event, parent, value);
		if (event.getType().equals(BrowserEvents.DBLCLICK)) {
			presenter.loadMatch(value);
		}
	}

	@Override
	public void render(Context context, Match value,
			SafeHtmlBuilder safeHtmlBuilder) {
		if (value == null)
			return;
		// Get date here and pass them into uiRenderer, which will do the real job.
		String blackPlayerId = value.getBlackPlayerId();
		String whitePlayerId = value.getWhitePlayerId();
		String opponentId;
		String currentPlayerId = value.getCurrentPlayerId();
		String pieceColor;
		Date date = value.getLastUpdateDate();
		String lastUpdateDate = date.toString();
		if (blackPlayerId.equals(playerId)) {
			pieceColor = "Black";
			opponentId = whitePlayerId;
		} else {
			pieceColor = "White";
			opponentId = blackPlayerId;
		}

		// We directly the uiRenderer and we pass the HtmlBuilder
		uiRenderer.render(safeHtmlBuilder, opponentId, currentPlayerId,
				pieceColor, lastUpdateDate);
	}

	@UiHandler({ "abandon" })
	void onAbandonPersonClicked(ClickEvent event, Element parent, Match value) {
//		Graphics.showWarning("Do you want to abandon : " + value.getMatchId()+"?", left, top);
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
}
