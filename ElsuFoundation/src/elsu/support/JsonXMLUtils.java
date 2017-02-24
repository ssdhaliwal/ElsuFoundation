package elsu.support;

import com.google.gson.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

/**
 *
 * @author dhaliwal-admin
 * 
 * 20170105 - updated to include jackson json libraries
 * 			- renamed original methods to Gson
 * 			- added <? extends Object> to the java.lang.Class to remove Type Safety warning
 */
public class JsonXMLUtils {

    public static String Object2GSon(Object obj) {
        String result = "";
        
        Gson gson = new Gson();
        result = gson.toJson(obj);

        return result;
    }

    public static Object GSon2Object(String jsonData, java.lang.Class<? extends Object> objClass) {
        Object result = null;
        
        Gson gson = new Gson();
        result = gson.fromJson(jsonData, objClass);
        
        return result;
    }

    public static Object GSon2Object(String jsonData, java.lang.reflect.Type objType) {
        Object result = null;
        
        Gson gson = new Gson();
        result = gson.fromJson(jsonData, objType);
        
        return result;
    }

    public static String Object2JSon(Object obj) throws Exception {
        String result = "";
        
        ObjectMapper mapper = new ObjectMapper();
        result = mapper.writeValueAsString(obj);

        return result;
    }

    public static Object JSon2Object(String jsonData, java.lang.Class<? extends Object> objClass) throws Exception {
        Object result = null;
        
        ObjectMapper mapper = new ObjectMapper();
        result = mapper.readValue(jsonData, objClass);
        
        return result;
    }

    public static Object JSon2Object(String jsonData, java.lang.reflect.Type objType) throws Exception {
        Object result = null;
        
        ObjectMapper mapper = new ObjectMapper();
        result = mapper.readValue(jsonData, mapper.getTypeFactory().constructType(objType));
        
        return result;
    }
}
