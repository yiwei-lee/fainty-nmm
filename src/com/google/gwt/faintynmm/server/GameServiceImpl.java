package com.google.gwt.faintynmm.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.faintynmm.client.GameService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GameServiceImpl extends RemoteServiceServlet implements
		GameService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static HashMap<String, String> playerPairs = new HashMap<String, String>();
	private static HashMap<String, String> revertedPlayerPairs = new HashMap<String, String>();
	private static ArrayList<String> waitingPlayerIds = new ArrayList<String>();
	private static ChannelService channelService = ChannelServiceFactory
			.getChannelService();

	@Override
	public void changeState(String newState, String channelId) {
		if (playerPairs.containsKey(channelId)) {
			System.out.println("Making move: " + newState + ", from: "
					+ channelId + " to: " + playerPairs.get(channelId));
			try{
				channelService.sendMessage(new ChannelMessage(playerPairs
						.get(channelId), newState));
			} catch(ChannelFailureException e){
				System.out.println("Channel Failure.");
			}
		} else if (revertedPlayerPairs.containsKey(channelId)) {
			System.out.println("Making move: " + newState + ", from: "
					+ channelId + " to: " + revertedPlayerPairs.get(channelId));
			try{
				channelService.sendMessage(new ChannelMessage(revertedPlayerPairs
						.get(channelId), newState));
			} catch(ChannelFailureException e){
				System.out.println("Channel Failure.");
			}
		} else if (!waitingPlayerIds.isEmpty()) {
			String oponentId = waitingPlayerIds.get(0);
			if (channelId == oponentId) return;
			System.out.println("Making match: " + channelId + ", " + oponentId);
			waitingPlayerIds.remove(0);
			playerPairs.put(channelId, oponentId);
			revertedPlayerPairs.put(oponentId, channelId);
			channelService.sendMessage(new ChannelMessage(channelId, "!"));
			channelService.sendMessage(new ChannelMessage(oponentId, "!"));
		} else {
			System.out.println("Insert into waiting list: " + channelId);
			waitingPlayerIds.add(channelId);
		}
	}
}
