package db;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/onlineexaminationsystem";
        String user = "root";
        String pass = "localhost";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, user, pass);
        System.out.println("New DB connection created successfully.");
        return conn;
    }
}