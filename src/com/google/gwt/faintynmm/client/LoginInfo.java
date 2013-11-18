package com.google.gwt.faintynmm.client;

import java.io.Serializable;

public class LoginInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean loggedIn = false;
	private String userId;
	private String userName;
	private String accessToken;
	private String token;
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}