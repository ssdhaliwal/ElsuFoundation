package elsu.support;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;

/**
 *
 * @author ss.dhaliwal_admin
 */
public class Log4JManager {

    private String _logConfig = "./log4j.properties";
    private Logger _logger = null;
    private int _maxMemoryLogSize = 10;
    private List<String> _memoryLog = new ArrayList<>();

    public Log4JManager(String logConfig, String logClass, String fileName) throws Exception {
        if ((getLogConfig() == null) || (getLogConfig().isEmpty())) {
            throw new Exception(this.getClass() + ", constructor(), logConfig param is null.");
        }
        
        // if the log4j-init-file context parameter is not set, then no point in trying
        setLogConfig(logConfig);
        
        if (getLogConfig() != null) {
            File logFile = new File(getLogConfig());

            if (logFile.exists()) {
                // update the log file property
                System.setProperty("log4j.log.filename", fileName);

                PropertyConfigurator.configure(getLogConfig());
            } else {
                BasicConfigurator.configure();
            }

            setLogger(Logger.getLogger(logClass));
        } else {
            BasicConfigurator.configure();
        }
    }

    public String getLogConfig()
    {
        return this._logConfig;
    }
    public void setLogConfig(String logConfig)
    {
        this._logConfig = logConfig;
    }
    
    public int getMaxMemoryLogSize() {
        return this._maxMemoryLogSize;
    }
    public void setMaxMemoryLogSize(int size) {
        this._maxMemoryLogSize = size;
    }
    
    public List<String> getLog() {
        return this._memoryLog;
    }

    public Logger getLogger() {
        return this._logger;
    }
    private void setLogger(Logger logger) {
        this._logger = logger;
    }
    
    protected void checkMemoryLog(Object message) {
        while (getLog().size() >= getMaxMemoryLogSize()) {
            getLog().remove(0);
        }
        
        getLog().add(message.toString());
    }
    
    public synchronized void clearMemoryLog() {
        getLog().clear();
    }
    
    public synchronized List<String> getMemoryLog() {
        List<String> result = new ArrayList<>();
        
        // copy the contents of the error log to the user
        for(String s : getLog()) {
            result.add(s);
        }
        
        return result;
    }
    
    public synchronized void debug(Object message) {
        getLogger().debug(message);
        checkMemoryLog("debug, " + message);
    }

    public synchronized void debug(Object message, Throwable t) {
        getLogger().debug(message, t);
        checkMemoryLog("debug, " + message);
    }

    public synchronized void error(Object message) {
        getLogger().error(message);
        checkMemoryLog("error, " + message);
    }

    public synchronized void error(Object message, Throwable t) {
        getLogger().error(message, t);
        checkMemoryLog("error, " + message);
    }

    public synchronized void fatal(Object message) {
        getLogger().fatal(message);
        checkMemoryLog("fatal, " + message);
    }

    public synchronized void fatal(Object message, Throwable t) {
        getLogger().fatal(message, t);
        checkMemoryLog("fatal, " + message);
    }

    public synchronized void info(Object message) {
        getLogger().info(message);
        checkMemoryLog("info, " + message);
    }

    public synchronized void info(Object message, Throwable t) {
        getLogger().info(message, t);
        checkMemoryLog("info, " + message);
    }
}
