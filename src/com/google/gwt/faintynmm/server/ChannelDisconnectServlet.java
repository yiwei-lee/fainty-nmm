package com.google.gwt.faintynmm.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.faintynmm.client.game.Player;
import com.googlecode.objectify.Key;

@SuppressWarnings("serial")
public class ChannelDisconnectServlet extends HttpServlet {
	private ChannelService channelService = ChannelServiceFactory
			.getChannelService();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		ChannelPresence presence = channelService.parsePresence(req);
		String playerId = presence.clientId();

		// Update datastore.
		Player player = OfyService.ofy().load().key(Key.create(Player.class, playerId)).now();
		player.decConnectedDeviceNumber();
		OfyService.ofy().save().entity(player).now();

		// For testing.
		System.out.println(playerId + " disconnected. Connected device number: "+player.getConnectedDeviceNumber());
	}
}
