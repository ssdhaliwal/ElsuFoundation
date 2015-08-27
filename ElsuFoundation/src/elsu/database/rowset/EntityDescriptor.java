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

    private transient int _columnCount = 0;
    private transient int _rowCount = 0;

    private transient Map<String, FieldDescriptor> _fields = null;
    private transient FieldDescriptor[] _fieldsById = null;
    private transient ArrayList<RowDescriptor> _rows = null;
    private Set<String> _indexKey = null;
    private transient Map<String, Map<String, RowDescriptor>> _index = null;

    public EntityDescriptor(Map<String, FieldDescriptor> fields,
            ArrayList<RowDescriptor> rows) {
        this._fields = fields;
        this._fieldsById = setFieldsById(this._fields);

        this._rows = rows;

        this._columnCount = this._fields.size();
        this._rowCount = this._rows.size();

        this._indexKey = new HashSet<String>();
        this._index = new HashMap<String, Map<String, RowDescriptor>>();
    }

    public EntityDescriptor(String jsonFields, String jsonRows) throws Exception {
        Type fieldType = new TypeToken<Map<String, FieldDescriptor>>() {
        }.getType();
        this._fields
                = (Map<String, FieldDescriptor>) GsonXMLStack.JSon2Object(jsonFields, fieldType);
        this._fieldsById = setFieldsById(this._fields);

        this._rows = new ArrayList<RowDescriptor>();

        RowDescriptor rd = null;
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(jsonRows).getAsJsonArray();
        for (JsonElement jElement : jArray) {
            rd = new RowDescriptor(this._fields, this._fieldsById, jElement.toString());
            this._rows.add(rd);
        }

        this._columnCount = this._fields.size();
        this._rowCount = this._rows.size();

        this._indexKey = new HashSet<String>();
        this._index = new HashMap<String, Map<String, RowDescriptor>>();
    }

    public int getColumnCount() {
        return this._columnCount;
    }

    public int getRowCount() {
        return this._rowCount;
    }

    public Set<String> getFieldKeySet() {
        return this._fields.keySet();
    }

    public FieldDescriptor getField(String field) {
        return this._fields.get(field.toUpperCase());
    }

    public Map<String, FieldDescriptor> getFields() {
        return this._fields;
    }

    public ArrayList<RowDescriptor> getRows() {
        return this._rows;
    }

    public RowDescriptor getRows(String index, String key) {
        return (this._index.get(index)).get(key);
    }

    public static FieldDescriptor[] setFieldsById(Map<String, FieldDescriptor> fields) {
        FieldDescriptor[] result = new FieldDescriptor[fields.size()];

        for (FieldDescriptor field : fields.values()) {
            result[field.getFieldPosition() - 1] = field;
        }

        return result;
    }

    public void buildIndex(String fields) throws Exception {
        String[] keyArray = fields.split(";");
        buildIndex(keyArray, false);
    }

    public void rebuildAllIndexes() throws Exception {
        // loop through all the index fields
        for (String keys : this._indexKey) {
            String[] keyArray = keys.split(";");
            buildIndex(keyArray, true);
        }
    }

    private void buildIndex(String[] fields, Boolean rebuild) throws Exception {
        int[] indexes = null;

        // index exists?
        if (rebuild) {
            // index exists?, Yes clear the current map
        } else {
            // if index exists, throw exception
            if (this._indexKey.contains(fields)) {
                throw new Exception(this.getClass() + "buildIndex(), index already exists");
            }
        }

        // collect field array ids
        indexes = new int[fields.length];
        for (int i = 0; i < fields.length; i++) {
            indexes[i] = this._fieldsById[i].getFieldPosition();
        }

        // build index
        for (RowDescriptor row : this.getRows()) {
            System.out.println(row.getValue(indexes));
        }
    }

    @Override
    public String toString() {
        String result = "";
        result = GsonXMLStack.Object2JSon(this);

        return result;
    }
}
