/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import elsu.common.*;
import elsu.support.*;
import java.util.*;
import java.io.*;
import org.apache.commons.lang3.*;

/**
 * ConfigLoader is the base class for factory. The core purpose is to load the
 * app.config provided through the application command line arguments or the
 * default app.config stored as the resource in the jar file.
 * <p>
 * app.config once extracted from the jar file is not over-written every time
 * but reused allowing the user to change the extracted app.config file.
 * <p>
 * The configuration load is done using the direct XPath references to the node
 * properties and recursive nodes are processed by first collecting the node
 * names into a list and then iterating over the list.
 * <p>
 * log4j.properties is also extracted upon initial run of the program. Logging
 * is configured during the initial load.
 *
 * @author Seraj Dhaliwal (seraj.s.dhaliwal@uscg.mil)
 * @version .51
 */
public class ConfigLoader {
    // <editor-fold desc="class private storage">

    // static property for app.config store and extraction from jar file
    private static int _MAXKEYLENGTH = 25;
    private static String _APPCONFIG = "config/app.config";
    // static property for data format across the application for display 
    // purposes
    private static String _DTGFORMAT = "YYYMMDD HH24:mm:ss";
    // variable to store the xml document object from XMLReader class
    protected XMLReader _xmlr = null;
    // store all application wide properties from app.config
    private Map<String, String> _frameworkProperties = new HashMap<>();
    // store all action specific configuration items from app.config
    private Map<String, Object> _groupProperties = new HashMap<>();
    private String[] _key = new String[ConfigLoader._MAXKEYLENGTH];
    // </editor-fold>

    // <editor-fold desc="class constructor destructor">
    /**
     * ConfigLoader() no-argument constructor is used to load the default
     * app.config which is set through the static APPCONFIG variable prior to
     * instantiation of the class.
     * <p>
     * Constructor will try to extract the stored app.config in the application
     * jar file if available.
     *
     * @throws Exception
     */
    public ConfigLoader() throws Exception {
        this(ConfigLoader._APPCONFIG);
    }

    /**
     * ConfigLoader(...) constructor is used to load custom configuration passed
     * through the string variable. Normally used by control service to pass
     * custom XML sent from the client.
     *
     * updated to parse the config variable to determine if it is not a properly
     * formatted xml (starts with "<?xml ")
     *
     * @param configData is the XML data passed from the calling function.
     * @throws Exception
     */
    public ConfigLoader(String config) throws Exception {
        try {
            // check the format, if raw xml?
            if (config.startsWith("<?xml ")) {
                // try to create the XML reader instance for XML document parsing
                // using the app.config file location
                _xmlr = new XMLReader(config);
            } else {
                // load the resource or file path
                ConfigLoader._APPCONFIG = config;
                String configFile;

                // check is app.config and log4j.properties file is stored in the
                // application; note, if variable already contains a path then 
                // external config is used view package extraction
                configFile = ConfigLoader._APPCONFIG;

                // extract file to local file system
                extractConfigFile(configFile);

                // try to create the XML reader instance for XML document parsing
                // using the app.config file location
                _xmlr = new XMLReader(configFile);
            }

            // display the config to the user
            showConfig();

            // load the config into application or service properties hashMaps
            initializeConfig();
        } catch (Exception ex) {
            // display exception to the user and exit
            System.out.println(getClass().toString() + "//" + ex.getMessage());
        }
    }

    /**
     * initializeConfig() clears the storage application and service hashMaps
     * and then loads the app.config using XPath to reference each property.
     *
     * @throws Exception
     */
    private void initializeConfig() throws Exception {
        // clear the storage hashMaps
        getFrameworkProperties().clear();

        // clear the storage hashMaps
        getGroupProperties().clear();
    }
    // </editor-fold>

    // <editor-fold desc="class getter/setters">
    /**
     * getApplicationProperties() method returns the hashMap containing the
     * application properties key/value pair extracted from the app.config
     * application.attributes section
     *
     * @return <code>hashMap</code> key/value set of all application properties
     */
    public Map<String, String> getFrameworkProperties() {
        return this._frameworkProperties;
    }

    public String getFrameworkProperty(String key) {
        return getFrameworkProperties().get(key);
    }

    /**
     * getActionProperties() method returns the hashMap containing the action
     * properties key/value pair extracted from the app.config action attributes
     * section
     *
     * @return <code>hashMap</code> key/value set of all action properties
     */
    public Map<String, Object> getGroupProperties() {
        return this._groupProperties;
    }

    public Object getGroupProperty(String key) {
        return (Object) getGroupProperties().get(key);
    }
    // </editor-fold>

