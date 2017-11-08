package filters;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class MapPointType implements Serializable {

	private static final long serialVersionUID = 8330339909128827529L;

	private Double longitude;
	private Double latitude;

	public MapPointType() { // needed for JAXB
		initialize();
	}

	private void initialize() {

	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double pLongitude) {
		this.longitude = pLongitude;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double pLatitude) {
		this.latitude = pLatitude;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("{");
		result.append("longitude: " + longitude.toString() + ", " );
		result.append("latitude: " + latitude.toString() + ", " );
		result.append("}");
		
		return result.toString();
	}
}
