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

public class LowPassFilter {
    private double smoothedAltitude;
    private double previousAltitude;
    private double smoothingFactor;

    public LowPassFilter(double altitude, double smoothingFactor) {
        this.smoothedAltitude = altitude;
        this.previousAltitude = altitude;
        this.smoothingFactor = smoothingFactor;
    }

    double[] applyFilter(double currentAltitude)
    {
        // return array for ascend and descend
        double[] ascDesc = {currentAltitude, 0, 0};
        //handle great differences
        //TODO: Watch this closely!!
//        if (currentAltitude - this.previousAltitude > 10 || this.previousAltitude - currentAltitude > 10) {
//            this.smoothedAltitude = currentAltitude;
//            this.previousAltitude = this.smoothedAltitude;
//            return ascDesc;
//        }
        this.smoothedAltitude += (currentAltitude - this.smoothedAltitude) / this.smoothingFactor;
        if (this.previousAltitude > this.smoothedAltitude)
        {
            ascDesc[2] = this.previousAltitude - this.smoothedAltitude;
        }
        if (this.smoothedAltitude > this.previousAltitude)
        {
            ascDesc[1] = this.smoothedAltitude - this.previousAltitude;
        }
        ascDesc[0] = this.smoothedAltitude;
        this.previousAltitude = this.smoothedAltitude;
        return ascDesc;
    }
}
