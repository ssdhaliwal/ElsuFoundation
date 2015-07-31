package elsu.common;

import java.util.*;

/**
 *
 * @author ss.dhaliwal_admin
 */
public class CollectionStack {

    public static String getMapValueAsString(Map list, String key) {
        return list.get(key).toString();
    }

    public static int getMapValueAsInteger(Map list, String key) {
        return Integer.parseInt(getMapValueAsString(list, key));
    }
    
    public static String ArrayToString(String[] values) {
        return ArrayToString(values, ',');
    }
    public static String ArrayToString(String[] values, char delimiter) {
        String result = "";
        int offset = 0;
        
        for(String value : values) {
            if (value.indexOf(delimiter) >= 0) {
                result += "\"" + value + "\"";
            } else result += value;
            
            offset++;

            if (offset < values.length) {
                result += delimiter;
            }
        }
        
        return result;
    }
    
    public static Object[] ArrayToObject(byte[] values) {
        Object[] objects = new Object[values.length];
        
        for(int i = 0; i < values.length; i++) {
            objects[i] = values[i];
        }
        
        return objects;
    }
    
    public static Object[] ArrayToObject(char[] values) {
        Object[] objects = new Object[values.length];
        
        for(int i = 0; i < values.length; i++) {
            objects[i] = values[i];
        }
        
        return objects;
    }
    
    public static Object[] ArrayToObject(short[] values) {
        Object[] objects = new Object[values.length];
        
        for(int i = 0; i < values.length; i++) {
            objects[i] = values[i];
        }
        
        return objects;
    }
    
    public static Object[] ArrayToObject(int[] values) {
        Object[] objects = new Object[values.length];
        
        for(int i = 0; i < values.length; i++) {
            objects[i] = values[i];
        }
        
        return objects;
    }
    
    public static Object[] ArrayToObject(long[] values) {
        Object[] objects = new Object[values.length];
        
        for(int i = 0; i < values.length; i++) {
            objects[i] = values[i];
        }
        
        return objects;
    }
    
    public static Object[] ArrayToObject(float[] values) {
        Object[] objects = new Object[values.length];
        
        for(int i = 0; i < values.length; i++) {
            objects[i] = values[i];
        }
        
        return objects;
    }
    
    public static Object[] ArrayToObject(double[] values) {
        Object[] objects = new Object[values.length];
        
        for(int i = 0; i < values.length; i++) {
            objects[i] = values[i];
        }
        
        return objects;
    }
}
