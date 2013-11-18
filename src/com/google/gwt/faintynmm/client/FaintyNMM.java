package com.google.gwt.faintynmm.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelError;
import com.google.gwt.appengine.channel.client.ChannelFactoryImpl;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.faintynmm.client.fb.FBCore;
import com.google.gwt.faintynmm.client.fb.FBEvent;
import com.google.gwt.faintynmm.client.game.Color;
import com.google.gwt.faintynmm.client.ui.Presenter;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
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
	private LoginServiceAsync loginService;
	private GameServiceAsync gameService;
	private FaintyNMMMessages messages = GWT.create(FaintyNMMMessages.class);
	private LoginInfo loginInfo = null;

	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(messages.loginMsg());
	private Label welcome = new Label("Welcome!");
	private Anchor signInLink = new Anchor("Login");
	private Presenter presenter;
	private Logger logger = Logger.getLogger("test_logger");

	private FBCore fbCore;
	private FBEvent fbEvent;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Cookies.setCookie("JSESSIONID", "JSESSIONID", null, null, "/", false);
		RootPanel.get("header").add(welcome);
		RootPanel.get("appTitle").add(new Label(messages.faintyNMM()));
		if (RootPanel.get("jsNotEnabledMsg") != null) {
			RootPanel.get("jsNotEnabledMsg").add(
					new Label(messages.jsNotEnabled()));
		}

		// Call XSRF protected login service.
		XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync) GWT
				.create(XsrfTokenService.class);
		((ServiceDefTarget) xsrf).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "xsrf");
		xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>() {
			public void onSuccess(XsrfToken token) {
				logger.log(Level.INFO, "Got XSRF token");
				loginService = GWT.create(LoginService.class);
				gameService = GWT.create(GameService.class);
				((HasRpcToken) loginService).setRpcToken(token);
				((HasRpcToken) gameService).setRpcToken(token);

				fbCore = new FBCore();
				fbEvent = new FBEvent();
				loginInfo = new LoginInfo();
				fbCore.init("198848766966202", true, true, true);
				fbCore.getLoginStatus(new AsyncCallback<JavaScriptObject>() {
					@Override
					public void onFailure(Throwable caught) {
						logger.log(Level.WARNING,
								"Failed to check login status.");
					}

					@Override
					public void onSuccess(JavaScriptObject result) {
						JSONObject js = new JSONObject(result);
						String status = js.get("status").toString().replace("\"", "");
						logger.log(Level.INFO, "Login status: " + status);
						if (status.equals("connected")) {
							loginInfo.setLoggedIn(true);
							JSONObject authInfo = (JSONObject) js
									.get("authResponse");
							loginInfo.setUserId(authInfo.get("userID")
									.toString().replace("\"", ""));
							loginInfo.setAccessToken(authInfo
									.get("accessToken").toString());
							hideLogin();
							loginService.login(loginInfo.getUserId(),
									new AsyncCallback<String>() {
										@Override
										public void onFailure(Throwable caught) {
											logger.log(Level.WARNING,
													"Failed to create channel in server side.");
										}

										@Override
										public void onSuccess(String result) {
											logger.log(Level.INFO,
													"Channel created in server side, token : "+result);
											loginInfo.setToken(result);
											fbCore.api(
													"/me?fields=name",
													new AsyncCallback<JavaScriptObject>() {

														@Override
														public void onFailure(
																Throwable caught) {
															logger.log(
																	Level.WARNING,
																	"Failed to get user name.");
														}

														@Override
														public void onSuccess(
																JavaScriptObject result) {
															JSONObject js = new JSONObject(
																	result);
															logger.log(
																	Level.INFO,
																	"User name: "
																			+ js.get(
																					"name")
																					.toString());
															loginInfo
																	.setUserName(js
																			.get("name")
																			.toString().replace("\"", ""));
															welcome.setText(messages
																	.welcomeLabelMsg(loginInfo
																			.getUserName()));
															createAndListenToChannel(loginInfo
																	.getToken());
															presenter = new Presenter(
																	gameService,
																	loginInfo
																			.getUserId());
															RootPanel
																	.get("gameContainer")
																	.add(presenter
																			.getGraphics());
															hideLogin();
														}
													});
										}
									});
						} else {
							loginInfo.setLoggedIn(false);
							showLogin();
						}
					}
				});
			}

			public void onFailure(Throwable caught) {
				logger.log(Level.WARNING,
						"Failed to call XSRF protected login service : "
								+ caught.getMessage());
			}
		});
	}

	//
	// Create and open channel, and define behaviors when receiving messages.
	//
	private void createAndListenToChannel(String token) {
		logger.log(Level.INFO, "Creating channel on client side.");
		Channel channel = channelFactory.createChannel(token);
		channel.open(new SocketListener() {
			@Override
			public void onOpen() {
				logger.log(Level.INFO, "Channel opened.");
			}

			@Override
			public void onMessage(String msg) {
				msg = msg.trim();
				String[] parameters;
				if (msg.startsWith("!")) {
					parameters = msg.substring(1).split("!");
					if (parameters.length != 3) {
						GWT.log("Error command: " + msg);
					}
					if (parameters[0].equals("black")) {
						presenter.setPlayerColor(Color.BLACK);
					}
					if (parameters[0].startsWith("white")) {
						presenter.setPlayerColor(Color.WHITE);
					}
					presenter.updateMatchInfo(parameters[1], parameters[2]);
				} else {
					parameters = msg.split("!");
					presenter.parseStateString(parameters[0], parameters[1]);
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
		loginPanel.getElement().getStyle()
				.setVerticalAlign(VerticalAlign.MIDDLE);
		SimplePanel anchorHolder = new SimplePanel();
		anchorHolder.setStyleName("loginAnchor");
		anchorHolder.add(signInLink);
		// signInLink.setHref(loginInfo.getLoginUrl());
		loginLabel.setStylePrimaryName("lobster");
		loginPanel.add(loginLabel);
		// loginPanel.add(anchorHolder);
		HTML fbLoginButton = new HTML(
				"<fb:login-button width=\"200\" onlogin=\"window.location.reload()\"></fb:login-button>");
		SimplePanel fbButtonPanel = new SimplePanel();
		fbButtonPanel.getElement().setId("fbloginpanel");
		fbButtonPanel.setStyleName("loginAnchor");
		fbButtonPanel.add(fbLoginButton);
		loginPanel.add(fbButtonPanel);
		signInLink.setStylePrimaryName("lobster");
		RootPanel.get("loginDialog").add(loginPanel);
		DOM.getElementById("loginContainer").getStyle()
				.setDisplay(Display.BLOCK);
		DOM.getElementById("loginDialog").getStyle().setDisplay(Display.BLOCK);
		renderFacebookButton("fbloginpanel");
	}

	//
	// Hide the login panel and allow players to play.
	//
	private void hideLogin() {
		DOM.getElementById("loginBackground").getStyle()
				.setDisplay(Display.NONE);
	}

	public LoginInfo getLoginInfo() {
		return loginInfo;
	}

	private static native void renderFacebookButton(String id) /*-{
		// Render the FB button
		$wnd.FB.XFBML.parse($doc.getElementById(id));
	}-*/;
}
