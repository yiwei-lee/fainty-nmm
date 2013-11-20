package com.google.gwt.faintynmm.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.faintynmm.client.FaintyNMMMessages;
import com.google.gwt.faintynmm.client.FriendInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;

public class FriendCell extends AbstractCell<FriendInfo> {
	public interface Renderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String picSrc,
				String friendName, String newGameButton);

		void onBrowserEvent(FriendCell o, NativeEvent e, Element p, FriendInfo value);
	}

	private final Renderer uiRenderer = GWT.create(Renderer.class);
	private Presenter presenter;

	public FriendCell(Presenter presenter) {
		super(BrowserEvents.DBLCLICK, BrowserEvents.CLICK);
		this.presenter = presenter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onBrowserEvent(Context context, Element parent, FriendInfo value,
			NativeEvent event, ValueUpdater valueUpdater) {
		uiRenderer.onBrowserEvent(this, event, parent, value);
		if (event.getType().equals(BrowserEvents.CLICK)) {
			event.preventDefault();
		}
	}

	@Override
	public void render(Context context, FriendInfo value,
			SafeHtmlBuilder safeHtmlBuilder) {
		if (value == null)
			return;
		//
		// Get localized strings from messages.
		//
		FaintyNMMMessages messages = presenter.getMessages();
		String newGameButtonMsg = messages.newGameButtonMsg(value.friendName.split(" ")[0]);

		// Send data to renderer.
		uiRenderer.render(safeHtmlBuilder, value.picScr, value.friendName,
				newGameButtonMsg);
	}

	@UiHandler({ "newGame" })
	void onNewGameClicked(ClickEvent event, Element parent, FriendInfo value) {
		presenter.startNewMatchWithFriend(value.friendId, value.friendName);
	}
}
