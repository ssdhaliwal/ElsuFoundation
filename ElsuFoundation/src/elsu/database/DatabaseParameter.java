package elsu.database;

import elsu.common.*;
import java.util.*;
import java.sql.*;
import java.math.BigDecimal;
import oracle.jdbc.OracleTypes;
import oracle.sql.*;

/**
 *
 * @author ss.dhaliwal_admin
 */
public class DatabaseParameter {

    public String _name = null;
    public DatabaseDataType _type = DatabaseDataType.dtstring;
    public boolean _input = true;
    public boolean _output = false;
    public Object _value = null;
    public String _format = "MM/dd/yyyy H:m:s";

    public DatabaseParameter() {
    }

    public DatabaseParameter(String name, DatabaseDataType type, Object value) {
        this._name = name;
        this._type = type;
        this._value = value;
    }

    public DatabaseParameter(String name, DatabaseDataType type, Object value,
            String format) {
        this._name = name;
        this._type = type;
        this._value = value;
        this._format = format;
    }

    public DatabaseParameter(String name, DatabaseDataType type, boolean output) {
        this._name = name;
        this._type = type;
        this._output = output;

        if (this._output) {
            this._input = false;
        }
    }

    public DatabaseParameter(String name, DatabaseDataType type, boolean input,
            boolean output, Object value) {
        this._name = name;
        this._type = type;
        this._input = input;
        this._output = output;
        this._value = value;
    }

    public DatabaseParameter(String name, DatabaseDataType type, boolean input,
            boolean output, Object value, String format) {
        this._name = name;
        this._type = type;
        this._input = input;
        this._output = output;
        this._value = value;
        this._format = format;
    }

    public String getFormat() {
        return this._format;
    }

    public String getName() {
        return this._name;
    }

    public static Map<String, Object> getResult(CallableStatement stmt,
            ArrayList<DatabaseParameter> params) throws Exception {
        Map<String, Object> result = null;
        int paramIndex = 1;

        if (params == null) {
            return result;
        }

        for (DatabaseParameter dbpm : params) {
            if (dbpm.isOutput()) {
                if (result == null) {
                    result = new HashMap<>();
                }

                result.put(dbpm.getName(), stmt.getObject(paramIndex));
            }

            paramIndex++;
        }

        return result;
    }

    public static ResultSet getResultSet(CallableStatement stmt,
            ArrayList<DatabaseParameter> params) throws Exception {
        ResultSet result = null;
        int paramIndex = 1;

        if (params == null) {
            return result;
        }

        for (DatabaseParameter dbpm : params) {
            if (dbpm.isOutput()) {
                if (result == null) {
                    result = (ResultSet) stmt.getObject(paramIndex);
                }
            }

            paramIndex++;
        }

        return result;
    }

    public DatabaseDataType getType() {
        return this._type;
    }

    public Object getValue() {
        return this._value;
    }

    public boolean isInput() {
        return this._input;
    }

    public boolean isOutput() {
        return this._output;
    }

    public boolean isInput(boolean value) {
        this._input = value;
        return isInput();
    }

    public boolean isOutput(boolean value) {
        this._output = value;
        return isOutput();
    }

    public void setFormat(String value) {
        this._format = value;
    }

    public void setName(String value) {
        this._name = value;
    }

    public static void setParameterValue(PreparedStatement stmt,
            ArrayList<DatabaseParameter> params) throws Exception {
        int paramIndex = 1;

        if (params != null) {
            for (DatabaseParameter dbpm : params) {
                // if null pointer, special case
                switch (dbpm.getType()) {
                    case dtarray:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            ArrayDescriptor ad = ArrayDescriptor.createDescriptor("STR_ARRAY_TYP", stmt.getConnection());
                            ARRAY ar = new ARRAY(ad, stmt.getConnection(), dbpm.getValue());
                            stmt.setArray(paramIndex, ar);
                        }
                        break;
                    case dtbigDecimal:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBigDecimal(paramIndex, (BigDecimal) dbpm.getValue());
                        }
                        break;
                    case dtblob:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBlob(paramIndex, (Blob) dbpm.getValue());
                        }
                        break;
                    case dtboolean:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBoolean(paramIndex, (Boolean) dbpm.getValue());
                        }
                        break;
                    case dtbyte:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setByte(paramIndex, (Byte) dbpm.getValue());
                        }
                        break;
                    case dtbyteArray:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBytes(paramIndex, (byte[]) dbpm.getValue());
                        }
                        break;
                    case dtclob:
