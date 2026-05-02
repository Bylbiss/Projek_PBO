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
public class KelolaDokter extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNama, txtAlamat, txtKota, txtNoHp, txtSpesialisasi, txtBiaya;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;

    public KelolaDokter() {
        setTitle("Kelola Data Dokter");
        setSize(900, 500);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());

        // Form input
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Dokter"));
        formPanel.add(new JLabel("Nama Dokter:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);
        formPanel.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        formPanel.add(txtAlamat);
        formPanel.add(new JLabel("Kota:"));
        txtKota = new JTextField();
        formPanel.add(txtKota);
        formPanel.add(new JLabel("No HP:"));
        txtNoHp = new JTextField();
        formPanel.add(txtNoHp);
        formPanel.add(new JLabel("Spesialisasi:"));
        txtSpesialisasi = new JTextField();
        formPanel.add(txtSpesialisasi);
        formPanel.add(new JLabel("Biaya Konsultasi:"));
        txtBiaya = new JTextField();
        formPanel.add(txtBiaya);

        // Tombol
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnPanel.add(btnTambah);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnRefresh);

        // Tabel
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Alamat", "Kota", "No HP", "Spesialisasi", "Biaya"}, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        add(formPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Event
        btnTambah.addActionListener(e -> tambahDokter());
        btnEdit.addActionListener(e -> editDokter());
        btnHapus.addActionListener(e -> hapusDokter());
        btnRefresh.addActionListener(e -> loadData());
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtNama.setText(model.getValueAt(row, 1).toString());
                txtAlamat.setText(model.getValueAt(row, 2).toString());
                txtKota.setText(model.getValueAt(row, 3).toString());
                txtNoHp.setText(model.getValueAt(row, 4).toString());
                txtSpesialisasi.setText(model.getValueAt(row, 5).toString());
                txtBiaya.setText(model.getValueAt(row, 6).toString());
            }
        });
    }
    
    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT * FROM dokter";
        try (Statement st = Koneksi.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_dokter"),
                        rs.getString("nama_dokter"),
                        rs.getString("alamat"),
                        rs.getString("kota"),
                        rs.getString("no_hp"),
                        rs.getString("spesialisasi"),
                        rs.getBigDecimal("biaya_konsultasi")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tambahDokter() {
        String sql = "INSERT INTO dokter (nama_dokter, alamat, kota, no_hp, spesialisasi, biaya_konsultasi) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Koneksi.getConnection().prepareStatement(sql)) {
            ps.setString(1, txtNama.getText());
            ps.setString(2, txtAlamat.getText());
            ps.setString(3, txtKota.getText());
            ps.setString(4, txtNoHp.getText());
            ps.setString(5, txtSpesialisasi.getText());
            ps.setBigDecimal(6, new java.math.BigDecimal(txtBiaya.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Dokter berhasil ditambahkan!");
            loadData();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void editDokter() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dokter yang akan diedit!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String sql = "UPDATE dokter SET nama_dokter=?, alamat=?, kota=?, no_hp=?, spesialisasi=?, biaya_konsultasi=? WHERE id_dokter=?";
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql)) {
            ps.setString(1, txtNama.getText());
            ps.setString(2, txtAlamat.getText());
            ps.setString(3, txtKota.getText());
            ps.setString(4, txtNoHp.getText());
            ps.setString(5, txtSpesialisasi.getText());
            ps.setBigDecimal(6, new java.math.BigDecimal(txtBiaya.getText()));
            ps.setInt(7, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data dokter diupdate!");
            loadData();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hapusDokter() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus dokter?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (PreparedStatement ps = Koneksi.getConnection().prepareStatement("DELETE FROM dokter WHERE id_dokter=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadData();
                clearForm();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearForm() {
        txtNama.setText("");
        txtAlamat.setText("");
        txtKota.setText("");
        txtNoHp.setText("");
        txtSpesialisasi.setText("");
        txtBiaya.setText("");
    }
}
