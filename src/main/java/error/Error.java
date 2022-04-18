package error;

public class Error {

  public static String ERROR_1045(String userName, String pwdFlag) {
    return String.format(
        "ERROR 1045 (28000): Access denied for user '%s'@'localhost' (using password: %s)",
        userName, pwdFlag);
  }
}
