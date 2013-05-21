package org.crappbytes.biketracker;

public class Track {
	private double distance; //km
	private long elapsedTime; //ms
	
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
	public void addDistance(double distance) {
		this.distance += distance;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(long timeDelta) {
		this.elapsedTime = timeDelta;
	}
	public void addElapsedTime(long timedelta) {
		this.elapsedTime += timedelta;
	}
}
