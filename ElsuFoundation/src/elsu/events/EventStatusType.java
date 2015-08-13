package elsu.events;

import java.util.*;

/**
 *
 * @author ss.dhaliwal_admin
 */
public enum EventStatusType implements IEventStatusType {

    // A
    ABORT("ABORT", 1000), ACCEPT("ACCEPT", 1001), ACTIVATE("ACTIVATE", 1002),
    ADD("ADD", 1003), ALLOCATE("ALLOCATE", 1004), ALTER("ALTER", 1005),
    APPEND("APPEND", 1006),
    // C
    CANCEL("CANCEL", 1007), CLEAR("CLEAR", 1008), CHANGE("CHANGE", 1009),
    CLOSE("CLOSE", 1010), COMPLETE("COMPLETE", 1011), COMPRESS("COMPRESS", 1012),
    CONNECT("CONNECT", 1013), COPY("COPY", 1014), CREATE("CREATE", 1015),
    // D
    DEACTIVATE("DEACTIVATE", 1016), DEBUG("DEBUG", 1017),
    DECOMPRESS("DECOMPRESS", 1018), DECRYPT("DECRYPT", 1019),
    DELETE("DELETE", 1020), DELIVERY("DELIVERY", 1021), DESTROY("DESTROY", 1022),
    DISCONNECT("DISCONNECT", 1023), DROP("DROP", 1024),
    // E
    ENCRYPT("ENCRYPT", 1025), ERROR("ERROR", 1026), EXECUTE("EXECUTE", 1027),
    EXISTS("EXISTS", 1028), EXPAND("EXPAND", 1029), EXTRACT("EXTRACT", 1030),
    // F
    FAIL("FAIL", 1031), FIND("FIND", 1032), FREE("FREE", 1033),
    // I
    INFORMATION("INFORMATION", 1034), INITIALIZE("INITIALIZE", 1035),
    INSERT("INSERT", 1036),
    // K
    KILL("KILL", 1037),
    // L
    LISTENING("LISTENING", 1038),
    // M
    MESSAGE("MESSAGE", 1039), MODIFY("MODIFY", 1040),
    // N
    NEW("NEW", 1041), NOTFOUND("NOTFOUND", 1042),
    // O
    OPEN("OPEN", 1043),
    // Q
    QUEUED("QUEUED", 1045),
    // R    
    READ("READ", 1046), READONLY("READONLY", 1047), READWRITE("READWRITE", 1048),
    REJECT("REJECT", 1049), RELEASE("RELEASE", 1050), REMOVE("REMOVE", 1051),
    RENAME("RENAME", 1052), RESET("RESET", 1053), ROLLBACK("ROLLBACK", 1054),
    RUN("RUN", 1055),
    // S
    SELECT("SELECT", 1056), SENT("SENT", 1057), SEQUENCE("SEQUENCE", 1058),
    SHUTDOWN("SHUTDOWN", 1059), START("START", 1060), STOP("STOP", 1061),
    // T
    TRANSACTION("TRANSACTION", 1062), TRUNCATE("TRUNCATE", 1062),
    // U
    UPDATE("UPDATE", 1063),
    // W
    WRITE("WRITE", 1064), WARNING("WARNING", 1065);

    private static Map< String, IEventStatusType> _map
            = new TreeMap< String, IEventStatusType>();

    private final String _name;
    private final int _id;

    static {
        for (IEventStatusType status : values()) {
            _map.put(status.getName(), status);
        }
    }

    public static IEventStatusType statusTypeFor(String name) {
        return (IEventStatusType) _map.get(name);
    }

    private EventStatusType(String name, int id) {
        this._name = name;
        this._id = id;
    }

    @Override
    public String getName() {
        return this._name;
    }

    @Override
    public int getId() {
        return this._id;
    }

    public static void addStatusType(String name, int id) {
        if (!_map.containsKey(name)) {
            final String lName = name;
            final int lId = id;

            IEventStatusType newStatus = new IEventStatusType() {
                @Override
                public String getName() {
                    return lName;
                }

                @Override
                public int getId() {
                    return lId;
                }
            };

            _map.put(name, newStatus);
        }
    }
}
