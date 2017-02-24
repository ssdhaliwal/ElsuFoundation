package elsu.io;

import elsu.common.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * AbstractFileChannelReader() class extends the FileChanneAbstract to provide
 * concrete implementation for reading files using file recovery capability. The
 * class supports two methods of recovery: LIVE and RECOVERY.
 * <p>
 * In LIVE mode the current date format file depending on the rollover
 * periodicity is read until the rollover signals time change.
 * <p>
 * In RECOVERY mode archive files are read based on date sequence and old files
 * past the rollover threshold are not read. Once the file has been read, the
 * file is removed.
 *
 * @author ssd.administrator
 */
public abstract class AbstractFileChannelReader extends AbstractFileChannel {

    // runtime sync object
    private Object _runtimeSync = new Object();

    // local storage, stores the reader file name being processed
    private volatile String _readerFilename = null;

    // local storage, stores the reader date (using dataFormatMask0
    private volatile String _readerDate = null;

    // local storage, stores the reader channel
    private volatile SeekableByteChannel _readerChannel = null;

    // local storage, status of the reader channel for public
    private volatile boolean _readerValid = false;

    // local storage, stores the reader stauts channel, which keeps track of
    // the reader bytes being read
    private volatile SeekableByteChannel _statusChannel = null;

    // local storage, stores the last status position of the read, initially
    // populated in open from last stored position
    private volatile long _statusPosition = 0L;

    // local storage, master read buffer for the read, which needs to be
    // maintained between multiple calls to the read method
    private volatile int _bufferSize = 256;
    private ByteBuffer _buffer = null;

    // local storage, end of file marker reached
    private volatile boolean _endOfFile = true;

    // local storage, has the file changed
    // 20141129 SSD added to signal file changed for client to process and reset
    private volatile boolean _fileChanged = false;

    public AbstractFileChannelReader(String fileMask, String fileLocation)
            throws Exception {
        super(fileMask, fileLocation);

        initialize();
    }

    public AbstractFileChannelReader(String fileMask, String fileLocation,
            FileProcessingType processingType) throws Exception {
        super(fileMask, fileLocation, processingType);

        initialize();
    }

    public AbstractFileChannelReader(String fileMask, String fileLocation,
            FileProcessingType processingType,
            FileRolloverPeriodicityType rolloverPeriodicity,
            int rolloverThreshold) throws Exception {
        super(fileMask, fileLocation, processingType, rolloverPeriodicity,
                rolloverThreshold);

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

        // allocate the buffer to default size
        setBuffer(256);
    }

    @Override
    public void finalize() {
        close();

        try {
            super.finalize();
        } catch (Throwable texi) {
        }
    }

    // 20141129 SSD added to signal file changed for client to process
    public boolean isFileChanged() {
        boolean result = false;

        synchronized (this._runtimeSync) {
            result = this._fileChanged;
        }

        return result;
    }

    // 20141129 SSD added to signal file changed for client to process
    public boolean isFileChanged(boolean state) {
        synchronized (this._runtimeSync) {
            this._fileChanged = state;
        }

        return isFileChanged();
    }

    public String getFileToProcess() {
        if ((getProcessingType() == FileProcessingType.LIVE)
                || (getProcessingType() == FileProcessingType.STANDARD)) {
            return getReaderFilenameLive();
        } else {
            return getNextRecoveryFile();
        }
    }

    public String getNextRecoveryFile() {
        // get the list of all pending files
        ArrayList<String> files = FileUtils.findFiles(getFileLocation() + "\\",
                String.format(getFileMask(), ".*"), false, true, 0);

        // list of all files which need to be deleted
        ArrayList<String> fileDeletes = new ArrayList<>();

        // sort the list so they are datehour sequence
        Collections.sort(files);

        // compare the pending files to the computed file datetime - threshold
        // to eliminate returning old data
        long maxRecoverySeq = Long.parseLong(getMaxRecoveryFiledate());

        // loop through the file list and select files which are past the rollover
        // threshold count
        int maskLen = getFileLocation().length() + 1 + getFileMask().indexOf(
                "%s") + getFileDateFormatMask().length();
        int maskStart = getFileLocation().length() + 1 + getFileMask().indexOf(
                "%s");

        for (int i = 0; i < files.size(); i++) {
            String file = files.get(i);
            long fileSeq = Long.parseLong(file.substring(maskStart, maskLen));

            // if fileseq is less than max recovery seq, then mark the file for
            // deletion
            if (fileSeq < maxRecoverySeq) {
                fileDeletes.add(file);
            }
        }

        // delete all the files identified in the list
        for (int i = 0; i < fileDeletes.size();) {
            // get file to delete
            String file = fileDeletes.get(i);

            // capture all exceptions to ensure proper handling of memory and
            // notification to client
            try {
                Files.delete(Paths.get(file));
            } catch (Exception exi) {
            }

            try {
                Files.delete(Paths.get(file.replace(".txt", ".ctl")));
            } catch (Exception exi) {
            }

            // remove the file from both the lists
            fileDeletes.remove(0);
            files.remove(file);
        }

        // set the file deletes array to null
        fileDeletes = null;

        // remove the readerFilename also from the list since that file is 
        // being processed and should not be re-processed
        files.remove(getReaderFilename());

        // loop through pending files and if matching is less than maxRecovery
        // than remove and delete them
        // if there are still files left, return the first from the list only
        // when the filename does not match the currentfilename
        if (files.size() > 0) {
            if (!getReaderFilenameLive().equals(files.get(0))) {
                return files.get(0);
            }
        }

        // there are no files left, return null
        return null;
    }