    // <editor-fold desc="class methods">
    /**
     * extractConfigFile(...) method verifies if the external config exists, if
     * not, it tries to extract the config file from jar file. If either are
     * unsuccessful, exception is thrown to notify user of missing config.
     *
     * @param filename location of the config file
     * @throws Exception
     */
    private void extractConfigFile(String filename) throws Exception {
        // create a reference to the location of the configuration file
        File cf = new File(filename);

        // if the file does not exist, try to extract it from the jar resource
        if (!cf.exists()) {
            // notify the user we are extracting the store app.config
            System.out.println("extracting config file: " + filename);

            // create directories
            cf.getParentFile().mkdirs();

            // open the input stream from the jar resource
            BufferedReader configIFile = null;
            configIFile = new BufferedReader(
                    new InputStreamReader(
                            getClass().getClassLoader().getResourceAsStream(
                                    filename.replace("\\", "/"))));

            // declare storage for the output file
            BufferedWriter configOFile = null;

            // if input file if valid, then extract the data
            if (configIFile != null) {
                try {
                    // open the output file
                    configOFile = new BufferedWriter(new FileWriter(cf));

                    // declare storage for the data from the input stream
                    String line;

                    // loop the config file, read each line until no more data
                    while ((line = configIFile.readLine()) != null) {
                        // write the data to the output file and insert the new
                        // line terminator after each line
                        configOFile.write(line + GlobalStack.LINESEPARATOR);

                        // yield processing to other threads
                        Thread.yield();
                    }

                    // notify user the status of the config file
                    System.out.println("config file extracted successfully");
                } catch (Exception ex) {
                    // if exception during processing, return it to the user
                    throw new Exception(getClass().toString() + "//"
                            + ex.getMessage());
                } finally {
                    // close the input file to prevent resource leaks
                    try {
                        configIFile.close();
                    } catch (Exception exi) {
                    }

                    // close the output file to prevent resource leaks
                    if (configOFile != null) {
                        try {
                            configOFile.flush();
                        } catch (Exception exi) {
                        }
                        try {
                            configOFile.close();
                        } catch (Exception exi) {
                        }
                    }
                }
            }
        } else {
            // config file already existed, notify user we are using it
            System.out.println("using config file: " + filename);
        }
    }

    /**
     * showConfig() method displays the configuration to the console output.
     *
     */
    private void showConfig() {
        showConfigNodes(_xmlr.getDocument(), 1);
        dataConfigNodes(_xmlr.getDocument(), 1);

        //System.out.println("------------");
        //org.w3c.dom.NodeList nl = _xmlr.getNodesByElement("connections");
        //for (int i = 0; i < nl.getLength(); i++) {
        //    showConfigNodes(nl.item(i), 1);
        //}
        //System.out.println("------------");
        //nl = _xmlr.getNodesByElement("service");
        //for (int i = 0; i < nl.getLength(); i++) {
        //    System.out.println("---" + nl.item(i).getNodeName());
        //    showConfigNodes(nl.item(i), 1);
        //}
    }

    /**
     * showConfigNodes(...) method is used to recursively scan the XML config
     * file and display the nodes in tree format.
     *
     * @param parent
     * @param level level of the node, increased for each child-node to allow
     * tabbed tree display output
     *
     */
    protected void showConfigNodes(org.w3c.dom.Node parent, int level) {
        // create a local class to display node value/text and associated
        // node attributes
        class SubShowNode {

            // loop through the node attributes for the node passed
            String displayNodeAttributes(org.w3c.dom.Node node) {
                // create string build object to support string concatanation
                StringBuilder sb = new StringBuilder();

                // retrieve node attributes (if any)
                ArrayList nAttributes = _xmlr.getNodeAttributes(node);

                // loop through the attributes array and append them to the
                // string builder object
                for (Object na : nAttributes) {
                    // append the attribute details (key/text) to the string
                    // builder object
                    sb.append(" [ATTR=").append(((org.w3c.dom.Node) na).getNodeName())
                            .append("//")
                            .append(((org.w3c.dom.Node) na).getNodeValue())
                            .append("]");

                    // yield processing to other threads
                    Thread.yield();
                }

                // return the string builder representation as a string
                return sb.toString();
            }
        }

        // declare the showNode class to allow methods to reference the display
        // method to prevent duplicaion in code
        SubShowNode showNode = new SubShowNode();

        // retrieve the child nodes for processing
        ArrayList nodes = _xmlr.getNodeChildren(parent);

        // if node level is 1, then this is root node, display it with no
        // indentation
        if (level == 1) {
            // display the parent node name
            String data = StringUtils.repeat('~', level) + parent.getNodeName();

            // use the sub function to extract node attributes
            data += showNode.displayNodeAttributes(parent);

            // display all collected data to the user output
            System.out.println(data);
        }

        // parse the list of child nodes for the node being processed
        for (Object node : nodes) {
            // display the parent node name
            String data = StringUtils.repeat('\t', level)
                    + ((org.w3c.dom.Node) node).getNodeName();

            // use the sub function to extract node attributes
            data += showNode.displayNodeAttributes((org.w3c.dom.Node) node);

            // if node has a text value, display the text
            if (_xmlr.getNodeText((org.w3c.dom.Node) node) != null) {
                data += " (TEXT=" + _xmlr.getNodeText((org.w3c.dom.Node) node)
                        + ")";
            }

            // display all collected data to the user output
            System.out.println(data);

            // recall the function (recursion) to see if the node has child 
            // nodes and preocess them in hierarchial level
            showConfigNodes((org.w3c.dom.Node) node, (level + 1));

            // yield processing to other threads
            Thread.yield();
        }
    }

