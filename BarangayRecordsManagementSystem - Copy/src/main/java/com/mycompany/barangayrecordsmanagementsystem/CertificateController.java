package com.mycompany.barangayrecordsmanagementsystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/** Connects the Certificate JFrame to the certificate_requests table. */
public class CertificateController {

    private final Certificate view;
    private List<ResidentItem> residents = new ArrayList<>();

    public CertificateController(Certificate view) {
        this.view = view;
        attachListeners();
        refreshResidents(false);
        resetForm();
        loadRequests();
    }

    private void attachListeners() {
        view.btnAdd.addActionListener(e -> addRequest());
        view.btnUpdate.addActionListener(e -> updateRequest());
        view.btnDelete.addActionListener(e -> deleteRequest());
        view.btnClear.addActionListener(e -> resetForm());
        view.btnSearch.addActionListener(e -> searchRequests());
        view.txtSearch.addActionListener(e -> searchRequests());
        view.btnShowAll.addActionListener(e -> {
            view.txtSearch.setText("");
            loadRequests();
        });

        view.tblRequests.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.tblRequests.getSelectedRow() != -1) {
                loadSelectedRow();
            }
        });

    }

    /** Reloads the Resident drop-down. silent=true never shows an error dialog. */
    public void refreshResidents(boolean silent) {
        int previousId = selectedResidentId();
        try {
            residents = ResidentItem.loadAll();
            view.cmbResident.removeAllItems();
            view.cmbResident.addItem("Select");
            for (ResidentItem item : residents) {
                view.cmbResident.addItem(item.toString());
            }
            if (previousId != -1) {
                selectResident(previousId);
            }
        } catch (SQLException ex) {
            if (!silent) {
                DB.showSqlError(view, ex);
            }
        }
    }

    private int selectedResidentId() {
        int i = view.cmbResident.getSelectedIndex();
        return (i >= 1 && i <= residents.size()) ? residents.get(i - 1).getId() : -1;
    }

    private void selectResident(int residentId) {
        for (int i = 0; i < residents.size(); i++) {
            if (residents.get(i).getId() == residentId) {
                view.cmbResident.setSelectedIndex(i + 1);
                return;
            }
        }
    }

    private CertificateRequest readForm() {
        int residentId = selectedResidentId();
        if (residentId == -1) {
            UiUtil.warn(view, "Please select a resident. (If the list is empty, "
                    + "add the resident in the Residents window first.)", "Missing Information");
            return null;
        }

        String purpose = view.txtPurpose.getText().trim();
        String requestedText = view.txtDateRequested.getText().trim();
        String issuedText = view.txtDateIssued.getText().trim();

        String type = (String) view.cmbType.getSelectedItem();
        String voter = (String) view.cmbVoter.getSelectedItem();
        String electorate = (String) view.cmbElectorate.getSelectedItem();

        if (purpose.isEmpty() || requestedText.isEmpty() || "Select".equals(type)
                || "Select".equals(voter) || "Select".equals(electorate)) {
            UiUtil.warn(view, "Please complete all required fields.", "Missing Information");
            return null;
        }

        LocalDate requested;
        LocalDate issued = null;
        try {
            requested = LocalDate.parse(requestedText);
            if (!issuedText.isEmpty()) {
                issued = LocalDate.parse(issuedText);
            }
        } catch (DateTimeParseException ex) {
            UiUtil.warn(view, "Enter dates in YYYY-MM-DD format (example: 2026-11-07).",
                    "Invalid Date");
            return null;
        }

        if (issued != null && issued.isBefore(requested)) {
            UiUtil.warn(view, "Date issued cannot be earlier than the date requested.",
                    "Invalid Date");
            return null;
        }

        CertificateRequest c = new CertificateRequest();
        c.setResidentId(residentId);
        c.setCertificateType(type);
        c.setPurpose(purpose);
        c.setDateRequested(requested);
        c.setDateIssued(issued);
        c.setBarangayVoter(voter);
        c.setElectorate(electorate);
        return c;
    }

    private void addRequest() {
        CertificateRequest c = readForm();
        if (c == null) return;

        try {
            CertificateDAO.save(c);
            JOptionPane.showMessageDialog(view, "Certificate request added successfully!");
            resetForm();
            loadRequests();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void updateRequest() {
        if (view.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Select a request from the table first.");
            return;
        }

        CertificateRequest c = readForm();
        if (c == null) return;
        c.setRequestId(Integer.parseInt(view.txtId.getText()));

        try {
            CertificateDAO.update(c);
            JOptionPane.showMessageDialog(view, "Certificate request updated successfully!");
            resetForm();
            loadRequests();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void deleteRequest() {
        if (view.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Select a request from the table first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this certificate request?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        try {
            CertificateDAO.delete(Integer.parseInt(view.txtId.getText()));
            JOptionPane.showMessageDialog(view, "Certificate request deleted successfully!");
            resetForm();
            loadRequests();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void loadRequests() {
        try {
            showRows(CertificateDAO.getAll());
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void searchRequests() {
        try {
            showRows(CertificateDAO.search(view.txtSearch.getText().trim()));
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void showRows(List<CertificateRequest> list) {
        view.tableModel.setRowCount(0);
        for (CertificateRequest c : list) {
            view.tableModel.addRow(new Object[]{
                c.getRequestId(), c.getResidentId(), c.getResidentName(),
                c.getCertificateType(), c.getPurpose(), c.getDateRequested(),
                c.getDateIssued() == null ? "" : c.getDateIssued(),
                c.getBarangayVoter(), c.getElectorate()
            });
        }
    }

    private void loadSelectedRow() {
        int row = view.tblRequests.getSelectedRow();
        if (row < 0) return;

        view.txtId.setText(text(row, 0));
        selectResident(Integer.parseInt(text(row, 1)));
        view.cmbType.setSelectedItem(text(row, 3));
        view.txtPurpose.setText(text(row, 4));
        view.txtDateRequested.setText(text(row, 5));
        view.txtDateIssued.setText(text(row, 6));
        view.cmbVoter.setSelectedItem(text(row, 7));
        view.cmbElectorate.setSelectedItem(text(row, 8));
    }

    private String text(int row, int col) {
        Object v = view.tableModel.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    private void resetForm() {
        view.txtId.setText("");
        if (view.cmbResident.getItemCount() > 0) view.cmbResident.setSelectedIndex(0);
        view.cmbType.setSelectedIndex(0);
        view.txtPurpose.setText("");
        view.txtDateRequested.setText(LocalDate.now().toString());
        view.txtDateIssued.setText("");
        view.cmbVoter.setSelectedIndex(0);
        view.cmbElectorate.setSelectedIndex(0);
        view.tblRequests.clearSelection();
    }
}
