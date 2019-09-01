package test.elsurowsetac;

import elsu.database.*;
import elsu.database.rowset.*;
import elsu.events.*;
import elsu.support.*;

import java.sql.*;
import java.util.*;

import javax.sql.rowset.*;

public class EntityRowSetDirectTest extends AbstractEventManager implements IEventSubscriber {
	private ConfigLoader _config = null;
	private Map<String, Object> _dbManager = new HashMap<>();

	public EntityRowSetDirectTest(String config) {
		try {
			this._config = new ConfigLoader(config, null);
	        // assign new config to the variable
	        for (String key : this._config.getProperties().keySet()) {
	            System.out.println(key + " (TEXT=" + this._config.getProperties().get(key) + ")");
	        }
			

			setDbManager();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public String getFrameworkProperty(String key) {
		return this._config.getProperty("application.framework.attributes.key." + key).toString();
	}

	public String getFrameworkValue(String key) {
		return this._config.getProperty("application.framework." + key).toString();
	}

	public Object getDbManager(String key) {
		Object result = null;

		// if key is null, then set it to default
		if (key == null) {
			key = "default";
		}

		if (this._dbManager.containsKey(key)) {
			result = this._dbManager.get(key);
		}

		return result;
	}

	private void setDbManager() throws Exception {
		if (this._dbManager.size() == 0) {
			String[] connectionList = getFrameworkProperty("dbmanager.activeList").split(",");
			String[] propsList;

			for (String connName : connectionList) {
				this._config.logInfo("starting connection: " + connName);
				String dbDriver = getFrameworkProperty("dbmanager.connection." + connName + ".driver");
				String dbConnectionString = getFrameworkProperty("dbmanager.connection." + connName + ".uri");
				int maxPool = 5;
				try {
					maxPool = Integer.parseInt(getFrameworkProperty("dbmanager.connection." + connName + ".poolSize"));
				} catch (Exception ex) {
					maxPool = 5;
				}

				// check if properties are defined
				HashMap properties = new HashMap<String, String>();
				propsList = getFrameworkProperty("dbmanager.connection." + connName + ".params.list").split(",");
				for (String prop : propsList) {
					properties.put(prop, getFrameworkProperty("dbmanager.connection." + connName + ".params." + prop));
				}

				// capture any exceptions to prevent resource leaks
				// create the database manager
				setDbManager(connName, new DatabaseManager(dbDriver, dbConnectionString, maxPool, properties));

				// connect the event notifiers
				((DatabaseManager) getDbManager(connName)).addEventListener(this);

				notifyListeners(new EventObject(this), EventStatusType.INFORMATION,
						getClass().toString() + ", setDbManager(), " + "dbManager initialized.", null);
			}
		}
	}

	private void setDbManager(String key, Object dbManager) {
		if (this._dbManager.containsKey(key)) {
			this._dbManager.remove(key);
		}

		this._dbManager.put(key, dbManager);
	}

	public void doReplication() {
		try {
			// instantiate the main controller class and call its run()
			// method to start service factory
			String site = getFrameworkValue("replication.site");
			String siteConnName = getFrameworkValue("replication." + site + ".connection");
			String siteHostId = getFrameworkValue("replication." + site + ".hostId");

			DatabaseManager siteDBM = (DatabaseManager) getDbManager(siteConnName);
			Connection siteConn = null;

			ArrayList<DatabaseParameter> dbp = null;
			CachedRowSet crs = null;

			try {
				siteConn = siteDBM.getConnection();
				
				String[] publishers = getFrameworkValue("replication.publishers.activeList").split(",");
				String pubConnName = "";
				String pubHostId = "";

				DatabaseManager pubDBM = null;
				Connection pubConn = null;

				for (String publisher : publishers) {
					pubConnName = getFrameworkValue("replication.publishers." + publisher + ".connection");
					pubHostId = getFrameworkValue("replication.publishers." + publisher + ".hostId");

					try {
						pubDBM = (DatabaseManager) getDbManager(pubConnName);
						pubConn = pubDBM.getConnection();

						// select record from change_tracker for hostname from publisher String
						String statusSQL = "SELECT id, source, source_column, source_id, action, server, source_values, dateUpdated "
								+ "from change_tracker " + "where (server = ?)";

						// get records for each type based on source, source_column, and source_id
						dbp = new ArrayList<DatabaseParameter>();
						dbp.add(new DatabaseParameter("server", java.sql.Types.VARCHAR, DatabaseParameterType.INPUT,
								(Object) pubHostId));

						// retrieve it as cached result
						this._config.logInfo("retrieving data from: " + pubConnName + ", " + pubHostId);
						crs = DatabaseUtils.getCachedRowset(pubConn, statusSQL, dbp);
						if (crs != null) {
							while (crs.next()) {
								if (crs.getString(2).equals("intent")) {
									replIntent(siteConn, crs.getOriginal());
								}
							}
						}

					} catch (Exception ex) {
						System.out.println(ex.getMessage());

						try {
							pubConn.rollback();
						} catch (Exception exi) {
						}

						throw ex;
					} finally {
						try {
							pubDBM.releaseConnection(pubConn);
						} catch (Exception ex) {
							throw ex;
						}
					}
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());

				try {
					siteConn.rollback();
				} catch (Exception exi) {
				}

				throw ex;
			} finally {
				try {
					siteDBM.releaseConnection(siteConn);
				} catch (Exception ex) {
					throw ex;
				}
			}
		} catch (Exception ex) {
			// Display a message if anything goes wrong
			System.err.println("doReplication, EntityRowSetDirectTest, " + ex.getMessage());
			System.exit(1);
		}
	}

	private void replIntent(Connection siteConn, ResultSet sourceRS) {
		try {
			String selectCountSQL = "select count(*) from intent where version = ? and action = ?";
			String insertSQL = "insert into intent(version, action) values (?, ?)";
			String updateSQL = "update intent set version = ?, action = ? where action = ?";
			String deleteSQL = "delete from intent where action = ?";
			ArrayList<DatabaseParameter> params = new ArrayList<DatabaseParameter>();
			
			while (sourceRS.next()) {
				System.out.println(sourceRS.getInt(1) + ", " + sourceRS.getString(2) + ", " + sourceRS.getString(3)
						+ ", " + sourceRS.getString(4) + ", " + sourceRS.getString(5) + ", " + sourceRS.getString(6)
						+ ", " + sourceRS.getString(7) + ", " + sourceRS.getTimestamp(8).getTime());

				// insert the change in change_tracker
				try {
					executeStatement(siteConn, selectCountSQL, params);
					// insert the record into intent
					// delete the record from change_tracker
					
					// commit the changes
					siteConn.commit();
		        } catch (SQLException ex) {
		        	siteConn.rollback();
		            throw new SQLException(ex);
		        } catch (Exception ex) {
		        	siteConn.rollback();
		            throw new Exception(ex);
		        }
			}
		} catch (Exception ex) {
			// Display a message if anything goes wrong
			System.err.println("replIntent, EntityRowSetDirectTest, " + ex.getMessage());
			System.exit(1);
		}
	}
	
	private void executeStatement(Connection conn, String sql, ArrayList<DatabaseParameter> params) 
			throws Exception {
	    PreparedStatement stmt = null;

	    try {
	        stmt = conn.prepareStatement(sql);
	        DatabaseUtils.populateParameters(stmt, params);
	
	        stmt.executeUpdate();
	    } finally {
	        if (stmt != null) {
	            stmt.close();
	        }
	    }
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			// instantiate the main controller class and call its run()
			// method to start service factory
			EntityRowSetDirectTest rut1 = new EntityRowSetDirectTest("config/app.config");
			rut1.doReplication();
		} catch (Exception ex) {
			// Display a message if anything goes wrong
			System.err.println("RowsetUnitTest, main, " + ex.getMessage());
			System.exit(1);
		}
	}

	@Override
	public Object EventHandler(Object sender, IEventStatusType status, String message, Object o) {
		switch (EventStatusType.valueOf(status.getName())) {
		case DEBUG:
		case ERROR:
		case INFORMATION:
			System.out.println(status.getName() + ":" + message);
			break;
		default:
			break;
		}

		return null;
	}
}
