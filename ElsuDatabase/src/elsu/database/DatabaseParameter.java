package elsu.database;

/**
 *
 * @author ss.dhaliwal_admin
 */
public class DatabaseParameter {

    private String _name = null;
    private int _type = java.sql.Types.VARCHAR;
    private DatabaseParameterType _io = DatabaseParameterType.INPUT;
    private Object _value = null;
    private String _format = "MM/dd/yyyy H:m:s";

    public DatabaseParameter() {
    }

    public DatabaseParameter(String name, int type, Object value) {
        this._name = name;
        this._type = type;
        this._value = value;
    }

    public DatabaseParameter(String name, int type, Object value,
            String format) {
        this._name = name;
        this._type = type;
        this._value = value;
        this._format = format;
    }

    public DatabaseParameter(String name, int type, DatabaseParameterType io) {
        this._name = name;
        this._type = type;
        this._io = io;
    }

    public DatabaseParameter(String name, int type, DatabaseParameterType io, Object value) {
        this._name = name;
        this._type = type;
        this._io = io;
        this._value = value;
    }

    public DatabaseParameter(String name, int type, DatabaseParameterType io, Object value, String format) {
        this._name = name;
        this._type = type;
        this._io = io;
        this._value = value;
        this._format = format;
    }

    public String getName() {
        return this._name;
    }

    public int getType() {
        return this._type;
    }

    public Object getValue() {
        return this._value;
    }

    public DatabaseParameterType getIO() {
        return this._io;
    }

    public String getFormat() {
        return this._format;
    }

    public void setName(String value) {
        this._name = value;
    }

    public void setType(int value) {
        this._type = value;
    }

    public void setValue(Object value) {
        this._value = value;
    }

    public void setIO(DatabaseParameterType io) {
        this._io = io;
    }

    public void setFormat(String value) {
        this._format = value;
    }
}
