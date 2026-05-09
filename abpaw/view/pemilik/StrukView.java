/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view.pemilik;

import abpaw.model.entity.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StrukView extends JDialog {
    private Transaksi transaksi;
    private boolean modeLangsung; // true untuk online langsung, false untuk online nanti atau offline
    private Dokter dokter;        // hanya diperlukan untuk chat langsung
    private Pemilik pemilik;      // untuk chat langsung
    
    // Constructor untuk online langsung (butuh dokter dan pemilik)
    public StrukView(JFrame parent, Transaksi transaksi, boolean modeLangsung, Pemilik pemilik, Dokter dokter) {
        super(parent, "Struk Pemesanan", true);
        this.transaksi = transaksi;
        this.modeLangsung = modeLangsung;
        this.pemilik = pemilik;
        this.dokter = dokter;
        initComponents();
        setSize(450, 550);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    // Constructor untuk online nanti atau offline (tanpa chat)
    public StrukView(JFrame parent, Transaksi transaksi) {
        super(parent, "Struk Pemesanan", true);
        this.transaksi = transaksi;
        this.modeLangsung = false;
        this.pemilik = null;
        this.dokter = null;
        initComponents();
        setSize(450, 550);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("STRUK PEMESANAN");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(15));

        if (transaksi instanceof PemesananOnline) {
            PemesananOnline po = (PemesananOnline) transaksi;
            addRow(mainPanel, "Kode Pemesanan:", po.getKodePemesanan());
            addRow(mainPanel, "Tanggal Konsultasi:", po.getTanggalKonsultasi().toString());
            addRow(mainPanel, "Waktu Konsultasi:", po.getWaktuKonsultasi());
            addRow(mainPanel, "Biaya Konsultasi:", "Rp " + po.getBiayaKonsultasi());
            addRow(mainPanel, "Diskon:", "Rp " + po.getJumlahDiskon());
            addRow(mainPanel, "Total Bayar:", "Rp " + po.getTotalBiaya());
            addRow(mainPanel, "Status:", po.getStatus());
            addRow(mainPanel, "Waktu Pemesanan:", po.getWaktuPemesanan().toString());

            // Tambahan untuk mode "Pesan Nanti": pengingat jadwal
            if (!modeLangsung && po.getStatus().equals("menunggu")) {
                mainPanel.add(Box.createVerticalStrut(10));
                JLabel reminder = new JLabel("<html><center>Silakan chat dokter pada jadwal yang telah ditentukan:<br>" +
                        po.getTanggalKonsultasi() + " " + po.getWaktuKonsultasi() + "</center></html>");
                reminder.setForeground(Color.RED);
                reminder.setAlignmentX(Component.CENTER_ALIGNMENT);
                mainPanel.add(reminder);
            }
        } else if (transaksi instanceof PemesananOffline) {
            PemesananOffline poff = (PemesananOffline) transaksi;
            addRow(mainPanel, "Nomor Antrean:", poff.getNomorAntrean());
            addRow(mainPanel, "Tanggal Antrean:", poff.getTanggalAntrean().toString());
            addRow(mainPanel, "Waktu Antrean:", poff.getWaktuAntrean().toString());
            if (poff.getEstimasiWaktu() != null)
                addRow(mainPanel, "Estimasi Waktu:", poff.getEstimasiWaktu().toString());
            addRow(mainPanel, "Status:", poff.getStatus());
        }

        mainPanel.add(Box.createVerticalStrut(20));

        // Tombol berdasarkan mode
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnDashboard = new JButton("Kembali ke Dashboard");
        btnDashboard.addActionListener(e -> dispose());
        buttonPanel.add(btnDashboard);

        if (modeLangsung && dokter != null && pemilik != null) {
            JButton btnChat = new JButton("Chat Dokter");
            btnChat.setBackground(new Color(0, 102, 204));
            btnChat.setForeground(Color.WHITE);
            btnChat.addActionListener(e -> {
                dispose();
                new ChatDetailView(pemilik, dokter);
            });
            buttonPanel.add(btnChat);
        }

        mainPanel.add(buttonPanel);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }
    
    private void addRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.setOpaque(false);
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel lblValue = new JLabel(value);
        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(8));
    }
}