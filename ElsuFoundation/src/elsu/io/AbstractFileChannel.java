package elsu.io;

import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * FileChanneAbstract class is abstract (cannot be instantiated) but is used as
 * super class for FileChannelReader and FileChannelWriter to hold shared
 * properties.
 * <p>
 * Shared properties are fileMask, fileLocation, fileDateFormatMask, and other
 * rollover properties like periodicity, format, and threshold.
 * <p>
 * FileProcessingType defines two reader types: LIVE and ARCHIVE. This is used
 * by FileChannelReader to either process active/current file which is being
 * used by the writers or read data from historical files. In RECOVERY mode the
 * input file once completely read will be removed from the system.
 *
 * @author ssd.administrator
 */
public abstract class AbstractFileChannel {

    // local storage, rollover periodicity, default hour
    private volatile FileRolloverPeriodicityType _rolloverPeriodicity
            = FileRolloverPeriodicityType.HOUR;

    // local storage, rollover format, hour
    private volatile String _rolloverFormat = "HH";

    // local storage, rollover cleanup threshold, default 5
    private volatile int _rolloverThreshold = 5;

    // local storage, file date form mask for searching files
    private volatile String _fileDateFormatMask = "yyyyMMddHH";

    // local storage, file mask where date format is substituded
    private volatile String _fileMask = "";

    // local storage, file location (directory) without trailing slash
    private volatile String _fileLocation = "";

    // local storage, file processing type for reader, LIVE or RECOVERY mode
    private volatile FileProcessingType _processingType
            = FileProcessingType.STANDARD;

    // lock object to prevent multi-access to the shareWrite method internals
    private final static Lock _writerLock = new ReentrantLock();

    // local object to store the lock timeout
    private volatile static int _lockTimeout = 5000;

    /**
     * FileChannelBase(...) constructor allows creation of class object with
     * default values for rollover properties.
     *
     * @param fileMask
     * @param fileLocation
     */
    public AbstractFileChannel(String fileMask, String fileLocation) {
        this._fileMask = fileMask;
        this._fileLocation = fileLocation;
    }

    /**
     * FileChannelBase(...) constructor allows creation of class object with
     * custom values for rollover properties and default properties for
     * processing type.
     *
     * @param fileMask
     * @param fileLocation
     * @param rolloverPeriodicity
     * @param rolloverThreshold
     */
    public AbstractFileChannel(String fileMask, String fileLocation,
            FileRolloverPeriodicityType rolloverPeriodicity,
            int rolloverThreshold) {
        this._fileMask = fileMask;
        this._fileLocation = fileLocation;
        this._rolloverPeriodicity = rolloverPeriodicity;
        this._rolloverThreshold = rolloverThreshold;

        // set the dateFormat to match periodicity
        setRolloverFormat();
    }

    /**
     * FileChannelBase(...) constructor allows creation of class object with
     * default values for rollover properties and specified processing type:
     * LIVE or RECOVERY.
     *
     * @param fileMask
     * @param fileLocation
     * @param processingType
     */
    public AbstractFileChannel(String fileMask, String fileLocation,
            FileProcessingType processingType) {
        this._fileMask = fileMask;
        this._fileLocation = fileLocation;
        this._processingType = processingType;
    }

    /**
     * FileChannelBase(...) constructor allows creation of class object with
     * default values for rollover properties and specified processing type:
     * LIVE or RECOVERY and custom properties for rollover.
     *
     * @param fileMask
     * @param fileLocation
     * @param processingType
     * @param rolloverPeriodicity
     * @param rolloverThreshold
     */
    public AbstractFileChannel(String fileMask, String fileLocation,
            FileProcessingType processingType,
            FileRolloverPeriodicityType rolloverPeriodicity,
            int rolloverThreshold) {
        this._fileMask = fileMask;
        this._fileLocation = fileLocation;
        this._processingType = processingType;
        this._rolloverPeriodicity = rolloverPeriodicity;
        this._rolloverThreshold = rolloverThreshold;

        // set the dateFormat to match periodicity
        setRolloverFormat();
    }

    /**
     * getFiledate() returns the current file date formatted using the date
     * format mask provided using the current time.
     *
     * @return <code>String</code> value of the current time in file date format
     */
    protected String getFileDate() {
        return new SimpleDateFormat(getFileDateFormatMask()).format(
                Calendar.getInstance().getTime());
    }

    /**
     * getFileDateFormatMask() returns the date format mask which will be used
     * to format the filename using current time.
     *
     * @return <code>String</code> value of the file date mask
     */
    protected String getFileDateFormatMask() {
        return this._fileDateFormatMask;
    }

    /**
     * getFileMask() method returns the file mask which is used to substitute
     * with the date or .* for searching.
     *
     * @return <code>String</code> value of the file mask
     */
    protected String getFileMask() {
        return this._fileMask;
    }

    /**
     * getFileLocation() method returns the file location (directory) where the
     * files will be stored or read from; the location should not have trailing
     * slash.
     *
     * @return <code>String</code> value of the file location
     */
    protected String getFileLocation() {
        return this._fileLocation;
    }

    /**
     * getLockTimeout() method returns the current lock timeout value.
     *
     * @return <code>int</code> value of the lock timeout
     */
    public static synchronized int getLockTimeout() {
        return AbstractFileChannel._lockTimeout;
    }

