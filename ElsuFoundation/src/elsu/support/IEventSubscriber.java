package elsu.support;

import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public interface IEventSubscriber {

    void EventHandler(EventObject e, StatusType s, String message, Object o);
}
