package protocol.con;

import java.util.Arrays;

public class CapabilitiesFlags {

    public static final int CLIENT_LONG_PASSWORD = 0x00000001; /* new more secure passwords */
    public static final int CLIENT_FOUND_ROWS = 0x00000002;
    public static final int CLIENT_LONG_FLAG = 0x00000004; /* Get all column flags */
    public static final int CLIENT_CONNECT_WITH_DB = 0x00000008;
    public static final int CLIENT_COMPRESS = 0x00000020; /* Can use compression protocol */
    public static final int CLIENT_LOCAL_FILES = 0x00000080; /* Can use LOAD DATA LOCAL */
    public static final int CLIENT_PROTOCOL_41 = 0x00000200; // for > 4.1.1
    public static final int CLIENT_INTERACTIVE = 0x00000400;
    public static final int CLIENT_SSL = 0x00000800;
    public static final int CLIENT_TRANSACTIONS = 0x00002000; // Client knows about transactions
    public static final int CLIENT_RESERVED = 0x00004000; // for 4.1.0 only
    public static final int CLIENT_SECURE_CONNECTION = 0x00008000;
    public static final int CLIENT_MULTI_STATEMENTS = 0x00010000; // Enable/disable multiquery support
    public static final int CLIENT_MULTI_RESULTS = 0x00020000; // Enable/disable multi-results
    public static final int CLIENT_PS_MULTI_RESULTS = 0x00040000; // Enable/disable multi-results for server prepared statements
    public static final int CLIENT_PLUGIN_AUTH = 0x00080000;
    public static final int CLIENT_CONNECT_ATTS = 0x00100000;
    public static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 0x00200000;
    public static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 0x00400000;
    public static final int CLIENT_SESSION_TRACK = 0x00800000;
    public static final int CLIENT_DEPRECATE_EOF = 0x01000000;
    public static final int CLIENT_QUERY_ATTRIBUTES = 0x08000000;
    public static final int CLIENT_MULTI_FACTOR_AUTHENTICATION = 0x10000000;

    public static int getDefaultCapabilitiesFlags() {
        int capabilitiesFlags = 0;
        capabilitiesFlags |= CLIENT_LONG_PASSWORD;
        capabilitiesFlags |= CLIENT_LONG_FLAG;
        capabilitiesFlags |= CLIENT_CONNECT_WITH_DB;
        capabilitiesFlags |= CLIENT_PROTOCOL_41;
        capabilitiesFlags |= CLIENT_TRANSACTIONS;
        capabilitiesFlags |= CLIENT_SECURE_CONNECTION;
        capabilitiesFlags |= CLIENT_FOUND_ROWS;
        capabilitiesFlags |= CLIENT_MULTI_STATEMENTS;
        capabilitiesFlags |= CLIENT_MULTI_RESULTS;
        capabilitiesFlags |= CLIENT_LOCAL_FILES;
        capabilitiesFlags |= CLIENT_CONNECT_ATTS;
        capabilitiesFlags |= CLIENT_PLUGIN_AUTH;
        capabilitiesFlags |= CLIENT_INTERACTIVE;
        return capabilitiesFlags;
    }
}
