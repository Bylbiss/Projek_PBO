/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import config.Koneksi;
import viewAdmin.AdminDashboard;
import viewDokter.DokterDashboard;
import viewUser.UserDashboard;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author hp
 */
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    
    public LoginFrame() {
        initComponents();
        setTitle("Login - AB Paw Clinic");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void initComponents() {
        // Panel utama dengan background putih
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Bagian atas: logo atau judul
        JLabel lblTitle = new JLabel("AB PAW CLINIC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username / Email:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtPassword, gbc);

        // Status label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);
        formPanel.add(lblStatus, gbc);

        // Tombol Login
        gbc.gridy = 3;
        btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(btnLogin, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JLabel lblFooter = new JLabel("© 2025 AB Paw Clinic - All Rights Reserved", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFooter.setForeground(Color.GRAY);
        mainPanel.add(lblFooter, BorderLayout.SOUTH);

        add(mainPanel);

        // Event Login
        btnLogin.addActionListener(e -> login());
        
        // Tekan Enter untuk login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void login() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            lblStatus.setText("Username dan password harus diisi!");
            return;
        }

        lblStatus.setText("Memproses...");
        btnLogin.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String role = "";
            private int idUser = -1;
            private String nama = "";

            @Override
            protected Boolean doInBackground() {
                try (Connection conn = Koneksi.getConnection()) {
                    if (conn == null) return false;

                    // 1. Cek di tabel admin
                    String sqlAdmin = "SELECT id_admin, nama FROM admin WHERE username=? AND password=?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
                        ps.setString(1, user);
                        ps.setString(2, pass);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            role = "admin";
                            idUser = rs.getInt("id_admin");
                            nama = rs.getString("nama");
                            return true;
                        }
                    }

                    // 2. Cek di tabel pemilik (gunakan email sebagai username)
                    String sqlPemilik = "SELECT id_pemilik, nama_pemilik FROM pemilik WHERE email=? AND password=?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlPemilik)) {
                        ps.setString(1, user);
                        ps.setString(2, pass);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            role = "pemilik";
                            idUser = rs.getInt("id_pemilik");
                            nama = rs.getString("nama_pemilik");
                            return true;
                        }
                    }

                    // 3. Cek di tabel dokter
                    //    Asumsi: nama_dokter digunakan sebagai username, 
                    //    dan password disimpan di kolom tersendiri (jika tidak ada, buat dulu)
                    //    Jika tabel dokter tidak punya password, kita bisa bypass atau tambah kolom.
                    //    Saya akan asumsikan ada kolom `password` di tabel dokter (anda bisa tambah via phpMyAdmin)
                    //    Alternatif: login dokter hanya dengan nama_dokter tanpa password (tidak aman).
                    //    Saya sarankan tambah kolom password.
                    String sqlDokter = "SELECT id_dokter, nama_dokter FROM dokter WHERE nama_dokter=? AND password=?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDokter)) {
                        ps.setString(1, user);
                        ps.setString(2, pass);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            role = "dokter";
                            idUser = rs.getInt("id_dokter");
                            nama = rs.getString("nama_dokter");
                            return true;
                        }
                    }

                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    boolean success = get();
                    if (success) {
                        lblStatus.setText("");
                        JOptionPane.showMessageDialog(LoginFrame.this, 
                            "Selamat datang, " + nama + "!\nRole: " + role, 
                            "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // tutup login
                        // Buka dashboard sesuai role
                        switch (role) {
                            case "admin":
                                new AdminDashboard().setVisible(true);
                                break;
                            case "dokter":
                                new DokterDashboard(idUser, nama).setVisible(true);
                                break;
                            case "pemilik":
                                new UserDashboard(idUser, nama).setVisible(true);
                                break;
                        }
                    } else {
                        lblStatus.setText("Username/Password salah!");
                    }
                } catch (Exception ex) {
                    lblStatus.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}