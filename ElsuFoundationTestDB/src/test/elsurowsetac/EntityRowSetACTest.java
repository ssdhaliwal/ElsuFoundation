package test.elsurowsetac;

import ac.core.*;
import ac.factory.*;
import elsu.database.*;
import elsu.database.rowset.*;
import elsu.events.*;
import elsu.support.*;

import java.sql.*;

public class EntityRowSetACTest implements IEventSubscriber {
	private ActionFactory _af = null;

	public EntityRowSetACTest(String config) {
		try {
			ConfigLoader cl = new ConfigLoader(config, null);

			this._af = new ActionFactory(cl);
			this._af.addEventListener(this);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			// instantiate the main controller class and call its run()
			// method to start service factory
			EntityRowSetACTest rut1 = new EntityRowSetACTest("config/app.config");
			DatabaseManager dbm = (DatabaseManager) rut1._af.getDbManager("LOCAL");
			Connection conn = null;

			try {
				conn = dbm.getConnection();
				try {
					EntityDescriptor wrs = ActionObjectUtils.View(conn, "SELECT * FROM change_tracker", null, null,
							null);
					System.out.println(ActionObjectUtils.toXML(wrs));
					System.out.println(".. records selected: " + wrs.getRowCount());
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

				try {
					EntityDescriptor wrs = ActionObjectUtils.View(conn, "SELECT * FROM change_tracker", "action LIKE ?",
							new int[] { java.sql.Types.VARCHAR }, new Object[] { "IN%" });
					System.out.println(ActionObjectUtils.toXML(wrs));
					System.out.println(".. records selected: " + wrs.getRowCount());
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
				/*
				try {
					EntityDescriptor wrs = ActionObjectUtils.View(conn, "SELECT * FROM NCS3.vwSITE", "SITE_ID LIKE ?",
							new int[] { java.sql.Types.VARCHAR }, new Object[] { "9%" });
					System.out.println(ActionObjectUtils.toXML(wrs));
					System.out.println(".. records selected: " + wrs.getRowCount());
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

				try {
					EntityDescriptor wrs = ActionObjectUtils.Cursor(conn, "NCS3.SPS_SITE",
							new int[] { java.sql.Types.ARRAY }, new Object[] { new Long[] { 830L, 838L } });
					System.out.println(ActionObjectUtils.toXML(wrs));
					System.out.println(".. records selected: " + wrs.getRowCount());
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

				try {
					EntityDescriptor wrs = ActionObjectUtils.View(conn, "SELECT * FROM NCS3.vwSITE", "SITE = ?",
							new int[] { java.sql.Types.VARCHAR }, new Object[] { "DHALIWAL2" });
					System.out.println(ActionObjectUtils.toXML(wrs));
					System.out.println(".. records selected: " + wrs.getRowCount());
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

				try {
					long siteId = 830L;

					long count = ActionObjectUtils.Execute(conn, "NCS3.SPD_SITE", new int[] { java.sql.Types.BIGINT },
							new Object[] { siteId }, null);
					// System.out.println(ActionObjectUtils.toXML(wrs));
					System.out.println(".. records affected: " + count);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

				try {
					EntityDescriptor wrs = ActionObjectUtils.View(conn, "SELECT * FROM NCS3.WEBSET", null, null, null);
					System.out.println(ActionObjectUtils.toXML(wrs));
					System.out.println(".. records selected: " + wrs.getRowCount());
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
				*/
			} catch (Exception exi) {
				System.out.println(exi.getMessage());
			}
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
