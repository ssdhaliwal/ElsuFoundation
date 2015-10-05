/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database.rowset;

import elsu.common.*;
import elsu.support.*;
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

    private transient Map<String, FieldDescriptor> _fields = null;
    private transient FieldDescriptor[] _fieldsById = null;
    private Object[] _originalRow = null;
    private Object[] _currentRow = null;

    public RowDescriptor(Map<String, FieldDescriptor> fields, FieldDescriptor[] fieldsById) {
        this._fields = fields;
        this._fieldsById = fieldsById;

        this._originalRow = new Object[getFieldCount()];
        this._currentRow = new Object[getFieldCount()];
    }

    public RowDescriptor(Map<String, FieldDescriptor> fields, FieldDescriptor[] fieldsById,
            String jsonRow) {
        this._fields = fields;
        this._fieldsById = fieldsById;

        RowDescriptor rd = (RowDescriptor) GsonXMLStack.JSon2Object(jsonRow, RowDescriptor.class);
        this.cloneRow(rd);
    }

    public RowDescriptor(Map<String, FieldDescriptor> fields, FieldDescriptor[] fieldsById, 
            Boolean deleted, Boolean changed, Object[] originalRow,
            Object[] currentRow) {
        this._fields = fields;
        this._fieldsById = fieldsById;

        this._deleted = deleted;
        this._changed = changed;
        
        this._originalRow = originalRow;
        this._currentRow = currentRow;
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

    public Object[] getOriginalRow() {
        if (!isChanged()) {
            return this._currentRow;
        } else {
            return this._originalRow;
        }
    }

    public Object[] getCurrentRow() {
        return this._currentRow;
    }

    public Object getValue(String columnName) {
        int index = this.getFieldPosition(columnName);
        return this._currentRow[index-1];
    }

    public Object getValue(String[] columnNames) {
        int[] index = new int[columnNames.length];
        
        for(int i = 0; i < columnNames.length; i++) {
            index[i] = this.getFieldPosition(columnNames[i]);
        }

        return getValue(index);
    }

    public Object getValue(int index) {
        return this._currentRow[index];
    }

    public String getValue(int[] indexes) {
        return getValue(indexes, false);
    }

    public String getValue(int[] indexes, Boolean fixedLength) {
        String result = "";
        String value = "";

        for(int i : indexes) {
            value = this._currentRow[i-1].toString();
            value = (fixedLength ? StringStack.padString(value, getFieldLength(i-1)) : value);
            
            result += (result.isEmpty() ? value : "," + value);
        }
        
        return result;
    }

    public void setValue(String columnName, Object value) {
        int index = getFieldPosition(columnName);

        this._originalRow[index-1] = this._currentRow[index-1];
        this._currentRow[index-1] = value;

        isChanged(true);
    }

    public void setValue(int index, Object value) {
        this._originalRow[index] = this._currentRow[index];
        this._currentRow[index] = value;

        isChanged(true);
    }

    public void resetToOriginal() {
        int index = 0;
        for (Object gw : this._originalRow) {
            this._currentRow[index] = gw;
            this._originalRow[index] = null;
            index++;
        }

        isChanged(false);
    }

    public void resetToOriginal(String columnName) {
        int index = getFieldPosition(columnName);

        this._currentRow[index-1] = this._originalRow[index-1];
        this._originalRow[index-1] = null;

        validateIsChanged();
    }

    public void resetToOriginal(int index) {
        this._currentRow[index] = this._originalRow[index];
        this._originalRow[index] = null;

        validateIsChanged();
    }

    private void validateIsChanged() {
        Boolean temp = false;

        for (int i = 0; i < _fields.size(); i++) {
            if (this._originalRow[i] != null) {
                temp = true;
                break;
            }
        }

        isChanged(temp);
    }

    private int getFieldPosition(String columnName) {
        return this._fields.get(columnName.toUpperCase()).getFieldPosition();
    }
    
    private int getFieldLength(String columnName) {
        int result = 0;
        
        result = this._fields.get(columnName.toUpperCase()).getDisplaySize();
        return result;
    }

    private int getFieldLength(int index) {
        int result = 0;

        result = this._fieldsById[index].getDisplaySize();
        return result;
    }
    
    private void cloneRow(RowDescriptor row) {
        this._deleted = row._deleted;
        this._changed = row._changed;
        
        this._originalRow = row._originalRow;
        this._currentRow = row._currentRow;
    }
    
    @Override
    public String toString() {
        String result = "";
        result = GsonXMLStack.Object2JSon(this);

        return result;
    }
}
