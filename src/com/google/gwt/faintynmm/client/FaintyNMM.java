package com.google.gwt.faintynmm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.faintynmm.client.ui.Graphics;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FaintyNMM implements EntryPoint {

	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account.");
	private Label welcome = new Label("Welcome!");
	private Anchor signInLink = new Anchor("Login");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Graphics graphics = new Graphics();
		RootPanel.get("gameContainer").add(graphics);
		RootPanel.get("header").add(welcome);
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							hideLogin();
							setHeader(loginInfo);
						} else {
							showLogin();
						}
					}
				});
	}

	private void setHeader(LoginInfo loginInfo) {
		welcome.setText("Welcome: "+loginInfo.getNickname()+"!");
	}

	private void showLogin() {
		loginPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		loginPanel.setWidth("100%");
		SimplePanel wtf = new SimplePanel();
		wtf.setStyleName("loginAnchor");
		wtf.add(signInLink);
		signInLink.setHref(loginInfo.getLoginUrl());
		loginLabel.setStyleName("lobster");
		loginPanel.add(loginLabel);
		loginPanel.add(wtf);
		signInLink.setStyleName("lobster");
		RootPanel.get("loginDialog").add(loginPanel);
		DOM.getElementById("loginBackground").getStyle()
				.setDisplay(Display.BLOCK);
		DOM.getElementById("loginContainer").getStyle()
				.setDisplay(Display.BLOCK);
		DOM.getElementById("loginDialog").getStyle().setDisplay(Display.BLOCK);
	}

	private void hideLogin() {
		DOM.getElementById("loginBackground").getStyle()
				.setDisplay(Display.NONE);
		DOM.getElementById("loginContainer").getStyle()
				.setDisplay(Display.NONE);
		DOM.getElementById("loginDialog").getStyle().setDisplay(Display.NONE);
	}
}
