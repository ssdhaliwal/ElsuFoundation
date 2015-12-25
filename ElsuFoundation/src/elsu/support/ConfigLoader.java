/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.support;

import elsu.common.*;
import java.util.*;
import java.io.*;
import java.text.*;
import org.apache.commons.lang3.*;
import org.apache.log4j.*;

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
    // runtime sync object
    private Object _runtimeSync = new Object();

    // static property for app.config store and extraction from jar file
    private static String _WEBREALPATH = "";
    private static String _APPCONFIG = "config/app.config";
    private static String _LOGCONFIG = "log4j.properties";
    private static String _LOGCLASS = "logDefault";

    // static property for data format across the application for display 
    // purposes
    private static String _DTGFORMAT = "YYYMMDD HH24:mm:ss";

    // variable to store the xml document object from XMLReader class
    protected XMLReader _xmlr = null;

    // store all properties from app.config
    private Map<String, Object> _properties = new HashMap<>();

    // array of path strings which need to be removed from hashmap
    private String[] _suppressPath = new String[]{};

    // system logger if configured
    public static String _LOGFILENAMEPROPERTY = "log.filename";
    public static String _LOGCONFIGPROPERTY = "log.config";
    public static String _LOGCLASSPROPERTY = "log.class";
    private Log4JManager _log4JManager = null;
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
        this(ConfigLoader._APPCONFIG, null);
    }

    /**
     * ConfigLoader(...) constructor is used to load custom configuration passed
     * through the string variable. Normally used by control service to pass
     * custom XML sent from the client.
     *
     * updated to parse the config variable to determine if it is not a properly
     * formatted xml (starts with "<?xml ")
     *
     * @param config is the XML data or file passed from the calling function.
     * @param suppressPath is array of strings which should be removed from key
     * @throws Exception
     */
    public ConfigLoader(String config, String[] suppressPath) throws Exception {
        try {
            // update suppress path before processing
            if (suppressPath != null) {
                this._suppressPath = suppressPath;
            }

            // if config is null, then use the default
            if ((config == null) || (config.isEmpty())) {
                config = _WEBREALPATH + ConfigLoader._APPCONFIG;
            }

            // check the format, if raw xml?
            if (config.startsWith("<?xml ")) {
                // try to create the XML reader instance for XML document parsing
                // using the app.config file location
                _xmlr = new XMLReader(config);
            } else {
                // load the resource or file path
                ConfigLoader._APPCONFIG = _WEBREALPATH + config;
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

            // open log if provided
            for (String key : getProperties().keySet()) {
                if (key.equals(_LOGCONFIGPROPERTY)) {
                    try {
                        initializeLogger(getProperty(key).toString());
                    } catch (Exception ex) {
                        System.out.println("log4J configuration error, " + ex.getMessage());
                    }

                    break;
                }
            }

            logInfo("configuration loaded.");
        } catch (Exception ex) {
            // display exception to the user and exit
            System.out.println(getClass().toString() + "//" + ex.getMessage());
        }
    }

    /**
     * ConfigLoader(...) constructor is used to load custom configuration passed
     * through the string variable. Normally used by control service to pass
     * custom XML sent from the client.
     *
     * @param config is the Map<String, Object> data passed from the calling
     * function.
     * @throws Exception
     */
    public ConfigLoader(Map<String, Object> config) throws Exception {
        try {
            // if config is null, then throw exception
            if ((config == null) || (config.size() == 0)) {
                throw new Exception("config passed is empty.");
            }

            // display the config to the user
            showConfig(config);

            // load the config into application or service properties hashMaps
            initializeConfig(config);

            // open log if provided
            for (String key : getProperties().keySet()) {
                if (key.equals(_LOGCONFIGPROPERTY)) {
                    try {
                        initializeLogger(getProperty(key).toString());
                    } catch (Exception ex) {
                        System.out.println("log4J configuration error, " + ex.getMessage());
                    }

                    break;
                }
            }

            logInfo("configuration loaded.");
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
        getProperties().clear();
        loadConfig(_xmlr.getDocument(), "", 1);
    }

    private void initializeConfig(Map<String, Object> config) throws Exception {
        // clear the storage hashMaps
        getProperties().clear();
        _properties = config;
    }
    // </editor-fold>

    // <editor-fold desc="class getter/setters">
    public static String getWEBRealPath() {
        return _WEBREALPATH;
    }

    public static void setWEBRealPath(String path) {
        _WEBREALPATH = path;
    }

    public static String getConfigPath() {
        return _APPCONFIG;
    }

    public static void setConfigPath(String path) {
        _APPCONFIG = path;
    }

    public static String getDTGFormat() {
        return _DTGFORMAT;
    }

    public static void setDTGFormat(String format) {
        _DTGFORMAT = format;
    }

    /**
     * getApplicationProperties() method returns the hashMap containing the
     * application properties key/value pair extracted from the app.config
     * application.attributes section
     *
     * @return <code>hashMap</code> key/value set of all application properties
     */
    public Map<String, Object> getProperties() {
        return this._properties;
    }

    public Object getProperty(String key) {
        return getProperties().get(key);
    }

    public List<String> getClassSet() {
        List<String> result = new ArrayList<>();

        for (String key : getProperties().keySet()) {
            if (key.endsWith(".class")) {
                result.add(key);
            }
        }

        return result;
    }

    public List<String> getClassSet(String partialKey) {
        List<String> result = new ArrayList<>();

        for (String key : getProperties().keySet()) {
            if ((key.startsWith(partialKey)) && key.endsWith(".class")) {
                result.add(key);
            }
        }

        return result;
    }

    public String getKeyByValue(String value) {
        String result = "";

        for (String key : getProperties().keySet()) {
            if (getProperty(key).equals(value)) {
                result = key;
                break;
            }
        }
        return result;
    }

    public static String getTempLogName() {
        return String.format("TMPLOG_%s.LOG",
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
    }

    public static String getLogName(String name) {
        return String.format(name + "_%s.LOG",
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
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
    public static void extractConfigFile(String filename) throws Exception {
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
                            ConfigLoader.class.getClassLoader().getResourceAsStream(
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
                    throw new Exception("ConfigLoader:extractConfigFile//"
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

    private void showConfig(Map<String, Object> config) {
        // assign new config to the variable
        for (String key : config.keySet()) {
            System.out.println(key + " (TEXT=" + config.get(key) + ")");
        }
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
                if (nAttributes != null) {
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

    protected void loadConfig(org.w3c.dom.Node parent, String nodePath, int level) {
        // retrieve the child nodes for processing
        ArrayList<org.w3c.dom.Node> nodes = _xmlr.getNodeChildren(parent);

        // parse the list of child nodes for the node being processed
        ArrayList<org.w3c.dom.Node> nAttributes = null;
        String nodePathHold = nodePath;
        String nodeAttrKey = "";
        for (org.w3c.dom.Node node : nodes) {
            nodePath += (nodePath.isEmpty() ? node.getNodeName() : "." + node.getNodeName());
            nAttributes = _xmlr.getNodeAttributes(node);

            // loop through the attributes array and append them to the
            // string builder object
            nodeAttrKey = "";
            if (nAttributes != null) {
                // get id, name, class attribute if any
                nodeAttrKey = getAttributeKey(nAttributes);

                // get the key value for path
                for (org.w3c.dom.Node na : nAttributes) {
                    if (na.getNodeName().equals(nodeAttrKey)) {
                        nodePath += "." + na.getNodeValue();
                        break;
                    }
                }

                // first get the key or if none; set it to first value
                for (org.w3c.dom.Node na : nAttributes) {
                    // append the attribute details (key/text) to the string
                    // builder object
                    if (!na.getNodeName().equals(nodeAttrKey)) {
                        addMap(nodePath + "." + na.getNodeName(), na.getNodeValue());
                        //System.out.println(nodePath + "." + na.getNodeName()
                        //        + "=" + na.getNodeValue());
                    }

                    // yield processing to other threads
                    Thread.yield();
                }
            }

            // if node has a text value, display the text
            if (_xmlr.getNodeText(node) != null) {
                addMap(nodePath, _xmlr.getNodeText(node));
                //System.out.println(nodePath
                //        + "=" + _xmlr.getNodeText(node));

            }

            // recall the function (recursion) to see if the node has child 
            // nodes and preocess them in hierarchial level
            loadConfig(node, nodePath, (level + 1));
            nodePath = nodePathHold;

            // yield processing to other threads
            Thread.yield();
        }
    }

    private String getAttributeKey(ArrayList<org.w3c.dom.Node> nodes) {
        String result = "";

        // attributes are in name order, so for class we keep looping
        for (org.w3c.dom.Node na : nodes) {
            if (na.getNodeName().equals("id")) {
                result = "id";
                break;
            } else if (na.getNodeName().equals("name")) {
                result = "name";
                break;
            } else if (na.getNodeName().equals("class")) {
                result = "class";
            }
        }

        return result;
    }

    private void addMap(String key, String value) {
        // check if the key ends with config.suppressPath
        // if yes, then load it into the global suppressPath variable
        if (key.endsWith(".config.suppressPath")) {
            _suppressPath = value.split(",");

            // now do a quick cleanup of the already loaded values
            Object cValue;
            for (String cKey : getProperties().keySet()) {
                cValue = getProperties().get(cKey);
                getProperties().remove(cKey);

                for (String suppress : _suppressPath) {
                    cKey = cKey.replaceFirst(suppress, "");
                }

                getProperties().put(cKey, cValue);
            }
        }

        // check and remove the values in _suppressPath variable
        for (String suppress : _suppressPath) {
            key = key.replaceFirst(suppress, "");
        }

        getProperties().put(key, value);
        System.out.println(key + "=" + value);
    }
    // </editor-fold>

    // <editor-fold desc="class logging">
    /**
     *
     */
    private void initializeLogger(String log) throws Exception {
        // log attribute value is defined, set the static variable to the 
        // log property file location; also, check if path is provided as
        // part of the file name - if yes, then ignore class path
        String logFileName = getProperty(ConfigLoader._LOGFILENAMEPROPERTY).toString();

        if (!log.contains("\\") && !log.contains("/")) {
            ConfigLoader._LOGCONFIG
                    = (new File(getClass().getName().replace(".", "\\"))).getParent()
                    + "\\" + log;
        } else {
            ConfigLoader._LOGCONFIG = log;
        }

        // check if the log property file exists, if not extract it 
        extractConfigFile(ConfigLoader._LOGCONFIG);

        // if log.filename is empty, then assign a temporary one
        if ((logFileName == null) || (logFileName.isEmpty())) {
            logFileName = "$TMP_LOG$.LOG";
        }

        ConfigLoader._LOGCLASS = getProperty(ConfigLoader._LOGCLASSPROPERTY).toString();
        _log4JManager = new Log4JManager(ConfigLoader._LOGCONFIG, ConfigLoader._LOGCLASS, logFileName);
    }

    public static Log4JManager initializeLogger(String logClass, String fileName) throws Exception {
        Log4JManager log4JManager = null;
        
        if (logClass.isEmpty() || (logClass.length() == 0)) {
            log4JManager = new Log4JManager(ConfigLoader._LOGCONFIG, ConfigLoader._LOGCLASS, getLogName(fileName));
        } else {
            log4JManager = new Log4JManager(ConfigLoader._LOGCONFIG, logClass, getLogName(fileName));
        }
        
        return log4JManager;
    }

    public static Log4JManager initializeLogger(String logPropertyFile, String logClass, String fileName) throws Exception {
        // log attribute value is defined, set the static variable to the 
        // log property file location; also, check if path is provided as
        Log4JManager log4JManager = null;

        // check if the log property file exists, if not extract it 
        extractConfigFile(logPropertyFile);

        if (logClass.isEmpty() || (logClass.length() == 0)) {
            log4JManager = new Log4JManager(logPropertyFile, ConfigLoader._LOGCLASS, getLogName(fileName));
        } else {
            log4JManager = new Log4JManager(logPropertyFile, logClass, fileName);
        }
        
        return log4JManager;
    }

    /**
     *
     */
    public Logger getLogger() {
        Logger result = null;

        synchronized (this._runtimeSync) {
            result = this._log4JManager.getLogger();
        }

        return result;
    }

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
    public void logDebug(Object info) {
        synchronized (this._runtimeSync) {
            getLogger().debug(info.toString());
        }
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
    public void logError(Object info) {
        synchronized (this._runtimeSync) {
            getLogger().error(info.toString());
        }
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
    public void logInfo(Object info) {
        synchronized (this._runtimeSync) {
            getLogger().info(info.toString());
        }
    }
    // </editor-fold>
}