    protected ByteBuffer getBuffer() {
        return this._buffer;
    }

    protected ByteBuffer setBuffer(int size) {
        this._buffer = ByteBuffer.allocate(getBufferSize());
        return getBuffer();
    }

    protected int getBufferSize() {
        return this._bufferSize;
    }

    protected int setBufferSize(int size) {
        if ((size != getBufferSize()) || (getBuffer() == null)) {
            this._bufferSize = size;

            setBuffer(size);
        }

        return getBufferSize();
    }

    public boolean isEndOfFile() {
        boolean result = false;

        synchronized (this._runtimeSync) {
            result = this._endOfFile;
        }

        return result;
    }

    public boolean isEndOfFile(boolean state) {
        synchronized (this._runtimeSync) {
            this._endOfFile = state;
        }

        return isEndOfFile();
    }

    protected SeekableByteChannel getReaderChannel() {
        return this._readerChannel;
    }

    public String getReaderDate() {
        return this._readerDate;
    }

    public String getReaderFilename() {
        return this._readerFilename;
    }

    public String getReaderFilenameLive() {
        return getFileLocation() + "\\"
                + String.format(getFileMask(), getFileDate());
    }

    public long getReaderSize() {
        long result = 0L;

        try {
            result = getReaderChannel().size();
        } catch (Exception exi) {
        }

        return result;
    }

    public boolean isReaderValid() {
        return this._readerValid;
    }

    protected boolean isReaderValid(boolean valid) {
        this._readerValid = valid;
        return isReaderValid();
    }

    protected SeekableByteChannel getStatusChannel() {
        return this._statusChannel;
    }

    public long getStatusPosition() {
        return this._statusPosition;
    }

    protected void setStatusPosition(long position) {
        this._statusPosition = position;
    }

    public void close() {
        synchronized (this._runtimeSync) {
            // set reader option
            isReaderValid(false);

            if (getReaderChannel() != null) {
                try {
                    getReaderChannel().close();
                } catch (Exception exi) {
                }
            }

            if (getStatusChannel() != null) {
                try {
                    getStatusChannel().close();
                } catch (Exception exi) {
                }
            }

            this._readerChannel = null;
            this._statusChannel = null;
        }
    }

    protected void closeReader() {
        close();

        // 20141128 SSD only delete the CTL file if the main file delete
        // is successfull.  this way if the file is reprocessed the recovery
        // will prevent resending of data
        if (getProcessingType() == FileProcessingType.ARCHIVE) {
            try {
                Files.delete(Paths.get(getReaderFilename()));

                try {
                    Files.delete(Paths.get(getReaderFilename().replace(".txt",
                            ".ctl")));
                } catch (Exception exi) {
                }
            } catch (Exception exi) {
            }
        }

        // clear local variables used for tracking
        this._readerFilename = null;
        setStatusPosition(0);
    }

    protected boolean openReader() throws Exception {
        boolean result = false;

        // clear the buffer
        getBuffer().clear();
        getBuffer().flip();

        // get the reader file name
        String nextFile = getFileToProcess();

        // if in LIVE mode, and file already matches, then return
        if ((getProcessingType() == FileProcessingType.LIVE)
                && (nextFile.equals(getReaderFilename()))) {
            isEndOfFile(false);
            return true;
        }

        // close the current reader
        closeReader();

        // store the filename
        this._readerFilename = nextFile;

        if (getReaderFilename() != null) {
            // create and store the channel for reading the input data
            this._readerChannel = Files.newByteChannel(Paths.get(
                    getReaderFilename()), EnumSet.of(StandardOpenOption.CREATE,
                            StandardOpenOption.SYNC, StandardOpenOption.READ,
                            StandardOpenOption.WRITE));

            // if recovery is enabled, then position the reader, else ignore
            if ((getProcessingType() == FileProcessingType.LIVE)
                    || (getProcessingType() == FileProcessingType.ARCHIVE)) {
                // reposition reader to the last read position from ctl file
                positionReader();
            }

            isReaderValid(true);
            isEndOfFile(false);

            // 20141129 SSD added to signal file changed for client to process
            isFileChanged(true);

            result = true;
        }

        return result;
    }

    protected void positionReader() throws Exception {
        // convert reader filename to status filename
        String filename = getReaderFilename().replace(".txt", ".ctl");

        // open the reader status file
        this._statusChannel = Files.newByteChannel(Paths.get(filename),
                EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.SYNC,
                        StandardOpenOption.READ, StandardOpenOption.WRITE));

        // if the file is not empty, then recovery is required
        if (getStatusChannel().size() > 0) {
            // allocate buffer to read the file
            ByteBuffer buff = ByteBuffer.allocate(256);

            // read the data from the file status
            getStatusChannel().read(buff);

            // reset the buffer position to the start
            buff.flip();

            // create string buffer to collect bytes read from the 
            // buffer
            StringBuilder sb = new StringBuilder();

            // loop for all bytes in the buffer, and store them into
            // the string buffer
            while (buff.hasRemaining()) {
                char c = (char) buff.get();
                sb.append(c);
            }

            // split the data in the string buffer to get the file 
            // position from the array of data stored
            String[] sData = sb.toString().split(",");

            // not used, contains the file name (matches reader)
            //String statusFilename = sData[0].trim();
            // convert the string value to long
            long statusPosition = Long.valueOf(sData[1].trim());
            setStatusPosition(statusPosition);

            // update the reader channel position to match the stored
            // value
            getReaderChannel().position(statusPosition);
        }
    }

    public abstract String readline() throws Exception;

    public abstract byte[] read() throws Exception;
}
