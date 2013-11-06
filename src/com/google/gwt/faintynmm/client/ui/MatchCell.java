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
	// interface Templates extends SafeHtmlTemplates {
	// @SafeHtmlTemplates.Template(" <div style='border-style:none none solid none; height:78px; width:360px; vertical-align:middle;'>"
	// +
	// "<div style='float:left; margin-right:4px; height:64px; width:64px; padding-top: 6.4px;'>"
	// +
	// "	<div style='{0}height: 80%; width: 80%; border-style: inset; border-radius: 32px;margin: auto;' />"
	// + "</div>"
	// + "<div style='clear:right;"
	// +
	// "-webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none;"
	// + "test-align: cetner; vertical-align: middle; margin: auto;'>"
	// + "	<div style='font-family: \"NightBits\", serif;'>"
	// + "		<ui:text from='{opponentMsg}' />"
	// + "	</div>"
	// + "	<div style='font-family: \"NightBits\", serif;'>"
	// + "		<ui:text from='{currentPlayerMsg}' />"
	// + "	</div>"
	// + "	<div style='font-family: \"NightBits\", serif;'>"
	// + "		<ui:text from='{lastUpdateDateMsg}' />"
	// + "	</div>"
	// + "	<span ui:field='delete' style='font-family: \"NightBits\", serif;"
	// +
	// "margin: auto; color: Blue; text-decoration: underline; text-align: right; display: block;'>"
	// + "		<ui:text from='{deleteButton}' />"
	// + "	</span>"
	// + "</div>"
	// + "</div>")
	// SafeHtml cell(SafeStyles color, SafeHtml opponentMsg,
	// SafeHtml currentPlayerMsg, SafeHtml lastUpdateDateMsg,
	// SafeHtml deleteButtonMsg);
	// }
	//
	// private static Templates templates = GWT.create(Templates.class);

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
		//
		// // Get localized strings from messages.
		FaintyNMMMessages messages = presenter.getMessages();
		String opponentMsg = messages.opponentMsg(opponentId);
		String currentPlayerMsg = messages.currentPlayerMsg(currentPlayerId);
		String lastUpdateDateMsg = messages.lastUpdateDateMsg(DateTimeFormat
				.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(date));

		// Send data to renderer.
		uiRenderer.render(safeHtmlBuilder, opponentMsg, currentPlayerMsg,
				pieceColor, lastUpdateDateMsg, messages.deleteButtonMsg());

		// SafeStyles color = SafeStylesUtils.fromTrustedString(SafeHtmlUtils
		// .fromString("background-color:" + pieceColor + ";").toString());
		// SafeHtml opponentMsg = SafeHtmlUtils.fromString(messages
		// .opponentMsg(opponentId));
		// SafeHtml currentPlayerMsg = SafeHtmlUtils.fromString(messages
		// .currentPlayerMsg(currentPlayerId));
		// SafeHtml lastUpdateDateMsg = SafeHtmlUtils.fromString(messages
		// .lastUpdateDateMsg(DateTimeFormat.getFormat(
		// PredefinedFormat.DATE_TIME_MEDIUM).format(date)));
		// SafeHtml deleteButtonMsg = SafeHtmlUtils.fromString(messages
		// .deleteButtonMsg());
		// SafeHtml rendered = templates.cell(color, opponentMsg,
		// currentPlayerMsg, lastUpdateDateMsg, deleteButtonMsg);
		// safeHtmlBuilder.append(rendered);
	}

	@UiHandler({ "delete" })
	void onDeleteMatchClicked(ClickEvent event, Element parent, Match value) {
		if (Window.confirm(presenter.getMessages().confirmDeleteMatchMsg())) {
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
