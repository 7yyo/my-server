package mysql;

import lombok.Data;

@Data
public class User {

  private String host;
  private String user;
  private String authenticationString;
  private String plugin;

  public User(String host, String user, String authenticationString, String plugin) {
    this.host = host;
    this.user = user;
    this.authenticationString = authenticationString;
    this.plugin = plugin;
  }

  public static User mockUser() {
    return new User(
        "%", "root", "*23AE809DDACAF96AF0FD78ED04B6A265E05AA257", "mysql_native_password");
  }
}
