
package com.mycompany.barangayrecordsmanagementsystem;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BarangayRecordsManagementSystem {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Set Nimbus Look and Feel
            try {
                for (UIManager.LookAndFeelInfo info :
                        UIManager.getInstalledLookAndFeels()) {

                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println("Look and Feel Error: " + e.getMessage());
            }

            // Open Residents JFrame
            ResidentView resident = new ResidentView();

            resident.setTitle("Barangay Records Management System");
            resident.setLocationRelativeTo(null);
            resident.setVisible(true);

        });
    }
}
