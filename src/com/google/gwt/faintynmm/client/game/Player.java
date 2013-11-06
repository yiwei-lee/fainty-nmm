package com.google.gwt.faintynmm.client.game;

import java.util.ArrayList;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Player {
	@Id
	String playerId;
	int connectedDeviceNumber = 0;
	double rating = 1500.0;
	ArrayList<String> matchIds = new ArrayList<String>();

	@SuppressWarnings("unused")
	private Player() {
	};

	public Player(String playerId, int connectedDevices) {
		this.playerId = playerId;
		this.connectedDeviceNumber = connectedDevices;
		this.matchIds = new ArrayList<String>();
		this.rating = 1500.0;
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

	public void setRating(double rating) {
		this.rating = rating;
	}

	public double getRating() {
		return this.rating;
	}
}
