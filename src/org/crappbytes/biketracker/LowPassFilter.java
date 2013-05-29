/*
 *  BikeTracker is an Android Application.
 *  Copyright (C) 2013  Christian Rapp
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
	
	public LowPassFilter(double altitude) {
		this.smoothedAltitude = altitude;
		this.previousAltitude = altitude;
	}
	
	double[] applyFilter(double currentAltitude)
	{
		// return array for ascend and descend
		double[] ascDesc = {currentAltitude, 0, 0};
		//TODO: Make smoothing factor configurable
		this.smoothedAltitude += (currentAltitude - this.smoothedAltitude) / 20.0;
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
