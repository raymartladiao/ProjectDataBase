package com.mycompany.barangayrecordsmanagementsystem;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DB {

    private static final String URL =
            "jdbc:mysql://localhost:3306/barangay_records"
            + "?useSSL=false&serverTimezone=Asia/Manila&allowPublicKeyRetrieval=true";

    // Change these to match your own MySQL account.
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Raymart112006@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    public static void showConnectionError(Exception ex) {
        JOptionPane.showMessageDialog(
                null,
                "Could not connect to MySQL. Check that MySQL Server is running, "
                + "the database exists, and DB_USER / DB_PASSWORD are correct.\n\n"
                + ex.getMessage(),
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /** Shows a message that matches the kind of SQL problem that happened. */
    public static void showSqlError(Component parent, SQLException ex) {
        String state = ex.getSQLState() == null ? "" : ex.getSQLState();
        String message;

        if (state.startsWith("08")) {
            message = "Could not connect to MySQL. Check that MySQL Server is running "
                    + "and the port in DB.java is correct.";
        } else if (state.equals("28000")) {
            message = "MySQL refused the login. Check DB_USER and DB_PASSWORD in DB.java.";
        } else if (ex.getErrorCode() == 1049) {
            message = "The database 'barangay_records' does not exist. "
                    + "Run database.sql in MySQL Workbench first.";
        } else if (ex.getErrorCode() == 1146) {
            message = "A table is missing. Run database.sql in MySQL Workbench first.";
        } else if (ex.getErrorCode() == 1452) {
            message = "That resident does not exist. Pick a resident from the list.";
        } else {
            message = "Database error.";
        }

        JOptionPane.showMessageDialog(parent, message + "\n\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
