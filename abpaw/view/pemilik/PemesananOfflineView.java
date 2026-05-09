/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view.pemilik;

import abpaw.controller.PemesananController;
import abpaw.controller.PetsController;
import abpaw.model.entity.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PemesananOfflineView extends JDialog {
    private Pemilik pemilik;
    private Dokter dokter;
    private Pets selectedPet;
    private PemesananController pemesananController;
    private PetsController petsController;
    
    private JComboBox<Pets> cbPet;
    private JTextArea taKeluhan;
    private JComboBox<String> cbJam;
    private JButton btnBooking;
    
    public PemesananOfflineView(JFrame parent, Pemilik pemilik, Dokter dokter) {
        super(parent, "Pemesanan Offline (Antrean)", true);
        this.pemilik = pemilik;
        this.dokter = dokter;
        this.pemesananController = new PemesananController();
        this.petsController = new PetsController();
        
        initComponents();
        loadPets();
        loadJam();
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        mainPanel.add(new JLabel("Dokter: " + dokter.getNamaLengkap()), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Pilih Hewan:"), gbc);
        cbPet = new JComboBox<>();
        gbc.gridy++;
        mainPanel.add(cbPet, gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Keluhan:"), gbc);
        taKeluhan = new JTextArea(3, 20);
        JScrollPane sp = new JScrollPane(taKeluhan);
        gbc.gridy++;
        mainPanel.add(sp, gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Pilih Waktu Antrean:"), gbc);
        cbJam = new JComboBox<>();
        gbc.gridy++;
        mainPanel.add(cbJam, gbc);
        
        btnBooking = new JButton("Booking Antrean");
        btnBooking.setBackground(new Color(0, 102, 204));
        btnBooking.setForeground(Color.WHITE);
        btnBooking.addActionListener(e -> prosesBooking());
        gbc.gridy++;
        mainPanel.add(btnBooking, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void loadPets() {
        List<Pets> petsList = petsController.getPetsByPemilik(pemilik.getIdPemilik());
        for (Pets p : petsList) {
            cbPet.addItem(p);
        }
        if (petsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Anda belum memiliki hewan.");
            btnBooking.setEnabled(false);
        }
    }
    
    private void loadJam() {
        // Jam tersedia 08:00 - 16:00
        for (int i = 8; i <= 16; i++) {
            cbJam.addItem(String.format("%02d:00", i));
        }
    }
    
    private void prosesBooking() {
        Pets pet = (Pets) cbPet.getSelectedItem();
        if (pet == null) return;
        String keluhan = taKeluhan.getText().trim();
        String waktuStr = (String) cbJam.getSelectedItem();
        LocalTime waktu = LocalTime.parse(waktuStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDate tanggal = LocalDate.now();
        
        // Generate nomor antrean sederhana (misal: ANT-YYYYMMDD-001)
        String nomorAntrean = "ANT-" + LocalDate.now().toString().replace("-", "") + "-" + System.currentTimeMillis() % 1000;
        
        PemesananOffline antrean = new PemesananOffline();
        antrean.setIdDokter(dokter.getIdDokter());
        antrean.setIdPemilik(pemilik.getIdPemilik());
        antrean.setIdPet(pet.getIdPet());
        antrean.setNomorAntrean(nomorAntrean);
        antrean.setTanggalAntrean(tanggal);
        antrean.setWaktuAntrean(waktu);
        antrean.setKeluhan(keluhan);
        antrean.setStatus("menunggu");
        antrean.setEstimasiWaktu(null);
        
        boolean success = pemesananController.createPemesananOffline(antrean);
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking berhasil! Nomor antrean: " + nomorAntrean);
            dispose();
            // Tampilkan struk offline
            new StrukView((JFrame) getParent(), antrean);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal melakukan booking.");
        }
    }
}