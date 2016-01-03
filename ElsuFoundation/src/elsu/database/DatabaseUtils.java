/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database;

import elsu.common.*;
import elsu.database.rowset.*;
import elsu.events.*;
import java.io.*;
import java.math.*;
import java.nio.charset.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.sql.rowset.*;
import javax.sql.rowset.serial.*;
import oracle.jdbc.*;
import oracle.sql.*;

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
public class DatabaseUtils {

    private static String _dbCharacterSet = "US-ASCII";

    public static String getDbCharacterSet() {
        return _dbCharacterSet;
    }

    public static void setDbCharacterSet(String value) {
        _dbCharacterSet = value;
    }

    // return the java object to sql type mapping
    public static int getDbDataType(Object o) {
        int result = java.sql.Types.VARCHAR;

        if (o.getClass().equals(Array.class)) {
            result = java.sql.Types.ARRAY;
        } else if (o.getClass().equals(Long.class)) {
            result = java.sql.Types.BIGINT;
        } else if (o.getClass().equals(Boolean.class)) {
            result = java.sql.Types.BIT;
        } else if (o.getClass().equals(Blob.class)) {
            result = java.sql.Types.BLOB;
        } else if (o.getClass().equals(Clob.class)) {
            result = java.sql.Types.CLOB;
        } else if ((o.getClass().equals(java.sql.Date.class))
                || (o.getClass().equals(java.util.Date.class))) {
            result = java.sql.Types.DATE;
        } else if (o.getClass().equals(Double.class)) {
            result = java.sql.Types.DOUBLE;
        } else if (o.getClass().equals(Integer.class)) {
            result = java.sql.Types.INTEGER;
        } else if (o.getClass().equals(Float.class)) {
            result = java.sql.Types.REAL;
        } else if (o.getClass().equals(BigDecimal.class)) {
            result = java.sql.Types.NUMERIC;
        } else if (o.getClass().equals(Ref.class)) {
            result = java.sql.Types.REF;
        } else if (o.getClass().equals(SQLXML.class)) {
            result = java.sql.Types.SQLXML;
        } else if (o.getClass().equals(Short.class)) {
            result = java.sql.Types.SMALLINT;
        } else if (o.getClass().equals(Struct.class)) {
            result = java.sql.Types.STRUCT;
        } else if ((o.getClass().equals(java.sql.Time.class)) || (o.getClass().equals(Time.class))) {
            result = java.sql.Types.TIME;
        } else if ((o.getClass().equals(java.sql.Timestamp.class)) || (o.getClass().equals(Timestamp.class))) {
            result = java.sql.Types.TIMESTAMP;
        } else if (o.getClass().equals(Byte.class)) {
            result = java.sql.Types.TINYINT;
        } else if (o.getClass().equals(Byte[].class)) {
            result = java.sql.Types.VARBINARY;
        } else if (o.getClass().equals(String.class)) {
            result = java.sql.Types.VARCHAR;
        }

        return result;
    }

    public static int getDbDataType(String value) {
        int result = java.sql.Types.VARCHAR;

        if ((value == null) || (value.length() == 0)) {
            result = java.sql.Types.VARCHAR;
        } else if (value.equals("ARRAY")) {
            result = java.sql.Types.ARRAY;
        } else if ((value.equals("BIGINT")) || (value.equals("LONG"))) {
            result = java.sql.Types.BIGINT;
        } else if ((value.equals("BIT")) || (value.equals("BOOLEAN"))) {
            result = java.sql.Types.BIT;
        } else if (value.equals("BLOB")) {
            result = java.sql.Types.BLOB;
        } else if (value.equals("CLOB")) {
            result = java.sql.Types.CLOB;
        } else if (value.equals("DATE")) {
            result = java.sql.Types.DATE;
        } else if (value.equals("DOUBLE")) {
            result = java.sql.Types.DOUBLE;
        } else if (value.equals("INTEGER")) {
            result = java.sql.Types.INTEGER;
        } else if ((value.equals("REAL")) || (value.equals("FLOAT"))) {
            result = java.sql.Types.REAL;
        } else if ((value.equals("NUMERIC")) || (value.equals("BIGDECIMAL"))) {
            result = java.sql.Types.NUMERIC;
        } else if (value.equals("REF")) {
            result = java.sql.Types.REF;
        } else if (value.equals("SQLXML")) {
            result = java.sql.Types.SQLXML;
        } else if ((value.equals("SMALLINT")) || (value.equals("SHORT"))) {
            result = java.sql.Types.SMALLINT;
        } else if (value.equals("STRUCT")) {
            result = java.sql.Types.STRUCT;
        } else if (value.equals("TIME")) {
            result = java.sql.Types.TIME;
        } else if (value.equals("TIMESTAMP")) {
            result = java.sql.Types.TIMESTAMP;
        } else if ((value.equals("TINYINT")) || (value.equals("BYTE"))) {
            result = java.sql.Types.TINYINT;
        } else if ((value.equals("VARBINARY")) || (value.equals("BYTEARRAY"))) {
            result = java.sql.Types.VARBINARY;
        } else if ((value.equals("VARCHAR")) || (value.equals("STRING"))) {
            result = java.sql.Types.VARCHAR;
        }

        return result;
    }

