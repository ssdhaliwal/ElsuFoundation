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
            if (this.getConnections().isEmpty()) {
                if (this.getConnectionsActive().size() == this._totalConnections) {
                } else {
                    initializeConnections();
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

    public void idle(long delay) {
        try {
            wait(delay);
        } catch (Exception exi) {
        }
    }
}
