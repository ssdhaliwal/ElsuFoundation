package elsu.support;

/**
 * XMLException extends Exception and is used by XML classes in utility package.
 * XMLReader and other XML classes capture custom exceptions thrown by javaAPI
 * and convert them and then throw one exception.
 *
 * @author: seraj.dhaliwal
 * @email: seraj.dhaliwal@live.com
 *
 * @changehistory (in descending order) date version user comments Jul, 21/09
 * 1.00 seraj.dhaliwal initial version
 *
 */
public class XMLException extends Exception {

    /**
     * Default class constructor.
     */
    public XMLException() {
    }

    /**
     * Class constructor to allow user to specify a custom message. XMLReader
     * and other XML classes uses this to streamline exception processing from
     * multiple exceptions into one.
     *
     * @param message string error message
     */
    public XMLException(String message) {
        super(message);
    }
}
