package elsu.support;

import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public abstract class AbstractEventPublisher implements IEventPublisher {

    List<IEventSubscriber> _listeners = new ArrayList<>();

    @Override
    public synchronized void addEventListener(IEventSubscriber listener) {
        _listeners.add(listener);
    }

    @Override
    public synchronized void removeEventListener(IEventSubscriber listener) {
        _listeners.remove(listener);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _listeners.clear();
        } catch (Exception exi) {
        } finally {
            super.finalize();
        }
    }

    @Override
    public synchronized Object notifyListeners(Object sender, StatusType status,
            String message, Object o) {
        Object result = null;
        
        // if listeners are not setup, then just output to console
        if (_listeners.size() == 0) {
            System.out.println(status.name() + ":" + message);
        } else {
            Iterator i = _listeners.iterator();

            while (i.hasNext()) {
                try {
                    result = ((IEventSubscriber) i.next()).EventHandler(sender, status, message, o);
                } catch (Exception ex) {
                    System.out.println(getClass().toString() + ", notifyListeners(), "
                            + ex.getMessage());
                }
            }
        }
        
        return result;
    }
}
