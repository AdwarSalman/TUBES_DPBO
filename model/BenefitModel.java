package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Model yang menangani operasi CRUD (Create, Read, Update, Delete)
 * ke database SQLite.
 */
public class BenefitModel {

    public BenefitModel() {
        // Memastikan tabel database siap saat model diinisialisasi
        DBConnection.initTable();
    }

    // Mengecek apakah user sudah ada di database
    public boolean userExists(String username) {
        String sql = "SELECT username FROM tbenefit WHERE username = ?";
        try {
            Connection c = DBConnection.getConnection();
            if (c == null) return false;

            // Menggunakan PreparedStatement untuk mencegah SQL Injection
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

    // Menambahkan user baru dengan nilai default
    public void insertUser(String username) {
        // Default: Skor 0, Miss 0, Sisa Peluru 0
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

    // Menghapus data user (Fitur Delete)
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

    // Mengambil statistik pemain untuk fitur "Load Game"
    // Mengembalikan array int: [0]=skor, [1]=miss, [2]=ammo
    public int[] getUserStats(String username) {
        String sql = "SELECT skor, peluru_meleset, sisa_peluru FROM tbenefit WHERE username = ?";
        int[] stats = {0, 0, 0}; // Default fallback
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

    // Menyimpan (Overwrite) data progres permainan terakhir ke database
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

    // Mengambil semua data untuk ditampilkan di Tabel Menu
    public List<String[]> getAllUsers() {
        List<String[]> data = new ArrayList<>();
        // Mengurutkan berdasarkan skor tertinggi (Leaderboard)
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