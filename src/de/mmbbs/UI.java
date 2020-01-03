package de.mmbbs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.*;

public class UI extends JFrame {
    private JTable table1;
    private JPanel panel1;
    private JTextField inputSqlQuery;
    private JButton queryButton;
    private JTextField inputSqlUpdate;
    private JButton updateButton;
    private JTextField sqlResult;
    private DefaultTableModel model;
    private DBlogic dbLogic;

    UI() {
        // Establish connection to local database
        try {
            this.dbLogic = new DBlogic();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initial window on app start
        createRootPanel();

        // Create table header
        this.model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "QTY"}, 0);

        // Append table header to Jtable
        table1.setModel(model);


        // SQL Statement input handling via Button clicks
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sqlQuery = inputSqlQuery.getText();

                try {
                    // Clear previous results in table
                    model.setRowCount(0);

                    sqlResult.setText("Query successfull!");
                    runJdbcQueryDemo(sqlQuery);
                } catch (SQLException ex) {
                    sqlResult.setText("Error while executing! Please check console for more info.");
                    ex.printStackTrace();
                }

            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sqlUpdate = inputSqlUpdate.getText();

                try {
                    // Clear previous results in table
                    // model.setRowCount(0);
                    sqlResult.setText("Statement successfull!");
                    runJdbcStatementDemo(sqlUpdate);
                    table1.repaint();
                } catch (SQLException ex) {
                    sqlResult.setText("Error while executing! Please check console for more info.");
                    ex.printStackTrace();
                }
            }
        });

        // Handle live editing of Jtable cells
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();

                // Update DB based on user input
                String id = table1.getValueAt(tcl.getRow(), 0).toString();
                String currentValue = tcl.getOldValue().toString();
                String newValue = tcl.getNewValue().toString();
                String columnName = table1.getColumnName(tcl.getColumn());

                // Only update DB if values are different and changed value is not ID since its PK
                if (!currentValue.equals(newValue) && !columnName.equals("ID")) {
                    try {
                        runJdbcStatementDemo("UPDATE books set " + columnName + " = '" + newValue + "' where id = '" + id + "';");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Changing of Primary Key not permitted!");
                }
            }
        };

        // Attach listener to table with DB entries
        TableCellListener tcl = new TableCellListener(table1, action);

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }
        });
    }

    private void createRootPanel() {
        setTitle("JDBC Demo");
        setSize(800, 600);
        setLocationRelativeTo(null); // Center window on screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Without, App continues running after closing window
        setVisible(true);

        add(this.panel1); // Add rootPanel to Jframe - without, there would only be a blank window
    }

    public void runJdbcQueryDemo(String sqlQuery) throws SQLException {

        // Results from SQL query
        ResultSet rset = this.dbLogic.jdbcQuery(sqlQuery);

        // Counter for displaying how many results were queried later
        int rowCount = 0;

        // Process the ResultSet by scrolling the cursor forward via next().
        // For each row, retrieve the contents of the cells with getXxx(columnName).
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


    public void runJdbcStatementDemo(String sqlStatement) throws SQLException {
        // SQL statement that modify the database
        dbLogic.jdbcStatement(sqlStatement);
    }

}

