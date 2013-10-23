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
	private HashMap<String, String> playerPairs = new HashMap<String, String>();
	private HashMap<String, String> revertedPlayerPairs = new HashMap<String, String>();
	private ArrayList<String> waitingPlayerIds = new ArrayList<String>();
	private ChannelService channelService = ChannelServiceFactory
			.getChannelService();


	@Override
	public void enterGame(String channelId) {
		if (!waitingPlayerIds.isEmpty()) {
			String oponentId = waitingPlayerIds.get(0);
			if (channelId.equals(oponentId))
				return;
			waitingPlayerIds.remove(0);
			playerPairs.put(channelId, oponentId);
			revertedPlayerPairs.put(oponentId, channelId);
			System.out.println("Making match: " + channelId + ", " + oponentId);
			try {
				channelService.sendMessage(new ChannelMessage(channelId,
						"!matched"));
				channelService.sendMessage(new ChannelMessage(oponentId,
						"!matched"));
			} catch (ChannelFailureException e) {
				System.out.println("Channel Failure: " + e.getMessage());
			}
		} else {
			System.out.println("Insert into waiting list: " + channelId);
			waitingPlayerIds.add(channelId);
		}
	}
	
	@Override
	public void changeState(String newState, String channelId) {
		String oponentId = getOponentId(channelId);
		if (oponentId != null){
			System.out.println("Making move: " + newState + ", from: "
					+ channelId + " to: " + oponentId);
			try {
				channelService.sendMessage(new ChannelMessage(oponentId, newState));
			} catch (ChannelFailureException e) {
				System.out.println("Channel Failure.");
			}
		} else {
			System.out.println("Warning: no oponent but try to change state!");
		}
	}

	private String getOponentId(String id){
		if (playerPairs.containsKey(id)) {
			return playerPairs.get(id);
		} else if (revertedPlayerPairs.containsKey(id)) {
			return revertedPlayerPairs.get(id);
		} else {
			return null;
		}
	}
}
