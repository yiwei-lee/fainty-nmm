package com.google.gwt.faintynmm.client;

import java.util.ArrayList;

import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.server.rpc.XsrfProtect;

@XsrfProtect
@RemoteServiceRelativePath("game")
public interface GameService extends RemoteService {
	public ArrayList<Match> getMatchList(String channelId);
	public void startNewMatch(String blackPlayer, String whitePlayer);
	public void startAutoMatch(String blackPlayer);
	public void loadMatch(String playerId, String matchId);
	public void changeState(String newState, String matchId, String playerId, String opponentId);
	public void deleteMatch(String matchId, String playerId);
	public void finishMatch(String matchId, String winnerId, String loserId);
}
