package com.google.gwt.faintynmm.server;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.faintynmm.client.LoginService;
import com.google.gwt.faintynmm.client.game.Player;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.googlecode.objectify.Key;

@SuppressWarnings("serial")
public class LoginServiceImpl extends XsrfProtectedServiceServlet implements
		LoginService {
	private ChannelService channelService = ChannelServiceFactory.getChannelService();
	
	@Override
	public String login(String userId, String userName) {
		Player player = OfyService.ofy().load().key(Key.create(Player.class, userId)).now();
		if (player == null){
			player = new Player(userId, userName , 0);
		}
		OfyService.ofy().save().entity(player).now();
		return channelService.createChannel(userId);
	}
}
