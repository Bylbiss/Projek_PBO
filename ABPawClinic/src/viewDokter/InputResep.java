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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hp
 */
public class InputResep extends JFrame {
    private int idDokter;
    private JComboBox<String> cbPemesanan;
    private DefaultTableModel modelObat;
    private JTable tableObat;
    private List<Integer> selectedObatIds = new ArrayList<>();
    private JTextField txtTakaran, txtJumlah, txtCatatan;
    private JButton btnTambahObat, btnSimpanResep;

    public InputResep(int idDokter) {
        this.idDokter = idDokter;
        setTitle("Input Resep Obat");
        setSize(800, 500);
        setLocationRelativeTo(null);
        initUI();
        loadDaftarPemesanan();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Pilih Pemesanan:"));
        cbPemesanan = new JComboBox<>();
        topPanel.add(cbPemesanan);
        add(topPanel, BorderLayout.NORTH);

        // Tabel obat yang dipilih
        modelObat = new DefaultTableModel(new String[]{"ID Obat","Nama Obat","Takaran","Jumlah","Catatan"},0);
        tableObat = new JTable(modelObat);
        JScrollPane scroll = new JScrollPane(tableObat);
        add(scroll, BorderLayout.CENTER);

        JPanel formObat = new JPanel(new GridLayout(4,2));
        formObat.setBorder(BorderFactory.createTitledBorder("Tambah Detail Obat"));
        formObat.add(new JLabel("ID Obat:"));
        JTextField txtIdObat = new JTextField();
        formObat.add(txtIdObat);
        formObat.add(new JLabel("Takaran:"));
        txtTakaran = new JTextField();
        formObat.add(txtTakaran);
        formObat.add(new JLabel("Jumlah:"));
        txtJumlah = new JTextField();
        formObat.add(txtJumlah);
        formObat.add(new JLabel("Catatan:"));
        txtCatatan = new JTextField();
        formObat.add(txtCatatan);
        btnTambahObat = new JButton("Tambah ke Resep");
        formObat.add(btnTambahObat);
        add(formObat, BorderLayout.SOUTH);

        btnSimpanResep = new JButton("Simpan Resep");
        add(btnSimpanResep, BorderLayout.EAST);

        btnTambahObat.addActionListener(e -> {
            try {
                int idObat = Integer.parseInt(txtIdObat.getText());
                String takaran = txtTakaran.getText();
                int jumlah = Integer.parseInt(txtJumlah.getText());
                String catatan = txtCatatan.getText();
                // Ambil nama obat dari DB
                String namaObat = "";
                try (Statement st = Koneksi.getConnection().createStatement();
                     ResultSet rs = st.executeQuery("SELECT nama_obat FROM obat WHERE id_obat="+idObat)) {
                    if (rs.next()) namaObat = rs.getString(1);
                }
                modelObat.addRow(new Object[]{idObat, namaObat, takaran, jumlah, catatan});
                selectedObatIds.add(idObat);
                txtIdObat.setText(""); txtTakaran.setText(""); txtJumlah.setText(""); txtCatatan.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input valid!");
            }
        });

        btnSimpanResep.addActionListener(e -> simpanResep());
    }

    private void loadDaftarPemesanan() {
        // Ambil pemesanan online/offline yang sudah selesai dan belum ada resep
        String sql = "SELECT id_pemesanan, kode_pemesanan FROM pemesanan_online WHERE id_dokter=? AND status_pemesanan='selesai' " +
                "AND id_pemesanan NOT IN (SELECT id_pemesanan FROM resep_obat WHERE tipe_pemesanan='online') " +
                "UNION " +
                "SELECT id_antrean, CONCAT('OFF-',id_antrean) FROM pemesanan_offline WHERE id_dokter=? AND status_antrean='selesai' " +
                "AND id_antrean NOT IN (SELECT id_pemesanan FROM resep_obat WHERE tipe_pemesanan='offline')";
        try (PreparedStatement ps = Koneksi.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idDokter);
            ps.setInt(2, idDokter);
            ResultSet rs = ps.executeQuery();
            cbPemesanan.removeAllItems();
            while (rs.next()) {
                cbPemesanan.addItem(rs.getInt(1) + " - " + rs.getString(2));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void simpanResep() {
        if (cbPemesanan.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada pemesanan selesai yang belum punya resep.");
            return;
        }
        String selected = (String) cbPemesanan.getSelectedItem();
        int idPemesanan = Integer.parseInt(selected.split(" - ")[0]);
        String tipe = selected.contains("OFF") ? "offline" : "online";

        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            // Insert ke resep_obat
            String sqlResep = "INSERT INTO resep_obat (id_pemesanan, tipe_pemesanan, id_dokter, tanggal_resep, status) VALUES (?,?,?,CURDATE(),'belum_diproses')";
            int idResep = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlResep, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idPemesanan);
                ps.setString(2, tipe);
                ps.setInt(3, idDokter);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) idResep = rs.getInt(1);
            }
            if (idResep == -1) throw new SQLException("Gagal membuat resep");
            // Insert detail_resep
            String sqlDetail = "INSERT INTO detail_resep (id_resep, id_obat, takaran, jumlah, catatan) VALUES (?,?,?,?,?)";
            for (int i = 0; i < modelObat.getRowCount(); i++) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                    ps.setInt(1, idResep);
                    ps.setInt(2, (int) modelObat.getValueAt(i, 0));
                    ps.setString(3, (String) modelObat.getValueAt(i, 2));
                    ps.setInt(4, (int) modelObat.getValueAt(i, 3));
                    ps.setString(5, (String) modelObat.getValueAt(i, 4));
                    ps.executeUpdate();
                }
            }
            conn.commit();
            JOptionPane.showMessageDialog(this, "Resep berhasil disimpan!");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal simpan resep: " + ex.getMessage());
        }
    }
}
