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
package elsu.gis.geohash.strategy;

import elsu.gis.geohash.model.Coordinates;

/**
 *
 * @author evgeniy
 */
public class ZoomGeohashLengthStrategy {

    private static int _minGeohashLength = 1;
    private static int _maxGeohashLength = 22;
    private static int _minZoom = 0;
    private static int _maxZoom = 17;

    public static int getGeohashLength(Coordinates southWest, Coordinates northEast, int zoom) {
        double a = getMinGeohashLength() / Math.exp(getMinZoom() / (getMaxZoom() - getMinZoom()) * 
        		Math.log(getMaxGeohashLength() / getMinGeohashLength()));
        double b = Math.log(getMaxGeohashLength() / getMinGeohashLength()) / (getMaxZoom() - getMinZoom());
        return (int) Math.max(getMinGeohashLength(), Math.min(a * Math.exp(b * zoom), getMaxGeohashLength()));
    }

    public static int getMinGeohashLength() {
        return _minGeohashLength;
    }

    public static void setMinGeohashLength(int minGeohashLength) {
    	_minGeohashLength = minGeohashLength;
    }

    public static int getMaxGeohashLength() {
        return _maxGeohashLength;
    }

    public static void setMaxGeohashLength(int maxGeohashLength) {
        _maxGeohashLength = maxGeohashLength;
    }

    public static int getMinZoom() {
        return _minZoom;
    }

    public static void setMinZoom(int minZoom) {
        _minZoom = minZoom;
    }

    public static int getMaxZoom() {
        return _maxZoom;
    }

    public static void setMaxZoom(int maxZoom) {
        _maxZoom = maxZoom;
    }
}
