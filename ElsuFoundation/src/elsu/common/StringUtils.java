package elsu.common;

import java.util.*;

/**
 * StringStack was created to provide static functions which can be called to
 * modify, update, manipulate strings or arrays.
 *
 * @author: seraj.dhaliwal
 * @email: seraj.dhaliwal@live.com
 *
 * @changehistory (in descending order) date version user comments Sep, 01/09
 * 1.00 seraj.dhaliwal initial version 1/ added IsNull() to evaluate the string
 * passed and return the default Jun, 21/10 1/ added IsNull() override to return
 * boolean with string value is null/empty
 *
 */
public class StringUtils {

    /**
     * IsNull returns the default value specified. It is handy when evaluating
     * strings as part of a statement vice having to do if/else.
     *
     * @param value
     * @param defaultValue
     * @return String
     */
    public static String IsNull(String value, String defaultValue) {
        if ((value == null) || (value.trim().length() == 0)
                || (value.trim().equalsIgnoreCase("null"))) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public static boolean IsNull(String value) {
        return (value == null) || (value.trim().length() == 0);
    }

    public static boolean IsDefault(String value, String defaultValue) {
        return IsNull(value)
                || (value.trim().equalsIgnoreCase(defaultValue.trim()));
    }

    /**
     * Return string representation of the boolean evaluation.
     *
     * @param value
     * @return String (upperCase)
     */
    public static String bool2String(boolean value) {
        if (value) {
            return "TRUE";
        } else {
            return "FALSE";
        }
    }

    /**
     * Return custom string (True/False) representation of the boolean
     * evaluation.
     *
     * @param value
     * @param trueValue
     * @param falseValue
     * @return <code>String</code> (upperCase)
     */
    public static String bool2String(boolean value, String trueValue,
            String falseValue) {
        if (value) {
            return trueValue;
        } else {
            return falseValue;
        }
    }

    /**
     * randomString(...) method returns a string of specific length based on
     * random characters.
     * <p>
     * adapted from
     * http://stackoverflow.com/questions/5683327/how-to-generate-a-random-string-of-20-characters
     * <p>
     * msdn
     * http://msdn.microsoft.com/en-us/library/windows/desktop/aa365247%28v=vs.85%29.aspx
     * file special chars < > : " / \ | ? *
     *
     * @param length
     * @return <code>String</code> value of the string
     */
    public static String randomString(int length) {
        char[] chars = "ab1@cd2#ef3$gh4%ij5^kl6&mn7_op8-qr9?st0~uv3~wx7!yz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Returns boolean equivalent of the string value provided. Does numerous
     * comparisons to ensure multiple variations are considered.
     *
     * @return
     * @param value
     */
    public static boolean string2Bool(String value) {
        return (value != null)
                && ((value.equalsIgnoreCase("true"))
                || (value.equalsIgnoreCase(".t.")) || (value
                .equalsIgnoreCase("1")));
    }

    /**
     * Compares two values and returns the answer if they match. Used like
     * inline function - valueComparer("STANDARD", "DETAIL", "checked");
     *
     * @param value1
     * @param value2
     * @param answer
     * @return
     */
    public static String valueComparer(String value1, String value2,
            String answer) {
        if (value1.equalsIgnoreCase(value2)) {
            return answer;
        } else {
            return "";
        }
    }

    /**
     * stringToBytes(...) method allows string to be converted to byte array
     * using ASCII encoding.
     *
     * @param value
     * @return <code>byte[]</code> value from the string array
     * @original
     * http://www.javacodegeeks.com/2010/11/java-best-practices-char-to-byte-and.html
     */
    public static byte[] stringToBytes(String value) {
        byte[] bytes = new byte[value.length()];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) value.charAt(i);
        }

        return bytes;
    }

    public static byte[] stringToBytesUTFCustom(String value) {
        byte[] bytes = new byte[value.length() << 1];

        for (int i = 0; i < value.length(); i++) {
            char strChar = value.charAt(i);
            int bpos = i << 1;
            bytes[bpos] = (byte) ((strChar & 0xFF00) >> 8);
            bytes[bpos + 1] = (byte) (strChar & 0x00FF);
        }

        return bytes;
    }

    public static String bytesToStringUTFCustom(byte[] bytes) {
        char[] result = new char[bytes.length >> 1];

        for (int i = 0; i < result.length; i++) {
            int bpos = i << 1;
            char c = (char) (((bytes[bpos] & 0x00FF) << 8) + (bytes[bpos + 1]
                    & 0x00FF));
            result[i] = c;
        }

        return new String(result);
    }
    
    public static String padString(String value, int length) {
        return padString(value, length, " ", null);
    }
    
    public static String padString(String value, int length, String padWith) {
        return padString(value, length, padWith, null);
    }
    
    public static String padString(String value, int length, String padWith, String delimiter) {
        String result = value;
        
        for(int i = 0; i < length; i++) {
            if ((i > 0) && (delimiter != null)) {
                result += delimiter + padWith;
            } else {
                result += padWith;
            }
        }
        
        // return the new value
        return result;
    }
}
