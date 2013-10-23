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
						"matched"+oponentId));
				Thread.sleep(500);
				channelService.sendMessage(new ChannelMessage(oponentId,
						"matched"+channelId));
			} catch (ChannelFailureException e) {
				System.out.println("Channel Failure: " + e.getMessage());
			} catch (InterruptedException e) {
				System.out.println("Why don't you let me sleep?!");
			}
		} else {
			System.out.println("Insert into waiting list: " + channelId);
			waitingPlayerIds.add(channelId);
		}
	}
	
	@Override
	public void changeState(String newState, String channelId) {
		if (playerPairs.containsKey(channelId)) {
			System.out.println("Making move: " + newState + ", from: "
					+ channelId + " to: " + playerPairs.get(channelId));
			try {
				channelService.sendMessage(new ChannelMessage(playerPairs
						.get(channelId), newState));
			} catch (ChannelFailureException e) {
				System.out.println("Channel Failure.");
			}
		} else if (revertedPlayerPairs.containsKey(channelId)) {
			System.out.println("Making move: " + newState + ", from: "
					+ channelId + " to: " + revertedPlayerPairs.get(channelId));
			try {
				channelService.sendMessage(new ChannelMessage(
						revertedPlayerPairs.get(channelId), newState));
			} catch (ChannelFailureException e) {
				System.out.println("Channel Failure: " + e.getMessage());
			}
		} else {
			System.out.println("Warning: no oponent but try to change state!");
		}
	}
}
