/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.text.*;
import javax.sql.rowset.serial.*;

/**
 *
 * @author ss.dhaliwal
 * http://docs.oracle.com/cd/B28359_01/server.111/b28318/datatype.htm#i16209
 *
 * note all of the get/set methods are direct/modified copies from
 * CachedRowSetImpl.java because it is better for them to static, accessible
 * globally as they can apply to numerous situations and did not want to mandate
 * inheritance
 *
 */
public class DatabaseStack {

    public static DatabaseDataType getDataType(Object o) {
        DatabaseDataType result = DatabaseDataType.dtstring;

        if (o == null) {
            result = DatabaseDataType.dtnull;
        } else if (o.getClass().equals(BigDecimal.class)) {
            result = DatabaseDataType.dtbigDecimal;
        } else if (o.getClass().equals(Blob.class)) {
            result = DatabaseDataType.dtblob;
        } else if (o.getClass().equals(Boolean.class)) {
            result = DatabaseDataType.dtboolean;
        } else if (o.getClass().equals(Byte.class)) {
            result = DatabaseDataType.dtboolean;
        } else if (o.getClass().equals(Byte[].class)) {
            result = DatabaseDataType.dtbyteArray;
        } else if (o.getClass().equals(Clob.class)) {
            result = DatabaseDataType.dtclob;
        } else if (o.getClass().equals(java.sql.Date.class)) {
            result = DatabaseDataType.dtdate;
        } else if (o.getClass().equals(Double.class)) {
            result = DatabaseDataType.dtdouble;
        } else if (o.getClass().equals(Float.class)) {
            result = DatabaseDataType.dtfloat;
        } else if (o.getClass().equals(Integer.class)) {
            result = DatabaseDataType.dtint;
        } else if (o.getClass().equals(Long.class)) {
            result = DatabaseDataType.dtlong;
        } else if (o.getClass().equals(String.class)) {
            result = DatabaseDataType.dtstring;
        } else if (o.getClass().equals(RowId.class)) {
            result = DatabaseDataType.dtrowid;
        } else if (o.getClass().equals(Short.class)) {
            result = DatabaseDataType.dtshort;
        } else if (o.getClass().equals(String.class)) {
            result = DatabaseDataType.dtstring;
        } else if (o.getClass().equals(Time.class)) {
            result = DatabaseDataType.dttime;
        } else if (o.getClass().equals(java.sql.Timestamp.class)) {
            result = DatabaseDataType.dttimestamp;
        }

        return result;
    }

