/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database.rowset;

import com.google.gson.*;
import elsu.support.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.Map;

/**
 *
 * @author ss.dhaliwal
 */
public class ColumnDescriptor implements Serializable, Cloneable {

    private static final long serialVersionUID = -7416407102795717036L;

    private String _name = "";
    private boolean _nullable = true;
    private boolean _caseSensitive = false;
    private boolean _readOnly = false;
    private boolean _identity = false;
    private boolean _currency = false;
    private boolean _signed = false;
    private int _displaySize = 0;
    private int _precision = 0;
    private int _scale = 0;
    private String _className = "";
    private int _type = 0;
    private String _typeName = "";

    private int _columnPosition = 0;

    public ColumnDescriptor(String name, boolean nullable, boolean caseSensitive,
            boolean readOnly, boolean identity, boolean currency, boolean signed,
            int displaySize, int precision, int scale, String className,
            int type, String typeName, int columnPosition) {
        this._name = name;

        this._nullable = nullable;
        this._caseSensitive = caseSensitive;
        this._readOnly = readOnly;
        this._identity = identity;
        this._currency = currency;
        this._signed = signed;

        this._displaySize = displaySize;
        this._precision = precision;
        this._scale = scale;

        this._className = className;
        this._type = type;
        this._typeName = typeName;

        this._columnPosition = columnPosition;
    }

    public ColumnDescriptor(String jsonColumn) {
        ColumnDescriptor fd = (ColumnDescriptor) GsonXMLStack.JSon2Object(jsonColumn, ColumnDescriptor.class);
        this.cloneColumn(fd);
    }

    public String getName() {
        return this._name;
    }

    public boolean isNullable() {
        return this._nullable;
    }

    public boolean isCaseSensitive() {
        return this._caseSensitive;
    }

    public boolean isReadOnly() {
        return this._readOnly;
    }

    public boolean isIdentity() {
        return this._identity;
    }

    public boolean isCurrency() {
        return this._identity;
    }

    public boolean isSigned() {
        return this._identity;
    }

    public int getDisplaySize() {
        return this._displaySize;
    }

    public int getPrecision() {
        return this._precision;
    }

    public int getScale() {
        return this._scale;
    }

    public String getClassName() {
        return this._className;
    }

    public int getType() {
        return this._type;
    }

    public String getTypeName() {
        return this._typeName;
    }

    public int getColumnPosition() {
        return this._columnPosition;
    }

    private void cloneColumn(ColumnDescriptor column) {
        this._name = column._name;

        this._nullable = column._nullable;
        this._caseSensitive = column._caseSensitive;
        this._readOnly = column._readOnly;
        this._identity = column._identity;
        this._currency = column._currency;
        this._signed = column._signed;

        this._displaySize = column._displaySize;
        this._precision = column._precision;
        this._scale = column._scale;

        this._className = column._className;
        this._type = column._type;
        this._typeName = column._typeName;

        this._columnPosition = column._columnPosition;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String spacer = "    ";

        sb.append(spacer + spacer + spacer + "<column-definition>" + newLine);

        sb.append(spacer + spacer + spacer + spacer + "<columnIndex>" + getColumnPosition() + "<columnIndex>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<name>" + getName() + "<name>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<nullable>" + isNullable() + "<nullable>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<caseSensitive>" + isCaseSensitive() + "<caseSensitive>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<readOnly>" + isReadOnly() + "<readOnly>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<identity>" + isIdentity() + "<identity>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<currency>" + isCurrency() + "<currency>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<signed>" + isSigned() + "<signed>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<displaySize>" + getDisplaySize() + "<displaySize>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<precision>" + getPrecision() + "<precision>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<scale>" + getScale() + "<scale>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<className>" + getClassName() + "<className>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<type>" + getType() + "<type>" + newLine);
        sb.append(spacer + spacer + spacer + spacer + "<typeName>" + getTypeName() + "<typeName>" + newLine);

        sb.append(spacer + spacer + spacer + "</column-definition>" + newLine);

        return sb.toString();
    }

    @Override
    public String toString() {
        String result = "";
        result = GsonXMLStack.Object2JSon(this);

        return result;
    }
}
