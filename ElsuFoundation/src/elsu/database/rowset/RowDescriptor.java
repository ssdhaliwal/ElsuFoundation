/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database.rowset;

import java.io.*;
import java.util.*;

/**
 *
 * @author ss.dhaliwal
 */
public class RowDescriptor implements Serializable, Cloneable {
    private static final long serialVersionUID = 934305883195588595L;
    
    private boolean _deleted = false;
    private boolean _changed = false;
    
    private Map<String, FieldDescriptor> _fields = null;
    private DbColumn[] _originalRow = null;
    private DbColumn[] _currentRow = null;
    
    public RowDescriptor(Map<String, FieldDescriptor> fields) {
        this._fields = fields;
        
        this._originalRow = new DbColumn[getFieldCount()];
        this._currentRow = new DbColumn[getFieldCount()];
    }
    
    public boolean isDeleted() {
        return this._deleted;
    }
    public boolean isDeleted(boolean value) {
        this._deleted = value;
        
        resetToOriginal();
        return isDeleted();
    }
    
    public boolean isChanged() {
        return this._changed;
    }
    public boolean isChanged(boolean value) {
        this._changed = value;
        return isChanged();
    }
    
    private int getFieldCount() {
        return this._fields.size();
    }
    
    public DbColumn[] getOriginalRow() {
        if (!isChanged()) {
            return this._currentRow;
        } else {
            return this._originalRow;
        }
    }
    
    public DbColumn[] getCurrentRow() {
        return this._currentRow;
    }
    
    public DbColumn getValue(String columnName) {
        int index = this._fields.get(columnName).getFieldPosition();
        return this._currentRow[index];
    }
    
    public void setValue(String columnName, DbColumn value) {
        int index = this._fields.get(columnName).getFieldPosition();
        
        this._originalRow[index] = this._currentRow[index];
        this._currentRow[index] = value;
        
        isChanged(true);
    }
    
    public void resetToOriginal() {
        int index = 0;
        for(DbColumn gw : this._originalRow) {
            this._currentRow[index] = gw;
            this._originalRow[index] = null;
            index++;
        }
        
        isChanged(false);
    }
}