    protected void dataConfigNodes(org.w3c.dom.Node parent, int level) {
        // create a local class to display node value/text and associated
        // node attributes
        class SubShowNode {

            // loop through the node attributes for the node passed
            String displayNodeAttributes(org.w3c.dom.Node node) {
                // create string build object to support string concatanation
                StringBuilder sb = new StringBuilder();

                // retrieve node attributes (if any)
                ArrayList nAttributes = _xmlr.getNodeAttributes(node);

                // loop through the attributes array and append them to the
                // string builder object
                for (Object na : nAttributes) {
                    // append the attribute details (key/text) to the string
                    // builder object
                    sb.append(" [ATTR=").append(((org.w3c.dom.Node) na).getNodeName())
                            .append("//")
                            .append(((org.w3c.dom.Node) na).getNodeValue())
                            .append("]");

                    // yield processing to other threads
                    Thread.yield();
                }

                // return the string builder representation as a string
                return sb.toString();
            }
        }

        // declare the showNode class to allow methods to reference the display
        // method to prevent duplicaion in code
        SubShowNode showNode = new SubShowNode();

        // retrieve the child nodes for processing
        ArrayList nodes = _xmlr.getNodeChildren(parent);

        // if node level is 1, then this is root node, display it with no
        // indentation
        if (level == 1) {
            // display the parent node name
            String data = StringUtils.repeat('~', level) + parent.getNodeName();
            _key[level - 1] = parent.getNodeName();

            // use the sub function to extract node attributes
            data += showNode.displayNodeAttributes(parent);

            // display all collected data to the user output
            System.out.println(CollectionStack.ArrayToString(Arrays.copyOfRange(_key, 0, level)));
            System.out.println(data);
        }

        // parse the list of child nodes for the node being processed
        for (Object node : nodes) {
            // display the parent node name
            String nodeText = "";
            String data = StringUtils.repeat('\t', level)
                    + ((org.w3c.dom.Node) node).getNodeName();
            _key[level - 1] = ((org.w3c.dom.Node) node).getNodeName();

            // use the sub function to extract node attributes
            data += showNode.displayNodeAttributes((org.w3c.dom.Node) node);

            // if node has a text value, display the text
            if (_xmlr.getNodeText((org.w3c.dom.Node) node) != null) {
                nodeText = " (TEXT=" + _xmlr.getNodeText((org.w3c.dom.Node) node)
                        + ")";
            }

            // display all collected data to the user output
            if (!nodeText.isEmpty()) {
                System.out.println(CollectionStack.ArrayToString(Arrays.copyOfRange(_key, 0, level)));
                System.out.println(data);
            }

            // recall the function (recursion) to see if the node has child 
            // nodes and preocess them in hierarchial level
            dataConfigNodes((org.w3c.dom.Node) node, (level + 1));

            // yield processing to other threads
            Thread.yield();
        }
    }
    // </editor-fold>

    // <editor-fold desc="class logging">
    /**
     * logDebug(...) method is an interface method to Log4JManager logging
     * capability. This method is provided to allow multiple threads to log to
     * one file.
     * <p>
     * Debug messages are will only be processed if log4j.properties are set to
     * log debug or info or warn or error or fatal messages
     *
     * @param info is the object whose string representation will be stored in
     * the log file
     */
    public synchronized void logDebug(Object info) {
        Log4JManager.debug(info.toString());
    }

    /**
     * logError(...) method is an interface method to Log4JManager logging
     * capability. This method is provided to allow multiple threads to log to
     * one file.
     * <p>
     * Error messages are will only be processed if log4j.properties are set to
     * log error or fatal messages
     *
     * @param info is the object whose string representation will be stored in
     * the log file
     */
    public synchronized void logError(Object info) {
        Log4JManager.error(info.toString());
    }

    /**
     * logInfo(...) method is an interface method to Log4JManager logging
     * capability. This method is provided to allow multiple threads to log to
     * one file.
     * <p>
     * Debug messages are will only be processed if log4j.properties are set to
     * log info or warn or error or fatal messages
     *
     * @param info is the object whose string representation will be stored in
     * the log file
     */
    public synchronized void logInfo(Object info) {
        Log4JManager.info(info.toString());
    }
    // </editor-fold>
}
