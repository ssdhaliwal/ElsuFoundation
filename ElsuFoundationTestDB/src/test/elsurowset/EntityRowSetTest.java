/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.elsurowset;

import elsu.events.*;
import elsu.support.*;
import java.sql.*;

/**
 *
 * @author ss.dhaliwal
 */
public class EntityRowSetTest implements IEventSubscriber {

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public EntityRowSetTest(String config) {    	
        try {
        	ConfigLoader cl = new ConfigLoader(config, null);
        	
            //af = new ActionFactory(cl);
            //af.addEventListener(this);
        	readDataBase();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void readDataBase() throws Exception {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/cfs?"
                            + "user=cfs&password=cfs&useLegacyDatetimeCode=false&serverTimezone=UTC");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from cfs.change_tracker");
            writeResultSet(resultSet);

            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  cfs.change_tracker (source, source_column, source_id, action, server) values (?, ?, ? , ?, ?)");
            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setString(1, "TEST");
            preparedStatement.setString(2, "id");
            preparedStatement.setString(3, "343243");
            preparedStatement.setString(4, "INSERT");
            preparedStatement.setString(5, "localhost@remote.site");
            preparedStatement.executeUpdate();

            preparedStatement = connect
                    .prepareStatement("SELECT id, source, source_column, source_id, action, server from cfs.change_tracker");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);

            // Remove again the insert comment
            //preparedStatement = connect
            //.prepareStatement("delete from feedback.comments where myuser= ? ; ");
            //preparedStatement.setString(1, "Test");
            //preparedStatement.executeUpdate();

            resultSet = statement
            .executeQuery("select * from cfs.change_tracker");
            writeMetaData(resultSet);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            int id = resultSet.getInt("id");
            String source = resultSet.getString("source");
            String source_column = resultSet.getString("source_column");
            String source_id = resultSet.getString("source_id");
            String action = resultSet.getString("action");
            String server = resultSet.getString("server");
            //Date date = resultSet.getDate("datum");
            System.out.println(id + ", " + source + ", " + source_column + ", " + source_id + ", " + 
            		action + ", " + server);
        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // instantiate the main controller class and call its run()
            // method to start service factory
            EntityRowSetTest rut1 = new EntityRowSetTest("config/app.config");
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
