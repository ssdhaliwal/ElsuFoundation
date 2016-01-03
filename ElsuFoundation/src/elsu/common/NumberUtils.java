package elsu.common;

import java.text.*;

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
}
