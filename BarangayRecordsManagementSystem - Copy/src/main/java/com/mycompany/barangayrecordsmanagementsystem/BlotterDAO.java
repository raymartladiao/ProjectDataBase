package com.mycompany.barangayrecordsmanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Database access for the blotter_records table. */
public class BlotterDAO {

    // JOIN with residents so the table can show the complainant's name.
    private static final String SELECT_BASE =
            "SELECT b.*, CONCAT(r.last_name, ', ', r.first_name, "
            + "IFNULL(CONCAT(' ', NULLIF(r.middle_name, '')), '')) AS complainant_name "
            + "FROM blotter_records b "
            + "JOIN residents r ON r.resident_id = b.complainant_id ";

    public static void save(BlotterRecord b) throws SQLException {
        String sql = "INSERT INTO blotter_records "
                + "(complainant_id, respondent_name, incident_date, incident_place, "
                + "incident_details, date_filed, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, b);
            stmt.executeUpdate();
        }
    }

    public static void update(BlotterRecord b) throws SQLException {
        String sql = "UPDATE blotter_records SET complainant_id=?, respondent_name=?, "
                + "incident_date=?, incident_place=?, incident_details=?, "
                + "date_filed=?, status=? WHERE blotter_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, b);
            stmt.setInt(8, b.getBlotterId());
            stmt.executeUpdate();
        }
    }

    public static void delete(int blotterId) throws SQLException {
        String sql = "DELETE FROM blotter_records WHERE blotter_id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blotterId);
            stmt.executeUpdate();
        }
    }

    public static List<BlotterRecord> getAll() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY b.blotter_id DESC";
        List<BlotterRecord> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public static List<BlotterRecord> search(String keyword) throws SQLException {
        String sql = SELECT_BASE
                + "WHERE CAST(b.blotter_id AS CHAR) LIKE ? "
                + "OR r.first_name LIKE ? OR r.last_name LIKE ? "
                + "OR b.respondent_name LIKE ? OR b.status LIKE ? "
                + "ORDER BY b.blotter_id DESC";
        List<BlotterRecord> list = new ArrayList<>();
        String value = "%" + keyword + "%";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, value);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private static void setParameters(PreparedStatement stmt, BlotterRecord b)
            throws SQLException {
        stmt.setInt(1, b.getComplainantId());
        stmt.setString(2, b.getRespondentName());
        stmt.setDate(3, Date.valueOf(b.getIncidentDate()));
        stmt.setString(4, b.getIncidentPlace());
        stmt.setString(5, b.getIncidentDetails());
        stmt.setDate(6, Date.valueOf(b.getDateFiled()));
        stmt.setString(7, b.getStatus());
    }

    private static BlotterRecord map(ResultSet rs) throws SQLException {
        BlotterRecord b = new BlotterRecord();
        b.setBlotterId(rs.getInt("blotter_id"));
        b.setComplainantId(rs.getInt("complainant_id"));
        b.setComplainantName(rs.getString("complainant_name"));
        b.setRespondentName(rs.getString("respondent_name"));
        b.setIncidentDate(rs.getDate("incident_date").toLocalDate());
        b.setIncidentPlace(rs.getString("incident_place"));
        b.setIncidentDetails(rs.getString("incident_details"));
        b.setDateFiled(rs.getDate("date_filed").toLocalDate());
        b.setStatus(rs.getString("status"));
        return b;
    }
}