    // custom return from object type modified from CachedRowSetImpl.java
    // note these are direct/modified copies from CachedRowSetImpl.java because
    // it is better for them to static, accessible globally as they can apply
    // to numerous situations and did not want to mandate inheritance
    public static boolean isBinary(int dataType) {
        switch (dataType) {
            case java.sql.Types.BLOB:
            case java.sql.Types.CLOB:
            case java.sql.Types.NCLOB:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.SQLXML:
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
        if ((value instanceof Byte)
                || (value instanceof Short)
                || (value instanceof Integer)
                || (value instanceof Long)
                || (value instanceof Float)
                || (value instanceof Double)
                || (value instanceof Character)
                || (value instanceof String)
                || (value instanceof Void)
                || (value instanceof Boolean)) {
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

    public static String getBinary(Object value, int columnType) throws Exception {
        String result = null;
        InputStream stream = null;
        char[] buffer = new char[1024];

        try {
            if ((columnType == java.sql.Types.BINARY) || (columnType == java.sql.Types.VARBINARY)
                    || (columnType == java.sql.Types.LONGVARBINARY) || (columnType == java.sql.Types.BLOB)) {
                byte[] blobData = ((Blob) value).getBytes(1, (int) ((Blob) value).length());
                result = new String(blobData, "US-ASCII");
            } else if (columnType == java.sql.Types.CLOB) {
                result = ((Clob) value).getSubString(1, (int) ((Clob) value).length());
            } else if ((columnType == java.sql.Types.VARCHAR)
                    || (columnType == java.sql.Types.LONGVARCHAR)) {
                byte[] blobData = ((Blob) value).getBytes(1, (int) ((Blob) value).length());
                result = new String(blobData, "UTF-8");
            }
        } catch (Exception ex) {
            throw new Exception("clob conversion failed. (" + value.toString().trim() + ")");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception exi) {
                }
            }
        }

        return result;
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

    public static java.io.InputStream getCharStream(Object value, int columnType) throws Exception {
        java.io.InputStream charStream = null;

        // check for NULL
        if (value == null) {
            return null;
        }

        if (isBinary(columnType)) {
            charStream = new ByteArrayInputStream((byte[]) value);
        } else if (isString(columnType)) {
            charStream = new ByteArrayInputStream(value.toString().getBytes(StandardCharsets.UTF_8));
        } else {
            throw new Exception("charStream conversion failed. (" + value.toString().trim() + "/" + columnType + ")");
        }

        return charStream;
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
                        return new java.sql.Date(((java.sql.Date) value).getTime());
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
                        return new java.sql.Time(((java.sql.Time) value).getTime());
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

    public static Object cloneObject(Object value) throws Exception {
        // check for NULL
        if (value == null) {
            return null;
        }

        // if primitive, return original back to source
        if (isPrimitive(value)) {
            return value;
        } else {
            switch (getDbDataType(value)) {
                case java.sql.Types.ARRAY:
                    return getArray(value);
                case java.sql.Types.BLOB:
                    return new SerialBlob((Blob) value);
                case java.sql.Types.CLOB:
                case java.sql.Types.NCLOB:
                    return getCharStream(value, java.sql.Types.VARCHAR);
                case java.sql.Types.DATALINK:
                    return (java.net.URL) value;
                case java.sql.Types.DISTINCT:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + ")");
                case java.sql.Types.JAVA_OBJECT:
                    return value;
                case java.sql.Types.NULL:
                    return null;
                case java.sql.Types.OTHER:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + ")");
                case java.sql.Types.REF:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + ")");
                case java.sql.Types.ROWID:
                    return ((RowId) value).toString();
                case java.sql.Types.SQLXML:
                    return ((SQLXML) value).toString();
                case java.sql.Types.STRUCT:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + ")");
                default:
                    throw new Exception("cloneObject conversion failed. (" + value.toString().trim() + ")");
            }
        }
    }

    public static CachedRowSet getCachedRowset(Connection conn, String sql, 
            ArrayList<DatabaseParameter> params) throws
            Exception {
        //Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        CachedRowSet crs = null;
        ResultSet rs = null;

        try {
            crs = RowSetProvider.newFactory().createCachedRowSet();

            try {
                stmt = conn.prepareStatement(sql);
                populateParameters(stmt, params);

                rs = stmt.executeQuery();
                crs.populate(rs);
            } catch (SQLException ex) {
                throw new SQLException(ex);
            } catch (Exception ex) {
                throw new Exception(ex);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }

        return crs;
    }

    public static EntityDescriptor getEntityDescriptor(Connection conn, String sql, 
            ArrayList<DatabaseParameter> params) throws
            Exception {
        //Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        EntityDescriptor result = null;

        try {
            stmt = conn.prepareStatement(sql);
            populateParameters(stmt, params);

            rs = stmt.executeQuery();
            result = populateEntityDescriptor(rs);
        } catch (SQLException ex) {
            throw new SQLException(ex);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }

        return result;
    }

    public static WebRowSet getWebRowSet(Connection conn, String sql, 
            ArrayList<DatabaseParameter> params) throws
            Exception {
        //Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        WebRowSet wrs = null;
        ResultSet rs = null;

        try {
            wrs = RowSetProvider.newFactory().createWebRowSet();

            try {
                stmt = conn.prepareStatement(sql);
                populateParameters(stmt, params);

                rs = stmt.executeQuery();
                wrs.populate(rs);
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }

        return wrs;
    }

    public static CachedRowSet getCachedRowSetViaCursor(Connection conn, String sql, 
            ArrayList<DatabaseParameter> params) throws
            Exception {
        //Connection conn = this.getConnection();
        CallableStatement stmt = null;
        CachedRowSet crs = null;
        ResultSet rs = null;

        try {
            crs = RowSetProvider.newFactory().createCachedRowSet();

            try {
                stmt = conn.prepareCall(sql);

                // add output cursor type to params
                params.add(new DatabaseParameter("paramOCursor", java.sql.Types.REF_CURSOR, true));
                populateParameters(stmt, params);

                stmt.execute();

                // load the output params into result by key
                rs = getResultSet(stmt, params);
                crs.populate(rs);
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }

        return crs;
    }

    public static EntityDescriptor getEntityDescriptorViaCursor(Connection conn, 
            String sql, ArrayList<DatabaseParameter> params) throws
            Exception {
        //Connection conn = this.getConnection();
        CallableStatement stmt = null;
        ResultSet rs = null;
        EntityDescriptor result = null;

        try {
            stmt = conn.prepareCall(sql);

            // add output cursor type to params
            params.add(new DatabaseParameter("paramOCursor", java.sql.Types.REF_CURSOR, true));
            populateParameters(stmt, params);

            stmt.execute();

            // load the output params into result by key
            rs = getResultSet(stmt, params);
            result = populateEntityDescriptor(rs);
        } catch (SQLException ex) {
            throw new SQLException(ex);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }

        return result;
    }

    public static WebRowSet getWebRowSetViaCursor(Connection conn, String sql, 
            ArrayList<DatabaseParameter> params) throws
            Exception {
        //Connection conn = this.getConnection();
        CallableStatement stmt = null;
        WebRowSet wrs = null;
        ResultSet rs = null;

        try {
            wrs = RowSetProvider.newFactory().createWebRowSet();

            try {
                stmt = conn.prepareCall(sql);

                // add output cursor type to params
                params.add(new DatabaseParameter("paramOCursor", java.sql.Types.REF_CURSOR, true));
                populateParameters(stmt, params);

                stmt.execute();

                // load the output params into result by key
                rs = getResultSet(stmt, params);
                wrs.populate(rs);
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }

        return wrs;
    }

    public static void executeDML(Connection conn, String sql, 
            ArrayList<DatabaseParameter> params) throws SQLException,
            Exception {
        //Connection conn = this.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            populateParameters(stmt, params);

            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw new SQLException(ex);
        } catch (Exception ex) {
            conn.rollback();
            throw new Exception(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }
    }

    public static PreparedStatement batchInitialize(Connection conn, String sql, 
            boolean procedure) throws
            Exception {
        //Connection conn = this.getConnection();
        PreparedStatement stmt = null;

        boolean isException = false;

        try {
            if (!procedure) {
                stmt = conn.prepareStatement(sql);
            } else {
                stmt = conn.prepareCall(sql);
            }
        } catch (SQLException ex) {
            conn.rollback();

            isException = true;
            throw new SQLException(ex);
        } catch (Exception ex) {
            conn.rollback();

            isException = true;
            throw new Exception(ex);
        } finally {
            if (isException) {
                if (stmt != null) {
                    stmt.close();
                }

                //this.releaseConnection(conn);
            }

            // return prepared statemetn
            return stmt;
        }
    }

    public static void batchExecute(Connection conn, PreparedStatement stmt, 
            ArrayList<DatabaseParameter> params) throws
            Exception {
        boolean isException = false;

        try {
            populateParameters(stmt, params);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            stmt.getConnection().rollback();

            isException = true;
            throw new SQLException(ex);
        } catch (Exception ex) {
            stmt.getConnection().rollback();

            isException = true;
            throw new Exception(ex);
        } finally {
            if (isException) {
                if (stmt != null) {
                    stmt.close();
                }

                //this.releaseConnection(stmt.getConnection());
            }
        }
    }

    public static void batchTerminate(Connection conn, PreparedStatement stmt) throws
            Exception {
        try {
            stmt.getConnection().commit();
        } catch (SQLException ex) {
            stmt.getConnection().rollback();
            throw new SQLException(ex);
        } catch (Exception ex) {
            stmt.getConnection().rollback();
            throw new Exception(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(stmt.getConnection());
        }
    }

    public static Map<String, Object> executeProcedure(Connection conn, String sql, 
            ArrayList<DatabaseParameter> params)
            throws Exception {
        Map<String, Object> result = null;
        //Connection conn = this.getConnection();
        CallableStatement stmt = null;

        try {
            stmt = conn.prepareCall(sql);
            populateParameters(stmt, params);

            stmt.executeUpdate();

            // load the output params into result by key
            result = getResultAsMap(stmt, params);

            // check if there is a cursor return param by name
            if (result.containsKey("paramOCursor")) {
                EntityDescriptor ed = populateEntityDescriptor((ResultSet) result.get("paramOCursor"));
                result.remove("paramOCursor");
                result.put("paramOCursor", ed);
            }

            // note, this is redundant - if SP has commit, data will be committed
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw new SQLException(ex);
        } catch (Exception ex) {
            conn.rollback();
            throw new Exception(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            //this.releaseConnection(conn);
        }

        return result;
    }

    public static EntityDescriptor populateEntityDescriptor(ResultSet rs) throws Exception {
        EntityDescriptor result = null;

        // extract the result set meta data and store it
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        Map<String, ColumnDescriptor> fdList = new HashMap<String, ColumnDescriptor>();
        ArrayList<RowDescriptor> rows = new ArrayList<RowDescriptor>();

        ColumnDescriptor fd = null;
        for (int i = 1; i <= cols; i++) {
            fd = new ColumnDescriptor(
                    rsmd.getColumnName(i),
                    (rsmd.isNullable(i) == ResultSetMetaData.columnNoNulls ? false : true),
                    rsmd.isCaseSensitive(i),
                    rsmd.isReadOnly(i),
                    rsmd.isAutoIncrement(i),
                    rsmd.isCurrency(i),
                    rsmd.isSigned(i),
                    rsmd.getColumnDisplaySize(i),
                    rsmd.getPrecision(i),
                    rsmd.getScale(i),
                    rsmd.getColumnClassName(i),
                    rsmd.getColumnType(i),
                    rsmd.getColumnTypeName(i),
                    i);

            fdList.put(rsmd.getColumnName(i), fd);
        }
        ColumnDescriptor[] fieldsById = EntityDescriptor.setColumnsById(fdList);
        result = new EntityDescriptor(fdList, rows);

        // store the resultset data.
        RowDescriptor rd = null;
        while (rs.next()) {
            rd = new RowDescriptor(fdList, fieldsById);

            for (int i = 1; i <= cols; i++) {
                // if primitive or wrapped, then direct assignment
                if (DatabaseUtils.isPrimitive(fieldsById[i - 1].getType())) {
                    rd.setValue(i, rs.getObject(i));
                } else {
                    rd.setValue(i, DatabaseUtils.cloneObject(rs.getObject(i)));
                }
            }

            result.getRows().add(rd);
        }

        return result;
    }

    public static Map<String, Object> getResultAsMap(CallableStatement stmt,
            ArrayList<DatabaseParameter> params) throws Exception {
        Map<String, Object> result = null;
        int paramIndex = 1;

        if (params == null) {
            return result;
        }

        for (DatabaseParameter dbpm : params) {
            if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
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
            if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                if (result == null) {
                    result = (ResultSet) stmt.getObject(paramIndex);
                }
            }

            paramIndex++;
        }

        return result;
    }

    public static void populateParameters(PreparedStatement stmt,
            ArrayList<DatabaseParameter> params) throws Exception {
        int paramIndex = 1;

        if (params != null) {
            for (DatabaseParameter dbpm : params) {
                // if null pointer, special case
                switch (dbpm.getType()) {
                    case java.sql.Types.ARRAY:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            ArrayDescriptor ad = ArrayDescriptor.createDescriptor("STR_ARRAY_TYP", stmt.getConnection());
                            ARRAY ar = new ARRAY(ad, stmt.getConnection(), dbpm.getValue());
                            stmt.setArray(paramIndex, ar);
                        }
                        break;
                    case java.sql.Types.DECIMAL:
                    case java.sql.Types.NUMERIC:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBigDecimal(paramIndex, (BigDecimal) dbpm.getValue());
                        }
                        break;
                    case java.sql.Types.BLOB:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBlob(paramIndex, (Blob) dbpm.getValue());
                        }
                        break;
                    case java.sql.Types.BIT:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBoolean(paramIndex, (Boolean) dbpm.getValue());
                        }
                        break;
                    case java.sql.Types.TINYINT:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setByte(paramIndex, (Byte) dbpm.getValue());
                        }
                        break;
                    case java.sql.Types.BINARY:
                    case java.sql.Types.VARBINARY:
                    case java.sql.Types.LONGVARBINARY:
                        //                        File file = new File("1.wma");  
                        //                        fis = new FileInputStream(file);  
                        //                        stmt.setBinaryStream(1,fis,fis.available();  
                        //                        fis.close();  
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setBytes(paramIndex, (byte[]) dbpm.getValue());
                        }
                        break;
                    case java.sql.Types.CLOB:
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
                    case java.sql.Types.REF_CURSOR:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            throw new Exception(
                                    "invalid datatype return (SYS_REFCURSOR)!");
                        }
                        break;
                    case java.sql.Types.DATE:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setDate(paramIndex,
                                    DateUtils.convertDate2SQLDate(
                                            dbpm.getValue().toString(),
                                            "MM/dd/yyyy H:m:s"));
                        }
                        break;
                    case java.sql.Types.DOUBLE:
                    case java.sql.Types.FLOAT:
                    case java.sql.Types.REAL:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                stmt.setDouble(paramIndex, ((BigDecimal) dbpm.getValue()).doubleValue());
                            } else if (dbpm.getValue().getClass().equals(Float.class)) {
                                stmt.setFloat(paramIndex, (Float) dbpm.getValue());
                            } else {
                                stmt.setDouble(paramIndex, (Double) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.INTEGER:
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
                    case java.sql.Types.BIGINT:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                stmt.setFloat(paramIndex, ((BigDecimal) dbpm.getValue()).longValue());
                            } else {
                                stmt.setLong(paramIndex, (Long) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.ROWID:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setRowId(paramIndex, (RowId) dbpm.getValue());
                        }
                        break;
                    case java.sql.Types.SMALLINT:
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
                    case java.sql.Types.CHAR:
                    case java.sql.Types.VARCHAR:
                    case java.sql.Types.LONGVARCHAR:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setString(paramIndex, (String) dbpm.getValue());
                        }
                        break;
                    case java.sql.Types.TIME:
                        if (dbpm.getValue() == null) {
                            stmt.setNull(paramIndex, java.sql.Types.NULL);
                        } else {
                            stmt.setDate(paramIndex,
                                    DateUtils.convertDate2SQLDate(
                                            dbpm.getValue().toString(),
                                            "MM/dd/yyyy H:m:s"));
                        }
                        break;
                    case java.sql.Types.TIMESTAMP:
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

    public static void populateParameters(CallableStatement stmt,
            ArrayList<DatabaseParameter> params) throws Exception {
        int paramIndex = 1;

        if (params != null) {
            for (DatabaseParameter dbpm : params) {
                switch (dbpm.getType()) {
                    case java.sql.Types.ARRAY:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.ARRAY);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                ArrayDescriptor ad = ArrayDescriptor.createDescriptor("STR_ARRAY_TYP", stmt.getConnection());
                                ARRAY ar = new ARRAY(ad, stmt.getConnection(), dbpm.getValue());
                                stmt.setArray(paramIndex, ar);
                            }
                        }
                        break;
                    case java.sql.Types.DECIMAL:
                    case java.sql.Types.NUMERIC:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.DOUBLE);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBigDecimal(paramIndex, (BigDecimal) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.BLOB:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BLOB);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBlob(paramIndex, (Blob) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.BIT:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BOOLEAN);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBoolean(paramIndex, (Boolean) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.TINYINT:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BOOLEAN);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setByte(paramIndex, (Byte) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.BINARY:
                    case java.sql.Types.VARBINARY:
                    case java.sql.Types.LONGVARBINARY:
                        //                        File file = new File("1.wma");  
                        //                        fis = new FileInputStream(file);  
                        //                        stmt.setBinaryStream(1,fis,fis.available();  
                        //                        fis.close();  
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex, java.sql.Types.ARRAY);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setBytes(paramIndex, (byte[]) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.CLOB:
//                        File file = new File("1.wma");  
//                        fis = new FileInputStream(file);  
//                        stmt.setBinaryStream(1,fis,fis.available();  
//                        fis.close();  
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.CLOB);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setClob(paramIndex, (Clob) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.REF_CURSOR:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    OracleTypes.CURSOR);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            throw new Exception(
                                    "invalid datatype input (SYS_REFCURSOR)!");
                        }
                        break;
                    case java.sql.Types.DATE:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.DATE);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setDate(paramIndex,
                                        DateUtils.convertDate2SQLDate(
                                                dbpm.getValue().toString(),
                                                dbpm.getFormat()));
                            }
                        }
                        break;
                    case java.sql.Types.DOUBLE:
                    case java.sql.Types.FLOAT:
                    case java.sql.Types.REAL:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.DOUBLE);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                if (dbpm.getValue().getClass().equals(BigDecimal.class)) {
                                    stmt.setDouble(paramIndex, ((BigDecimal) dbpm.getValue()).doubleValue());
                                } else if (dbpm.getValue().getClass().equals(Float.class)) {
                                    stmt.setFloat(paramIndex, (Float) dbpm.getValue());
                                } else {
                                    stmt.setDouble(paramIndex, (Double) dbpm.getValue());
                                }
                            }
                        }
                        break;
                    case java.sql.Types.INTEGER:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.INTEGER);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
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
                    case java.sql.Types.BIGINT:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.BIGINT);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
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
                    case java.sql.Types.ROWID:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.ROWID);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setRowId(paramIndex, (RowId) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.SMALLINT:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.SMALLINT);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
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
                    case java.sql.Types.CHAR:
                    case java.sql.Types.VARCHAR:
                    case java.sql.Types.LONGVARCHAR:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.VARCHAR);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setString(paramIndex, (String) dbpm.getValue());
                            }
                        }
                        break;
                    case java.sql.Types.TIME:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.TIME);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setDate(paramIndex,
                                        DateUtils.convertDate2SQLDate(
                                                dbpm.getValue().toString(),
                                                dbpm.getFormat()));
                            }
                        }
                        break;
                    case java.sql.Types.TIMESTAMP:
                        //stmt.setTimestamp(paramIndex, DateStack.convertDate2SQLTimestamp(dbpm.getValue().toString(), "MM/dd/yyyy H:m:s"));
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.TIMESTAMP);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
                            if (dbpm.getValue() == null) {
                                stmt.setNull(paramIndex, java.sql.Types.NULL);
                            } else {
                                stmt.setTimestamp(paramIndex,
                                        (java.sql.Timestamp) dbpm.getValue());
                            }
                        }
                        break;
                    default:
                        if (dbpm.getIO() == DatabaseParameterType.OUTPUT) {
                            stmt.registerOutParameter(paramIndex,
                                    java.sql.Types.VARCHAR);
                        }
                        if (dbpm.getIO() == DatabaseParameterType.INPUT) {
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
}
