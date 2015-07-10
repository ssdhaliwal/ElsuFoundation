package elsu.io;

import static elsu.io.FileChannelAbstract.*;

/**
 *
 * @author ssd.administrator
 */
public class FileChannelWriter extends FileChannelWriterAbstract {

    public FileChannelWriter(String fileMask, String fileLocation) throws
            Exception {
        super(fileMask, fileLocation);
    }

    public FileChannelWriter(String fileMask, String fileLocation,
            boolean append) throws Exception {
        super(fileMask, fileLocation, append);
    }

    public FileChannelWriter(String fileMask, String fileLocation,
            FileRolloverPeriodicityType rolloverPeriodicity) throws Exception {
        super(fileMask, fileLocation, rolloverPeriodicity);
    }

    public int write(String data) throws Exception {
        return write(data.getBytes());
    }

    public int write(char[] data) throws Exception {
        return write(new String(data).getBytes());
    }

    @Override
    public synchronized int write(byte[] buffer) throws Exception {
        checkRollover();
        return sharedWrite(getWriterChannel(), buffer, false);
    }
}