//                        File file = new File("1.wma");  
//                        fis = new FileInputStream(file);  
//                        stmt.setBinaryStream(1,fis,fis.available();  
//                        fis.close();  
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setClob(paramIndex, (Clob) dbpm.getValue());
                        }
                        break;
                    case dtcursor:
                        throw new Exception(
                                "invalid datatype return (SYS_REFCURSOR)!");
                    case dtdate:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setDate(paramIndex,
                                    DateStack.convertDate2SQLDate(
                                            dbpm.getValue().toString(),
                                            "MM/dd/yyyy H:m:s"));
                        }
                        break;
                    case dtdouble:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                stmt.setLong(paramIndex, ((BigDecimal) dbpm.getValue()).longValue());
                            } else {
                                stmt.setDouble(paramIndex, (Double) dbpm.getValue());
                            }
                        }
                        break;
                    case dtfloat:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                stmt.setFloat(paramIndex, ((BigDecimal) dbpm.getValue()).floatValue());
                            } else {
                                stmt.setFloat(paramIndex, (Float) dbpm.getValue());
                            }
                        }
                        break;
                    case dtint:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                stmt.setInt(paramIndex, ((BigDecimal) dbpm.getValue()).intValue());
                            } else {
                                stmt.setInt(paramIndex, (Integer) dbpm.getValue());
                            }
                        }
                        break;
                    case dtlong:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                stmt.setFloat(paramIndex, ((BigDecimal) dbpm.getValue()).floatValue());
                            } else {
                                stmt.setLong(paramIndex, (Long) dbpm.getValue());
                            }
                        }
                        break;
                    case dtrowid:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setRowId(paramIndex, (RowId) dbpm.getValue());
                        }
                        break;
                    case dtshort:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                stmt.setShort(paramIndex, ((BigDecimal) dbpm.getValue()).shortValue());
                            } else {
                                stmt.setShort(paramIndex, (Short) dbpm.getValue());
                            }
                        }
                        break;
                    case dtstream:
                            //                        File file = new File("1.wma");  
                        //                        fis = new FileInputStream(file);  
                        //                        stmt.setBinaryStream(1,fis,fis.available();  
                        //                        fis.close();  
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setString(paramIndex, (String) dbpm.getValue());
                        }
                        break;
                    case dtstring:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setString(paramIndex, (String) dbpm.getValue());
                        }
                        break;
                    case dttime:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setDate(paramIndex,
                                    DateStack.convertDate2SQLDate(
                                            dbpm.getValue().toString(),
                                            "MM/dd/yyyy H:m:s"));
                        }
                        break;
                    case dttimestamp:
                        //stmt.setTimestamp(paramIndex, DateStack.convertDate2SQLTimestamp(dbpm.getValue().toString(), "MM/dd/yyyy H:m:s"));
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setTimestamp(paramIndex,
                                    (java.sql.Timestamp) dbpm.getValue());
                        }
                        break;
                    default:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setString(paramIndex, (String) dbpm.getValue());
                        }
                        break;
                }

                paramIndex++;
            }
        }
    }

    public static void setParameterValue(CallableStatement stmt,
            ArrayList<DatabaseParameter> params) throws Exception {
        int paramIndex = 1;

        if (params != null) {
            for (DatabaseParameter dbpm : params) {
                switch (dbpm.getType()) {
                    case dtarray:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.ARRAY);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                ArrayDescriptor ad = ArrayDescriptor.createDescriptor("STR_ARRAY_TYP", stmt.getConnection());
                                ARRAY ar = new ARRAY(ad, stmt.getConnection(), dbpm.getValue());
                                stmt.setArray(paramIndex, ar);
                            }
                        }
                        break;
                    case dtbigDecimal:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.DOUBLE);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBigDecimal(paramIndex, (BigDecimal) dbpm.getValue());
                            }
                        }
                        break;
                    case dtblob:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BLOB);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBlob(paramIndex, (Blob) dbpm.getValue());
                            }
                        }
                        break;
                    case dtboolean:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BOOLEAN);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBoolean(paramIndex, (Boolean) dbpm.getValue());
                            }
                        }
                        break;
                    case dtbyte:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BOOLEAN);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setByte(paramIndex, (Byte) dbpm.getValue());
                            }
                        }
                        break;
                    case dtbyteArray:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex, java.sql.Types.ARRAY);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBytes(paramIndex, (byte[]) dbpm.getValue());
                            }
                        }
                        break;
                    case dtclob:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.CLOB);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setClob(paramIndex, (Clob) dbpm.getValue());
                            }
                        }
                        break;
                    case dtcursor:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    OracleTypes.CURSOR);
                        }
                        if (dbpm.isInput()) {
                            throw new Exception(
                                    "invalid datatype input (SYS_REFCURSOR)!");
                        }
                        break;
                    case dtdate:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.DATE);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setDate(paramIndex,
                                        DateStack.convertDate2SQLDate(
                                                dbpm.getValue().toString(),
                                                dbpm.getFormat()));
                            }
                        }
                        break;
                    case dtdouble:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.DOUBLE);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                    stmt.setDouble(paramIndex, ((BigDecimal) dbpm.getValue()).doubleValue());
                                } else {
                                    stmt.setDouble(paramIndex, (Double) dbpm.getValue());
                                }
                            }
                        }
                        break;
                    case dtfloat:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.REAL);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                    stmt.setFloat(paramIndex, ((BigDecimal) dbpm.getValue()).floatValue());
                                } else {
                                    stmt.setFloat(paramIndex, (Float) dbpm.getValue());
                                }
                            }
                        }
                        break;
                    case dtint:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.INTEGER);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                    stmt.setInt(paramIndex, ((BigDecimal) dbpm.getValue()).intValue());
                                } else {
                                    stmt.setInt(paramIndex, (Integer) dbpm.getValue());
                                }
                            }
                        }
                        break;
                    case dtlong:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BIGINT);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                    stmt.setLong(paramIndex, ((BigDecimal) dbpm.getValue()).longValue());
                                } else {
                                    stmt.setLong(paramIndex, (Long) dbpm.getValue());
                                }
                            }
                        }
                        break;
                    case dtrowid:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.ROWID);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setRowId(paramIndex, (RowId) dbpm.getValue());
                            }
                        }
                        break;
                    case dtshort:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.SMALLINT);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                    stmt.setShort(paramIndex, ((BigDecimal) dbpm.getValue()).shortValue());
                                } else {
                                    stmt.setShort(paramIndex, (Short) dbpm.getValue());
                                }
                            }
                        }
                        break;
                    case dtstream:
