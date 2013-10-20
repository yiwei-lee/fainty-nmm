package com.google.gwt.faintynmm.client;

import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelError;
import com.google.gwt.appengine.channel.client.ChannelFactoryImpl;
import com.google.gwt.appengine.channel.client.SocketListener;
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

	private ChannelFactoryImpl channelFactory = new ChannelFactoryImpl();
	private LoginServiceAsync loginService = GWT.create(LoginService.class);
	private GameServiceAsync gameService = GWT.create(GameService.class);
	private LoginInfo loginInfo = null;

	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account.");
	private Label welcome = new Label("Welcome!");
	private Label connectionStatus = new Label("---Waiting another player---");
	private Anchor signInLink = new Anchor("Login");
	private Graphics graphics;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		graphics = new Graphics(gameService);
		RootPanel.get("gameContainer").add(graphics);
		RootPanel.get("header").add(welcome);
		RootPanel.get("header").add(connectionStatus);
		//
		// Have to change the URL for deployment.
		//
		loginService.login(GWT.getHostPageBaseURL()+"fainty-nmm.html?gwt.codesvr=127.0.0.1:9997",
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						System.err.println("WTH?!");
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							hideLogin();
							createAndListenToChannel(loginInfo.getToken());
							welcome.setText("Welcome: "
									+ loginInfo.getNickname() + "!");
							graphics.setChannelId(loginInfo.getEmailAddress());
							graphics.notifyServer();
						} else {
							showLogin();
						}
					}
				});
	}

	private void createAndListenToChannel(String token) {
		assert (graphics != null);
		Channel channel = channelFactory.createChannel(token);
		channel.open(new SocketListener() {
			@Override
			public void onOpen() {
			}

			@Override
			public void onMessage(String newState) {
				System.out.println("Channel message: " + newState);
				if (newState.equals("!")) {
					connectionStatus.setText("---Connected---");
				} else {
					graphics.getPresenter().parseStateString(newState);
				}
			}

			@Override
			public void onError(ChannelError error) {
				System.err.println("Channel error: " + error.getCode() + " "
						+ error.getDescription());
			}

			@Override
			public void onClose() {
				System.out.println("Channel closed.");
			}
		});
	}

	private void showLogin() {
		loginPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		loginPanel.setWidth("100%");
		SimplePanel anchorHolder = new SimplePanel();
		anchorHolder.setStyleName("loginAnchor");
		anchorHolder.add(signInLink);
		signInLink.setHref(loginInfo.getLoginUrl());
		loginLabel.setStyleName("lobster");
		loginPanel.add(loginLabel);
		loginPanel.add(anchorHolder);
		signInLink.setStyleName("lobster");
		RootPanel.get("loginDialog").add(loginPanel);
		DOM.getElementById("loginContainer").getStyle()
				.setDisplay(Display.BLOCK);
		DOM.getElementById("loginDialog").getStyle().setDisplay(Display.BLOCK);
	}

	private void hideLogin() {
		DOM.getElementById("loginBackground").getStyle()
				.setDisplay(Display.NONE);
	}
}
