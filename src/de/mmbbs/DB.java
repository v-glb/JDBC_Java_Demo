package de.mmbbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    public Connection createDatabaseConnection() {
        // Allocate a database 'Connection' object
        // Hint: Add db-driver-.jar as Library in IntelliJ!

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    "jdbc_user", "password"); // Super safe and strong password!

            // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
}
