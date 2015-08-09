package elsu.support;

import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public interface IEventPublisher {

    void addEventListener(IEventSubscriber listener);

    void removeEventListener(IEventSubscriber listener);

    void notifyListeners(EventObject event, StatusType s, String message, Object o);
}
