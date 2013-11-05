package com.google.gwt.faintynmm.client.game;

import java.util.ArrayList;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Player {
	@Id
	String playerId;
	int connectedDeviceNumber;
	ArrayList<String> matchIds = new ArrayList<String>();

	@SuppressWarnings("unused")
	private Player() {
	};

	public Player(String playerId, int connectedDevices) {
		this.playerId = playerId;
		this.connectedDeviceNumber = connectedDevices;
		this.matchIds = new ArrayList<String>();
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public Boolean isConnectd() {
		return (connectedDeviceNumber != 0);
	}

	public ArrayList<String> getMatchIds() {
		return matchIds;
	}

	public void incConnectedDeviceNumber() {
		connectedDeviceNumber++;
	}

	public void decConnectedDeviceNumber() {
		connectedDeviceNumber--;
	}

	public int getConnectedDeviceNumber() {
		return connectedDeviceNumber;
	}
}
