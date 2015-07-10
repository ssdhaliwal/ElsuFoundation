package elsu.database;

/**
 *
 * @author ss.dhaliwal_admin
 */
public enum DatabaseDataTypes {

    dtarray, // VARRAY
    dtbigDecimal, // DECIMAL(p,s), NUMBER, NUMERIC
    dtblob, // BLOB(n)
    dtboolean, // SMALLING
    dtbyte, // SMALLINT
    dtbyteArray, // CHAR(n), NCHAR(n), VARCHAR2(n), BLOB(n), ROWID, UROWID(n), RAW(n), LONG RAW
    dtclob, // CLOB(n), DBCLOB(n), NCLOB(n)
    dtcursor, // SYS_REFCURSOR
    dtdate, // DATE
    dtdouble, // DOUBLE
    dtfloat, // REAL, FLOAT
    dtint, // INTEGER, BINARY_INTEGER, PLS_INTEGER, NATURAL, INT
    dtlong, // DECIMAL(19, 0), BIGINT
    dtnull, // DBNULL
    dtrowid, // ROWID, UROWID(n)
    dtshort, // SMALLINT
    dtstream, // BLOB(n)
    dtstring, // CHAR(n), NCHAR(n), GRAPHIC(m), VARCHAR2(n), VARGRAPHIC(m), NVARCHAR2(n)
    dttime, // TIME
    dttimestamp // TIMESTAMP
}
