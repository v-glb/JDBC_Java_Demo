package de.mmbbs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBlogic {
    private DB db;
    private Connection conn;
    private Statement stmt;

    public DBlogic() throws SQLException {
        this.db = new DB();
        this.conn = db.createDatabaseConnection();
        this.stmt = conn.createStatement();
    }

    public ResultSet jdbcQueryDemo(String query) throws SQLException {
        // Output just for info
        System.out.println("The SQL statement is: " + query + "\n"); // Echo For debugging

        // Return resultSet from SQL query
        return stmt.executeQuery(query);
    }
}
