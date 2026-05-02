/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewDokter;

import config.Koneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author hp
 */
public class DaftarAntrean extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private int idDokter;

    public DaftarAntrean(int idDokter) {
        this.idDokter = idDokter;
        setTitle("Daftar Antrean & Pasien");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        initTable();
        loadAntrean();
    }

    private void initTable() {
        model = new DefaultTableModel(new String[]{"ID","Tipe","Nama Pemilik","Nama Pet","Keluhan","Tanggal","Waktu","Status","Aksi"}, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll);
        // Tombol aksi di kolom terakhir (custom render/editor sederhana, untuk kemudahan pakai JOptionPane saat double klik)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (evt.getClickCount() == 2 && row >= 0) {
                    ubahStatus(row);
                }
            }
        });
    }

    private void loadAntrean() {
        model.setRowCount(0);
        // Antrean online (status menunggu atau sudah bayar)
        String sqlOnline = "SELECT o.id_pemesanan, 'Online', p.nama_pemilik, pet.nama_pet, o.keluhan, o.tanggal_konsultasi, o.waktu_konsultasi, o.status_pemesanan " +
                "FROM pemesanan_online o JOIN pemilik p ON o.id_pemilik = p.id_pemilik JOIN pets pet ON o.id_pet = pet.id_pet " +
                "WHERE o.id_dokter = ? AND o.status_pemesanan IN ('menunggu','sudah bayar')";
        // Antrean offline
        String sqlOffline = "SELECT a.id_antrean, 'Offline', p.nama_pemilik, pet.nama_pet, a.keluhan, a.tanggal_antrean, a.waktu_antrean, a.status_antrean " +
                "FROM pemesanan_offline a JOIN pemilik p ON a.id_pemilik = p.id_pemilik JOIN pets pet ON a.id_pet = pet.id_pet " +
                "WHERE a.id_dokter = ? AND a.status_antrean = 'menunggu'";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement psOnline = conn.prepareStatement(sqlOnline);
             PreparedStatement psOffline = conn.prepareStatement(sqlOffline)) {
            psOnline.setInt(1, idDokter);
            ResultSet rsOnline = psOnline.executeQuery();
            while (rsOnline.next()) {
                model.addRow(new Object[]{
                        rsOnline.getInt(1), rsOnline.getString(2), rsOnline.getString(3),
                        rsOnline.getString(4), rsOnline.getString(5), rsOnline.getDate(6),
                        rsOnline.getString(7), rsOnline.getString(8), "Proses"
                });
            }
            psOffline.setInt(1, idDokter);
            ResultSet rsOffline = psOffline.executeQuery();
            while (rsOffline.next()) {
                model.addRow(new Object[]{
                        rsOffline.getInt(1), rsOffline.getString(2), rsOffline.getString(3),
                        rsOffline.getString(4), rsOffline.getString(5), rsOffline.getDate(6),
                        rsOffline.getString(7), rsOffline.getString(8), "Proses"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void ubahStatus(int row) {
        String tipe = (String) model.getValueAt(row, 1);
        int id = (int) model.getValueAt(row, 0);
        String statusBaru = "selesai";
        String sql;
        if (tipe.equals("Online")) {
            sql = "UPDATE pemesanan_online SET status_pemesanan = ? WHERE id_pemesanan = ?";
        } else {
            sql = "UPDATE pemesanan_offline SET status_antrean = ? WHERE id_antrean = ?";
        }
        try (PreparedStatement ps = Koneksi.getConnection().prepareStatement(sql)) {
            ps.setString(1, statusBaru);
            ps.setInt(2, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Status diubah menjadi selesai");
            loadAntrean();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
