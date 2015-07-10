package elsu.io;

import elsu.common.*;

public class FileChannelTextReader extends FileChannelReaderAbstract {

    // local storage, ignore empty lines when reading
    private volatile boolean _ignoreEmptyLines = false;

    // local storage, line terminator for buffer reading
    private volatile String _lineTerminator = GlobalStack.LINESEPARATOR;

    public FileChannelTextReader(String fileMask, String fileLocation) throws
            Exception {
        super(fileMask, fileLocation);
    }

    public FileChannelTextReader(String fileMask, String fileLocation,
            FileProcessingType processingType) throws Exception {
        super(fileMask, fileLocation, processingType);
    }

    public FileChannelTextReader(String fileMask, String fileLocation,
            FileProcessingType processingType,
            FileRolloverPeriodicityType rolloverPeriodicity,
            int rolloverThreshold) throws Exception {
        super(fileMask, fileLocation, processingType, rolloverPeriodicity,
                rolloverThreshold);
    }

    public boolean isIgnoreEmptyLines() {
        return this._ignoreEmptyLines;
    }

    public boolean isIgnoreEmptyLines(boolean ignoreEmptyLines) {
        this._ignoreEmptyLines = ignoreEmptyLines;
        return isIgnoreEmptyLines();
    }

    protected String getLineTerminator() {
        return this._lineTerminator;
    }

    protected void setLineTerminator(String terminator) {
        this._lineTerminator = terminator;
    }

    @Override
    public String readline() throws Exception {
        // track # of bytes read from the media
        int readCount;

        // newline indicator since DOS EOL is two chars
        boolean newLine = false;

        // bytebuffer to store the string retrieved; pre-create it to the size
        // of the read buffer to allow for contiguous memory allocation; the
        // buffer will grow if needed
        ASCIIByteBuffer byteBuffer = new ASCIIByteBuffer(getBufferSize());

        // if the input media is null, yield to other threads for 
        // specific time and try to reopen the waiting files
        if (getReaderChannel() == null) {
            if ((getProcessingType() == FileProcessingType.LIVE)
                    || (getProcessingType() == FileProcessingType.ARCHIVE)) {
                // yield processing to other threads
                Thread.sleep(getLockTimeout());
            }

            // open the next file, if available
            openReader();
        }

        // as long as the reader media is valid
        int bytesProcessed = 0;
        while (getReaderChannel() != null) {
            if (!getBuffer().hasRemaining()) {
                // clear the buffer
                getBuffer().clear();

                // read the length of the buffer from media
                readCount = getReaderChannel().read(getBuffer());

                // reset the buffer pointer to start
                getBuffer().flip();

                // if the file channel does not have any more data, 
                // yield processing to other threads for specified 
                // time, and exit the read loop
                if (readCount == -1) {
                    // set end of file marker
                    isEndOfFile(true);

                    // if there is pending data in the buffer, send it out
                    if (byteBuffer.length() > 0) {
                        // return the data
                        return new String(byteBuffer.getBytes());
                    }

                    // close the reader for standard processing
                    if (getProcessingType() == FileProcessingType.STANDARD) {
                        closeReader();
                    }

                    // if the reader is LIVE or ARCHIVE then it keeps processing
                    if ((getProcessingType() == FileProcessingType.LIVE)
                            || (getProcessingType()
                            == FileProcessingType.ARCHIVE)) {
                        // yield processing to other threads
                        Thread.sleep(getLockTimeout());

                        // open the next file, if available
                        openReader();

                        // if reader is still empty, exit
                        if (getReaderChannel() == null) {
                            break;
                        }
                    }
                }
            } else {
                // loop as long as there is data in the buffer
                while (getBuffer().hasRemaining()) {
                    // read a char from the buffer, position is
                    // increased to next char position
                    bytesProcessed++;
                    byte b = getBuffer().get();

                    // if byte is a new line char, then one string
                    // has been read - process it (or)
                    //(c == '\n') || (c == '\r')
                    if (getLineTerminator().indexOf(b) != -1) {
                        // if data length is greater than zero or ignore
                        // empty lines is not active, send it to the out stream
                        if ((byteBuffer.length() > 0) || !isIgnoreEmptyLines()) {
                            // if recovery is enabled, then store recovery
                            // information
                            if ((getProcessingType() == FileProcessingType.LIVE)
                                    || (getProcessingType()
                                    == FileProcessingType.ARCHIVE)) {
                                // store the media status control file
                                // which tracks the position of the 
                                // last read from input media - used
                                // for recovery
                                setStatusPosition(getStatusPosition()
                                        + bytesProcessed);
                                sharedWrite(getStatusChannel(),
                                        (getReaderFilename() + ","
                                        + String.valueOf(getStatusPosition())).getBytes(),
                                        true);
                            }

                            // send only if new line is detected; this is required
                            // due to DOS two byte line terminator
                            if (newLine) {
                                return new String(byteBuffer.getBytes());
                            }
                        }
                    } else {
                        // append the char read to the string buffer
                        byteBuffer.append(b);

                        // set newline marker; this is required due to DOS 
                        // two char line terminator
                        newLine = true;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public synchronized byte[] read() throws Exception {
        throw new Exception("not implemented in text reader.");
    }
}
