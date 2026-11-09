package com.mycompany.barangayrecordsmanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    public static void save(Resident r) throws SQLException {
        String sql = "INSERT INTO residents "
                + "(first_name, middle_name, last_name, sex, birth_date, address, "
                + "contact_no, civil_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setResidentParameters(stmt, r);
            stmt.executeUpdate();
        }
    }

    public static void update(Resident r) throws SQLException {
        String sql = "UPDATE residents SET first_name=?, middle_name=?, last_name=?, sex=?, "
                + "birth_date=?, address=?, contact_no=?, civil_status=? "
                + "WHERE resident_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setResidentParameters(stmt, r);
            stmt.setInt(9, r.getResidentId());
            stmt.executeUpdate();
        }
    }

    public static void delete(int residentId) throws SQLException {
        String sql = "DELETE FROM residents WHERE resident_id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            stmt.executeUpdate();
        }
    }

    public static List<Resident> getAll() throws SQLException {
        String sql = "SELECT * FROM residents ORDER BY resident_id DESC";
        List<Resident> residents = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                residents.add(mapResident(rs));
            }
        }
        return residents;
    }

    public static List<Resident> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM residents WHERE CAST(resident_id AS CHAR) LIKE ? "
                + "OR first_name LIKE ? OR last_name LIKE ? "
                + "ORDER BY resident_id DESC";
        List<Resident> residents = new ArrayList<>();
        String value = "%" + keyword + "%";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setString(2, value);
            stmt.setString(3, value);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    residents.add(mapResident(rs));
                }
            }
        }
        return residents;
    }

    private static void setResidentParameters(PreparedStatement stmt, Resident r)
            throws SQLException {
        stmt.setString(1, r.getFirstName());
        stmt.setString(2, r.getMiddleName());      // null when left blank
        stmt.setString(3, r.getLastName());
        stmt.setString(4, r.getSex());
        stmt.setDate(5, Date.valueOf(r.getBirthDate()));
        stmt.setString(6, r.getAddress());
        stmt.setString(7, r.getContactNo());
        stmt.setString(8, r.getCivilStatus());
    }

    private static Resident mapResident(ResultSet rs) throws SQLException {
        Resident r = new Resident();
        r.setResidentId(rs.getInt("resident_id"));
        r.setFirstName(rs.getString("first_name"));
        r.setMiddleName(rs.getString("middle_name"));
        r.setLastName(rs.getString("last_name"));
        r.setSex(rs.getString("sex"));
        r.setBirthDate(rs.getDate("birth_date").toLocalDate());
        r.setAddress(rs.getString("address"));
        r.setContactNo(rs.getString("contact_no"));
        r.setCivilStatus(rs.getString("civil_status"));
        return r;
    }
}