    /**
     * setLockTimeout(...) method sets the lock timeout value.
     *
     * @param value
     */
    protected void setLockTimeout(int value) {
        AbstractFileChannel._lockTimeout = value;
    }

    /**
     * getMaxRecoveryFiledate() returns the file date formatted using the date
     * format mask provided using the current time subtracting the rollover
     * threshold.
     *
     * @return <code>String</code> value of the current time in file date format
     */
    protected String getMaxRecoveryFiledate() {
        Calendar time = Calendar.getInstance();

        if (getRollverPeriodicity() == FileRolloverPeriodicityType.DAY) {
            time.add(Calendar.DATE, getRolloverThreshold() * -1);
        } else if (getRollverPeriodicity() == FileRolloverPeriodicityType.HOUR) {
            time.add(Calendar.HOUR, getRolloverThreshold() * -1);
        }

        return new SimpleDateFormat(getFileDateFormatMask()).format(
                time.getTime());
    }

    /**
     * getProcessingType() method returns the reader processing type: LIVE or
     * RECOCOVERY. The value is used in FileChannelReader to perform custom
     * operations when processing current file or archive file.
     *
     * @return <code>FileProcessingType</code> value of the storage.
     */
    protected FileProcessingType getProcessingType() {
        return this._processingType;
    }

    /**
     * setProcessingType(...) method allows subtypes of the class to set the
     * processing type during initialization allowing writer to allow both
     * STARDARD or other types.
     *
     * @param processingType
     */
    protected void setProcessingType(FileProcessingType processingType) {
        this._processingType = processingType;
    }

    /**
     * getRolloverFormat() method returns the rollover format which is used to
     * compare current time to new time. HH for hour and dd for day - this is
     * set based on the periodicity selection.
     *
     * @return <code>String</code> value of the rollover format
     */
    protected String getRolloverFormat() {
        return this._rolloverFormat;
    }

    /**
     * setRolloverFormat() method sets the rollover format based on the
     * specified periodicity. If periodicity = DAY, then rollover format = dd,
     * else HH.
     */
    private void setRolloverFormat() {
        // if periodicity is DAY, then set the appropriate rollover format
        // and file date format masks
        if (getRollverPeriodicity() == FileRolloverPeriodicityType.DAY) {
            this._rolloverFormat = "dd";
            this._fileDateFormatMask = "yyyyMMdd";
        } else if (getRollverPeriodicity() == FileRolloverPeriodicityType.HOUR) {
            this._rolloverFormat = "HH";
            this._fileDateFormatMask = "yyyyMMddHH";
        }
    }

    /**
     * getRolloverPeriodicity() method returns the rollover periodicity
     * specified when the class was created.
     *
     * @return <code>FileRolloverPeriodicityType</code> value
     */
    protected FileRolloverPeriodicityType getRollverPeriodicity() {
        return this._rolloverPeriodicity;
    }

    /**
     * getRolloverThreshold() method returns the # of occurances of the file
     * which will be maintained and all others will be removed when processing
     * scans for pending files. This is only applicable to the FileChannelReader
     *
     * @return <code>int</code> value # of files to maintain.
     */
    protected int getRolloverThreshold() {
        return this._rolloverThreshold;
    }

    /**
     * getWriterLock() method returns the global lock for threads to synchronize
     * their access for shared resources.
     *
     * @return <code>Lock</code> object for ReentrantLock
     */
    public static Lock getWriterLock() {
        return AbstractFileChannel._writerLock;
    }

    /**
     * sharedWrite(...) method is used by the subscriber child services to store
     * the data into one output file. Alarm and Messages have their own separate
     * stores, but all subscribers share one file for the datehour period.
     * <p>
     * Service _writerLock is used to manage multi-thread access.
     *
     * @param channel
     * @param buffer
     * @param overwrite
     * @return <code>int</code> number of bytes written
     * @throws Exception
     */
    protected static int sharedWrite(SeekableByteChannel channel,
            byte[] buffer, boolean overwrite) throws Exception {
        // return variable
        int result = 0;

        // tracking for the writer lock to ensure if locked it will be unlocked
        boolean writerLocked = false;

        // capture any exception in processing, to ensure we unlock the writer
        // lock
        try {
            // wrap the input string into a byte buffer
            ByteBuffer buff = ByteBuffer.wrap(buffer);

            // lock the writerLock object, if already locked, wait
            if (getWriterLock().tryLock() || getWriterLock().tryLock(
                    getLockTimeout(), TimeUnit.SECONDS)) {
                // set the writer as locked
                writerLocked = true;

                // if not overwrite, then update the channel position to the 
                // end, this is required due to multiple threads writing and 
                // each channel maintains its own position
                if (overwrite) {
                    channel.position(0);
                } else {
                    channel.position(channel.size());
                }

                // write the buffer to the channel
                int count = channel.write(buff);

                // if the output count does not match, report error
                if (count != buffer.length) {
                    throw new Exception(
                            "FileChannelWriter, sharedWrite(), write error, bytes requested "
                            + buffer.length + ", bytes written " + count);
                }

                // assign return value
                result = count;
            } else {
                throw new Exception(
                        "FileChannelWriter, sharedWrite(), lock error, re-entrant lock failed");
            }
        } catch (Exception ex){
            // throw the exception so the calling service can notify the client
            throw new Exception(ex);
        } finally {
            // unlock the writerLock so other threads can do their work
            if (writerLocked) {
                getWriterLock().unlock();
            }
        }

        // return the count of bytes written
        return result;
    }
}
