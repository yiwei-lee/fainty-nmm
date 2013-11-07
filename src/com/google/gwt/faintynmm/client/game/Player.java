package com.google.gwt.faintynmm.client.game;

import java.util.ArrayList;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Player {
	@Id
	String playerId;
	int connectedDeviceNumber = 0;
	Date lastMatchDate;
	double rating = 1500.0;
	double rd = 350.0;
	ArrayList<String> matchIds = new ArrayList<String>();

	@SuppressWarnings("unused")
	private Player() {
	};

	public Player(String playerId, int connectedDevices) {
		this.playerId = playerId;
		this.connectedDeviceNumber = connectedDevices;
		this.lastMatchDate = new Date();
		this.matchIds = new ArrayList<String>();
		this.rating = 1500.0;
		this.rd = 350.0;
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
		return rating;
	}

	public double getRD() {
		return rd;
	}

	public void setRD(double rd) {
		this.rd = rd;
	}

	public Date getLastMatchDate() {
		return lastMatchDate;
	}

	public void setLastMatchDate(Date lastMatchDate) {
		this.lastMatchDate = lastMatchDate;
	}
}
