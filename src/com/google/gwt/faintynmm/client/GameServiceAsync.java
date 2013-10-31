package com.google.gwt.faintynmm.client;

import java.util.ArrayList;

import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GameServiceAsync {
	public void initialize(String channelId, AsyncCallback<Void> async);
	public void getMatchList(String channelId, AsyncCallback<ArrayList<Match>> async);
	public void startNewMatch(String blackPlayerId, String whitePlayerId, AsyncCallback<Void> async);
	public void loadMatch(String playerId, String matchId, AsyncCallback<Void> async);
	public void changeState(String newState, String matchId, String playerId, String opponentId, AsyncCallback<Void> async);
}
