package com.google.gwt.faintynmm.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.server.rpc.XsrfProtect;

@RemoteServiceRelativePath("login")
@XsrfProtect
public interface LoginService extends RemoteService{
	public String login(String userId, String userName);
}
