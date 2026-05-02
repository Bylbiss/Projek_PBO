package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static Connection koneksi;
    
    public static Connection getConnection() {
        if (koneksi == null) {
            try {
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                String url = "jdbc:mysql://localhost:3306/drh_part2";
                
                String user = "root";
                String password = "";
                
                koneksi = DriverManager.getConnection(url, user, password);
                
                System.out.println("Koneksi ke database Berhasil!");
            } catch (SQLException e) {
                System.out.println("Koneksi Gagal: " + e.getMessage());
            }
        }
        return koneksi;
    }
    
    public static void closeConnection() {
        if (koneksi != null) {
            try {
                koneksi.close();
                koneksi = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}