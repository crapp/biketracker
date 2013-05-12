package org.crappbytes.biketracker;

public class Track {
	private double distance;
	private long elapsedTime;
	
	public Track() {
		this.distance = 0;
		this.elapsedTime = 0;
	}
	
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(long timeDelta) {
		this.elapsedTime = timeDelta;
	}
}
