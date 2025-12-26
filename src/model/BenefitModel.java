package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BenefitModel {

    public BenefitModel() {
        DBConnection.initTable();
    }

    public boolean userExists(String username) {
        String sql = "SELECT username FROM tbenefit WHERE username = ?";
        try {
            Connection c = DBConnection.getConnection();
            if (c == null) return false;
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close(); ps.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertUser(String username) {
        String sql = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES (?, 0, 0, 0)";
        try {
            Connection c = DBConnection.getConnection();
            if (c == null) return;
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // [BARU] Hapus User
    public void deleteUser(String username) {
        String sql = "DELETE FROM tbenefit WHERE username = ?";
        try {
            Connection c = DBConnection.getConnection();
            if (c == null) return;
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] getUserStats(String username) {
        String sql = "SELECT skor, peluru_meleset, sisa_peluru FROM tbenefit WHERE username = ?";
        int[] stats = {0, 0, 0};
        try {
            Connection c = DBConnection.getConnection();
            if (c == null) return stats;
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats[0] = rs.getInt("skor");
                stats[1] = rs.getInt("peluru_meleset");
                stats[2] = rs.getInt("sisa_peluru");
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public void updateStats(String username, int totalSkor, int totalMiss, int sisaPeluru) {
        String sqlUpdate = """
                UPDATE tbenefit
                SET skor = ?,
                    peluru_meleset = ?,
                    sisa_peluru = ?
                WHERE username = ?;
                """;
        try {
            Connection c = DBConnection.getConnection();
            if (c == null) return;
            PreparedStatement ps = c.prepareStatement(sqlUpdate);
            ps.setInt(1, totalSkor);
            ps.setInt(2, totalMiss);
            ps.setInt(3, sisaPeluru);
            ps.setString(4, username);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getAllUsers() {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT * FROM tbenefit ORDER BY skor DESC";
        try {
            Connection c = DBConnection.getConnection();
            if (c == null) return data;
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("username"),
                        String.valueOf(rs.getInt("skor")),
                        String.valueOf(rs.getInt("peluru_meleset")),
                        String.valueOf(rs.getInt("sisa_peluru"))
                });
            }
            rs.close(); st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}