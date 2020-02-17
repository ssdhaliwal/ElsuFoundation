package elsu.common;

import java.text.*;
import java.util.*;

/**
 *
 * @author SSDhaliwal
 */
public class NumberUtils {

    static public String NumberFormat(int pNumber, String pFormat) {
        DecimalFormat nf;

        if (StringUtils.IsNull(pFormat)) {
            nf = new DecimalFormat("###,###,###,###");
        } else {
            nf = new DecimalFormat(pFormat);
        }

        return nf.format(pNumber);
    }

    static public String FloatFormat(Float pFloat, String pFormat) {
        DecimalFormat nf;

        if (StringUtils.IsNull(pFormat)) {
            nf = new DecimalFormat("###,###,###,###.00");
        } else {
            nf = new DecimalFormat(pFormat);
        }

        return nf.format(pFloat);
    }

    static public int RandomInt(int min, int max) {
    	return ((int) (Math.random()*(max - min))) + min;
    }

    static public float RandomFloat(float min, float max) {
    	return ((float) (Math.random()*(max - min))) + min;
    }

    static public double RandomDouble(double min, double max) {
    	return ((double) (Math.random()*(max - min))) + min;
    }
}
