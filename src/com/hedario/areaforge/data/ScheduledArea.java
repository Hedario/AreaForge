package com.hedario.areaforge.data;

import com.hedario.areaforge.Methods;

public class ScheduledArea {
	
	private String name, time;
	private boolean enabled;
	private long lastLoad;
	
	public ScheduledArea(final String name, final boolean enabled, final String time) {
		this.name = name;
		this.enabled = enabled;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public long getParsedTime()	{
		return Methods.parseTime(time);
	}

	public long getLastLoad() {
		return lastLoad;
	}
	
	public void setLastLoad(long lastLoad) {
		this.lastLoad = lastLoad;
	}
}
