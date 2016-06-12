package elsu.support;

import de.micromata.opengis.kml.v_2_2_0.*;
import elsu.common.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author ssdhaliwal
 * // copied from JavaAPIforKML project de.micromata.jak examples
 */
public class KMLUtilities {
	/**
	 * Convert three int values into a hex string
	 * 
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param inserverOrder set true to use inverse order of RGB => BGR
	 * @return RGB string in hex format
	 */
	public static String getHexColor(int r, int g, int b, boolean inverseOrder) {
		String red, green, blue;
		String val = Integer.toHexString(r).toUpperCase();
		red = val.length() == 1 ? "0" + val : val; // add leading zero
		val = Integer.toHexString(g).toUpperCase();
		green = val.length() == 1 ? "0" + val : val; // add leading zero
		val = Integer.toHexString(b).toUpperCase();
		blue = val.length() == 1 ? "0" + val : val; // add leading zero
		if (!inverseOrder) {
			return blue + green + red;
		} else {
			return red + green + blue;
		}
	}

	/**
	 * @param placemark change the Style of this placemark
	 * @param color color format: hex value, default: white
	 * @param polyMode colormode of the polygon
	 * @param width width of the LineStyle, default: 0
	 * @param lineColor alpha and color value in hex format, default: black
	 * @param lineMode colormode of the line/border
	 */
	public static void setPolyStyleAndLineStyle(Placemark placemark, String color, ColorMode polyMode, double width, String lineColor,
	    ColorMode lineMode) {
		if (color == null || color.length() != 8) {
			color = "FFFFFFFF";
			polyMode = ColorMode.NORMAL;
		}

		if (width <= 0) {
			width = 0;
		}
		if (lineColor == null || lineColor.length() != 8) {
			lineColor = "FF000000";
			lineMode = ColorMode.NORMAL;
		}
		List<StyleSelector> styleSelector = placemark.getStyleSelector();
		if (styleSelector.isEmpty()) {
			Style style = new Style();
			style.createAndSetPolyStyle();
			styleSelector.add(style);
		}
		Iterator<StyleSelector> iterator = styleSelector.iterator();
		Style style = null;
		while (iterator.hasNext()) {
			StyleSelector tmp = iterator.next();
			if (tmp instanceof Style) {
				style = (Style) tmp;
				style.getPolyStyle().withColor(color).withColorMode(polyMode);
				style.createAndSetLineStyle().withWidth(width).withColor(lineColor).withColorMode(lineMode);
			}
		}
	}



	/**
	 * set the PolyStyle of the placemark with a color the color range is a gradient from yellow to red the color calculate with the value and
	 * maximum
	 * 
	 * @param placemark change Style of this placemark
	 * @param max maximum value of the data
	 * @param value current value for the placemark
	 * @param width width of the line (LineStyle)
	 * @param transparency alpha value for the color
	 */
	public static void setDataValueColor(Placemark placemark, Double max, double value, String transparency, double width, String lineColor,
	    ColorMode lineMode) {
		if (transparency == null || transparency.length() != 2) {
			transparency = "FF";
		}
		if (lineColor == null || lineColor.length() != 8) {
			lineColor = "FF000000"; // black
			lineMode = ColorMode.NORMAL;
		}

		int colorValue = (int) ((value / max) * 255);
		/*
		 * value <-> color conversion calculate percental value (with the max value) and use it for the hex color value 0 % => yellow 50 % =>
		 * orange 100 % => red
		 */
		String color = getHexColor(0, (255 - colorValue), 255, true); // KML color format: inverse order of RGB
		setPolyStyleAndLineStyle(placemark, transparency + color, ColorMode.NORMAL, width, lineColor, lineMode);
	}
    
        // method to take string and return kml
        public static Kml toKml(String sequence) throws Exception {
            OutputStream output = StringUtils.StreamforString();
            Kml kml = new Kml();
            
            kml.marshal(output);
            output.close();
            output = null;
            
            return kml;
        }
        
        public static String fromKml(Kml kml) throws Exception {
            String result = null;
            OutputStream output = StringUtils.StreamforString();
            
            kml.marshal(output);
            result = output.toString();
            output.close();
            output = null;
            
            return result;
        }
}
