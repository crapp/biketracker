/*
 *  BikeTracker is an Android Application.
 *  Copyright (C) 2013 - 2016 Christian Rapp <0x2a at posteo dot org>
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
