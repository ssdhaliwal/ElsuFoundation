package filters;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class MapExtentType implements Serializable {

	private static final long serialVersionUID = 8312416631148713919L;

	private Boolean ignore;
	private MapPointType southWest;
	private MapPointType northEast;

	public MapExtentType() { // needed for JAXB
		initialize();
	}

	private void initialize() {
		this.ignore = true;
		this.southWest = new MapPointType();
		this.northEast = new MapPointType();
	}

	public Boolean getIgnore() {
		return this.ignore;
	}

	public void setIgnore(Boolean pIgnore) {
		this.ignore = pIgnore;
	}

	public void setMapExtent(MapPointType pSouthWest, MapPointType pNorthEast) {
		this.ignore = false;
		this.southWest = pSouthWest;
		this.northEast = pNorthEast;
	}

	public MapPointType getSouthWest() {
		return this.southWest;
	}

	public void setSouthWest(MapPointType pSouthWest) {
		this.southWest = pSouthWest;
	}

	public void setSouthWest(Double pLongitude, Double pLatitude) {
		this.ignore = false;
		this.southWest.setLongitude(pLongitude);
		this.southWest.setLatitude(pLatitude);
	}

	public MapPointType getNorthEast() {
		return this.northEast;
	}

	public void setNorthEast(MapPointType pNorthEast) {
		this.northEast = pNorthEast;
	}

	public void setNorthEast(Double pLongitude, Double pLatitude) {
		this.ignore = false;
		this.northEast.setLongitude(pLongitude);
		this.northEast.setLatitude(pLatitude);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("{");
		result.append("ignore: " + ignore.toString() + ", " );
		result.append("southWest: " + southWest.toString() + ", " );
		result.append("northEast: " + northEast.toString() + ", " );
		result.append("}");
		
		return result.toString();
	}
}
