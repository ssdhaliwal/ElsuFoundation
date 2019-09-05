package elsu.io;

import java.text.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * AbstractFileChannelWriter() class extends the FileChanneAbstract to provide
 * concrete implementation for writing to file or files. Multiple threads can
 * write to same file as the threads are synchronized through static shareWrite
 * method of base class.
 * <p>
 * Note: Write synchronization is only for current application, if multiple,
 * applications write to same file (which they can), then data corruption will
 * occur.
 *
 * @author ssd.administrator
 */
public abstract class AbstractFileChannelWriter extends AbstractFileChannel {

    // runtime sync object
    private Object _runtimeSync = new Object();

    // local storage, stores the writer channel
    private volatile SeekableByteChannel _writerChannel = null;

    // local storage, store the current rollover period
    private volatile int _rolloverPeriod = 0;

    // local storage, append the data if file exists
    private volatile boolean _appendIfExists = true;

    public AbstractFileChannelWriter(String fileMask, String fileLocation)
            throws Exception {
        super(fileMask, fileLocation);

        initialize();
    }

    public AbstractFileChannelWriter(String fileMask, String fileLocation,
            boolean append) throws Exception {
        super(fileMask, fileLocation);

        // set the append indicator
        this._appendIfExists = append;

        initialize();
    }

    public AbstractFileChannelWriter(String fileMask, String fileLocation,
            FileRolloverPeriodicityType rolloverPeriodicity) throws Exception {
        super(fileMask, fileLocation, rolloverPeriodicity, 0);

        // set the write to be anything other than STANDARD
        setProcessingType(FileProcessingType.LIVE);

        // set append to true since this is rollover threshold
        this._appendIfExists = true;

        initialize();
    }

    private void initialize() throws Exception {
        // if the processing type is LIVE or ARCHIVE then make sure that there
        // is one substitution string in fileMask
        if (getProcessingType() != FileProcessingType.STANDARD) {
            if (!getFileMask().contains("%s")) {
                throw new Exception(
                        "fileMask should allow dateTime substition, ex: msg_file_%s.txt");
            }
        }
    }

    @Override
    public void finalize() {
        close();

        try {
            super.finalize();
        } catch (Throwable texi) {
        }
    }

    public boolean isAppendIfExists() {
        return this._appendIfExists;
    }

    protected boolean isAppendIfExists(boolean append) {
        this._appendIfExists = append;
        return isAppendIfExists();
    }

    protected SeekableByteChannel getWriterChannel() {
        return this._writerChannel;
    }

    private int getCurrentRolloverPeriod() {
        return Integer.parseInt(
                new SimpleDateFormat(getRolloverFormat()).format(
                        Calendar.getInstance().getTime()));
    }

    private int getRolloverPeriod() {
        return this._rolloverPeriod;
    }

    private void setRolloverPeriod() {
        this._rolloverPeriod = Integer.parseInt(new SimpleDateFormat(
                getRolloverFormat()).format(Calendar.getInstance().getTime()));
    }

    /**
     * checkRollover() method is used to check if the output file needs to be
     * changed due to rollover comparison. If the rollover has changed, then the
     * existing file is closed and new one opened.
     *
     * @return <code>boolean</boolean> signalling if file changed
     * @throws java.lang.Exception
     */
    protected boolean checkRollover() throws Exception {
        // tracks if the file need to be changed
        boolean fileChange = false;

        // if the writer is not null, check if needs to be changed
        if (getWriterChannel() != null) {
            // if this is not a standard file processing type, then check for
            // rollover
            if (getProcessingType() != FileProcessingType.STANDARD) {
                // check the file rollover is showing a change, if yes, then need to 
                // close current file and open new one.
                if (getRolloverPeriod() != getCurrentRolloverPeriod()) {
                    // signal that file is being changed
                    fileChange = true;

                    // update the period to reflect the current period
                    setRolloverPeriod();
                }

                // does the file need to be changed?
                if (fileChange) {
                    // close the current writer, if error, ignore it
                    try {
                        getWriterChannel().close();
                    } catch (Exception exi) {
                    }
                }
            }
        } else {
            // first time, there is no writer, signal that file is being changed
            fileChange = true;
        }

        // if file change is set, then open the writer
        if (fileChange) {
            // capture all exceptions to ensure proper handling of memory and
            // notification to client
            try {
                // create new file name for the current datehour
                String filename = getFileLocation() + "\\"
                        + String.format(getFileMask(), getFileDate());

                // try to open the writer for the new file, if not append exists
                // then truncate the data from the file
                if (isAppendIfExists()) {
                    this._writerChannel = Files.newByteChannel(Paths.get(
                            filename),
                            EnumSet.of(StandardOpenOption.CREATE,
                                    StandardOpenOption.SYNC,
                                    StandardOpenOption.READ,
                                    StandardOpenOption.WRITE));
                } else {
                    this._writerChannel = Files.newByteChannel(Paths.get(
                            filename),
                            EnumSet.of(StandardOpenOption.CREATE,
                                    StandardOpenOption.SYNC,
                                    StandardOpenOption.READ,
                                    StandardOpenOption.WRITE,
                                    StandardOpenOption.TRUNCATE_EXISTING));
                }
            } catch (Exception ex) {
                throw new Exception(ex);
            }
        }

        return fileChange;
    }

    public void close() {
        synchronized (this._runtimeSync) {
            if (getWriterChannel() != null) {
                try {
                    getWriterChannel().close();
                } catch (Exception exi) {
                }

                this._writerChannel = null;
            }
        }
    }

    public abstract int write(byte[] buffer) throws Exception;
}
