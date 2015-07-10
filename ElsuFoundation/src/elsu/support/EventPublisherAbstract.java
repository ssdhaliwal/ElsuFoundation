package elsu.support;

import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public abstract class EventPublisherAbstract implements IEventPublisher {

    @Override
    public synchronized void addEventListener(IEventSubscriber listener) {
        _listeners.add(listener);
    }

    @Override
    public synchronized void removeEventListener(IEventSubscriber listener) {
        _listeners.remove(listener);
    }

    @Override
    public synchronized void notifyListeners(EventAbstract event) {
        Iterator i = _listeners.iterator();

        while (i.hasNext()) {
            ((IEventSubscriber) i.next()).EventHandler(event);
        }
    }

    @Override
    public synchronized void notifyListeners(EventAbstract event, Object o) {
        Iterator i = _listeners.iterator();

        while (i.hasNext()) {
            ((IEventSubscriber) i.next()).EventHandler(event, o);
        }
    }

    @Override
    public synchronized void notifyListeners(EventAbstract event, StatusType s,
            Object o) {
        Iterator i = _listeners.iterator();

        while (i.hasNext()) {
            ((IEventSubscriber) i.next()).EventHandler(event, s, o);
        }
    }
}
