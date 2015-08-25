/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database.rowset;

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
    private Object[] _originalRow = null;
    private Object[] _currentRow = null;

    public RowDescriptor(Map<String, FieldDescriptor> fields) {
        this._fields = fields;

        this._originalRow = new Object[getFieldCount()];
        this._currentRow = new Object[getFieldCount()];
    }

    public RowDescriptor(Map<String, FieldDescriptor> fields,
            String jsonData) {
        this._fields = fields;
        this._originalRow = new Object[getFieldCount()];
        
        RowDescriptor rd = (RowDescriptor)GsonXMLStack.JSon2Object(jsonData, RowDescriptor.class);
        
        this._deleted = rd._deleted;
        this._currentRow = rd._currentRow;
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
        int index = this._fields.get(columnName).getFieldPosition();
        return this._currentRow[index];
    }

    public void setValue(String columnName, Object value) {
        int index = this._fields.get(columnName).getFieldPosition();

        this._originalRow[index] = this._currentRow[index];
        this._currentRow[index] = value;

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
        int index = this._fields.get(columnName).getFieldPosition();

        this._currentRow[index] = this._originalRow[index];
        this._originalRow[index] = null;

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

    @Override
    public String toString() {
        String result = "";
        result = GsonXMLStack.Object2XML(this);

        return result;
    }
}
