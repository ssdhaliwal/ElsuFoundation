package elsu.events;

import elsu.events.IEventStatusType;
import elsu.common.*;
import java.util.*;

/**
 *
 * @author ssd.administrator
 */
public interface IEventSubscriber {

    Object EventHandler(Object sender, IEventStatusType status, String message, Object o);
}
