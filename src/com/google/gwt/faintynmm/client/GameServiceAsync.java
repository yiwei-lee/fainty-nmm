package com.google.gwt.faintynmm.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GameServiceAsync {
	public void changeState(String newState, String channelId, AsyncCallback<Void> async);
}
