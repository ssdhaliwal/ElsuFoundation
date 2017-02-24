package elsu.events;

/**
 *
 * @author ssd.administrator
 */
public interface IEventPublisher {

    void addEventListener(IEventSubscriber listener);

    void removeEventListener(IEventSubscriber listener);

    Object notifyListeners(Object sender, IEventStatusType status, String message, Object o);
}
