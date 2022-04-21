package protocol.con;

public interface Command {
    int COM_QUIT = 0x01;
    int COM_INIT_DB = 0x02;
    int COM_QUERY = 0x03;
    int COM_FIELD_LIST = 0x04;
    int COM_REFRESH = 0x07;
    int COM_STATISTICS = 0x08;
    int COM_PROCESS_INFO = 0x0A;
    int COM_PROCESS_KILL = 0x0C;
    int COM_DEBUG = 0x0D;
    int COM_PING = 0x0E;
    int COM_CHANGE_USER = 0x11;
    int COM_RESET_CONNECTION = 0x1F;
    int COM_SET_OPTION = 0x1A;
}
