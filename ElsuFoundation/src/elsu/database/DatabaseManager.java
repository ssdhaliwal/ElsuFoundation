package elsu.database;

import elsu.events.*;
import elsu.common.*;
import elsu.database.rowset.*;
import java.math.*;
import java.util.*;
import java.sql.*;
import javax.sql.rowset.*;
import oracle.jdbc.*;
import oracle.sql.*;

/**
 *
 * @author ss.dhaliwal_admin
 *
 * 20141128 SSD updated exceptions
 */
// http://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
public class DatabaseManager extends AbstractEventManager implements IEventPublisher {

    private Object _runtimeSync = new Object();
    private volatile ArrayList<Connection> _connections
            = new ArrayList<>();
    private volatile ArrayList<Connection> _connectionsActive
            = new ArrayList<>();
    private volatile int _totalConnections;
    private String _connectionString;
    private String _user;
    private String _password;

    public DatabaseManager(String dbDriver, String connectionString,
            int totalConnections) throws Exception {
        this._totalConnections = totalConnections;
        this._connectionString = connectionString;

        LoadDriver(dbDriver);

        initializeConnections();
    }

    public DatabaseManager(String dbDriver, String connectionString,
            int totalConnections, String user, String password) throws Exception {
        this._totalConnections = totalConnections;
        this._connectionString = connectionString;
        this._user = user;
        this._password = password;

        LoadDriver(dbDriver);

        initializeConnections();
    }

