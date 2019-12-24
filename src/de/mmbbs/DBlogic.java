package de.mmbbs;

import java.sql.*;

public class DBlogic {
    private DB db;
    private Connection conn;
    private Statement stmt;

    public DBlogic() throws SQLException {
        this.db = new DB();
        this.conn = db.createDatabaseConnection();
        this.stmt = conn.createStatement();
    }

    public ResultSet jdbcSelectQuery(String selectQuery) throws SQLException {
        // Output just for info
        System.out.println("The SQL statement is: " + selectQuery + "\n"); // Echo For debugging

        // Return resultSet from SQL SELECT query
        return stmt.executeQuery(selectQuery);
    }

    public void jdbcInsertQuery(int id, String title, String author, double price, int qty) throws SQLException {
        // Use prepared Statement to prevent SQL Injection and escape single quotes
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?)");

        // Fill "?" with properly formated values
        pstmt.setInt(1, id);
        pstmt.setString(2, title);
        pstmt.setString(3, author);
        pstmt.setDouble(4, price);
        pstmt.setInt(5, qty);

        // Execute the INSERT INTO statement
        pstmt.executeUpdate();


        // Info
        System.out.println("Executed INSERT INTO!");
    }
}
