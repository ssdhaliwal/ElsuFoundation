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

import elsu.gis.geohash.GeohashUtils;
import java.io.Serializable;

/**
 *
 * @author evgeniy
 */
public class GeoPoint implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final int GEOHASH_LENGTH = 22;
    
    private Coordinates _coordinates;
    private String _geohash;
    
    public GeoPoint(Coordinates coordinates, String geohash) {
    	setCoordinates(coordinates);
    	setGeohash(geohash);
    }
    
    public Coordinates getCoordinates() {
    	return this._coordinates;
    }
    
    public Coordinates setCoordinates(Coordinates coordinates) {
    	this._coordinates = coordinates;
    	return this.getCoordinates();
    }
        
    public String getGeohash() {
    	return this._geohash;
    }
    
    public String setGeohash(String geohash) {
    	this._geohash = geohash;
    	return this.getGeohash();
    }
    
    public static GeoPoint fromCoordinates(Coordinates coordinates) {
        return new GeoPoint(coordinates, GeohashUtils.encodeGeohash(coordinates, GEOHASH_LENGTH));
    }
      
    public static GeoPoint fromGeohash(String geohash) {
        return new GeoPoint(GeohashUtils.decodeGeohash(geohash), geohash);
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append(getCoordinates().toString());
    	sb.append(", ");
    	sb.append("geoHash: " + getGeohash());
    	
    	return sb.toString();
    }
}
