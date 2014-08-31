/*
 *  BikeTracker is an Android Application.
 *  Copyright (C) 2013, 2014 Christian Rapp <0x2a@posteo.org>
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

import java.util.Date;

/**
 * Created by saedelaere on 03.06.13.
 */
public class Node {
    private Integer id;
    private Integer trackid;
    private Double accuracy;
    private Double altitude;
    private Double altitudelpf;
    private Double altitudeup;
    private Double altitudedown;
    private Double bearing;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double distance;
    private Integer raceTime;
    private Date timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTrackid() {
        return trackid;
    }

    public void setTrackid(Integer trackid) {
        this.trackid = trackid;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getAltitudelpf() {
        return altitudelpf;
    }

    public void setAltitudelpf(Double altitudelpf) {
        this.altitudelpf = altitudelpf;
    }

    public Double getAltitudeup() {
        return altitudeup;
    }

    public void setAltitudeup(Double altitudeup) {
        this.altitudeup = altitudeup;
    }

    public Double getAltitudedown() {
        return altitudedown;
    }

    public void setAltitudedown(Double altitudedown) {
        this.altitudedown = altitudedown;
    }

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getRaceTime() {
        return raceTime;
    }

    public void setRaceTime(Integer raceTime) {
        this.raceTime = raceTime;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
