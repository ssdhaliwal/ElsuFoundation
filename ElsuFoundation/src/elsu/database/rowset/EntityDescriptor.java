/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database.rowset;

import com.google.gson.*;
import com.google.gson.reflect.*;
import elsu.support.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author ss.dhaliwal
 */
public class EntityDescriptor implements Serializable, Cloneable {

    private static final long serialVersionUID = 5099557402995654788L;

    private boolean _dirty = false;
    private int _columnCount = 0;
    private int _rowCount = 0;

    private Map<String, FieldDescriptor> _fields = null;
    private ArrayList<RowDescriptor> _rows = null;
    private Set<String> _indexKey = null;
    private Map<String, Map<String, RowDescriptor>> _index = null;

    public EntityDescriptor(Map<String, FieldDescriptor> fields,
            ArrayList<RowDescriptor> rows) {
        this._fields = fields;
        this._rows = rows;

        this._dirty = false;

        this._columnCount = this._fields.size();
        this._rowCount = this._rows.size();

        this._indexKey = new HashSet<String>();
        this._index = new HashMap<String, Map<String, RowDescriptor>>();
    }

    public EntityDescriptor(String jsonFields, String jsonRows) {
        Type fieldType = new TypeToken<Map<String, FieldDescriptor>>() {}.getType();
        final Map<String, FieldDescriptor> fields = (Map<String, FieldDescriptor>) GsonXMLStack.JSon2Object(jsonFields, fieldType);
        this._fields = fields;

        Type rowType = new TypeToken<ArrayList<String>>() {}.getType();
        class RowDescriptorInstanceCreator implements InstanceCreator<RowDescriptor> {
            @Override
            public RowDescriptor createInstance(Type fieldType) {
                return new RowDescriptor(fields);
            }
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RowDescriptor.class, new RowDescriptorInstanceCreator());
        Gson gson = gsonBuilder.create();
        
        //JSONObject obj = new JSONObject(jsonRows);
        //JsonReader jReader = new JsonReader(new InputStreamReader(new StringReader(jsonRows)));
        this._rows = (ArrayList<RowDescriptor>) GsonXMLStack.JSon2Object(jsonFields, rowType);

        this._dirty = false;

        this._columnCount = this._fields.size();
        this._rowCount = this._rows.size();

        this._indexKey = new HashSet<String>();
        this._index = new HashMap<String, Map<String, RowDescriptor>>();
    }

    public Boolean isDirty() {
        return this._dirty;
    }

    protected Boolean isDirty(Boolean dirty) {
        this._dirty = dirty;
        return this.isDirty();
    }

    public int getColumnCount() {
        return this._columnCount;
    }

    public int getRowCount() {
        return this._rowCount;
    }

    public Set<String> getFields() {
        return this._fields.keySet();
    }

    public FieldDescriptor getField(String field) {
        return this._fields.get(field);
    }

    public ArrayList<RowDescriptor> getRows() {
        return this._rows;
    }

    public RowDescriptor getRows(String index, String key) {
        return (this._index.get(index)).get(key);
    }

    public void buildIndex(String index) {
        // to-do
    }

    public void rebuildAllIndexes() {
        // to-do
    }
}
