package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    private static Connection conn;

    public static Connection getConnection() {
        // Menggunakan Singleton Pattern untuk koneksi database
        // agar efisien dan tidak membuka banyak koneksi secara berulang.
        try {
            if (conn == null || conn.isClosed()) {
                // Memuat driver JDBC SQLite
                Class.forName("org.sqlite.JDBC");

                // Menggunakan SQLite agar database bersifat portable (file-based)
                // sehingga aplikasi dapat dijalankan di mana saja tanpa setup server database rumit.
                conn = DriverManager.getConnection("jdbc:sqlite:tbenefit.db");
            }
        } catch (Exception e) {
            System.err.println("Gagal koneksi database: " + e.getMessage());
        }
        return conn;
    }

    public static void initTable() {
        // DDL (Data Definition Language) otomatis.
        // Memastikan tabel 'tbenefit' selalu tersedia saat aplikasi pertama kali dijalankan.
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