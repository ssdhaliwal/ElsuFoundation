/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.database;

import java.math.*;
import java.sql.*;

/**
 *
 * @author ss.dhaliwal
 * http://docs.oracle.com/cd/B28359_01/server.111/b28318/datatype.htm#i16209
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

    public static DatabaseDataType getDataType(String className) {
        DatabaseDataType result = DatabaseDataType.dtstring;
/*
        if (className.equals() == null) {
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
*/
        return result;
    }
}
