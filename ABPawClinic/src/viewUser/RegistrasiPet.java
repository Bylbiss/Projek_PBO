/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewUser;

import config.Koneksi;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

/**
 *
 * @author hp
 */
public class RegistrasiPet extends JFrame {
    private int idPemilik;
    private JTextField txtNamaPet, txtRas, txtBerat;
    private JComboBox<String> cbJenisKelamin, cbJenisHewan, cbSterilisasi;
    private JButton btnSimpan;

    public RegistrasiPet(int idPemilik) {
        this.idPemilik = idPemilik;
        setTitle("Registrasi Hewan Peliharaan");
        setSize(400, 350);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Nama Hewan:")); txtNamaPet = new JTextField(); panel.add(txtNamaPet);
        panel.add(new JLabel("Jenis Kelamin:")); cbJenisKelamin = new JComboBox<>(new String[]{"jantan","betina","tidak_diketahui"}); panel.add(cbJenisKelamin);
        panel.add(new JLabel("Jenis Hewan:")); cbJenisHewan = new JComboBox<>(new String[]{"sapi","kambing","kerbau","ayam","kucing","kelinci","anjing","hamster","burung","ikan","musang","kura-kura","landak","babi","kuda","domba","monyet"}); panel.add(cbJenisHewan);
        panel.add(new JLabel("Ras:")); txtRas = new JTextField(); panel.add(txtRas);
        panel.add(new JLabel("Tanggal Lahir (YYYY-MM-DD):")); JTextField txtTglLahir = new JTextField(); panel.add(txtTglLahir);
        panel.add(new JLabel("Berat (kg):")); txtBerat = new JTextField(); panel.add(txtBerat);
        panel.add(new JLabel("Sterilisasi:")); cbSterilisasi = new JComboBox<>(new String[]{"belum","sudah"}); panel.add(cbSterilisasi);

        btnSimpan = new JButton("Simpan");
        panel.add(new JLabel()); panel.add(btnSimpan);

        add(panel);
        btnSimpan.addActionListener(e -> simpanPet(txtTglLahir.getText()));
    }

    private void simpanPet(String tglLahirStr) {
        String nama = txtNamaPet.getText();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama hewan harus diisi!");
            return;
        }
        Date tglLahir = null;
        Integer usia = null;
        if (!tglLahirStr.isEmpty()) {
            try {
                tglLahir = Date.valueOf(tglLahirStr);
                LocalDate birth = tglLahir.toLocalDate();
                usia = Period.between(birth, LocalDate.now()).getYears();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Format tanggal salah"); return; }
        }
        double berat = 0;
        try { if(!txtBerat.getText().isEmpty()) berat = Double.parseDouble(txtBerat.getText()); } catch(Exception e){}
        String sql = "INSERT INTO pets (id_pemilik, nama_pet, jenis_kelamin, jenis_hewan, ras, tanggal_lahir, usia, berat, sterilisasi) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Koneksi.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idPemilik);
            ps.setString(2, nama);
            ps.setString(3, (String) cbJenisKelamin.getSelectedItem());
            ps.setString(4, (String) cbJenisHewan.getSelectedItem());
            ps.setString(5, txtRas.getText());
            ps.setDate(6, tglLahir);
            if (usia != null) ps.setInt(7, usia); else ps.setNull(7, Types.INTEGER);
            ps.setDouble(8, berat);
            ps.setString(9, (String) cbSterilisasi.getSelectedItem());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Hewan berhasil didaftarkan!");
            dispose();
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Gagal simpan: "+e.getMessage()); }
    }
}
