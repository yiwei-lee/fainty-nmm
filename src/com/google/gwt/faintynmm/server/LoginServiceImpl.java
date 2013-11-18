package com.google.gwt.faintynmm.server;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.faintynmm.client.LoginInfo;
import com.google.gwt.faintynmm.client.LoginService;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

@SuppressWarnings("serial")
public class LoginServiceImpl extends XsrfProtectedServiceServlet implements
		LoginService {
	private ChannelService channelService = ChannelServiceFactory.getChannelService();
	
	@Override
	public String login(String userId) {
		return channelService.createChannel(userId);
	}
}
