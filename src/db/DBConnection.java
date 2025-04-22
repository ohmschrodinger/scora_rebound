package db;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/EXAMINATION_SYSTEM";
        String user = "root";
        String pass = "Root@123";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, user, pass);
        System.out.println("New DB connection created successfully.");
        return conn;
    }
}