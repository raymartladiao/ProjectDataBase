package com.mycompany.barangayrecordsmanagementsystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;

/** Connects the ResidentView JFrame to the residents table. */
public class ResidentController {

    private final ResidentView view;

    public ResidentController(ResidentView view) {
        this.view = view;
        attachListeners();
        loadResidents();
    }

    private void attachListeners() {
        view.btnAdd.addActionListener(e -> addResident());
        view.btnUpdate.addActionListener(e -> updateResident());
        view.btnDelete.addActionListener(e -> deleteResident());
        view.btnClear.addActionListener(e -> view.clearFields());
        view.btnSearch.addActionListener(e -> searchResidents());
        view.btnShowAll.addActionListener(e -> {
            view.txtSearch.setText("");
            loadResidents();
        });
        view.txtSearch.addActionListener(e -> searchResidents());

        view.tblResidents.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.tblResidents.getSelectedRow() != -1) {
                loadSelectedResident();
            }
        });
    }

    private Resident readForm() {
        String first = view.txtFirstName.getText().trim();
        String middle = view.txtMiddleName.getText().trim();   // optional
        String last = view.txtLastName.getText().trim();
        String sex = (String) view.cmbSex.getSelectedItem();
        String birth = view.txtBirthDate.getText().trim();
        String address = view.txtAddress.getText().trim();
        String contact = view.txtContact.getText().trim();
        String civil = (String) view.cmbCivilStatus.getSelectedItem();

        if (first.isEmpty() || last.isEmpty() || address.isEmpty()
                || birth.isEmpty() || "Select".equals(sex) || "Select".equals(civil)) {
            UiUtil.warn(view, "Please complete all required fields.", "Missing Information");
            return null;
        }

        try {
            Resident r = new Resident();
            r.setFirstName(first);
            r.setMiddleName(middle.isEmpty() ? null : middle);
            r.setLastName(last);
            r.setSex(sex);
            r.setBirthDate(LocalDate.parse(birth));
            r.setAddress(address);
            r.setContactNo(contact);
            r.setCivilStatus(civil);
            return r;
        } catch (java.time.format.DateTimeParseException ex) {
            UiUtil.warn(view, "Enter the birth date in YYYY-MM-DD format (example: 2002-05-18).",
                    "Invalid Date");
            return null;
        }
    }

    private void addResident() {
        Resident r = readForm();
        if (r == null) return;

        try {
            DAO.save(r);
            JOptionPane.showMessageDialog(view, "Resident added successfully!");
            view.clearFields();
            loadResidents();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void updateResident() {
        if (view.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Select a resident from the table first.");
            return;
        }

        Resident r = readForm();
        if (r == null) return;
        r.setResidentId(Integer.parseInt(view.txtId.getText()));

        try {
            DAO.update(r);
            JOptionPane.showMessageDialog(view, "Resident updated successfully!");
            view.clearFields();
            loadResidents();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void deleteResident() {
        if (view.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Select a resident from the table first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this resident record?\n"
                + "Their certificate requests and blotter records will be deleted too.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        try {
            DAO.delete(Integer.parseInt(view.txtId.getText()));
            JOptionPane.showMessageDialog(view, "Resident deleted successfully!");
            view.clearFields();
            loadResidents();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void loadResidents() {
        try {
            displayRows(DAO.getAll());
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void searchResidents() {
        try {
            displayRows(DAO.search(view.txtSearch.getText().trim()));
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void displayRows(List<Resident> residents) {
        view.tableModel.setRowCount(0);
        for (Resident r : residents) {
            view.tableModel.addRow(new Object[]{
                r.getResidentId(), r.getFirstName(),
                r.getMiddleName() == null ? "" : r.getMiddleName(),
                r.getLastName(), r.getSex(), r.getBirthDate(), r.getAddress(),
                r.getContactNo() == null ? "" : r.getContactNo(), r.getCivilStatus()
            });
        }
    }

    private void loadSelectedResident() {
        int row = view.tblResidents.getSelectedRow();
        if (row < 0) return;

        view.txtId.setText(text(row, 0));
        view.txtFirstName.setText(text(row, 1));
        view.txtMiddleName.setText(text(row, 2));
        view.txtLastName.setText(text(row, 3));
        view.cmbSex.setSelectedItem(text(row, 4));
        view.txtBirthDate.setText(text(row, 5));
        view.txtAddress.setText(text(row, 6));
        view.txtContact.setText(text(row, 7));
        view.cmbCivilStatus.setSelectedItem(text(row, 8));
    }

    private String text(int row, int col) {
        Object v = view.tableModel.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }
}
