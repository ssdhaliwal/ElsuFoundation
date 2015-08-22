package elsu.support;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;

/**
 *
 * @author ss.dhaliwal_admin
 */
public class Log4JManager {

    private Object _runtimeSync = new Object();
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

    public String getLogConfig() {
        String result = "";

        synchronized (this._runtimeSync) {
            result = this._logConfig;
        }

        return result;
    }

    public void setLogConfig(String logConfig) {
        synchronized (this._runtimeSync) {
            this._logConfig = logConfig;
        }
    }

    public int getMaxMemoryLogSize() {
        int result = 0;

        synchronized (this._runtimeSync) {
            result = this._maxMemoryLogSize;
        }

        return result;
    }

    public void setMaxMemoryLogSize(int size) {
        synchronized (this._runtimeSync) {
            this._maxMemoryLogSize = size;
        }
    }

    public List<String> getLog() {
        List<String> result = null;

        synchronized (this._runtimeSync) {
            result = this._memoryLog;
        }

        return result;
    }

    public Logger getLogger() {
        Logger result = null;

        synchronized (this._runtimeSync) {
            result = this._logger;
        }

        return result;
    }

    private void setLogger(Logger logger) {
        this._logger = logger;
    }

    protected void checkMemoryLog(Object message) {
        synchronized (this._runtimeSync) {
            while (getLog().size() >= getMaxMemoryLogSize()) {
                getLog().remove(0);
            }

            getLog().add(message.toString());
        }
    }

    public void clearMemoryLog() {
        synchronized (this._runtimeSync) {
            getLog().clear();
        }
    }

    public List<String> getMemoryLog() {
        List<String> result = new ArrayList<>();

        // copy the contents of the error log to the user
        synchronized (this._runtimeSync) {
            result.addAll(getLog());
        }

        return result;
    }

    public void debug(Object message) {
        synchronized (this._runtimeSync) {
            getLogger().debug(message);
        }

        checkMemoryLog("debug, " + message);
    }

    public void debug(Object message, Throwable t) {
        synchronized (this._runtimeSync) {
            getLogger().debug(message, t);
        }

        checkMemoryLog("debug, " + message);
    }

    public void error(Object message) {
        synchronized (this._runtimeSync) {
            getLogger().error(message);
        }

        checkMemoryLog("error, " + message);
    }

    public void error(Object message, Throwable t) {
        synchronized (this._runtimeSync) {

            getLogger().error(message, t);
        }

        checkMemoryLog("error, " + message);
    }

    public void fatal(Object message) {
        synchronized (this._runtimeSync) {
            getLogger().fatal(message);
        }

        checkMemoryLog("fatal, " + message);
    }

    public void fatal(Object message, Throwable t) {
        synchronized (this._runtimeSync) {
            getLogger().fatal(message, t);
        }

        checkMemoryLog("fatal, " + message);
    }

    public void info(Object message) {
        synchronized (this._runtimeSync) {
            getLogger().info(message);
        }

        checkMemoryLog("info, " + message);
    }

    public void info(Object message, Throwable t) {
        synchronized (this._runtimeSync) {
            getLogger().info(message, t);
        }

        checkMemoryLog("info, " + message);
    }
}
