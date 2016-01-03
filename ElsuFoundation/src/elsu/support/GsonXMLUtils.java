package elsu.support;

import com.google.gson.*;

/**
 *
 * @author dhaliwal-admin
 */
public class GsonXMLUtils {

    public static String Object2JSon(Object obj) {
        String result = "";
        
        Gson gson = new Gson();
        result = gson.toJson(obj);

        return result;
    }

    public static Object JSon2Object(String jsonData, java.lang.Class objClass) {
        Object result = null;
        
        Gson gson = new Gson();
        result = gson.fromJson(jsonData, objClass);
        
        return result;
    }

    public static Object JSon2Object(String jsonData, java.lang.reflect.Type objType) {
        Object result = null;
        
        Gson gson = new Gson();
        result = gson.fromJson(jsonData, objType);
        
        return result;
    }
}
