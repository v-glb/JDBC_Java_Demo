package de.mmbbs;

import java.sql.*;

public class DBlogic {
    private DB db;
    private Connection conn;
    private Statement stmt;

    public DBlogic() throws SQLException {
        // Create DB Object to base query / statements on
        this.db = new DB();
        this.conn = db.createDatabaseConnection();
        this.stmt = conn.createStatement();
    }

    public ResultSet jdbcQuery(String sqlQuery) throws SQLException {
        System.out.println("The SQL Query is: " + sqlQuery + "\n");

        // Iterate over ResultSet in UI to display rows
        return stmt.executeQuery(sqlQuery);
    }

    public void jdbcStatement(String sqlStatement) throws SQLException {
        System.out.println("The SQL Statement is: " + sqlStatement  + "\n");
        stmt.executeUpdate(sqlStatement);
    }
}
