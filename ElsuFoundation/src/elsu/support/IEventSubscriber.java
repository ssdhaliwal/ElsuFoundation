package elsu.support;

import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public interface IEventSubscriber {

    public void EventHandler(EventObject e);

    public void EventHandler(EventObject e, Object o);

    public void EventHandler(EventObject e, StatusType s, Object o);
}
