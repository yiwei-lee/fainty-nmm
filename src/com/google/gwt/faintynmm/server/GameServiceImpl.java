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
import com.google.gwt.faintynmm.client.game.Color;
import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.faintynmm.client.game.Player;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@SuppressWarnings("serial")
public class GameServiceImpl extends XsrfProtectedServiceServlet implements
		GameService {

	// Default state when a game start.
	private final static String DEFAULT_STATE = "1109999000000000000000000000000";

	// c, q value used in Glicko rating system.
	private final double C = 17.5561721;
	private final double Q = 0.00575646273;

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
	public void startNewMatch(String blackPlayerId, String whitePlayerId) {
		try {
			// Get match id.
			String matchId = generateRandomString(16);
			while (OfyService.ofy().load()
					.key(Key.create(Match.class, matchId)).now() != null) {
				matchId = generateRandomString(16);
			}
			System.out.println("Match id: " + matchId);

			// Update players of the new match.
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
			blackPlayer.getMatchIds().add(matchId);
			whitePlayer.getMatchIds().add(matchId);

			// Save the new match into datastore, and update player info.
			Match match = new Match(matchId, blackPlayerId, whitePlayerId);
			OfyService.ofy().save().entities(match, blackPlayer, whitePlayer)
					.now();

			// Send new match to both players.
			sendMessage(blackPlayerId, "!black!" + whitePlayerId + "!"
					+ matchId);
			sendMessage(blackPlayerId, matchId + "!" + DEFAULT_STATE);
			sendMessage(whitePlayerId, matchId + "!" + DEFAULT_STATE);
			System.out.println("New match started.");
		} catch (ChannelFailureException e) {
			System.out.println("Channel Failure: " + e.getMessage());
		}
	}

	@Override
	public void startAutoMatch(String blackPlayerId) {
		// Get all players.
		Query<Player> players = OfyService.ofy().load().type(Player.class);
		Player initPlayer = OfyService.ofy().load()
				.key(Key.create(Player.class, blackPlayerId)).now();

		// Find a decent opponent!
		Player opponent = null;
		double distance = 100000;
		double rating = initPlayer.getRating();
		for (Player player : players) {
			if (!blackPlayerId.equals(player.getPlayerId())) {
				double currentDistance = Math.abs(player.getRating() - rating);
				if (currentDistance < distance) {
					distance = currentDistance;
					opponent = player;
				}
			}
		}
		if (opponent != null) {
			startNewMatch(blackPlayerId, opponent.getPlayerId());
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
		// System.out.println("Making move: " + newState + ", from: " + playerId
		// + " to: " + opponentId);
		Match match = OfyService.ofy().load()
				.key(Key.create(Match.class, matchId)).now();
		match.setStateString(newState);
		match.setLastUpdateDate(new Date());
		match.switchTurn();
		OfyService.ofy().save().entity(match).now();
		sendMessage(opponentId, matchId + "!" + newState);
		sendMessage(playerId, matchId + "!" + newState);
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
		// Clear the session, otherwise the update date will not be correct:
		// Objectify will fetch something in cache with an earlier update date.
		OfyService.ofy().clear();
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
			return;
		}
		if (playerId.equals(match.getBlackPlayerId())) {
			sendMessage(playerId, "!black!" + match.getWhitePlayerId() + "!"
					+ matchId);
		} else if (match.getWhitePlayerId().equals(playerId)) {
			sendMessage(playerId, "!white!" + match.getBlackPlayerId() + "!"
					+ matchId);
		}
		sendMessage(match.getBlackPlayerId(),
				matchId + "!" + match.getStateString());
		sendMessage(match.getWhitePlayerId(),
				matchId + "!" + match.getStateString());
	}

	@Override
	public void deleteMatch(String matchId, String playerId) {
		// Update match info, record which player choose to delete match.
		Match match = OfyService.ofy().load()
				.key(Key.create(Match.class, matchId)).now();
		boolean deleted = false;
		if (playerId.equals(match.getBlackPlayerId())) {
			deleted = match.deleteMatch(Color.BLACK);
		} else if (playerId.equals(match.getWhitePlayerId())) {
			deleted = match.deleteMatch(Color.WHITE);
		} else {
			return;
		}

		// Delete match from player's match list.
		Player player = OfyService.ofy().load()
				.key(Key.create(Player.class, playerId)).now();
		player.getMatchIds().remove(matchId);
		OfyService.ofy().save().entity(player).now();

		// Update match in datastore.
		if (deleted) {
			OfyService.ofy().delete().entity(match).now();
		} else {
			OfyService.ofy().save().entity(match).now();
		}
	}

	public void sendMessage(String channelId, String message) {
		Player player = OfyService.ofy().load()
				.key(Key.create(Player.class, channelId)).now();
		assert (player != null);
		if (player.isConnectd()) {
			channelService.sendMessage(new ChannelMessage(channelId, message));
		}
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

	private int getDaysBetween(Date from, Date to) {
		long aTime = from.getTime();
		long bTime = to.getTime();
		long adjust = 60 * 60 * 1000;
		return (int) ((bTime - aTime + adjust) / (24 * 60 * 60 * 1000));
	}

	@Override
	public void finishMatch(String matchId, String winnerId, String loserId) {
		Match match = OfyService.ofy().load()
				.key(Key.create(Match.class, matchId)).now();
		Player winner = OfyService.ofy().load()
				.key(Key.create(Player.class, winnerId)).now();
		Player loser = OfyService.ofy().load()
				.key(Key.create(Player.class, loserId)).now();
		Date today = new Date();

		// Calculate ratings.
		double winnerRating = winner.getRating();
		double loserRating = loser.getRating();
		double winnerRD = Math.min(
				Math.sqrt(winner.getRD() * winner.getRD() + C * C
						* (getDaysBetween(winner.getLastMatchDate(), today))),
				350.0);
		double loserRD = Math.min(
				Math.sqrt(loser.getRD() * loser.getRD() + C * C
						* (getDaysBetween(loser.getLastMatchDate(), today))),
				350.0);
		double gWinnerRD = 1.0 / Math.sqrt(1.0 + 3 * Q * Q * winnerRD
				* winnerRD / Math.PI / Math.PI);
		double gLoserRD = 1.0 / Math.sqrt(1.0 + 3 * Q * Q * loserRD * loserRD
				/ Math.PI / Math.PI);
		double winChance = winnerRating / (winnerRating + loserRating);
		double loseChance = 1.0 - winChance;
		double winnerD = Q * Q * gLoserRD * gLoserRD * winChance * loseChance;
		double loserD = Q * Q * gWinnerRD * gWinnerRD * winChance * loseChance;
		double winnerNewRating = winnerRating + Q
				/ (1.0 / winnerRD / winnerRD + winnerD) * gLoserRD * loseChance;
		double loserNewRating = loserRating + Q
				/ (1.0 / loserRD / loserRD + loserD) * gWinnerRD * -loseChance;
		double winnerNewRD = Math
				.sqrt(1.0 / (1.0 / winnerRD / winnerRD + winnerD));
		double loserNewRD = Math
				.sqrt(1.0 / (1.0 / loserRD / loserRD + loserD));

		// Update ratings in datastore.
		winner.setRating(winnerNewRating);
		winner.setRD(winnerNewRD);
		winner.setLastMatchDate(today);
		loser.setRating(loserNewRating);
		loser.setRD(loserNewRD);
		loser.setLastMatchDate(today);
		match.setWinner(winnerId);
		match.setIsOver(true);
		match.setLastUpdateDate(today);
		OfyService.ofy().save().entities(match, winner, loser).now();

		System.out.println("Winner new rating : " + winnerNewRating + " , "
				+ winnerNewRD);
		System.out.println("Loser new rating : " + loserNewRating + " , "
				+ loserNewRD);
	}

	@Override
	public double getRating(String playerId) {
		Player player = OfyService.ofy().load().key(Key.create(Player.class, playerId)).now();
		return player.getRating();
	}
}
