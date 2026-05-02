/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewUser;

import config.Koneksi;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 *
 * @author hp
 */
public class BookingKonsultasi extends JFrame {
    private int idPemilik;
    private JComboBox<String> cbDokter, cbPet;
    private JTextField txtTanggal, txtWaktu, txtKeluhan;
    private JButton btnBooking;

    public BookingKonsultasi(int idPemilik) {
        this.idPemilik = idPemilik;
        setTitle("Booking Konsultasi Online");
        setSize(450, 400);
        setLocationRelativeTo(null);
        initUI();
        loadDokter();
        loadPets();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.add(new JLabel("Pilih Dokter:")); cbDokter = new JComboBox<>(); panel.add(cbDokter);
        panel.add(new JLabel("Pilih Hewan:")); cbPet = new JComboBox<>(); panel.add(cbPet);
        panel.add(new JLabel("Tanggal (YYYY-MM-DD):")); txtTanggal = new JTextField(); panel.add(txtTanggal);
        panel.add(new JLabel("Waktu (HH:MM):")); txtWaktu = new JTextField(); panel.add(txtWaktu);
        panel.add(new JLabel("Keluhan:")); txtKeluhan = new JTextField(); panel.add(txtKeluhan);
        btnBooking = new JButton("Booking");
        panel.add(new JLabel()); panel.add(btnBooking);
        add(panel);
        btnBooking.addActionListener(e -> booking());
    }

    private void loadDokter() {
        try (Statement st = Koneksi.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT id_dokter, nama_dokter FROM dokter")) {
            while (rs.next()) {
                cbDokter.addItem(rs.getInt("id_dokter") + " - " + rs.getString("nama_dokter"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadPets() {
        try (PreparedStatement ps = Koneksi.getConnection().prepareStatement("SELECT id_pet, nama_pet FROM pets WHERE id_pemilik=?")) {
            ps.setInt(1, idPemilik);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbPet.addItem(rs.getInt("id_pet") + " - " + rs.getString("nama_pet"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void booking() {
        if (cbDokter.getItemCount() == 0 || cbPet.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Data dokter atau hewan kosong.");
            return;
        }
        int idDokter = Integer.parseInt(cbDokter.getSelectedItem().toString().split(" - ")[0]);
        int idPet = Integer.parseInt(cbPet.getSelectedItem().toString().split(" - ")[0]);
        String tgl = txtTanggal.getText();
        String waktu = txtWaktu.getText().trim(); // format HH:MM
        String keluhan = txtKeluhan.getText();

        // Ambil biaya dokter
        BigDecimal biaya = BigDecimal.ZERO;
        try (PreparedStatement ps = Koneksi.getConnection().prepareStatement("SELECT biaya_konsultasi FROM dokter WHERE id_dokter=?")) {
            ps.setInt(1, idDokter);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) biaya = rs.getBigDecimal(1);
        } catch (SQLException e) { e.printStackTrace(); }

        // Generate kode unik
        String kode = "ABP" + System.currentTimeMillis() + new Random().nextInt(1000);
        String sql = "INSERT INTO pemesanan_online (kode_pemesanan, id_pemilik, id_dokter, id_pet, tanggal_konsultasi, waktu_konsultasi, keluhan, biaya_konsultasi, total_biaya, status_pemesanan, waktu_pemesanan) " +
                "VALUES (?,?,?,?,?,?,?,?,?,'menunggu', NOW())";
        try (PreparedStatement ps = Koneksi.getConnection().prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setInt(2, idPemilik);
            ps.setInt(3, idDokter);
            ps.setInt(4, idPet);
            ps.setDate(5, Date.valueOf(tgl));
            ps.setString(6, waktu);
            ps.setString(7, keluhan);
            ps.setBigDecimal(8, biaya);
            ps.setBigDecimal(9, biaya); // total_biaya sementara tanpa diskon
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Booking berhasil! Kode pemesanan: " + kode);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal booking: " + ex.getMessage());
        }
    }
}