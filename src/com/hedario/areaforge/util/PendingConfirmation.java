package com.hedario.areaforge.util;

import java.util.HashMap;
import java.util.Map;

public class PendingConfirmation {
	public enum Action {
		LOAD,
		DELETE,
		AUTO_LOAD;
	}
	private static final Map<String, PendingConfirmation> CONFIRMATIONS = new HashMap<String, PendingConfirmation>();
	private String player, area;
	private Action action;
	private long expiry;
	
	public PendingConfirmation(final String player, final String area, final Action action) {
		this.player = player;
		this.area = area;
		this.action = action;
		this.expiry = System.currentTimeMillis() + 10000;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}

	public static Map<String, PendingConfirmation> getConfirmations() {
		return CONFIRMATIONS;
	}
}
