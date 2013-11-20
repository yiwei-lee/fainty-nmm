package com.google.gwt.faintynmm.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void login(String userId, String userName, AsyncCallback<String> async);
}
