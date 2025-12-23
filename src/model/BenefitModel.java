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
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            boolean exists = rs.next();
            rs.close();
            ps.close();
            return exists;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertUser(String username) {
        String sql = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES (?, 0, 0, 0)";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLastAmmo(String username) {
        String sql = "SELECT sisa_peluru FROM tbenefit WHERE username = ?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            int result = rs.next() ? rs.getInt("sisa_peluru") : 0;

            rs.close();
            ps.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void updateStats(String username, int skorRound, int missRound, int sisaPeluru) {
        // Ambil data lama (skor & miss)
        String sqlSelect = "SELECT skor, peluru_meleset FROM tbenefit WHERE username = ?";

        int skor = 0;
        int miss = 0;

        try {
            PreparedStatement psSel = DBConnection.getConnection().prepareStatement(sqlSelect);
            psSel.setString(1, username);
            ResultSet rs = psSel.executeQuery();

            if (rs.next()) {
                skor = rs.getInt("skor");
                miss = rs.getInt("peluru_meleset");
            }

            rs.close();
            psSel.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update nilai baru
        String sqlUpdate = """
                UPDATE tbenefit
                SET skor = ?,
                    peluru_meleset = ?,
                    sisa_peluru = ?
                WHERE username = ?;
                """;

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sqlUpdate);
            ps.setInt(1, skor + skorRound);
            ps.setInt(2, miss + missRound);
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

        String sql = "SELECT * FROM tbenefit";
        try {
            Statement st = DBConnection.getConnection().createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("username"),
                        String.valueOf(rs.getInt("skor")),
                        String.valueOf(rs.getInt("peluru_meleset")),
                        String.valueOf(rs.getInt("sisa_peluru"))
                });
            }

            rs.close();
            st.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
