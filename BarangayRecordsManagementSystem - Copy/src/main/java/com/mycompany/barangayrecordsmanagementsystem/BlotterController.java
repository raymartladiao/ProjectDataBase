package com.mycompany.barangayrecordsmanagementsystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/** Connects the Blotter JFrame to the blotter_records table. */
public class BlotterController {

    private final Blotter view;
    private List<ResidentItem> residents = new ArrayList<>();

    public BlotterController(Blotter view) {
        this.view = view;
        attachListeners();
        refreshResidents(false);
        resetForm();
        loadBlotters();
    }

    private void attachListeners() {
        view.btnAdd.addActionListener(e -> addBlotter());
        view.btnUpdate.addActionListener(e -> updateBlotter());
        view.btnDelete.addActionListener(e -> deleteBlotter());
        view.btnClear.addActionListener(e -> resetForm());
        view.btnSearch.addActionListener(e -> searchBlotters());
        view.txtSearch.addActionListener(e -> searchBlotters());
        view.btnShowAll.addActionListener(e -> {
            view.txtSearch.setText("");
            loadBlotters();
        });

        view.tblBlotters.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.tblBlotters.getSelectedRow() != -1) {
                loadSelectedRow();
            }
        });

    }

    /** Reloads the Complainant drop-down. silent=true never shows an error dialog. */
    public void refreshResidents(boolean silent) {
        int previousId = selectedComplainantId();
        try {
            residents = ResidentItem.loadAll();
            view.cmbComplainant.removeAllItems();
            view.cmbComplainant.addItem("Select");
            for (ResidentItem item : residents) {
                view.cmbComplainant.addItem(item.toString());
            }
            if (previousId != -1) {
                selectComplainant(previousId);
            }
        } catch (SQLException ex) {
            if (!silent) {
                DB.showSqlError(view, ex);
            }
        }
    }

    private int selectedComplainantId() {
        int i = view.cmbComplainant.getSelectedIndex();
        return (i >= 1 && i <= residents.size()) ? residents.get(i - 1).getId() : -1;
    }

    private void selectComplainant(int residentId) {
        for (int i = 0; i < residents.size(); i++) {
            if (residents.get(i).getId() == residentId) {
                view.cmbComplainant.setSelectedIndex(i + 1);
                return;
            }
        }
    }

    private BlotterRecord readForm() {
        int complainantId = selectedComplainantId();
        if (complainantId == -1) {
            UiUtil.warn(view, "Please select the complainant. (If the list is empty, "
                    + "add the resident in the Residents window first.)", "Missing Information");
            return null;
        }

        String respondent = view.txtRespondent.getText().trim();
        String place = view.txtPlace.getText().trim();
        String details = view.txtDetails.getText().trim();
        String incidentText = view.txtIncidentDate.getText().trim();
        String filedText = view.txtDateFiled.getText().trim();

        if (respondent.isEmpty() || place.isEmpty() || details.isEmpty()
                || incidentText.isEmpty() || filedText.isEmpty()) {
            UiUtil.warn(view, "Please complete all required fields.", "Missing Information");
            return null;
        }

        LocalDate incidentDate;
        LocalDate dateFiled;
        try {
            incidentDate = LocalDate.parse(incidentText);
            dateFiled = LocalDate.parse(filedText);
        } catch (DateTimeParseException ex) {
            UiUtil.warn(view, "Enter dates in YYYY-MM-DD format (example: 2026-11-07).",
                    "Invalid Date");
            return null;
        }

        if (dateFiled.isBefore(incidentDate)) {
            UiUtil.warn(view, "Date filed cannot be earlier than the incident date.",
                    "Invalid Date");
            return null;
        }

        BlotterRecord b = new BlotterRecord();
        b.setComplainantId(complainantId);
        b.setRespondentName(respondent);
        b.setIncidentDate(incidentDate);
        b.setIncidentPlace(place);
        b.setIncidentDetails(details);
        b.setDateFiled(dateFiled);
        b.setStatus((String) view.cmbStatus.getSelectedItem());
        return b;
    }

    private void addBlotter() {
        BlotterRecord b = readForm();
        if (b == null) return;

        try {
            BlotterDAO.save(b);
            JOptionPane.showMessageDialog(view, "Blotter record added successfully!");
            resetForm();
            loadBlotters();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void updateBlotter() {
        if (view.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Select a blotter record from the table first.");
            return;
        }

        BlotterRecord b = readForm();
        if (b == null) return;
        b.setBlotterId(Integer.parseInt(view.txtId.getText()));

        try {
            BlotterDAO.update(b);
            JOptionPane.showMessageDialog(view, "Blotter record updated successfully!");
            resetForm();
            loadBlotters();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void deleteBlotter() {
        if (view.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Select a blotter record from the table first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this blotter record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        try {
            BlotterDAO.delete(Integer.parseInt(view.txtId.getText()));
            JOptionPane.showMessageDialog(view, "Blotter record deleted successfully!");
            resetForm();
            loadBlotters();
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void loadBlotters() {
        try {
            showRows(BlotterDAO.getAll());
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void searchBlotters() {
        try {
            showRows(BlotterDAO.search(view.txtSearch.getText().trim()));
        } catch (SQLException ex) {
            DB.showSqlError(view, ex);
        }
    }

    private void showRows(List<BlotterRecord> list) {
        view.tableModel.setRowCount(0);
        for (BlotterRecord b : list) {
            view.tableModel.addRow(new Object[]{
                b.getBlotterId(), b.getComplainantId(), b.getComplainantName(),
                b.getRespondentName(), b.getIncidentDate(), b.getIncidentPlace(),
                b.getIncidentDetails(), b.getDateFiled(), b.getStatus()
            });
        }
    }

    private void loadSelectedRow() {
        int row = view.tblBlotters.getSelectedRow();
        if (row < 0) return;

        view.txtId.setText(text(row, 0));
        selectComplainant(Integer.parseInt(text(row, 1)));
        view.txtRespondent.setText(text(row, 3));
        view.txtIncidentDate.setText(text(row, 4));
        view.txtPlace.setText(text(row, 5));
        view.txtDetails.setText(text(row, 6));
        view.txtDateFiled.setText(text(row, 7));
        view.cmbStatus.setSelectedItem(text(row, 8));
    }

    private String text(int row, int col) {
        Object v = view.tableModel.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    private void resetForm() {
        view.txtId.setText("");
        if (view.cmbComplainant.getItemCount() > 0) view.cmbComplainant.setSelectedIndex(0);
        view.txtRespondent.setText("");
        view.txtPlace.setText("");
        view.txtIncidentDate.setText(LocalDate.now().toString());
        view.txtDateFiled.setText(LocalDate.now().toString());
        view.cmbStatus.setSelectedIndex(0);
        view.txtDetails.setText("");
        view.tblBlotters.clearSelection();
    }
}
