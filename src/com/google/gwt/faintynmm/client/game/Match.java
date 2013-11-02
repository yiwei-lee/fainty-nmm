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
	Date lastUpdateDate;
	String blackPlayerId;
	String whitePlayerId;
	boolean blackDeleteFlag;
	boolean whiteDeleteFlag;
	String currentPlayerId;
	String stateString;
	Boolean isOver;
	String winner;

	@SuppressWarnings("unused")
	private Match() {
	};

	public Match(String matchId, String blackPlayerId, String whitePlayerId) {
		this.matchId = matchId;
		this.blackPlayerId = blackPlayerId;
		this.whitePlayerId = whitePlayerId;
		this.currentPlayerId = blackPlayerId;
		lastUpdateDate = new Date();
		stateString = "1109999000000000000000000000000";
		isOver = false;
		blackDeleteFlag = whiteDeleteFlag = false;
	}

	public boolean isInGame(String playerId){
		boolean flag = false;
		if (blackPlayerId.equals(playerId)){
			flag = !blackDeleteFlag;
		} else if (whitePlayerId.equals(playerId)){
			flag = !whiteDeleteFlag;
		}
		return flag;
	}
	
	public String getMatchId() {
		return matchId;
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

	public String getBlackPlayerId() {
		return blackPlayerId;
	}

	public String getWhitePlayerId() {
		return whitePlayerId;
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
	
	public boolean deleteMatch(Color color){
		if (color == Color.BLACK){
			blackDeleteFlag = true;
		} else if (color == Color.WHITE){
			whiteDeleteFlag = true;
		}
		return (blackDeleteFlag && whiteDeleteFlag);
	}
}
