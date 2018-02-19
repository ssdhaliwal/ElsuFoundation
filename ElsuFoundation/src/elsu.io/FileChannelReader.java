package elsu.io;

import elsu.common.*;

/**
 *
 * @author ssd.administrator
 */
public class FileChannelReader extends AbstractFileChannelReader {

    // runtime sync object
    private Object _runtimeSync = new Object();

    public FileChannelReader(String fileMask, String fileLocation) throws
            Exception {
        super(fileMask, fileLocation);
    }

    public FileChannelReader(String fileMask, String fileLocation,
            FileProcessingType processingType) throws Exception {
        super(fileMask, fileLocation, processingType);
    }

    public FileChannelReader(String fileMask, String fileLocation,
            FileProcessingType processingType,
            FileRolloverPeriodicityType rolloverPeriodicity,
            int rolloverThreshold) throws Exception {
        super(fileMask, fileLocation, processingType, rolloverPeriodicity,
                rolloverThreshold);
    }

    @Override
    public String readline() throws Exception {
        throw new Exception("not implemented in binary reader.");
    }

    @Override
    public byte[] read() throws Exception {
        byte[] result = null;

        synchronized (this._runtimeSync) {
            // track # of bytes read from the media
            int readCount;

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
                    // if there is data in the buffer
                    if (getBuffer().hasRemaining()) {
                        // read a char from the buffer, position is
                        // increased to next char position
                        int bytesProcessed = getBuffer().remaining();
                        ASCIIByteBuffer byteBuffer = new ASCIIByteBuffer(
                                bytesProcessed);
                        byteBuffer.setLength(bytesProcessed);
                        getBuffer().get(byteBuffer.getByteBuffer());

                        // if recovery is enabled, then store recovery
                        // information
                        if ((getProcessingType() == FileProcessingType.LIVE)
                                || (getProcessingType()
                                == FileProcessingType.ARCHIVE)) {
                            // store the media status control file
                            // which tracks the position of the 
                            // last read from input media - used
                            // for recovery
                            setStatusPosition(getStatusPosition() + bytesProcessed);
                            sharedWrite(getStatusChannel(), (getReaderFilename()
                                    + "," + String.valueOf(getStatusPosition())).getBytes(),
                                    true);
                        }

                        // convert list to byte[]
                        //-Byte[] bytes = byteBuffer.toArray(new Byte[byteBuffer.size()]);
                        //-byte[] rBytes = ArrayUtils.toPrimitive(bytes);
                        // return the data
                        //-return rBytes;
                        result = byteBuffer.getBytes();
                    }
                }
            }
        }

        return result;
    }
}
