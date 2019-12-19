package de.mmbbs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class UI extends JFrame {
    private JTable table1;
    private JPanel panel1;
    private DefaultTableModel model;

    UI() {
        // TODO: Implement form to input SQL-statements?

        // Initial window on app start
        createRootPanel();

        // Create table header
        this.model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "QTY"}, 0);

        // Append table header to Jtable
        table1.setModel(model);

        // Perform DB operation demo
        try {
            runJDBCdemo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createRootPanel() {
        setTitle("JDBC Demo");
        setSize(600, 480);
        setLocationRelativeTo(null); // Center window on screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Without, App continues running after closing window
        setVisible(true);

        add(this.panel1); // Add rootPanel to Jframe - without, there would only be a blank window
    }

    public void runJDBCdemo() throws SQLException {
        // Instantiate DB
        DB db = new DB();

        // Create connection to JDBC
        Connection conn = db.createDatabaseConnection();

        // Create SQL statement
        Statement stmt = conn.createStatement();

        // Define a SQL SELECT query: The query result is returned in a 'ResultSet' object.
        String strSelect = "SELECT * FROM books;";
        System.out.println("The SQL statement is: " + strSelect + "\n"); // Echo For debugging

        // Execute predefined SQL query
        ResultSet rset = stmt.executeQuery(strSelect);

        // Process the ResultSet by scrolling the cursor forward via next().
        // For each row, retrieve the contents of the cells with getXxx(columnName).
        System.out.println("The records selected are:");

        // Counter for displaying how many results were queried later
        int rowCount = 0;

        // Iterate over queried ResultSet object
        while (rset.next()) {   // Move the cursor to the next row, return false if no more row
            int id = rset.getInt("id");
            String title = rset.getString("title");
            String author = rset.getString("author");
            double price = rset.getDouble("price");
            int qty = rset.getInt("qty");

            // Write results to Jtable
            model.addRow(new Object[]{id, title, author, price, qty});

            rowCount++;
        }

        // Output just for info purposes
        System.out.println("Total number of records = " + rowCount);
    }
}

