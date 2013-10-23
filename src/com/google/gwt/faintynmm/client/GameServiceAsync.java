package com.google.gwt.faintynmm.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GameServiceAsync {
	public void enterGame(String channelId, AsyncCallback<Void> async);
	public void changeState(String newState, String oponentId, AsyncCallback<Void> async);
}
