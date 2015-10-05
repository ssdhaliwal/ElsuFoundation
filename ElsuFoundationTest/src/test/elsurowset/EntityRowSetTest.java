/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.elsurowset;

import ac.core.*;
import ac.factory.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import elsu.database.rowset.*;
import elsu.events.*;
import elsu.support.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author ss.dhaliwal
 */
public class EntityRowSetTest implements IEventSubscriber {

    public ActionFactory af = null;

    public EntityRowSetTest() {
        try {
            af = new ActionFactory();
            af.addEventListener(this);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public EntityRowSetTest(String config) {
        try {
            af = new ActionFactory(config);
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
            EntityRowSetTest rut1 = new EntityRowSetTest();

            // update the action object direct syncprovider (if applicable)
            try {
                ActionObjectStack.setSyncProvider(rut1.af.getSyncProvider());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            try {
                EntityDescriptor wrs = rut1.af.getDbManager().getDataRowSet("SELECT * FROM ncs3.vwSite",
                        null);
                //System.out.println(ActionObject.toXML(wrs));
                //System.out.println(".. records selected: " + wrs.size());

                String jFields = GsonXMLStack.Object2JSon(wrs.getFields());
                String jRows = GsonXMLStack.Object2JSon(wrs.getRows());

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
                
                wrs.buildIndex("Site");
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
