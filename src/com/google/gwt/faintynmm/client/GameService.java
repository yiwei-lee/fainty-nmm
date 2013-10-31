package com.google.gwt.faintynmm.client;

import java.util.ArrayList;

import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("game")
public interface GameService extends RemoteService {
	public void initialize(String channelId);
	public ArrayList<Match> getMatchList(String channelId);
	public void startNewMatch(String blackPlayer, String whitePlayer);
	public void loadMatch(String playerId, String matchId);
	public void changeState(String newState, String matchId, String playerId, String opponentId);
}
