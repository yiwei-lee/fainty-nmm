package com.google.gwt.faintynmm.client.ui;

import java.util.Date;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.faintynmm.client.FaintyNMMMessages;
import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.Window;

public class MatchCell extends AbstractCell<Match> {

	public interface Renderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String opponentMsg,
				String currentPlayerMsg, String pieceColor,
				String lastUpdateDateMsg, String deleteButton);

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
		} else if (event.getType().equals(BrowserEvents.CLICK)) {
			event.preventDefault();
		}
	}

	@Override
	public void render(Context context, Match value,
			SafeHtmlBuilder safeHtmlBuilder) {
		if (value == null)
			return;
		// Get date here and pass them into uiRenderer, which will do the real
		// job.
		String blackPlayerId = value.getBlackPlayerId();
		String whitePlayerId = value.getWhitePlayerId();
		String opponentId;
		String currentPlayerId = value.getCurrentPlayerId();
		String pieceColor;
		Date date = value.getLastUpdateDate();

		if (blackPlayerId.equals(playerId)) {
			pieceColor = "Black";
			opponentId = whitePlayerId;
		} else {
			pieceColor = "White";
			opponentId = blackPlayerId;
		}

		// Get localized strings from messages.
		FaintyNMMMessages messages = presenter.getMessages();
		String opponentMsg = messages.opponentMsg(opponentId);
		String currentPlayerMsg = messages.currentPlayerMsg(currentPlayerId);
		String lastUpdateDateMsg = messages.lastUpdateDateMsg(DateTimeFormat
				.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(date));
		
		// Send data to renderer.
		uiRenderer.render(safeHtmlBuilder, opponentMsg, currentPlayerMsg,
				pieceColor, lastUpdateDateMsg, messages.deleteButtonMsg());
	}

	@UiHandler({ "delete" })
	void onDeleteMatchClicked(ClickEvent event, Element parent, Match value) {
		if (Window.confirm("Do you really want to delete the match?")) {
			presenter.deleteMatch(value.getMatchId());
		}
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
}
