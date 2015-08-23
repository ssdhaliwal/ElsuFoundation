package elsu.database;

import elsu.events.*;
import elsu.common.*;
import java.util.*;
import java.sql.*;
import javax.sql.rowset.*;

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
                DatabaseParameter.setParameterValue(stmt, params);

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
                DatabaseParameter.setParameterValue(stmt, params);

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
            } catch (Exception ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getDataXML(), "
                        + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
                throw new Exception(ex);
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

    public WebRowSet getDataXMLViaCursor(String sql, ArrayList<DatabaseParameter> params) throws
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
                params.add(new DatabaseParameter("paramcursor", DatabaseDataType.dtcursor, true));
                DatabaseParameter.setParameterValue(stmt, params);

                stmt.execute();

                // load the output params into result by key
                rs = DatabaseParameter.getResultSet(stmt, params);

                wrs.populate(rs);
                notifyListeners(new EventObject(this), EventStatusType.SELECT,
                        getClass().toString() + ", getDataXMLViaCursor(), "
                        + "WebRowSet returned", wrs);
            } catch (SQLException ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getDataXML(), "
                        + ex.getErrorCode() + GlobalStack.LINESEPARATOR
                        + ex.getMessage(), sql);
                throw new SQLException(ex);
            } catch (Exception ex) {
                notifyListeners(new EventObject(this), EventStatusType.ERROR,
                        getClass().toString() + ", getDataXML(), "
                        + GlobalStack.LINESEPARATOR + ex.getMessage(), sql);
                throw new Exception(ex);
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

    public void executeDirect(String sql, ArrayList<DatabaseParameter> params) throws SQLException,
            Exception {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            DatabaseParameter.setParameterValue(stmt, params);

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
            DatabaseParameter.setParameterValue(stmt, params);
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
            DatabaseParameter.setParameterValue(stmt, params);

            stmt.executeUpdate();

            // load the output params into result by key
            result = DatabaseParameter.getResult(stmt, params);
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

    public void idle(long delay) {
        try {
            wait(delay);
        } catch (Exception exi) {
        }
    }
}
