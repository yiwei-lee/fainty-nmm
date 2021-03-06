package com.google.gwt.faintynmm.client;

import java.util.ArrayList;

import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GameServiceAsync {
	public void getMatchList(String channelId, AsyncCallback<ArrayList<Match>> async);
	public void startNewMatch(String blackPlayerId, String whitePlayerId, AsyncCallback<Void> async);
	public void startAutoMatch(String blackPlayerId, AsyncCallback<Void> async); 
	public void loadMatch(String playerId, String matchId, AsyncCallback<Void> async);
	public void changeState(String newState, String matchId, String playerId, String opponentId, boolean switchTurn, AsyncCallback<Void> async);
	public void deleteMatch(String matchId, String playerId, AsyncCallback<Void> async);
	public void finishMatch(String matchId, String winnerId, String loserId, AsyncCallback<Void> async);
	public void getRating(String playerId, AsyncCallback<Double> callback);
}
