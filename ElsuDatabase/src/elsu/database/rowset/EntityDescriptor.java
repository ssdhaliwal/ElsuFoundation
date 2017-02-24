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

    private Map<String, ColumnDescriptor> _columns = null;
    private transient ColumnDescriptor[] _columnsById = null;
    private ArrayList<RowDescriptor> _rows = null;

    public EntityDescriptor(Map<String, ColumnDescriptor> columns,
            ArrayList<RowDescriptor> rows) {
        this._columns = columns;
        this._columnsById = setColumnsById(this._columns);

        this._rows = rows;
    }

    public EntityDescriptor(String jsonColumns, String jsonRows) throws Exception {
        Type columnType = new TypeToken<Map<String, ColumnDescriptor>>() {
        }.getType();
        this._columns
                = (Map<String, ColumnDescriptor>) JsonXMLUtils.JSon2Object(jsonColumns, columnType);
        this._columnsById = setColumnsById(this._columns);

        this._rows = new ArrayList<RowDescriptor>();

        RowDescriptor rd = null;
        JsonParser parser = new JsonParser();

        JsonArray jArray = parser.parse(jsonRows).getAsJsonArray();
        for (JsonElement jElement : jArray) {
            rd = new RowDescriptor(this._columns, this._columnsById, jElement.toString());
            this._rows.add(rd);
        }
    }

    public void clear() {
        // clear the arraylist
        getRows().clear();
    }

    public int getColumnCount() {
        return this.getColumns().size();
    }

    public int getRowCount() {
        return this.getRows().size();
    }

    public Set<String> getColumnKeySet() {
        return this._columns.keySet();
    }

    public ColumnDescriptor getColumn(int index) {
        return getColumn(getColumnsById(), index);
    }

    public ColumnDescriptor getColumn(String column) {
        return this._columns.get(column.toUpperCase());
    }

    public static ColumnDescriptor getColumn(ColumnDescriptor[] columns, int columnIndex) {
        ColumnDescriptor result = null;

        for (ColumnDescriptor column : columns) {
            if (column.getColumnPosition() == columnIndex) {
                result = column;
                break;
            }
        }

        return result;
    }

    public Map<String, ColumnDescriptor> getColumns() {
        return this._columns;
    }

    public ArrayList<RowDescriptor> getRows() {
        return this._rows;
    }

    public ColumnDescriptor[] getColumnsById() {
        return this._columnsById;
    }

    public static ColumnDescriptor[] setColumnsById(Map<String, ColumnDescriptor> columns) {
        ColumnDescriptor[] result = new ColumnDescriptor[columns.size()];

        // column index is offset 1 not 0 but array offset is 0
        for (ColumnDescriptor column : columns.values()) {
            result[column.getColumnPosition() - 1] = column;
        }

        return result;
    }

    public void fromXML(String entity) throws Exception {
        throw new Exception(".. not yet implemented!!");
    }

    // http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/com/sun/rowset/WebRowSetImpl.java#WebRowSetImpl.writeXml%28java.sql.ResultSet%2Cjava.io.Writer%29
    public String toXML() throws Exception {
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String spacer = "    ";

        try {
            sb.append("<?xml version=\\\"1.0\\\"?>" + newLine);
            sb.append("<webRowSet xmlns=\\\"http://java.sun.com/xml/ns/jdbc\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"" + newLine);
            sb.append("xsi:schemaLocation=\\\"http://java.sun.com/xml/ns/jdbc http://java.sun.com/xml/ns/jdbc/webrowset.xsd\\\">" + newLine);

            sb.append(spacer + "<metadata>" + newLine);
            sb.append(spacer + spacer + "<column-count>" + getColumnCount() + "<column-count>" + newLine);
            // column index is offset 1 not 0 but array offset is 0
            sb.append(spacer + spacer + "<columns>" + newLine);
            for (int colIndex = 1; colIndex <= getColumnCount(); colIndex++) {
                sb.append(getColumn(_columnsById, colIndex).toXML());
            }
            sb.append(spacer + spacer + "</columns>" + newLine);
            sb.append(spacer + "</metadata>" + newLine);

            sb.append(spacer + "<data>" + newLine);
            sb.append(spacer + spacer + "<row-count>" + getRowCount() + "</row-count>" + newLine);
            sb.append(spacer + spacer + "<rows>" + newLine);
            for (RowDescriptor row : _rows) {
                sb.append(row.toXML());
            }
            sb.append(spacer + spacer + "</rows>" + newLine);
            sb.append(spacer + "</data>" + newLine);

            sb.append("</webRowSet>" + newLine);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
        }

        return sb.toString();
    }

    @Override
    public String toString() {
    	String result = "";
    	
    	try {
    		result = JsonXMLUtils.Object2JSon(this);
    	} catch (Exception ex) {
    		result = this.getClass().toString() + ".toString(), \n" + ex.getMessage() + "\n" + ex.getStackTrace();
    	}
    	
    	return result;
    }
}
