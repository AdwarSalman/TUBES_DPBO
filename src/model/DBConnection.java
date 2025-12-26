package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    private static Connection conn;

    public static Connection getConnection() {
        try {
            // Cek apakah koneksi null ATAU sudah tertutup
            if (conn == null || conn.isClosed()) {
                // 1. Load driver SQLite
                Class.forName("org.sqlite.JDBC");

                // 2. Buat koneksi ke file database
                conn = DriverManager.getConnection("jdbc:sqlite:tbenefit.db");
            }
        } catch (Exception e) {
            System.err.println("GAGAL KONEKSI DATABASE:");
            e.printStackTrace();
        }
        return conn;
    }

    public static void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tbenefit (
                    username TEXT PRIMARY KEY,
                    skor INTEGER,
                    peluru_meleset INTEGER,
                    sisa_peluru INTEGER
                );
                """;

        try {
            Statement st = getConnection().createStatement();
            st.execute(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
