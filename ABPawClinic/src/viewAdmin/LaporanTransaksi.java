/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewAdmin;

import config.Koneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author hp
 */
public class LaporanTransaksi extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public LaporanTransaksi() {
        setTitle("Laporan Transaksi");
        setSize(900, 500);
        setLocationRelativeTo(null);
        initTable();
        loadData();
    }

    private void initTable() {
        model = new DefaultTableModel(new String[]{"ID", "Tipe", "Kode", "Pemilik", "Dokter", "Tanggal", "Total Biaya", "Status"}, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll);
    }

    private void loadData() {
        model.setRowCount(0);
        // Data dari pemesanan_online
        String sqlOnline = "SELECT o.id_pemesanan, 'Online' as tipe, o.kode_pemesanan, p.nama_pemilik, d.nama_dokter, o.tanggal_konsultasi, o.total_biaya, o.status_pemesanan " +
                "FROM pemesanan_online o JOIN pemilik p ON o.id_pemilik = p.id_pemilik JOIN dokter d ON o.id_dokter = d.id_dokter";
        try (Statement st = Koneksi.getConnection().createStatement(); ResultSet rs = st.executeQuery(sqlOnline)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_pemesanan"), rs.getString("tipe"), rs.getString("kode_pemesanan"),
                        rs.getString("nama_pemilik"), rs.getString("nama_dokter"), rs.getDate("tanggal_konsultasi"),
                        rs.getBigDecimal("total_biaya"), rs.getString("status_pemesanan")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Data offline (antrean) -> tidak punya biaya di tabel pemesanan_offline, bisa dikosongkan atau join dengan biaya konsultasi dokter
        String sqlOffline = "SELECT a.id_antrean, 'Offline' as tipe, '-' as kode, p.nama_pemilik, d.nama_dokter, a.tanggal_antrean, d.biaya_konsultasi, a.status_antrean " +
                "FROM pemesanan_offline a JOIN pemilik p ON a.id_pemilik = p.id_pemilik JOIN dokter d ON a.id_dokter = d.id_dokter";
        try (Statement st = Koneksi.getConnection().createStatement(); ResultSet rs = st.executeQuery(sqlOffline)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_antrean"), rs.getString("tipe"), rs.getString("kode"),
                        rs.getString("nama_pemilik"), rs.getString("nama_dokter"), rs.getDate("tanggal_antrean"),
                        rs.getBigDecimal("biaya_konsultasi"), rs.getString("status_antrean")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