//                        File file = new File("1.wma");  
//                        fis = new FileInputStream(file);  
//                        stmt.setBinaryStream(1,fis,fis.available();  
//                        fis.close();  
                        if (dbpm.isOutput()) {
                            throw new Exception(
                                    "invalid datatype return (Stream)!");
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setString(paramIndex, (String) dbpm.getValue());
                            }
                        }
                        break;
                    case dtstring:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.VARCHAR);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setString(paramIndex, (String) dbpm.getValue());
                            }
                        }
                        break;
                    case dttime:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.TIME);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setDate(paramIndex,
                                        DateStack.convertDate2SQLDate(
                                                dbpm.getValue().toString(),
                                                dbpm.getFormat()));
                            }
                        }
                        break;
                    case dttimestamp:
                        //stmt.setTimestamp(paramIndex, DateStack.convertDate2SQLTimestamp(dbpm.getValue().toString(), "MM/dd/yyyy H:m:s"));
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.TIMESTAMP);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setTimestamp(paramIndex,
                                        (java.sql.Timestamp) dbpm.getValue());
                            }
                        }
                        break;
                    default:
                        if (dbpm.isOutput()) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.VARCHAR);
                        }
                        if (dbpm.isInput()) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setString(paramIndex, (String) dbpm.getValue());
                            }
                        }
                        break;
                }

                paramIndex++;
            }
        }
    }

    public void setType(DatabaseDataType value) {
        this._type = value;
    }

    public void setValue(Object value) {
        this._value = value;
    }
}
