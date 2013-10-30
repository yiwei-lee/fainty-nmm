package com.google.gwt.faintynmm.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.faintynmm.client.GameService;
import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.faintynmm.client.game.Player;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;

@SuppressWarnings("serial")
public class GameServiceImpl extends RemoteServiceServlet implements
		GameService {

//	private static HashMap<String, String> playerPairs = new HashMap<String, String>();
//	private static HashMap<String, String> revertedPlayerPairs = new HashMap<String, String>();
//	private static ArrayList<String> waitingPlayerIds = new ArrayList<String>();
	private final static String DEFAULT_STATE = "1109999000000000000000000000000";
	private static Comparator<Match> matchComparator = new Comparator<Match>() {
		@Override
		public int compare(Match arg0, Match arg1) {
			return arg0.getLastUpdateDate().compareTo(arg1.getLastUpdateDate());
		}
	};
	private static ChannelService channelService = ChannelServiceFactory
			.getChannelService();

	@Override
	public void initialize(String channelId) {
		// TODO Auto-generated method stub
	}

	@Override
	public String startNewMatch(String blackPlayerId, String whitePlayerId) {
		// if (!waitingPlayerIds.isEmpty()) {
		// String oponentId = waitingPlayerIds.get(0);
		// if (channelId.equals(oponentId))
		// return;
		// waitingPlayerIds.remove(0);
		// playerPairs.put(channelId, oponentId);
		// revertedPlayerPairs.put(oponentId, channelId);
		// System.out.println("Making match: " + channelId + ", " + oponentId);
		// try {
		// channelService.sendMessage(new ChannelMessage(channelId,
		// "!matched"));
		// channelService.sendMessage(new ChannelMessage(oponentId,
		// "!matched"));
		// } catch (ChannelFailureException e) {
		// System.out.println("Channel Failure: " + e.getMessage());
		// }
		// } else {
		// System.out.println("Insert into waiting list: " + channelId);
		// waitingPlayerIds.add(channelId);
		// }
		try {
			// Get match id.
			String matchId = generateRandomString(16);
			while (OfyService.ofy().load().key(Key.create(Match.class, matchId)).now() != null){
				matchId = generateRandomString(16);
			}
			System.out.println("Match id: "+matchId);
			
			// Notify players of the new match.
			Player blackPlayer = OfyService.ofy().load()
					.key(Key.create(Player.class, blackPlayerId)).now();
			Player whitePlayer = OfyService.ofy().load()
					.key(Key.create(Player.class, whitePlayerId)).now();
			if (blackPlayer == null) {
				blackPlayer = new Player(blackPlayerId, 0);
			} else if (blackPlayer.isConnectd()) {
				sendMessage(blackPlayerId, "!black");
				sendMessage(blackPlayerId, DEFAULT_STATE);
			}
			if (whitePlayer == null) {
				whitePlayer = new Player(whitePlayerId, 0);
			} else if (whitePlayer.isConnectd()) {
				sendMessage(whitePlayerId, "!white");
				sendMessage(whitePlayerId, DEFAULT_STATE);
			}
			blackPlayer.getMatchIds().add(matchId);
			whitePlayer.getMatchIds().add(matchId);

			// Save the new match into datastore, and update player info.
			Match match = new Match(matchId, blackPlayerId, whitePlayerId);
			OfyService.ofy().save().entity(match).now();
			OfyService.ofy().save().entity(blackPlayer).now();
			OfyService.ofy().save().entity(whitePlayer).now();
			return matchId;
		} catch (ChannelFailureException e) {
			System.out.println("Channel Failure: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void changeState(String newState, String matchId, String playerId, String oponentId) {
		System.out.println("Making move: " + newState + ", from: "
				+ playerId + " to: " + oponentId);
		sendMessage(oponentId, newState);
		sendMessage(playerId, newState);
		Match match = OfyService.ofy().load().key(Key.create(Match.class, matchId)).now();
		match.setStateString(newState);
		match.setLastUpdateDate(new Date());
		match.switchTurn();
		OfyService.ofy().save().entity(match).now();
	}

	@Override
	public ArrayList<Match> getMatchList(String channelId) {
		ArrayList<Match> matchList = new ArrayList<Match>();
		List<Match> allMatches = OfyService.ofy().load().type(Match.class)
				.list();
		for (Match match : allMatches) {
			if (match.isInGame(channelId))
				matchList.add(match);
		}
		Collections.sort(matchList, matchComparator);
		return matchList;
	}

	@Override
	public Match loadMatch(String playerId, String matchId) {
		Match match = OfyService.ofy().load().key(Key.create(Match.class, matchId)).now();
		if (match == null){
			System.err.println("No such match on server side!");
			return null;
		}
		sendMessage(match.getBlackPlayerId(), "!black");
		sendMessage(match.getBlackPlayerId(), match.getStateString());
		sendMessage(match.getWhitePlayerId(), "!white");
		sendMessage(match.getWhitePlayerId(), match.getStateString());
		return match;
	}

	public void sendMessage(String channelId, String message) {
		Player player = OfyService.ofy().load()
				.key(Key.create(Player.class, channelId)).now();
		assert (player != null);
		if (player.isConnectd()) {
			channelService.sendMessage(new ChannelMessage(channelId, message));
		} else {
			System.err.println("Player :"+channelId+" is not connected.");
		}
	}
	
	private String generateRandomString(int length) {
	    char charSet[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        sb.append(charSet[random.nextInt(charSet.length)]);   
	    }   
	    return sb.toString();   
	 }   
}
