package elsu.database;

/**
 *
 * @author ss.dhaliwal_admin
 */
public enum DatabaseDataType {

    dtarray, // VARRAY
    dtbigDecimal, // DECIMAL(p,s), NUMBER, NUMERIC
    dtblob, // BLOB(n)
    dtboolean, // SMALLING
    dtbyte, // SMALLINT
    dtbyteArray, // BLOB(n), BINARY, VARBINARY, LONGVARBINARY, RAW(n), LONGRAW
    dtclob, // CLOB(n), DBCLOB(n), NCLOB(n), XML
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
    dtstring, // CHAR(n), NCHAR(n), VARCHAR2(n), NVARCHAR2(n), GRAPHIC(m), VARGRAPHIC(m)
    dttime, // TIME
    dttimestamp // TIMESTAMP
}
