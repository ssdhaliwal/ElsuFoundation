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

    Object notifyListeners(Object sender, StatusType status, String message, Object o);
}
