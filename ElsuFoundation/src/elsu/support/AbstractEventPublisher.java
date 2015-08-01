package elsu.support;

import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public abstract class AbstractEventPublisher implements IEventPublisher {

    @Override
    public synchronized void addEventListener(IEventSubscriber listener) {
        _listeners.add(listener);
    }

    @Override
    public synchronized void removeEventListener(IEventSubscriber listener) {
        _listeners.remove(listener);
    }

    @Override
    public synchronized void notifyListeners(EventObject event, StatusType s,
            Object o) {
        Iterator i = _listeners.iterator();

        while (i.hasNext()) {
            try {
                ((IEventSubscriber) i.next()).EventHandler(event, s, o);
            } catch (Exception ex) {
                System.out.println(getClass().toString() + ", notifyListeners(), "
                        + ex.getMessage());
            }
        }
    }
}
