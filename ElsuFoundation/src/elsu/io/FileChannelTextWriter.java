package elsu.io;

import elsu.common.*;

/**
 *
 * @author ssd.administrator
 */
public class FileChannelTextWriter extends FileChannelWriter {

    // local storage, ignore empty lines when writing
    private volatile boolean _ignoreEmptyLines = false;

    public FileChannelTextWriter(String fileMask, String fileLocation) throws
            Exception {
        super(fileMask, fileLocation);
    }

    public FileChannelTextWriter(String fileMask, String fileLocation,
            boolean append) throws Exception {
        super(fileMask, fileLocation, append);
    }

    public FileChannelTextWriter(String fileMask, String fileLocation,
            FileRolloverPeriodicityType rolloverPeriodicity) throws Exception {
        super(fileMask, fileLocation, rolloverPeriodicity);
    }

    public boolean isIgnoreEmptyLines() {
        return this._ignoreEmptyLines;
    }

    public boolean isIgnoreEmptyLines(boolean ignoreEmptyLines) {
        this._ignoreEmptyLines = ignoreEmptyLines;
        return isIgnoreEmptyLines();
    }

    public int writeline(String data) throws Exception {
        if (isIgnoreEmptyLines() && data.length() == 0) {
            return 0;
        }

        return write((data + GlobalStack.LINESEPARATOR).getBytes());
    }
}
