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
 * @author evgeniy
 */
public class GeoCluster implements Serializable {

    private static final long serialVersionUID = 1L;

    private Coordinates _coordinates;
    private long _quantity;
    private String _geohashPrefix;
    
    public GeoCluster(Coordinates coordinates, long quantity, String geohashPrefix) {
    	setCoordinates(coordinates);
    	setQuantity(quantity);
    	setGeohashPrefix(geohashPrefix);
    }
    
    public Coordinates getCoordinates() {
    	return this._coordinates;
    }
    
    public Coordinates setCoordinates(Coordinates coordinates) {
    	this._coordinates = coordinates;
    	return this.getCoordinates();
    }
    
    public long getQuantity() {
    	return this._quantity;
    }
    
    public long setQuantity(long quantity) {
    	this._quantity = quantity;
    	return this.getQuantity();
    }
    
    public String getGeohashPrefix() {
    	return this._geohashPrefix;
    }
    
    public String setGeohashPrefix(String geohashPrefix) {
    	this._geohashPrefix = geohashPrefix;
    	return this.getGeohashPrefix();
    }
 }
