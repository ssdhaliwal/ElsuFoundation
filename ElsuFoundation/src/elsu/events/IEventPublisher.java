package elsu.events;

import elsu.events.IEventStatusType;
import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public interface IEventPublisher {

    void addEventListener(IEventSubscriber listener);

    void removeEventListener(IEventSubscriber listener);

    Object notifyListeners(Object sender, IEventStatusType status, String message, Object o);
}
