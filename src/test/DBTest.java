package test;

import db.DBConnection;
import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        try {
            System.out.println("Attempting to connect to database...");
            Connection conn = DBConnection.getConnection();
            System.out.println("Connection successful!");
            conn.close();
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
} 