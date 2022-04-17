package protocol.con;

// https://dev.mysql.com/doc/internals/en/status-flags.html
public interface ServerStatus {

  public static final int SERVER_STATUS_IN_TRANS = 1;
  public static final int SERVER_STATUS_AUTOCOMMIT = 2; // Server in auto_commit mode
  public static final int SERVER_MORE_RESULTS_EXISTS = 8; // Multi query - next query exists
  public static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
  public static final int SERVER_QUERY_NO_INDEX_USED = 32;
  public static final int SERVER_STATUS_CURSOR_EXISTS = 64;
  public static final int SERVER_STATUS_LAST_ROW_SENT = 128; // The server status for 'last-row-sent'
  public static final int SERVER_QUERY_WAS_SLOW = 2048;
  public static final int SERVER_SESSION_STATE_CHANGED = 1 << 14; // 16384
}
