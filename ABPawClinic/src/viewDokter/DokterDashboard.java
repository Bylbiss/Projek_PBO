/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewDokter;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author hp
 */
public class DokterDashboard extends JFrame {
    private int idDokter;
    private String namaDokter;

    public DokterDashboard(int idDokter, String namaDokter) {
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
        setTitle("Dashboard Dokter - " + namaDokter);
        setSize(450, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnAntrean = new JButton("Daftar Antrean & Periksa");
        JButton btnResep = new JButton("Input Resep Obat");
        JButton btnLogout = new JButton("Logout");

        btnAntrean.addActionListener(e -> new DaftarAntrean(idDokter).setVisible(true));
        btnResep.addActionListener(e -> new InputResep(idDokter).setVisible(true));
        btnLogout.addActionListener(e -> {
            dispose();
            new abpaw.main.LoginFrame().setVisible(true);
        });

        panel.add(btnAntrean);
        panel.add(btnResep);
        panel.add(btnLogout);
        add(panel);
    }
}
