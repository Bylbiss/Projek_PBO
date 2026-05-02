/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewAdmin;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author hp
 */
public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard - AB Paw Clinic");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initMenu();
    }

    private void initMenu() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnDokter = new JButton("Kelola Data Dokter");
        JButton btnObat = new JButton("Kelola Data Obat");
        JButton btnLaporan = new JButton("Laporan Transaksi");
        JButton btnLogout = new JButton("Logout");

        btnDokter.addActionListener(e -> new KelolaDokter().setVisible(true));
        btnObat.addActionListener(e -> new KelolaObat().setVisible(true));
        btnLaporan.addActionListener(e -> new LaporanTransaksi().setVisible(true));
        btnLogout.addActionListener(e -> {
            dispose();
            new main.LoginFrame().setVisible(true);
        });

        panel.add(btnDokter);
        panel.add(btnObat);
        panel.add(btnLaporan);
        panel.add(btnLogout);

        add(panel);
    }
}
