package com.google.gwt.faintynmm.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	public void login(String url, AsyncCallback<LoginInfo> async);
}
