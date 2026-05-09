/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view;

import abpaw.view.components.ImagePanel;
import abpaw.view.components.RoundedPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LandingPageView extends JFrame {
    private JButton btnLogin;
    private JButton btnRegister;

    public LandingPageView() {
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Selamat Datang di AB Paw Klinik Hewan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        // Panel utama dengan background (optional)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header panel untuk logo dan tombol
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo kiri
        JLabel logoLabel = new JLabel("AB Paw");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // Tombol kanan
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        btnLogin = new JButton("Masuk");
        btnRegister = new JButton("Daftar");
        styleButton(btnLogin, new Color(0, 102, 204));
        styleButton(btnRegister, new Color(34, 139, 34));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Konten tengah (sambutan)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Selamat Datang di Klinik Hewan AB Paw");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0, 51, 102));
        JLabel subLabel = new JLabel("Kesehatan Hewan Kesayangan Anda Adalah Prioritas Kami");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(welcomeLabel);
        centerPanel.add(subLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Event listeners
        btnLogin.addActionListener(e -> {
            dispose();
            new LoginView();
        });
        btnRegister.addActionListener(e -> {
            dispose();
            new RegisterView();
        });
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}