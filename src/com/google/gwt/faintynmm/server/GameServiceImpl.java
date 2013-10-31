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
import com.googlecode.objectify.VoidWork;

@SuppressWarnings("serial")
public class GameServiceImpl extends RemoteServiceServlet implements
		GameService {

	// Default state when a game start.
	private final static String DEFAULT_STATE = "1109999000000000000000000000000";
	// Comparator used to sort match list according to last update time.
	private static Comparator<Match> matchComparator = new Comparator<Match>() {
		@Override
		public int compare(Match arg0, Match arg1) {
			return -(arg0.getLastUpdateDate().compareTo(arg1
					.getLastUpdateDate()));
		}
	};
	private static ChannelService channelService = ChannelServiceFactory
			.getChannelService();

	@Override
	public void initialize(String channelId) {
		// TODO Auto-generated method stub
	}

	/**
	 * Start a new match between two players.
	 * 
	 * @param blackPlayerId
	 *            Id of black player.
	 * @param whitePlayerId
	 *            Id of white player.
	 * 
	 */
	@Override
	public void startNewMatch(String blackPlayerId, String whitePlayerId) {
		try {
			// Get match id.
			String matchId = generateRandomString(16);
			while (OfyService.ofy().load()
					.key(Key.create(Match.class, matchId)).now() != null) {
				matchId = generateRandomString(16);
			}
			System.out.println("Match id: " + matchId);

			OfyService.ofy().transact(new VoidWork() {

				@Override
				public void vrun() {

				}

			});
			// Notify players of the new match.
			Player blackPlayer = OfyService.ofy().load()
					.key(Key.create(Player.class, blackPlayerId)).now();
			Player whitePlayer = OfyService.ofy().load()
					.key(Key.create(Player.class, whitePlayerId)).now();
			if (blackPlayer == null) {
				blackPlayer = new Player(blackPlayerId, 0);
			}
			if (whitePlayer == null) {
				whitePlayer = new Player(whitePlayerId, 0);
			}
			sendMessage(blackPlayerId, "!black!" + whitePlayerId + "!"
					+ matchId);
			sendMessage(blackPlayerId, DEFAULT_STATE);
			sendMessage(whitePlayerId, "!white!" + blackPlayerId + "!"
					+ matchId);
			sendMessage(whitePlayerId, DEFAULT_STATE);
			blackPlayer.getMatchIds().add(matchId);
			whitePlayer.getMatchIds().add(matchId);

			// Save the new match into datastore, and update player info.
			Match match = new Match(matchId, blackPlayerId, whitePlayerId);
			OfyService.ofy().save().entity(match).now();
			OfyService.ofy().save().entity(blackPlayer).now();
			OfyService.ofy().save().entity(whitePlayer).now();
		} catch (ChannelFailureException e) {
			System.out.println("Channel Failure: " + e.getMessage());
		}
	}

	/**
	 * Change state of a match.
	 * 
	 * @param newState
	 *            The new state of the match
	 * @param matchId
	 *            Id of the match
	 * @param playerId
	 *            Id of player who just make the move
	 * @param opponentId
	 *            Id of the opponent player
	 * 
	 */
	@Override
	public void changeState(String newState, String matchId, String playerId,
			String opponentId) {
		System.out.println("Making move: " + newState + ", from: " + playerId
				+ " to: " + opponentId);
		sendMessage(opponentId, newState);
		sendMessage(playerId, newState);
		Match match = OfyService.ofy().load()
				.key(Key.create(Match.class, matchId)).now();
		match.setStateString(newState);
		match.setLastUpdateDate(new Date());
		match.switchTurn();
		OfyService.ofy().save().entity(match).now();
	}

	/**
	 * Get the match list of a player. The given list is sorted according to
	 * last update date.
	 * 
	 * @param playerId
	 *            Id of player
	 * @return ArrayList<Match>
	 */
	@Override
	public ArrayList<Match> getMatchList(String playerId) {
		ArrayList<Match> matchList = new ArrayList<Match>();
		List<Match> allMatches = OfyService.ofy().load().type(Match.class)
				.list();
		for (Match match : allMatches) {
			if (match.isInGame(playerId))
				matchList.add(match);
		}
		Collections.sort(matchList, matchComparator);
		return matchList;
	}

	/**
	 * Load a match for a player.
	 * 
	 * @param playerId
	 *            Id of player
	 * @param matchId
	 *            Id of the match to load
	 */
	@Override
	public void loadMatch(String playerId, String matchId) {
		Match match = OfyService.ofy().load()
				.key(Key.create(Match.class, matchId)).now();
		if (match == null) {
			System.err.println("No such match on server side!");
			return;
		}
		sendMessage(match.getBlackPlayerId(),
				"!black!" + match.getWhitePlayerId() + "!" + matchId);
		sendMessage(match.getBlackPlayerId(), match.getStateString());
		sendMessage(match.getWhitePlayerId(),
				"!white!" + match.getBlackPlayerId() + "!" + matchId);
		sendMessage(match.getWhitePlayerId(), match.getStateString());
	}

	public void sendMessage(String channelId, String message) {
		Player player = OfyService.ofy().load()
				.key(Key.create(Player.class, channelId)).now();
		assert (player != null);
		channelService.sendMessage(new ChannelMessage(channelId, message));
	}

	/**
	 * Utility used to generate random string given length.
	 * 
	 * @param length
	 *            Length of the string
	 * @return String
	 */
	private String generateRandomString(int length) {
		char charSet[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
				.toCharArray();
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(charSet[random.nextInt(charSet.length)]);
		}
		return sb.toString();
	}
}