    // custom return from object type modified from CachedRowSetImpl.java
    // note these are direct/modified copies from CachedRowSetImpl.java because
    // it is better for them to static, accessible globally as they can apply
    // to numerous situations and did not want to mandate inheritance
    public static boolean isBinary(int dataType) {
        switch (dataType) {
            case java.sql.Types.CLOB:
            case java.sql.Types.NCLOB:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDate(int dataType) {
        switch (dataType) {
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                return true;
            default:
                return false;
        }
    }

    public static boolean isBoolean(int dataType) {
        switch (dataType) {
            case java.sql.Types.BIT:
            case java.sql.Types.BOOLEAN:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNumeric(int dataType) {
        switch (dataType) {
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DECIMAL:
            case java.sql.Types.BIT:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
            case java.sql.Types.REAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isString(int dataType) {
        switch (dataType) {
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTemporal(int dataType) {
        switch (dataType) {
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPrimitive(Object value) {
        if ((value instanceof Byte) ||
                (value instanceof Short) ||
                (value instanceof Integer) ||
                (value instanceof Long) ||
                (value instanceof Float) ||
                (value instanceof Double) ||
                (value instanceof Character) ||
                (value instanceof String) ||
                (value instanceof Boolean)) {
            return true;
        } else {
            return false;
        }
    }

    public static Array getArray(Object value) throws Exception {
        // check for null
        if (value == null) {
            return null;
        }

        return (java.sql.Array) (value);
    }

    public static java.io.InputStream getAsciiStream(Object value, int columnType) throws Exception {
        InputStream asciiStream = null;

        // check for NULL
        if (value == null) {
            return null;
        }

        try {
            if (isString(columnType)) {
                asciiStream = new ByteArrayInputStream(((String) value).getBytes("ASCII"));
            } else {
                throw new Exception("asciiStream conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
            }
        } catch (java.io.UnsupportedEncodingException ex) {
            throw new Exception("asciiStream conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
        }

        return (java.io.InputStream) asciiStream;
    }

    public static BigDecimal getBigDecimal(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return null;
        }

        try {
            return (new BigDecimal(value.toString().trim()));
        } catch (NumberFormatException ex) {
            throw new Exception("bigdecimal conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static BigDecimal getBigDecimal(Object value, int scale) throws Exception {
        BigDecimal bDecimal, result;

        // check for NULL
        if (value == null) {
            return (new BigDecimal(0));
        }

        bDecimal = getBigDecimal(value);
        result = bDecimal.setScale(scale);

        return result;
    }

    public static java.io.InputStream getBinaryStream(Object value, int columnType) throws Exception {
        InputStream binaryStream = null;

        // check for NULL
        if (value == null) {
            return null;
        }

        if ((isBinary(columnType) == false) && (isString(columnType) == false)) {
            throw new Exception("binaryStream conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
        }

        binaryStream = new ByteArrayInputStream((byte[]) value);
        return (java.io.InputStream) binaryStream;
    }

    public static boolean getBoolean(Object value) throws Exception {
        // check for null
        if (value == null) {
            return false;
        }

        // check for Boolean...
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }

        // convert to a Double and compare to zero
        try {
            Double d = new Double(value.toString());
            if (d.compareTo(new Double((double) 0)) == 0) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException ex) {
            throw new Exception("boolean conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static byte getByte(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return (byte) 0;
        }

        try {
            return ((new Byte(value.toString())).byteValue());
        } catch (NumberFormatException ex) {
            throw new Exception("byte conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static byte[] getBytes(Object value, int columnType) throws Exception {
        return (byte[]) (value);
    }

    public static java.io.Reader getCharStream(Object value, int columnType) throws Exception {
        java.io.Reader charStream = null;

        // check for NULL
        if (value == null) {
            return null;
        }

        if (isBinary(columnType)) {
            charStream = new InputStreamReader(new ByteArrayInputStream((byte[]) value));
        } else if (isString(columnType)) {
            charStream = new StringReader(value.toString());
        } else {
            throw new Exception("charStream conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
        }

        return (java.io.Reader) charStream;
    }

    public static java.sql.Date getDate(Object value, int columnType) throws Exception {
        // check for null
        if (value == null) {
            return null;
        }

        /*
         * The object coming back from the db could be a date, a timestamp, or 
         * a char field variety. If it's a date type return it, a timestamp
         * we turn into a long and then into a date, char strings we try to 
         * parse.
         */
        switch (columnType) {
            case java.sql.Types.DATE: {
                long sec = ((java.sql.Date) value).getTime();
                return new java.sql.Date(sec);
            }
            case java.sql.Types.TIMESTAMP: {
                long sec = ((java.sql.Timestamp) value).getTime();
                return new java.sql.Date(sec);
            }
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR: 
            case java.sql.Types.NCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR: {
                try {
                    DateFormat df = DateFormat.getDateInstance();
                    return ((java.sql.Date) (df.parse(value.toString())));
                } catch (ParseException ex) {
                    throw new Exception("date conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
                }
            }
            default: {
                throw new Exception("date conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
            }
        }
    }

    public static Double getDouble(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return (double) 0;
        }

        try {
            return ((new Double(value.toString().trim())).doubleValue());
        } catch (NumberFormatException ex) {
            throw new Exception("double conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static float getFloat(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return (float) 0;
        }

        try {
            return ((new Float(value.toString().trim())).floatValue());
        } catch (NumberFormatException ex) {
            throw new Exception("float conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static int getInt(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return (int) 0;
        }

        try {
            return ((new Integer(value.toString().trim())).intValue());
        } catch (NumberFormatException ex) {
            throw new Exception("int conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static long getLong(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return (long) 0;
        }

        try {
            return ((new Long(value.toString().trim())).longValue());
        } catch (NumberFormatException ex) {
            throw new Exception("long conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static short getShort(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return (short) 0;
        }

        try {
            return ((new Short(value.toString().trim())).shortValue());
        } catch (NumberFormatException ex) {
            throw new Exception("short conversion failed. (" + value.toString().trim() + ")");
        }
    }

    public static String getString(Object value) throws Exception {
        // check for null
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public static java.sql.Time getTime(Object value, int columnType) throws Exception {
        // check for null
        if (value == null) {
            return null;
        }

        /*
         * The object coming back from the db could be a date, a timestamp, or 
         * a char field variety. If it's a date type return it, a timestamp
         * we turn into a long and then into a date, char strings we try to 
         * parse.
         */
        switch (columnType) {
            case java.sql.Types.TIME: {
                return (java.sql.Time) value;
            }
            case java.sql.Types.TIMESTAMP: {
                long sec = ((java.sql.Timestamp) value).getTime();
                return new java.sql.Time(sec);
            }
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR: {
                try {
                    DateFormat df = DateFormat.getDateInstance();
                    return ((java.sql.Time) (df.parse(value.toString())));
                } catch (ParseException ex) {
                    throw new Exception("time conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
                }
            }
            default: {
                throw new Exception("time conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
            }
        }
    }

    public static java.sql.Timestamp getTimestamp(Object value, int columnType) throws Exception {
        // check for null
        if (value == null) {
            return null;
        }

        /*
         * The object coming back from the db could be a date, a timestamp, or 
         * a char field variety. If it's a date type return it, a timestamp
         * we turn into a long and then into a date, char strings we try to 
         * parse.
         */
        switch (columnType) {
            case java.sql.Types.TIMESTAMP: {
                return (java.sql.Timestamp) value;
            }
            case java.sql.Types.TIME: {
                long sec = ((java.sql.Time) value).getTime();
                return new java.sql.Timestamp(sec);
            }
            case java.sql.Types.DATE: {
                long sec = ((java.sql.Date) value).getTime();
                return new java.sql.Timestamp(sec);
            }
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR: {
                try {
                    DateFormat df = DateFormat.getDateInstance();
                    return ((java.sql.Timestamp) (df.parse(value.toString())));
                } catch (ParseException ex) {
                    throw new Exception("timestamp conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
                }
            }
            default: {
                throw new Exception("timestamp conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
            }
        }
    }

    public static java.io.InputStream getUnicodeStream(Object value, int columnType) throws Exception {
        InputStream unicodeStream = null;

        // check for NULL
        if (value == null) {
            return null;
        }

        if ((isBinary(columnType) == false) && (isString(columnType) == false)) {
            throw new Exception("unicodeStream conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
        }

        unicodeStream = new ByteArrayInputStream(((String) value).getBytes("UTF-8"));
        return (java.io.InputStream) unicodeStream;
    }

    public static Object convertBoolean(Object value, int srcType, int destType) throws Exception {
        // is same source/destination then return value
        // * commented to allow cloneObject to allow clone creation
        // if (srcType == destType) {
        //     return value;
        // }

        if ((isNumeric(destType) == true)
                || ((isString(destType) == false) && (isBoolean(destType) == false))) {
            throw new Exception("boolean conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
        }

        try {
            switch (destType) {
                case java.sql.Types.BIT:
                    Integer i = new Integer(value.toString().trim());
                    return i.equals(new Integer((int) 0))
                            ? new Boolean(false)
                            : new Boolean(true);
                case java.sql.Types.BOOLEAN:
                    return new Boolean(value.toString().trim());
                default:
                    throw new Exception("boolean conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
            }
        } catch (NumberFormatException ex) {
            throw new Exception("boolean conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
        }
    }

    public static Object convertNumeric(Object value, int srcType, int destType) throws Exception {
        // is same source/destination then return value
        // * commented to allow cloneObject to allow clone creation
        // if (srcType == destType) {
        //     return value;
        // }

        if (isNumeric(destType) == false && isString(destType) == false) {
            throw new Exception("numeric conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
        }

        try {
            switch (destType) {
                case java.sql.Types.BIT:
                    Integer i = new Integer(value.toString().trim());
                    return i.equals(new Integer((int) 0))
                            ? new Boolean(false)
                            : new Boolean(true);
                case java.sql.Types.TINYINT:
                    return new Byte(value.toString().trim());
                case java.sql.Types.SMALLINT:
                    return new Short(value.toString().trim());
                case java.sql.Types.INTEGER:
                    return new Integer(value.toString().trim());
                case java.sql.Types.BIGINT:
                    return new Long(value.toString().trim());
                case java.sql.Types.NUMERIC:
                case java.sql.Types.DECIMAL:
                    return new BigDecimal(value.toString().trim());
                case java.sql.Types.REAL:
                case java.sql.Types.FLOAT:
                    return new Float(value.toString().trim());
                case java.sql.Types.DOUBLE:
                    return new Double(value.toString().trim());
                case java.sql.Types.CHAR:
                case java.sql.Types.VARCHAR:
                case java.sql.Types.LONGVARCHAR:
                case java.sql.Types.NCHAR:
                case java.sql.Types.NVARCHAR:
                case java.sql.Types.LONGNVARCHAR:
                    return new String(value.toString());
                default:
                    throw new Exception("numeric conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
            }
        } catch (NumberFormatException ex) {
            throw new Exception("numeric conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
        }
    }

    public static Object convertTemporal(Object value, int srcType, int destType) throws Exception {
        // is same source/destination then return value
        // * commented to allow cloneObject to allow clone creation
        // if (srcType == destType) {
        //     return value;
        // }

        if (isNumeric(destType) == false && isString(destType) == false) {
            throw new Exception("temporal conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
        }

        try {
            switch (destType) {
                case java.sql.Types.DATE:
                    if (srcType == java.sql.Types.TIMESTAMP) {
                        return new java.sql.Date(((java.sql.Timestamp) value).getTime());
                    } else {
                        return new java.sql.Date(((java.sql.Date)value).getTime());
                    }
                case java.sql.Types.TIMESTAMP:
                    if (srcType == java.sql.Types.TIME) {
                        return new Timestamp(((java.sql.Time) value).getTime());
                    } else {
                        return new Timestamp(((java.sql.Date) value).getTime());
                    }
                case java.sql.Types.TIME:
                    if (srcType == java.sql.Types.TIMESTAMP) {
                        return new Time(((java.sql.Timestamp) value).getTime());
                    } else {
                        return new java.sql.Time(((java.sql.Time)value).getTime());
                    }
                case java.sql.Types.CHAR:
                case java.sql.Types.VARCHAR:
                case java.sql.Types.LONGVARCHAR:
                case java.sql.Types.NCHAR:
                case java.sql.Types.NVARCHAR:
                case java.sql.Types.LONGNVARCHAR:
                    return new String(value.toString());
                default:
                    throw new Exception("temporal conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
            }
        } catch (NumberFormatException ex) {
            throw new Exception("temporal conversion failed. (" + value.toString().trim() + "/" + srcType + ":" + destType + ")");
        }
    }
    
    public static Object cloneObject(Object value, int dataType) throws Exception {
        // check for NULL
        if (value == null) {
            return null;
        }
        
        // if primitive, return original back to source
        if (isNumeric(dataType)) {
            return convertNumeric(value, dataType, dataType);
        } else if (isBoolean(dataType)) {
            return convertBoolean(value, dataType, dataType);
        } else if (isTemporal(dataType)) {
            return convertTemporal(value, dataType, dataType);
        } else {
            switch (dataType) {
                case java.sql.Types.ARRAY:
                    return getArray(value);
                case java.sql.Types.BLOB:
                    return new SerialBlob((Blob)value);
                case java.sql.Types.CLOB:
                case java.sql.Types.NCLOB:
                    return getCharStream(value, dataType);
                case java.sql.Types.DATALINK:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + "/" + dataType + ")");
                case java.sql.Types.DISTINCT:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + "/" + dataType + ")");
                case java.sql.Types.JAVA_OBJECT:
                    return value;
                case java.sql.Types.NULL:
                    return null;
                case java.sql.Types.OTHER:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + "/" + dataType + ")");
                case java.sql.Types.REF:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + "/" + dataType + ")");
                case java.sql.Types.ROWID:
                    return ((RowId)value).toString();
                case java.sql.Types.SQLXML:
                    return ((SQLXML)value).toString();
                case java.sql.Types.STRUCT:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + "/" + dataType + ")");
                default:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + "/" + dataType + ")");
            }
        }
    }
}
