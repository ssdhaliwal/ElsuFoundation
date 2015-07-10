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
 */
public class DatabaseStack {

    public static DatabaseDataTypes getDataType(Object o) {
        DatabaseDataTypes result = DatabaseDataTypes.dtstring;

        if (o == null) {
            result = DatabaseDataTypes.dtnull;
        } else if (o.getClass().equals(BigDecimal.class)) {
            result = DatabaseDataTypes.dtbigDecimal;
        } else if (o.getClass().equals(Blob.class)) {
            result = DatabaseDataTypes.dtblob;
        } else if (o.getClass().equals(Boolean.class)) {
            result = DatabaseDataTypes.dtboolean;
        } else if (o.getClass().equals(Byte.class)) {
            result = DatabaseDataTypes.dtboolean;
        } else if (o.getClass().equals(Byte[].class)) {
            result = DatabaseDataTypes.dtbyteArray;
        } else if (o.getClass().equals(Clob.class)) {
            result = DatabaseDataTypes.dtclob;
        } else if (o.getClass().equals(java.sql.Date.class)) {
            result = DatabaseDataTypes.dtdate;
        } else if (o.getClass().equals(Double.class)) {
            result = DatabaseDataTypes.dtdouble;
        } else if (o.getClass().equals(Float.class)) {
            result = DatabaseDataTypes.dtfloat;
        } else if (o.getClass().equals(Integer.class)) {
            result = DatabaseDataTypes.dtint;
        } else if (o.getClass().equals(Long.class)) {
            result = DatabaseDataTypes.dtlong;
        } else if (o.getClass().equals(String.class)) {
            result = DatabaseDataTypes.dtstring;
        } else if (o.getClass().equals(RowId.class)) {
            result = DatabaseDataTypes.dtrowid;
        } else if (o.getClass().equals(Short.class)) {
            result = DatabaseDataTypes.dtshort;
        } else if (o.getClass().equals(String.class)) {
            result = DatabaseDataTypes.dtstring;
        } else if (o.getClass().equals(Time.class)) {
            result = DatabaseDataTypes.dttime;
        } else if (o.getClass().equals(java.sql.Timestamp.class)) {
            result = DatabaseDataTypes.dttimestamp;
        }

        return result;
    }
}
