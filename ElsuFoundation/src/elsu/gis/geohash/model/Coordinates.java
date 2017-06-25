/*
 * Copyright 2015 evgeniy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package elsu.gis.geohash.model;

import java.io.Serializable;

/**
 *
 * @author evgeniy
 */
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1L;

    private double _latitude;
    private double _longitude;
    
    public Coordinates(double latitude, double longitude) {
    	setLatitude(latitude);
    	setLongitude(longitude);
    }
    
    public double getLatitude() {
    	return this._latitude;
    }
    
    public double setLatitude(double latitude) {
    	this._latitude = latitude;
    	return getLatitude();
    }
    
    public double getLongitude() {
    	return this._longitude;
    }
    
    public double setLongitude(double longitude) {
    	this._longitude = longitude;
    	return this.getLongitude();
    }
    
    @Override
    public String toString() {
    	return "Coordinates: (lat: " + String.format("%.12f", getLatitude()) + " ), " +
    			"(long: " + String.format("%.12f", getLongitude()) + " )";
    }
}
