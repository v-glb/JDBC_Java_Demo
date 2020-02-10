package de.mmbbs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UI extends JFrame {
    private JTable dbTable;
    private JPanel panel1;
    private JTextField inputSqlQuery;
    private JButton queryButton;
    private JTextField inputSqlUpdate;
    private JButton updateButton;
    private JTextField sqlResult;
    private JButton addNewButton;
    private JButton showAllButton;
    private DefaultTableModel dbTableModel;
    private DBlogic dbLogic;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemRemove;

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
        this.dbTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "QTY"}, 0);

        // Append table header to Jtable
        dbTable.setModel(dbTableModel);

        // SQL Statement input handling via Button clicks
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sqlQuery = inputSqlQuery.getText();

                try {
                    // Clear previous results in table
                    dbTableModel.setRowCount(0);

                    runJdbcQueryDemo(sqlQuery);
                    displaySqlExecInfo("Query successful!");

                } catch (SQLException ex) {
                    displaySqlExecInfo("Error while executing! Please check console for more info.");
                    ex.printStackTrace();
                }

            }
        });

        // Select whole row and show context menu on right click
        // Handle menu actions
        dbTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                int r = dbTable.rowAtPoint(e.getPoint());

                if (r >= 0 && r < dbTable.getRowCount()) {
                    dbTable.setRowSelectionInterval(r, r);
                } else {
                    dbTable.clearSelection();
                }

                int rowindex = dbTable.getSelectedRow();

                // Do nothing if user is not clicking on an element in table
                if (rowindex < 0) {
                    return;
                }

                // Only show menu if user clicks on a row
                if (e.isShiftDown() && e.getComponent() instanceof JTable || e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = createContextMenu();
                    popup.show(e.getComponent(), e.getX(), e.getY());

                    // Context menu listeners

                    // Remove selected row
                    menuItemRemove.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            removeSelectedRow();
                        }
                    });
                }
            }
        });

        // Handle live editing of Jtable cells
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();

                // Update DB based on user input
                String id = dbTable.getValueAt(tcl.getRow(), 0).toString();
                String currentValue = tcl.getOldValue().toString();
                String newValue = tcl.getNewValue().toString();
                String columnName = dbTable.getColumnName(tcl.getColumn());

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
        TableCellListener tcl = new TableCellListener(dbTable, action);

        dbTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }
        });

        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewRow();
            }
        });

        showAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Clear previous results in table
                    dbTableModel.setRowCount(0);

                    runJdbcQueryDemo("SELECT * FROM books;");
                    displaySqlExecInfo("Query Successfull!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
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
            dbTableModel.addRow(new Object[]{id, title, author, price, qty});

            rowCount++;
        }

        // Output just for info purposes
        System.out.println("Total number of records = " + rowCount);
    }

    public void runJdbcStatementDemo(String sqlStatement) throws SQLException {
        // SQL statement that modify the database
        dbLogic.jdbcStatement(sqlStatement);
    }

    public JPopupMenu createContextMenu() {
        popupMenu = new JPopupMenu();
        menuItemRemove = new JMenuItem("Remove Selected Row");

        popupMenu.add(menuItemRemove);

        return popupMenu;
    }

    public void removeSelectedRow() {
        int selectedRow = dbTable.getSelectedRow();
        int rowID = (int) dbTable.getValueAt(selectedRow, 0);

        // Remove row from DB
        try {
            runJdbcStatementDemo("DELETE FROM books WHERE id = " + "'" + rowID + "';");
            displaySqlExecInfo("Delete from DB successful!");
            // Remove row from table
            dbTableModel.removeRow(selectedRow);
        } catch (SQLException e) {
            displaySqlExecInfo("Error while deleting, please check console for more info");
            e.printStackTrace();
        }
    }

    public void addNewRow() {
        // Get user input for new row
        int id = Integer.parseInt(JOptionPane.showInputDialog(panel1, "Enter book ID:", null));
        String title = JOptionPane.showInputDialog(panel1, "Enter book title:", null);
        String author = JOptionPane.showInputDialog(panel1, "Enter book author:", null);
        double price = Double.parseDouble(JOptionPane.showInputDialog(panel1, "Enter book price:", null));
        int qty = Integer.parseInt(JOptionPane.showInputDialog(panel1, "Enter book quantity:", null));

        // Insert new book into DB
        try {
            runJdbcStatementDemo("INSERT INTO books values (" + id + ", '" + title + "', '" + author + "', " +
                    price + ", " + qty + ");");
            displaySqlExecInfo("Insert value to DB successful!");

            // Append new row to table
            Object[] row = {id, title, author, price, qty};
            dbTableModel.addRow(row);

        } catch (SQLException e) {
            e.printStackTrace();
            displaySqlExecInfo("Error while inserting, please check console for more info");
        }
    }

    public void displaySqlExecInfo(String message) {
        // Informs user if query or statement was successful or not
        sqlResult.setText(message);
    }
}

