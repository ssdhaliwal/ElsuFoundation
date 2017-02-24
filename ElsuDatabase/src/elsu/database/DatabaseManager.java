package elsu.database;

import elsu.events.*;
import java.util.*;
import java.sql.*;

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
    private String _connectionValidationSQL = "SELECT 1 FROM DUAL";

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
            	// 20170120 - updated to use properties to support derby
        		Properties props = new Properties();
        		props.put("user", this._user);
        		props.put("password", this._password);
                
        		//conn = DriverManager.getConnection(this._connectionString,
                //        this._user, this._password);
        		conn = DriverManager.getConnection(this._connectionString,
                        props);            }
            	conn.setAutoCommit(false);

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
                ArrayList<Connection> connections = this.getConnections();

                for (Iterator<Connection> itr = connections.iterator(); itr.hasNext();) {
                    Connection conn = itr.next();
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

    public String getConnectionValidationSQL() {
    	return _connectionValidationSQL;
    }

    public void setConnectionValidationSQL(String pValidationSQL) {
    	this._connectionValidationSQL = pValidationSQL;
    }
    
    // return connection
    public Connection getConnection() throws Exception {
        Connection result = null;

        synchronized (this._runtimeSync) {
            if (this.getConnections().isEmpty()) {
                if (this.getConnectionsActive().size() == this._totalConnections) {
                } else {
                    initializeConnections();
                }
            }

            // is there an open connection available
            if (this.getConnections().size() > 0) {
                result = (Connection) this.getConnections().get(0);

                // 20170205 - removed validation due to in-consistency in sql implementation
                //if (isValid(result)) {
                    this.getConnections().remove(result);
                    this.getConnectionsActive().add(result);
                /*} else {
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
                }*/
            }

            if (result != null) {
                notifyListeners(new EventObject(this), EventStatusType.CONNECT,
                        getClass().toString() + ", getConnection(), "
                        + "db connection reserved", result);
            }
        }

        if (result == null) {
            throw new Exception(getClass().toString() + ", getConnection(), "
                    + "db connection not available");
        }

        return result;
    }

    // http://stackoverflow.com/questions/3668506/efficient-sql-test-query-or-validation-query-that-will-work-across-all-or-most
    // H2, MySQL, Microsoft SQL Server (according to NimChimpsky), PostgreSQL, SQLite
    // SELECT 1
    //
    // ORACLE
    // SELECT 1 FROM DUAL
    //
    // HSQLDB
    // SELECT 1 FROM any_existing_table WHERE 1=0
    // SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS
    //
    // Apache Derby
    // VALUES 1 or SELECT 1 FROM SYSIBM.SYSDUMMY1
    //
    // DB2
    // SELECT 1 FROM SYSIBM.SYSDUMMY1
    //
    // INFORMIX
    // select count(*) from systables
    private boolean isValid(Connection conn) {
        boolean result = false;
        PreparedStatement stmt = null;

        try {
            if (conn == null) {
                result = false;
            } else if (conn.isClosed()) {
                result = false;
            } else {
                stmt = conn.prepareStatement(getConnectionValidationSQL());

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
    public synchronized void releaseConnection(Connection connection) throws Exception {
        synchronized (this._runtimeSync) {
            try {
                connection.close();
            } catch (Exception exi) { }
            
            // remove connection from active list and put it back in connections
            this.getConnectionsActive().remove(connection);

            notifyListeners(new EventObject(this), EventStatusType.DISCONNECT,
                    getClass().toString() + ", releaseConnection(), "
                    + "db connection released", connection);

            // interrupt the wait for connections
            notify();
        }

        this.initializeConnections();
    }

    // scans through all connections and checks if they have valid connection
    // 20141130 SSD implementation completed
    public void validateConnections() throws Exception {
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

            this.initializeConnections();

            notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
                    getClass().toString() + ", validateConnections(), "
                    + "db connections validated", null);
        }
    }
    
    // 20170205 added to handle bad connections
    public void validateConnection(Connection pConnection) throws Exception {
    	if (!isValid(pConnection)) {
            this.getConnectionsActive().remove(pConnection);
            this.getConnections().remove(pConnection);

            this.initializeConnections();
    	}

        notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
                getClass().toString() + ", validateConnections(), "
                + "db connections validated", null);
    }

    public void idle(long delay) {
        try {
            wait(delay);
        } catch (Exception exi) {
        }
    }
}
