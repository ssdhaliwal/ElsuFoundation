package elsu.support;

import com.google.gson.*;
import net.sf.json.*;
import net.sf.json.xml.*;

/**
 *
 * @author dhaliwal-admin
 */
public class GsonXMLStack {

    public static String Object2JSon(Object obj) {
        Gson gson = new Gson();
        String result = gson.toJson(obj);
        return result;
    }

    public static String Object2XML(Object obj) {
        String jsonStr = Object2JSon(obj);

        JSON json = JSONSerializer.toJSON(jsonStr);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String result = xmlSerializer.write(json);

        return result;
    }

    public static Object JSon2Object(String jsonData, java.lang.Class objClass) {
        Gson gson = new Gson();
        Object result = gson.fromJson(jsonData, objClass);
        return result;
    }

    public static Object JSon2Object(String jsonData, java.lang.reflect.Type objType) {
        Gson gson = new Gson();
        Object result = gson.fromJson(jsonData, objType);
        return result;
    }
}
