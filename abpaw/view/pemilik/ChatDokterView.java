/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view.pemilik;

import abpaw.controller.DokterController;
import abpaw.view.pemilik.PemesananOnlineView;
import abpaw.model.entity.Dokter;
import abpaw.model.entity.Pemilik;
import abpaw.view.components.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ChatDokterView extends JPanel {
    private Pemilik pemilik;
    private JPanel spesialisasiPanel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JPanel dokterListPanel;
    private JScrollPane scrollPane;
    private DokterController dokterController;
    private String selectedSpesialisasi = "Semua";

    public ChatDokterView(Pemilik pemilik) {
        this.pemilik = pemilik;
        dokterController = new DokterController();
        initComponents();
        loadSpesialisasi();
        loadDokter();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Panel atas: spesialisasi dan pencarian
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Pilih Dokter Hewan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Spesialisasi panel dengan tombol-tombol
        spesialisasiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        spesialisasiPanel.setBackground(Color.WHITE);
        JScrollPane spesialisasiScroll = new JScrollPane(spesialisasiPanel);
        spesialisasiScroll.setBorder(null);
        spesialisasiScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spesialisasiScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        topPanel.add(spesialisasiScroll, BorderLayout.CENTER);

        // Pencarian dan filter
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField(20);
        searchField.setToolTipText("Cari nama dokter...");
        JButton searchBtn = new JButton("Cari");
        filterCombo = new JComboBox<>(new String[]{"Rating Tertinggi", "Harga Termurah", "Harga Termahal", "Sesuai Hewan Saya"});
        filterCombo.addActionListener(e -> loadDokter());
        searchBtn.addActionListener(e -> loadDokter());
        searchPanel.add(new JLabel("Cari:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterCombo);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // Panel daftar dokter
        dokterListPanel = new JPanel();
        dokterListPanel.setLayout(new BoxLayout(dokterListPanel, BoxLayout.Y_AXIS));
        dokterListPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(dokterListPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Dokter"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadSpesialisasi() {
        spesialisasiPanel.removeAll();
        String[] spesialisasiList = {"Semua", "Dokter Umum", "Spesialis Penyakit Dalam", "Spesialis Gigi",
                "Spesialis Kulit", "Spesialis Reproduksi", "Spesialis Parasitologi & Infeksi", "Spesialis Bedah"};
        for (String spes : spesialisasiList) {
            JButton btn = new JButton(spes);
            btn.setBackground(selectedSpesialisasi.equals(spes) ? new Color(0, 102, 204) : Color.LIGHT_GRAY);
            btn.setForeground(selectedSpesialisasi.equals(spes) ? Color.WHITE : Color.BLACK);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                selectedSpesialisasi = spes;
                loadSpesialisasi(); // refresh warna tombol
                loadDokter();
            });
            spesialisasiPanel.add(btn);
        }
        spesialisasiPanel.revalidate();
        spesialisasiPanel.repaint();
    }

    private void loadDokter() {
        dokterListPanel.removeAll();
        String keyword = searchField.getText().trim();
        String filter = (String) filterCombo.getSelectedItem();

        List<Dokter> dokterList;
        if (!selectedSpesialisasi.equals("Semua")) {
            dokterList = dokterController.getDokterBySpesialisasi(selectedSpesialisasi);
        } else {
            dokterList = dokterController.getAllDokter();
        }
        // Filter keyword
        if (!keyword.isEmpty()) {
            dokterList.removeIf(d -> !d.getNamaLengkap().toLowerCase().contains(keyword.toLowerCase()));
        }
        // Sorting berdasarkan filter (sederhana)
        if (filter.equals("Harga Termurah")) {
            dokterList.sort((a,b) -> a.getBiayaKonsultasi().compareTo(b.getBiayaKonsultasi()));
        } else if (filter.equals("Harga Termahal")) {
            dokterList.sort((a,b) -> b.getBiayaKonsultasi().compareTo(a.getBiayaKonsultasi()));
        } else if (filter.equals("Rating Tertinggi")) {
            // Sementara dummy, karena rating belum ada di database
            // Biarkan urutan asli
        } else if (filter.equals("Sesuai Hewan Saya")) {
            // Akan diimplementasikan nanti dengan hewan pemilik
        }

        if (dokterList.isEmpty()) {
            dokterListPanel.add(new JLabel("Tidak ada dokter yang ditemukan."));
        } else {
            for (Dokter d : dokterList) {
                dokterListPanel.add(createDokterCard(d));
                dokterListPanel.add(Box.createVerticalStrut(10));
            }
        }
        dokterListPanel.revalidate();
        dokterListPanel.repaint();
    }

    private JPanel createDokterCard(Dokter dokter) {
        RoundedPanel card = new RoundedPanel(15);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel(dokter.getNamaLengkap());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel spesLabel = new JLabel("Spesialisasi: " + dokter.getSpesialisasi());
        JLabel priceLabel = new JLabel("Biaya Konsultasi: Rp " + dokter.getBiayaKonsultasi());
        priceLabel.setForeground(new Color(0, 102, 204));
        JButton pilihBtn = new JButton("Pilih");
        pilihBtn.setBackground(new Color(0, 102, 204));
        pilihBtn.setForeground(Color.WHITE);
        pilihBtn.setFocusPainted(false);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(spesLabel);
        infoPanel.add(priceLabel);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(pilihBtn, BorderLayout.EAST);

        pilihBtn.addActionListener(e -> {
            // Buka ChatDetailView (dalam card atau frame baru)
            // Untuk sementara, kita buka JDialog atau ganti panel di dashboard?
            // Karena ini dipanggil dari DashboardPemilikView, kita perlu akses ke parent.
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Bisa buka ChatDetailView sebagai dialog atau mengganti card
            // Sesuai permintaan, ketika pilih dokter akan ada pilihan "Pesan Sekarang" atau "Pesan Nanti"
            // Tapi di sini kita sederhanakan: langsung buka dialog pilih jenis pesanan.
            int option = JOptionPane.showOptionDialog(parent,
                    "Pilih metode konsultasi untuk " + dokter.getNamaLengkap(),
                    "Pilih Metode",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Pesan Sekarang", "Pesan Nanti"},
                    "Pesan Sekarang");
            if (option == 0 || option == 1) {
                boolean langsung = (option == 0);
                // BUKA HALAMAN PEMBAYARAN (PemesananOnlineView)
                new PemesananOnlineView(parent, pemilik, dokter, langsung);
            }
        });

        return card;
    }
}