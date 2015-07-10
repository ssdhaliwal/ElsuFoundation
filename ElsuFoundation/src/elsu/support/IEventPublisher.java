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

    void notifyListeners(EventAbstract event);

    void notifyListeners(EventAbstract event, Object o);

    void notifyListeners(EventAbstract event, StatusType s, Object o);
}
