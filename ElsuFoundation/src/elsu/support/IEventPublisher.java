package elsu.support;

import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public interface IEventPublisher {

    List<IEventSubscriber> _listeners = new ArrayList<>();

    void addEventListener(IEventSubscriber listener);

    void removeEventListener(IEventSubscriber listener);

    void notifyListeners(EventObject event, StatusType s, Object o);
}
