/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database.rowset;

import elsu.common.*;
import elsu.support.*;
import java.io.*;
import java.math.*;
import java.util.*;

/**
 *
 * @author ss.dhaliwal
 */
public class RowDescriptor implements Serializable, Cloneable {

    private static final long serialVersionUID = 934305883195588595L;

    private boolean _deleted = false;
    private boolean _changed = false;

    private transient Map<String, ColumnDescriptor> _columns = null;
    private transient ColumnDescriptor[] _columnsById = null;
    private Object[] _originalRow = null;
    private Object[] _currentRow = null;

    public RowDescriptor(Map<String, ColumnDescriptor> columns, ColumnDescriptor[] columnsById) {
        this._columns = columns;
        this._columnsById = columnsById;

        this._originalRow = new Object[getColumnCount()];
        this._currentRow = new Object[getColumnCount()];
    }

    public RowDescriptor(Map<String, ColumnDescriptor> columns, ColumnDescriptor[] columnsById,
            String jsonRow) {
        this._columns = columns;
        this._columnsById = columnsById;

        RowDescriptor rd = (RowDescriptor) GsonXMLStack.JSon2Object(jsonRow, RowDescriptor.class);
        this.cloneRow(rd);
    }

    public RowDescriptor(Map<String, ColumnDescriptor> columns, ColumnDescriptor[] columnsById,
            Object[] currentRow) throws Exception {
        if ((columns.size() != columnsById.length) ||
                (columns.size() != currentRow.length)) {
            throw new Exception("column count does not match values provided in current row array");
        }
        
        this._columns = columns;
        this._columnsById = columnsById;

        this._deleted = false;
        this._changed = false;

        // store data passed into current row
        for(int i = 0; i < getColumnCount(); i++) {
            this._originalRow[i] = null;
            this._currentRow[i] = currentRow[i];
        }
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

    public void clear() {
        this._deleted = false;
        this._changed = false;
        
        this._originalRow = new Object[getColumnCount()];
        this._currentRow = new Object[getColumnCount()];
    }
    
    public int getColumnCount() {
        return this._columns.size();
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
        // column index is offset 1 not 0 but array offset is 0
        int index = this.getColumnPosition(columnName);
        return this._currentRow[index - 1];
    }

    public Object getValue(String[] columnNames) {
        int[] index = new int[columnNames.length];

        // column index is offset 1 not 0 but array offset is 0
        for (int i = 0; i < columnNames.length; i++) {
            index[i] = this.getColumnPosition(columnNames[i]);
        }

        return getValue(index);
    }

    public Object getValue(int index) {
        return this._currentRow[index - 1];
    }

    public String getValue(int[] indexes) {
        return getValue(indexes, false);
    }

    public String getValue(int[] indexes, Boolean fixedLength) {
        String result = "";
        String value = "";

        // column index is offset 1 not 0 but array offset is 0
        for (int i : indexes) {
            value = this._currentRow[i - 1].toString();
            value = (fixedLength ? StringStack.padString(value, getColumnLength(i - 1)) : value);

            result += (result.isEmpty() ? value : "," + value);
        }

        return result;
    }

    public void setValue(String columnName, Object value) {
        int index = getColumnPosition(columnName);

        // column index is offset 1 not 0 but array offset is 0
        this._originalRow[index - 1] = this._currentRow[index - 1];
        this._currentRow[index - 1] = value;

        isChanged(true);
    }

    public void setValue(int index, Object value) {
        // column index is offset 1 not 0 but array offset is 0
        this._originalRow[index - 1] = this._currentRow[index - 1];
        this._currentRow[index - 1] = value;

        isChanged(true);
    }

    public void resetToOriginal() {
        int index = 0;

        // column index is offset 1 not 0 but array offset is 0
        for (Object gw : this._originalRow) {
            this._currentRow[index] = gw;
            this._originalRow[index] = null;
            index++;
        }

        isChanged(false);
    }

    public void resetToOriginal(String columnName) {
        int index = getColumnPosition(columnName);

        // column index is offset 1 not 0 but array offset is 0
        this._currentRow[index - 1] = this._originalRow[index - 1];
        this._originalRow[index - 1] = null;

        validateIsChanged();
    }

    public void resetToOriginal(String[] columnNames) {
        for(String column : columnNames) {
            resetToOriginal(column);
        }
    }

    public void resetToOriginal(int index) {
        // column index is offset 1 not 0 but array offset is 0
        this._currentRow[index - 1] = this._originalRow[index - 1];
        this._originalRow[index - 1] = null;

        validateIsChanged();
    }

    private void validateIsChanged() {
        Boolean temp = false;

        // column index is offset 1 not 0 but array offset is 0
        for (int i = 0; i < _columns.size(); i++) {
            if (this._originalRow[i] != null) {
                temp = true;
                break;
            }
        }

        isChanged(temp);
    }

    public ColumnDescriptor getColumn(int columnIndex) {
        return EntityDescriptor.getColumn(_columnsById, columnIndex);
    }

    public int getColumnPosition(String columnName) {
        return this._columns.get(columnName.toUpperCase()).getColumnPosition();
    }

    public int getColumnLength(String columnName) {
        int result = 0;

        result = this._columns.get(columnName.toUpperCase()).getDisplaySize();
        return result;
    }

    public int getColumnLength(int index) {
        int result = 0;

        // column index is offset 1 not 0 but array offset is 0
        result = this._columnsById[index - 1].getDisplaySize();
        return result;
    }

    public void cloneRow(RowDescriptor row) {
        this._deleted = row._deleted;
        this._changed = row._changed;

        // column index is offset 1 not 0 but array offset is 0
        for(int i = 0; i < getColumnCount(); i++) {
            this._originalRow[i] = row._originalRow[i];
            this._currentRow[i] = row._currentRow[i];
        }
    }

    public String toXML() throws Exception {
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String spacer = "    ";

        sb.append(spacer + spacer + spacer + "<row>" + newLine);

        // column index is offset 1 not 0 but array offset is 0
        for (int i = 0; i < getColumnCount(); i++) {
            sb.append(spacer + spacer + spacer + spacer + "<columnValue>");
            writeValue(i + 1, sb);
            sb.append("</columnValue>" + newLine);
        }

        sb.append(spacer + spacer + spacer + "</row>" + newLine);

        return sb.toString();
    }

    private void writeValue(int idx, StringBuilder builder) throws Exception {
        try {
            // column index is offset 1 not 0 but array offset is 0
            Object data = getValue(idx);

            if (data == null) {
                builder.append("null");
            } else {
                // column index is offset 1 not 0 but array offset is 0
                int type = EntityDescriptor.getColumn(_columnsById, idx).getType();

                switch (type) {
                    case java.sql.Types.BIT:
                    case java.sql.Types.BOOLEAN:
                        builder.append((Boolean) data);
                        break;
                    case java.sql.Types.TINYINT:
                    case java.sql.Types.SMALLINT:
                        builder.append((Short) data);
                        break;
                    case java.sql.Types.INTEGER:
                        builder.append((Integer) data);
                        break;
                    case java.sql.Types.BIGINT:
                        builder.append((Long) data);
                        break;
                    case java.sql.Types.REAL:
                    case java.sql.Types.FLOAT:
                        builder.append((Float) data);
                        break;
                    case java.sql.Types.DOUBLE:
                        builder.append((Double) data);
                        break;
                    case java.sql.Types.NUMERIC:
                    case java.sql.Types.DECIMAL:
                        if (data.getClass().equals(Long.class)) {
                            builder.append((Long) data);
                        } else {
                            builder.append((BigDecimal) data);
                        }
                        break;
                    case java.sql.Types.BINARY:
                    case java.sql.Types.VARBINARY:
                    case java.sql.Types.LONGVARBINARY:
                        break;
                    case java.sql.Types.DATE:
                        builder.append((Long) data);
                        break;
                    case java.sql.Types.TIME:
                        builder.append((Long) data);
                        break;
                    case java.sql.Types.TIMESTAMP:
                        builder.append((java.sql.Timestamp) data);
                        break;
                    case java.sql.Types.CHAR:
                    case java.sql.Types.VARCHAR:
                    case java.sql.Types.LONGVARCHAR:
                        builder.append(data);
                        break;
                    default:
                        throw new Exception("invalid column type (" + type + ")");
                    //Need to take care of BLOB, CLOB, Array, Ref here
                }
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public String toString() {
        String result = "";
        result = GsonXMLStack.Object2JSon(this);

        return result;
    }
}
