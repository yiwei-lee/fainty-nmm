package com.google.gwt.faintynmm.client;

import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelError;
import com.google.gwt.appengine.channel.client.ChannelFactoryImpl;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.faintynmm.client.ui.Graphics;
import com.google.gwt.faintynmm.client.ui.Presenter;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
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
	private Socket socket;
	private LoginInfo loginInfo = null;

	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account.");
	private Label welcome = new Label("Welcome!");
	private Label connectionStatus = new Label(
			"---Waiting for another player---");
	private Anchor signInLink = new Anchor("Login");
	private Presenter presenter;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("header").add(welcome);
		RootPanel.get("header").add(connectionStatus);
//		loginService.login(GWT.getHostPageBaseURL(),
		loginService.login(GWT.getHostPageBaseURL()+"fainty-nmm.html?gwt.codesvr=127.0.0.1:9997",
				new AsyncCallback<LoginInfo>() {
					//
					// This should not happen...
					//
					public void onFailure(Throwable error) {
						Graphics.sendWarning("WTH?!", 0, 0);
					}

					//
					// Check if successfully login. If not, show login panel.
					// Otherwise show game board.
					//
					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							presenter = new Presenter(gameService, loginInfo.getEmailAddress());
							RootPanel.get("gameContainer").add(presenter.getGraphics());
							hideLogin();
							createAndListenToChannel(loginInfo.getToken());
							welcome.setText("Welcome: "
									+ loginInfo.getNickname() + "!");
							presenter.notifyServer();
						} else {
							showLogin();
						}
					}
				});
		Window.addCloseHandler(new CloseHandler<Window>() {
			@Override
			public void onClose(CloseEvent<Window> event) {
				closeChannel();
			}
		});
	}

	//
	// Close channel and do some cleanup.
	//
	private void closeChannel() {
		if (socket != null) socket.close();
	}

	//
	// Create and open channel, and define behaviors when receiving messages.
	//
	private void createAndListenToChannel(String token) {
		assert (presenter != null);
		Channel channel = channelFactory.createChannel(token);
		socket = channel.open(new SocketListener() {
			@Override
			public void onOpen() {
			}

			@Override
			public void onMessage(String newState) {
				if (newState.startsWith("matched")) {
					connectionStatus.setText("---Connected---");
				} else {
					presenter.parseStateString(newState);
				}
			}

			@Override
			public void onError(ChannelError error) {
			}

			@Override
			public void onClose() {
			}
		});
	}

	//
	// Show the login panel and keep players from playing before login.
	//
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

	//
	// Hide the login panel and allow players to play.
	//
	private void hideLogin() {
		DOM.getElementById("loginBackground").getStyle()
				.setDisplay(Display.NONE);
	}
}