    private void initializeConnections() throws Exception {
        Connection conn;

        int i = this.getConnections().size()
                + this.getConnectionsActive().size();
        for (; i < this._totalConnections; i++) {
            if (this._user.isEmpty() && this._password.isEmpty()) {
                conn = DriverManager.getConnection(this._connectionString);
            } else {
                conn = DriverManager.getConnection(this._connectionString,
                        this._user, this._password);
            }

            this.getConnections().add(conn);
        }

        notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
                getClass().toString() + ", initializeConnections(), "
                + "successful.", null);
    }

    @Override
    public void finalize() throws Throwable {
        try {
            if (this.getConnections() != null) {
                ArrayList connections = this.getConnections();

                for (Iterator itr = connections.iterator(); itr.hasNext();) {
                    Connection conn = (Connection) itr.next();
                    conn.close();
                }
            }
        } catch (Exception exi) {
        } finally {
            super.finalize();
        }
    }

    // load driver
    public void LoadDriver(String dbDriver) throws Exception {
        Driver driver = (Driver) Class.forName(dbDriver).newInstance();
        DriverManager.registerDriver(driver);

        notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
                getClass().toString() + ", LoadDriver(), "
                + dbDriver + " loaded successfully.", null);
    }

    public ArrayList<Connection> getConnections() {
        return _connections;
    }

    public ArrayList<Connection> getConnectionsActive() {
        return _connectionsActive;
    }

    // return connection
    public Connection getConnection() throws Exception {
        Connection result = null;

        synchronized (this._runtimeSync) {
            while (this.getConnections().isEmpty()) {
                if (this.getConnectionsActive().size() == this._totalConnections) {
                    notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
                            getClass().toString() + ", getConnection(), "
                            + "all db connnections in use, none available.", null);
                    idle(30000);
                } else {
                    initializeConnections();
                    notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
                            getClass().toString() + ", getConnection(), "
                            + "db connections initialized.", null);
                }
            }

            // is there an open connection available
            if (this.getConnections().size() > 0) {
                result = (Connection) this.getConnections().get(0);

                if (isValid(result)) {
                    this.getConnections().remove(result);
                    this.getConnectionsActive().add(result);
                } else {
                    String errMsg = getClass().toString() + ", getConnection(), "
                            + " connection is not valid!!";
                    notifyListeners(new EventObject(this), EventStatusType.ERROR, errMsg, null);

                    // 20141130 SSD remove the connection and force close it
                    this.getConnections().remove(result);

                    try {
                        result.close();
                    } catch (Exception exi) {
                    }

                    result = null;
                    throw new Exception(errMsg);
                }
            }

            notifyListeners(new EventObject(this), EventStatusType.CONNECT,
                    getClass().toString() + ", getConnection(), "
                    + "db connection reserved", result);
        }

        return result;
    }

    private boolean isValid(Connection conn) {
        boolean result = false;
        PreparedStatement stmt = null;

        try {
            if (conn.isClosed()) {
                result = false;
            } else if (conn == null) {
                result = false;
            } else {
                stmt = conn.prepareStatement("SELECT 1 FROM DUAL");

                result = true;
            }
        } catch (Exception ex) {
            result = false;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception exi) {
            }
        }

        return result;
    }

    // note method synchronization is required for notify() to work, otherwise
    // illegalMonitorStateException is reported.
    public synchronized void releaseConnection(Connection connection) {
        synchronized (this._runtimeSync) {
            // remove connection from active list and put it back in connections
            this.getConnectionsActive().remove(connection);
            this.getConnections().add(connection);

            notifyListeners(new EventObject(this), EventStatusType.DISCONNECT,
                    getClass().toString() + ", releaseConnection(), "
                    + "db connection released", connection);

            // interrupt the wait for connections
            notify();
        }
    }

    // scans through all connections and checks if they have valid connection
    // 20141130 SSD implementation completed
    public void validateConnections() {
        synchronized (this._runtimeSync) {
            ArrayList<Connection> badConnections = new ArrayList<>();

            // scan available connections list and validate them
            for (Connection conn : getConnections()) {
                if (!isValid(conn)) {
                    badConnections.add(conn);
                }
            }

            // remove bad connections from list
            for (Connection conn : badConnections) {
                this.getConnections().remove(conn);

                try {
                    conn.close();
                } catch (Exception exi) {
                }
            }

            notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
                    getClass().toString() + ", validateConnections(), "
                    + "db connections validated", null);
        }
    }

    public CachedRowSet getData(String sql, ArrayList<DatabaseParameter> params) throws
            Exception {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        CachedRowSet crs = null;
        ResultSet rs = null;

        try {
            crs = RowSetProvider.newFactory().createCachedRowSet();

            try {
                stmt = conn.prepareStatement(sql);
                setParameterValue(stmt, params);

                rs = stmt.executeQuery();
                crs.populate(rs);

                notifyListeners(new EventObject(this), EventStatusType.SELECT,
                        getClass().toString() + ", getData(), "
                        + "CachedRowSet returned", crs);
            } catch (SQLException ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getData(), "
                        + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                        + ex.getMessage(), sql);
                throw new SQLException(ex);
            } catch (Exception ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getData(), "
                        + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
                throw new Exception(ex);
            }
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getData(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }

        return crs;
    }

    public EntityDescriptor getDataED(String sql, ArrayList<DatabaseParameter> params) throws
            Exception {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        EntityDescriptor result = null;

        try {
            stmt = conn.prepareStatement(sql);
            setParameterValue(stmt, params);

            rs = stmt.executeQuery();
            result = setEntityDescriptor(rs);

            notifyListeners(new EventObject(this), EventStatusType.SELECT,
                    getClass().toString() + ", getDataRowSet(), "
                    + "CachedRowSet returned", result);
        } catch (SQLException ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getDataRowSet(), "
                    + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                    + ex.getMessage(), sql);
            throw new SQLException(ex);
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getDataRowSet(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }

        return result;
    }

    public WebRowSet getDataXML(String sql, ArrayList<DatabaseParameter> params) throws
            Exception {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        WebRowSet wrs = null;
        ResultSet rs = null;

        try {
            wrs = RowSetProvider.newFactory().createWebRowSet();

            try {
                stmt = conn.prepareStatement(sql);
                setParameterValue(stmt, params);

                rs = stmt.executeQuery();
                wrs.populate(rs);

                notifyListeners(new EventObject(this), EventStatusType.SELECT,
                        getClass().toString() + ", getDataXML(), "
                        + "WebRowSet returned", wrs);
            } catch (SQLException ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getDataXML(), "
                        + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                        + ex.getMessage(), sql);
                throw new SQLException(ex);
            }
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getDataXML(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }

        return wrs;
    }

    public CachedRowSet getCursor(String sql, ArrayList<DatabaseParameter> params) throws
            Exception {
        Connection conn = this.getConnection();
        CallableStatement stmt = null;
        CachedRowSet crs = null;
        ResultSet rs = null;

        try {
            crs = RowSetProvider.newFactory().createCachedRowSet();

            try {
                stmt = conn.prepareCall(sql);

                // add output cursor type to params
                params.add(new DatabaseParameter("paramOCursor", java.sql.Types.REF_CURSOR, true));
                setParameterValue(stmt, params);

                stmt.execute();

                // load the output params into result by key
                rs = getResultSet(stmt, params);

                crs.populate(rs);
                notifyListeners(new EventObject(this), EventStatusType.SELECT,
                        getClass().toString() + ", getCursor(), "
                        + "WebRowSet returned", crs);
            } catch (SQLException ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getCursor(), "
                        + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                        + ex.getMessage(), sql);
                throw new SQLException(ex);
            }
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getCursor(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }

        return crs;
    }

    public EntityDescriptor getCursorED(String sql, ArrayList<DatabaseParameter> params) throws
            Exception {
        Connection conn = this.getConnection();
        CallableStatement stmt = null;
        ResultSet rs = null;
        EntityDescriptor result = null;

        try {
            stmt = conn.prepareCall(sql);

            // add output cursor type to params
            params.add(new DatabaseParameter("paramOCursor", java.sql.Types.REF_CURSOR, true));
            setParameterValue(stmt, params);

            stmt.execute();

            // load the output params into result by key
            rs = getResultSet(stmt, params);
            result = setEntityDescriptor(rs);

            notifyListeners(new EventObject(this), EventStatusType.SELECT,
                    getClass().toString() + ", getCursorRowSet(), "
                    + "WebRowSet returned", result);
        } catch (SQLException ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getCursorRowSet(), "
                    + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                    + ex.getMessage(), sql);
            throw new SQLException(ex);
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getCursorRowSet(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }

        return result;
    }

    public WebRowSet getCursorXML(String sql, ArrayList<DatabaseParameter> params) throws
            Exception {
        Connection conn = this.getConnection();
        CallableStatement stmt = null;
        WebRowSet wrs = null;
        ResultSet rs = null;

        try {
            wrs = RowSetProvider.newFactory().createWebRowSet();

            try {
                stmt = conn.prepareCall(sql);

                // add output cursor type to params
                params.add(new DatabaseParameter("paramOCursor", java.sql.Types.REF_CURSOR, true));
                setParameterValue(stmt, params);

                stmt.execute();

                // load the output params into result by key
                rs = getResultSet(stmt, params);

                wrs.populate(rs);
                notifyListeners(new EventObject(this), EventStatusType.SELECT,
                        getClass().toString() + ", getCursorXML(), "
                        + "WebRowSet returned", wrs);
            } catch (SQLException ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getCursorXML(), "
                        + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                        + ex.getMessage(), sql);
                throw new SQLException(ex);
            }
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", getCursorXML(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            throw new Exception(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }

        return wrs;
    }

    public void executeDirect(String sql, ArrayList<DatabaseParameter> params) throws SQLException,
            Exception {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            setParameterValue(stmt, params);

            stmt.executeUpdate();
            conn.commit();

            notifyListeners(new EventObject(this), EventStatusType.EXECUTE,
                    getClass().toString() + ", executeDirect(), "
                    + "executed successfully", sql);
        } catch (SQLException ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", executeDirect(), "
                    + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                    + ex.getMessage(), sql);
            conn.rollback();
            throw new SQLException(ex);
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", executeDirect(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            conn.rollback();
            throw new Exception(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }
    }

    public PreparedStatement batchInitialize(String sql, boolean procedure) throws
            Exception {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;

        boolean isException = false;

        try {
            if (!procedure) {
                stmt = conn.prepareStatement(sql);
            } else {
                stmt = conn.prepareCall(sql);
            }

            notifyListeners(new EventObject(this), EventStatusType.EXECUTE,
                    getClass().toString() + ", batchInitialize(), "
                    + "statement prepare successful.", sql);
        } catch (SQLException ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", batchInitialize(), "
                    + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                    + ex.getMessage(), sql);
            conn.rollback();

            isException = true;
            throw new SQLException(ex);
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", batchInitialize(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            conn.rollback();

            isException = true;
            throw new Exception(ex);
        } finally {
            if (isException) {
                if (stmt != null) {
                    stmt.close();
                }

                this.releaseConnection(conn);
            }

            // return prepared statemetn
            return stmt;
        }
    }

    public void batchRun(PreparedStatement stmt, ArrayList<DatabaseParameter> params) throws
            Exception {
        boolean isException = false;

        try {
            setParameterValue(stmt, params);
            stmt.executeUpdate();

            notifyListeners(new EventObject(this), EventStatusType.EXECUTE,
                    getClass().toString() + ", batchRun(), "
                    + "executed successfully", stmt);
        } catch (SQLException ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", batchRun(), "
                    + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                    + ex.getMessage(), stmt);
            stmt.getConnection().rollback();

            isException = true;
            throw new SQLException(ex);
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", batchRun(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), stmt);
            stmt.getConnection().rollback();

            isException = true;
            throw new Exception(ex);
        } finally {
            if (isException) {
                if (stmt != null) {
                    stmt.close();
                }

                this.releaseConnection(stmt.getConnection());
            }
        }
    }

    public void batchTerminate(PreparedStatement stmt) throws
            Exception {
        try {
            stmt.getConnection().commit();

            notifyListeners(new EventObject(this), EventStatusType.EXECUTE,
                    getClass().toString() + ", batchTerminate(), "
                    + "batch commit successful.", stmt);
        } catch (SQLException ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", batchTerminate(), "
                    + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                    + ex.getMessage(), stmt);
            stmt.getConnection().rollback();
            throw new SQLException(ex);
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", batchTerminate(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), stmt);
            stmt.getConnection().rollback();
            throw new Exception(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(stmt.getConnection());
        }
    }

    public Map<String, Object> executeProcedure(String sql, ArrayList<DatabaseParameter> params)
            throws Exception {
        Map<String, Object> result = null;
        Connection conn = this.getConnection();
        CallableStatement stmt = null;

        try {
            stmt = conn.prepareCall(sql);
            setParameterValue(stmt, params);

            stmt.executeUpdate();

            // load the output params into result by key
            result = getResult(stmt, params);

            // check if there is a cursor return param by name
            if (result.containsKey("paramOCursor")) {
                EntityDescriptor ed = setEntityDescriptor((ResultSet) result.get("paramOCursor"));
                result.remove("paramOCursor");
                result.put("paramOCursor", ed);
            }

            // note, this is redundant - if SP has commit, data will be committed
            conn.commit();

            notifyListeners(new EventObject(this), EventStatusType.EXECUTE,
                    getClass().toString() + ", executeProcedure(), "
                    + "executed successfully", result);
        } catch (SQLException ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", executeProcedure(), "
                    + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                    + ex.getMessage(), sql);
            conn.rollback();
            throw new SQLException(ex);
        } catch (Exception ex) {
            notifyListeners(new EventObject(this), EventStatusType.ERROR,
                    getClass().toString() + ", executeProcedure(), "
                    + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
            conn.rollback();
            throw new Exception(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            this.releaseConnection(conn);
        }

        return result;
    }

    public static EntityDescriptor setEntityDescriptor(ResultSet rs) throws Exception {
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
                if (DatabaseStack.isPrimitive(fieldsById[i - 1].getType())) {
                    rd.setValue(i, rs.getObject(i));
                } else {
                    rd.setValue(i, DatabaseStack.cloneObject(rs.getObject(i)));
                }
            }

            result.getRows().add(rd);
        }

        return result;
    }

    public static Map<String, Object> getResult(CallableStatement stmt,
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

    public static void setParameterValue(PreparedStatement stmt,
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
                                    DateStack.convertDate2SQLDate(
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
                                    DateStack.convertDate2SQLDate(
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

    public static void setParameterValue(CallableStatement stmt,
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
                                        DateStack.convertDate2SQLDate(
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
                                        DateStack.convertDate2SQLDate(
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

    public void idle(long delay) {
        try {
            wait(delay);
        } catch (Exception exi) {
        }
    }
}
