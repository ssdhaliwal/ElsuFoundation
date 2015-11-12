package elsu.events;

/**
 *
 * @author ssd.administrator
 */
public interface IEventSubscriber {

    Object EventHandler(Object sender, IEventStatusType status, String message, Object o);
}
