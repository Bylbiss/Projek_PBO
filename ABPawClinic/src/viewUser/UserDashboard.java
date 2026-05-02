/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewUser;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author hp
 */
public class UserDashboard extends JFrame {
    private int idPemilik;
    private String namaPemilik;

    public UserDashboard(int idPemilik, String namaPemilik) {
        this.idPemilik = idPemilik;
        this.namaPemilik = namaPemilik;
        setTitle("Dashboard Pemilik - " + namaPemilik);
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnRegistrasi = new JButton("Registrasi Hewan Peliharaan");
        JButton btnBooking = new JButton("Booking Konsultasi Online");
        JButton btnLogout = new JButton("Logout");

        btnRegistrasi.addActionListener(e -> new RegistrasiPet(idPemilik).setVisible(true));
        btnBooking.addActionListener(e -> new BookingKonsultasi(idPemilik).setVisible(true));
        btnLogout.addActionListener(e -> {
            dispose();
            new main.LoginFrame().setVisible(true);
        });

        panel.add(btnRegistrasi);
        panel.add(btnBooking);
        panel.add(btnLogout);
        add(panel);
    }
}
