/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.elsurowset;

import ac.factory.*;
import elsu.database.*;
import elsu.database.rowset.*;
import elsu.events.*;
import elsu.support.*;
import java.sql.*;

/**
 *
 * @author ss.dhaliwal
 */
public class EntityRowSetTest implements IEventSubscriber {

    public ActionFactory af = null;

    public EntityRowSetTest(String config) {
        try {
        	ConfigLoader cl = new ConfigLoader(config, null);
        	
            af = new ActionFactory(cl);
            af.addEventListener(this);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // instantiate the main controller class and call its run()
            // method to start service factory
            EntityRowSetTest rut1 = new EntityRowSetTest("config/app.config");

            try {
                Object dbManager = rut1.af.getDbManager("NCS");
                Connection conn = ((DatabaseManager)dbManager).getConnection();
                EntityDescriptor wrs = DatabaseUtils.getEntityDescriptor(conn, "SELECT * FROM ncs3.vwSite",
                        null);
                //System.out.println(ActionObject.toXML(wrs));
                //System.out.println(".. records selected: " + wrs.size());

                String jFields = JsonXMLUtils.Object2JSon(wrs.getColumns());
                String jRows = JsonXMLUtils.Object2JSon(wrs.getRows());

                //System.out.println(jFields);
                //Type fieldType = new TypeToken<Map<String, FieldDescriptor>>() {}.getType();
                //final Map<String, FieldDescriptor> fields = (Map<String, FieldDescriptor>) GsonXMLStack.JSon2Object(jFields, fieldType);

                //for (RowDescriptor rd : wrs.getRows()) {
                //    System.out.println(rd.toString());
                //}

                //JsonParser parser = new JsonParser();
                //JsonArray jArray = parser.parse(jRows).getAsJsonArray();
                //for (JsonElement jElement : jArray) {
                //    //JsonObject jObject = (JsonObject)jElement;
                //    System.out.println(jElement);
                //    RowDescriptor rd = new RowDescriptor(jFields, jElement.toString());
                //    System.out.println(rd);
                //}

                wrs = new EntityDescriptor(jFields, jRows);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception ex) {
            // Display a message if anything goes wrong
            System.err.println("RowsetUnitTest, main, " + ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public Object EventHandler(Object sender, IEventStatusType status, String message, Object o) {
        switch (EventStatusType.valueOf(status.getName())) {
            case DEBUG:
            case ERROR:
            case INFORMATION:
                System.out.println(status.getName() + ":" + message);
                break;
            default:
                break;
        }

        return null;
    }
}
