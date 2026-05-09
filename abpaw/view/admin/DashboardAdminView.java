/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view.admin;

import abpaw.controller.AdminController;
import abpaw.model.entity.Admin;
import abpaw.view.components.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardAdminView extends JFrame {
    private Admin admin;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton btnDokter, btnPemilik, btnKupon, btnLaporan, btnProfil;
    private AdminController adminController;

    public DashboardAdminView(Admin admin) {
        this.admin = admin;
        this.adminController = new AdminController();
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Dashboard Admin - AB Paw Klinik Hewan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel logoLabel = new JLabel("AB Paw - Admin Panel");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.WEST);
        JLabel userLabel = new JLabel("Admin: " + admin.getUsername());
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Sidebar kiri (dengan submenu expand/collapse)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 248, 255));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));

        // Menu Data Master (expandable)
        JButton masterBtn = createSidebarButton("DATA MASTER", true);
        JPanel subMasterPanel = new JPanel();
        subMasterPanel.setLayout(new BoxLayout(subMasterPanel, BoxLayout.Y_AXIS));
        subMasterPanel.setBackground(new Color(240, 248, 255));
        subMasterPanel.setVisible(false);
        btnDokter = createSubMenuButton("   • Manajemen Dokter");
        btnPemilik = createSubMenuButton("   • Manajemen Pemilik");
        subMasterPanel.add(btnDokter);
        subMasterPanel.add(Box.createVerticalStrut(5));
        subMasterPanel.add(btnPemilik);
        masterBtn.addActionListener(e -> subMasterPanel.setVisible(!subMasterPanel.isVisible()));

        // Menu Diskon
        btnKupon = createSidebarButton("DISKON & KUPON", false);
        // Menu Laporan
        btnLaporan = createSidebarButton("LAPORAN", false);
        // Menu Profil
        btnProfil = createSidebarButton("PROFIL ADMIN", false);

        sidebar.add(masterBtn);
        sidebar.add(subMasterPanel);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnKupon);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnLaporan);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnProfil);
        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        // Content panel dengan CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Tambahkan panel
        contentPanel.add(new ManageDokterView(), "dokter");
        contentPanel.add(new ManagePemilikView(), "pemilik");
        contentPanel.add(new ManageKuponView(), "kupon");
        contentPanel.add(new LaporanView(), "laporan");
        contentPanel.add(createProfilPanel(), "profil");

        add(contentPanel, BorderLayout.CENTER);

        // Event menu
        btnDokter.addActionListener(e -> cardLayout.show(contentPanel, "dokter"));
        btnPemilik.addActionListener(e -> cardLayout.show(contentPanel, "pemilik"));
        btnKupon.addActionListener(e -> cardLayout.show(contentPanel, "kupon"));
        btnLaporan.addActionListener(e -> cardLayout.show(contentPanel, "laporan"));
        btnProfil.addActionListener(e -> cardLayout.show(contentPanel, "profil"));

        // Tampilkan default
        cardLayout.show(contentPanel, "dokter");
    }

    private JButton createSidebarButton(String text, boolean isBold) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", isBold ? Font.BOLD : Font.PLAIN, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 80, 160));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 102, 204));
            }
        });
        return btn;
    }

    private JButton createSubMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 30));
        btn.setBackground(new Color(240, 248, 255));
        btn.setForeground(new Color(0, 51, 102));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 220, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(240, 248, 255));
            }
        });
        return btn;
    }

    private JPanel createProfilPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID Admin:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(admin.getIdAdmin())), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(admin.getUsername()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(admin.getNama()), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(admin.getEmail()), gbc);

        JButton gantiPassword = new JButton("Ganti Password");
        gantiPassword.addActionListener(e -> {
            String newPass = JOptionPane.showInputDialog(this, "Masukkan password baru:");
            if (newPass != null && !newPass.trim().isEmpty()) {
                boolean success = adminController.updatePassword(admin.getIdAdmin(), newPass);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Password berhasil diubah.");
                    admin.setPassword(newPass);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengubah password.");
                }
            }
        });
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(gantiPassword, gbc);

        return panel;
    }
}