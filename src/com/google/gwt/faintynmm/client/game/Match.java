package com.google.gwt.faintynmm.client.game;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Match implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	String matchId;
	Date lastUpdateDate = new Date();
	String blackPlayerId;
	String whitePlayerId;
	String currentPlayerId;
	String stateString = "1109999000000000000000000000000";
	Boolean isOver = false;
	String winner;

	@SuppressWarnings("unused")
	private Match() {
	};

	public Match(String matchId, String blackPlayerId, String whitePlayerId) {
		this.matchId = matchId;
		this.blackPlayerId = blackPlayerId;
		this.whitePlayerId = whitePlayerId;
		this.currentPlayerId = blackPlayerId;
	}

	public boolean isInGame(String playerId){
		return (blackPlayerId.equals(playerId) || whitePlayerId.equals(playerId));
	}
	
	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public String getStateString() {
		return stateString;
	}

	public void setStateString(String stateString) {
		this.stateString = stateString;
	}

	public Boolean getIsOver() {
		return isOver;
	}

	public void setIsOver(Boolean isOver) {
		this.isOver = isOver;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public String getCurrentPlayerId() {
		return currentPlayerId;
	}

	public void setCurrentPlayerId(String currentPlayerId) {
		this.currentPlayerId = currentPlayerId;
	}

	public String getBlackPlayerId() {
		return blackPlayerId;
	}

	public void setBlackPlayerId(String blackPlayerId) {
		this.blackPlayerId = blackPlayerId;
	}

	public String getWhitePlayerId() {
		return whitePlayerId;
	}

	public void setWhitePlayerId(String whitePlayerId) {
		this.whitePlayerId = whitePlayerId;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public void switchTurn() {
		if (currentPlayerId.equals(blackPlayerId)){
			currentPlayerId = whitePlayerId;
		} else {
			currentPlayerId = blackPlayerId;
		}
	}
}
