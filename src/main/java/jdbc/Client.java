package jdbc;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Client {

    @SneakyThrows
    public static void main(String[] args) {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:4000/test", "root", "123");
        System.out.println("create connection success");
        Statement sm = conn.createStatement();
        sm.close();
        conn.close();
    }
}
