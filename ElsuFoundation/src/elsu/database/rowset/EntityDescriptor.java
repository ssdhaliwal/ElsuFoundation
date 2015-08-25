/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database.rowset;

import elsu.database.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author ss.dhaliwal
 */
public class EntityDescriptor implements Serializable, Cloneable {
    private static final long serialVersionUID = 5099557402995654788L;
    
    private String _sql = "";
    private ArrayList<DatabaseParameter> _parameters = null;
    private boolean _dirty = false;
    private int _columnCount = 0;
    private int _rowCount = 0;

    private Map<String, FieldDescriptor> _fields = null;
    private ArrayList<RowDescriptor> _rows = null;
    private Map<String, Map<String, RowDescriptor>> _index = null;
    
    public EntityDescriptor(String sql, ArrayList<DatabaseParameter> parameters,
            Map<String, FieldDescriptor> fields,
            ArrayList<RowDescriptor> rows) {
        
        this._readOnly = readOnly;
        this._dirty = false;
        
        this._columnCount = fields.size();
        this._rowCount = rows.size();
        
        this._fields = fields;
        this._rows = rows;
        this._index = new HashMap<String, Map<String, RowDescriptor>>();
    }
    
    public Boolean isReadOnly() {
        return this._readOnly;
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
}
