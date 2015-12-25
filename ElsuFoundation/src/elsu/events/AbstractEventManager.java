package elsu.events;

import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public abstract class AbstractEventManager implements IEventPublisher {

    private Object _runtimeSync = new Object();
    List<IEventSubscriber> _listeners = new ArrayList<>();

    protected List<IEventSubscriber> getEventListeners() {
        List<IEventSubscriber> result = null;

        synchronized (this._runtimeSync) {
            result = this._listeners;
        }

        return result;
    }

    @Override
    public void addEventListener(IEventSubscriber listener) {
        synchronized (this._runtimeSync) {
            getEventListeners().add(listener);
        }
    }

    @Override
    public void removeEventListener(IEventSubscriber listener) {
        synchronized (this._runtimeSync) {
            getEventListeners().remove(listener);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            synchronized (this._runtimeSync) {
                getEventListeners().clear();
            }
        } catch (Exception exi) {
        } finally {
            super.finalize();
        }
    }

    @Override
    public Object notifyListeners(Object sender, IEventStatusType status,
            String message, Object o) {
        Object result = null;

        for (IEventSubscriber sub : getEventListeners()) {
            try {
                result = sub.EventHandler(sender, status, message, o);
            } catch (Exception ex) {
                System.out.println(getClass().toString() + ", notifyListeners(), "
                        + ex.getMessage());
            }
        }

        return result;
    }
}
