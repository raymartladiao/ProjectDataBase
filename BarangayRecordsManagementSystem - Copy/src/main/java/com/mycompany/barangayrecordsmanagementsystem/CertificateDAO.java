package com.mycompany.barangayrecordsmanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Database access for the certificate_requests table. */
public class CertificateDAO {

    // JOIN with residents so the table can show the resident's name.
    private static final String SELECT_BASE =
            "SELECT c.*, CONCAT(r.last_name, ', ', r.first_name, "
            + "IFNULL(CONCAT(' ', NULLIF(r.middle_name, '')), '')) AS resident_name "
            + "FROM certificate_requests c "
            + "JOIN residents r ON r.resident_id = c.resident_id ";

    public static void save(CertificateRequest c) throws SQLException {
        String sql = "INSERT INTO certificate_requests "
                + "(resident_id, certificate_type, purpose, date_requested, "
                + "date_issued, barangay_voter, electorate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, c);
            stmt.executeUpdate();
        }
    }

    public static void update(CertificateRequest c) throws SQLException {
        String sql = "UPDATE certificate_requests SET resident_id=?, certificate_type=?, "
                + "purpose=?, date_requested=?, date_issued=?, barangay_voter=?, "
                + "electorate=? WHERE request_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, c);
            stmt.setInt(8, c.getRequestId());
            stmt.executeUpdate();
        }
    }

    public static void delete(int requestId) throws SQLException {
        String sql = "DELETE FROM certificate_requests WHERE request_id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            stmt.executeUpdate();
        }
    }

    public static List<CertificateRequest> getAll() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY c.request_id DESC";
        List<CertificateRequest> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public static List<CertificateRequest> search(String keyword) throws SQLException {
        String sql = SELECT_BASE
                + "WHERE CAST(c.request_id AS CHAR) LIKE ? "
                + "OR r.first_name LIKE ? OR r.last_name LIKE ? "
                + "OR c.certificate_type LIKE ? OR c.status LIKE ? "
                + "ORDER BY c.request_id DESC";
        List<CertificateRequest> list = new ArrayList<>();
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

    private static void setParameters(PreparedStatement stmt, CertificateRequest c)
            throws SQLException {
        stmt.setInt(1, c.getResidentId());
        stmt.setString(2, c.getCertificateType());
        stmt.setString(3, c.getPurpose());
        stmt.setDate(4, Date.valueOf(c.getDateRequested()));
        if (c.getDateIssued() == null) {
            stmt.setNull(5, Types.DATE);
        } else {
            stmt.setDate(5, Date.valueOf(c.getDateIssued()));
        }
        stmt.setString(6, c.getBarangayVoter());
        stmt.setString(7, c.getElectorate());
    }

    private static CertificateRequest map(ResultSet rs) throws SQLException {
        CertificateRequest c = new CertificateRequest();
        c.setRequestId(rs.getInt("request_id"));
        c.setResidentId(rs.getInt("resident_id"));
        c.setResidentName(rs.getString("resident_name"));
        c.setCertificateType(rs.getString("certificate_type"));
        c.setPurpose(rs.getString("purpose"));
        c.setDateRequested(rs.getDate("date_requested").toLocalDate());
        Date issued = rs.getDate("date_issued");
        c.setDateIssued(issued == null ? null : issued.toLocalDate());
        c.setBarangayVoter(rs.getString("barangay_voter"));
        c.setElectorate(rs.getString("electorate"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
