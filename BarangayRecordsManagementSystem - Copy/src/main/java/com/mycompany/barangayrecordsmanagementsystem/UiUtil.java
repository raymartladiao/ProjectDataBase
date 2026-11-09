package com.mycompany.barangayrecordsmanagementsystem;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/** Small helpers shared by the three screens. */
public class UiUtil {

    /** Table model whose cells cannot be edited by typing in the table. */
    public static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void warn(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
}
