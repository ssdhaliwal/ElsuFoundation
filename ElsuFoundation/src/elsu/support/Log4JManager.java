package elsu.support;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;

/**
 *
 * @author ss.dhaliwal_admin
 */
public class Log4JManager {

    public static String LOG4JCONFIG = "./log4j.properties";
    public static Logger LOG = null;
    protected static List<String> MEMERRORLOG = new ArrayList<>();
    protected static int MAXMEMLOGENTRIES = 10;

    public Log4JManager(String logClass) {
        // if the log4j-init-file context parameter is not set, then no point in trying
        if (LOG4JCONFIG != null) {
            File logFile = new File(LOG4JCONFIG);

            if (logFile.exists()) {
                PropertyConfigurator.configure(LOG4JCONFIG);
            } else {
                BasicConfigurator.configure();
            }

            Log4JManager.LOG = Logger.getLogger(logClass);
        } else {
            BasicConfigurator.configure();
        }
    }

    protected static void checkMemoryLog(Object message) {
        while (MEMERRORLOG.size() >= MAXMEMLOGENTRIES) {
            MEMERRORLOG.remove(0);
        }
        
        MEMERRORLOG.add(message.toString());
    }
    
    public static synchronized void clearMemoryLog() {
        MEMERRORLOG.clear();
    }
    
    public static synchronized List<String> getMemoryLog() {
        List<String> result = new ArrayList<>();
        
        // copy the contents of the error log to the user
        for(String s : MEMERRORLOG) {
            result.add(s);
        }
        
        return result;
    }
    
    public static synchronized void debug(Object message) {
        LOG.debug(message);
        checkMemoryLog("debug, " + message);
    }

    public static synchronized void debug(Object message, Throwable t) {
        LOG.debug(message, t);
        checkMemoryLog("debug, " + message);
    }

    public static synchronized void error(Object message) {
        LOG.error(message);
        checkMemoryLog("error, " + message);
    }

    public static synchronized void error(Object message, Throwable t) {
        LOG.error(message, t);
        checkMemoryLog("error, " + message);
    }

    public static synchronized void fatal(Object message) {
        LOG.fatal(message);
        checkMemoryLog("fatal, " + message);
    }

    public static synchronized void fatal(Object message, Throwable t) {
        LOG.fatal(message, t);
        checkMemoryLog("fatal, " + message);
    }

    public static synchronized void info(Object message) {
        LOG.info(message);
        checkMemoryLog("info, " + message);
    }

    public static synchronized void info(Object message, Throwable t) {
        LOG.info(message, t);
        checkMemoryLog("info, " + message);
    }
}
