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
import java.math.BigDecimal;

/**
 *
 * @author hp
 */
public class KelolaObat extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNama, txtHarga, txtStok, txtStokMin, txtKadaluarsa;
    private JComboBox<String> cbBentuk, cbPerluResep;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;

    public KelolaObat() {
        setTitle("Kelola Data Obat");
        setSize(900, 500);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Obat"));
        formPanel.add(new JLabel("Nama Obat:")); txtNama = new JTextField(); formPanel.add(txtNama);
        formPanel.add(new JLabel("Harga:")); txtHarga = new JTextField(); formPanel.add(txtHarga);
        formPanel.add(new JLabel("Stok:")); txtStok = new JTextField(); formPanel.add(txtStok);
        formPanel.add(new JLabel("Stok Minimal:")); txtStokMin = new JTextField(); formPanel.add(txtStokMin);
        formPanel.add(new JLabel("Tgl Kadaluarsa (YYYY-MM-DD):")); txtKadaluarsa = new JTextField(); formPanel.add(txtKadaluarsa);
        formPanel.add(new JLabel("Bentuk Obat:")); cbBentuk = new JComboBox<>(new String[]{"tablet","kapsul","suntik","salep","cair"}); formPanel.add(cbBentuk);
        formPanel.add(new JLabel("Perlu Resep:")); cbPerluResep = new JComboBox<>(new String[]{"1","0"}); formPanel.add(cbPerluResep);

        JPanel btnPanel = new JPanel();
        btnTambah = new JButton("Tambah"); btnEdit = new JButton("Edit"); btnHapus = new JButton("Hapus"); btnRefresh = new JButton("Refresh");
        btnPanel.add(btnTambah); btnPanel.add(btnEdit); btnPanel.add(btnHapus); btnPanel.add(btnRefresh);

        model = new DefaultTableModel(new String[]{"ID","Nama","Harga","Stok","StokMin","Kadaluarsa","Bentuk","PerluResep"},0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(formPanel, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER); add(btnPanel, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> tambahObat());
        btnEdit.addActionListener(e -> editObat());
        btnHapus.addActionListener(e -> hapusObat());
        btnRefresh.addActionListener(e -> loadData());
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if(row>=0){
                txtNama.setText(model.getValueAt(row,1).toString());
                txtHarga.setText(model.getValueAt(row,2).toString());
                txtStok.setText(model.getValueAt(row,3).toString());
                txtStokMin.setText(model.getValueAt(row,4).toString());
                txtKadaluarsa.setText(model.getValueAt(row,5).toString());
                cbBentuk.setSelectedItem(model.getValueAt(row,6).toString());
                cbPerluResep.setSelectedItem(model.getValueAt(row,7).toString());
            }
        });
    }

    private void loadData(){
        model.setRowCount(0);
        try(Statement st = Koneksi.getConnection().createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM obat")){
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("id_obat"), rs.getString("nama_obat"), rs.getBigDecimal("harga"),
                        rs.getInt("stok"), rs.getInt("stok_minimal"), rs.getDate("tgl_kadaluarsa"),
                        rs.getString("bentuk_obat"), rs.getInt("perlu_resep")
                });
            }
        } catch(SQLException e){e.printStackTrace();}
    }

    private void tambahObat(){
        String sql = "INSERT INTO obat (nama_obat, harga, stok, stok_minimal, tgl_kadaluarsa, bentuk_obat, perlu_resep) VALUES (?,?,?,?,?,?,?)";
        try(PreparedStatement ps = Koneksi.getConnection().prepareStatement(sql)){
            ps.setString(1, txtNama.getText());
            ps.setBigDecimal(2, new BigDecimal(txtHarga.getText()));
            ps.setInt(3, Integer.parseInt(txtStok.getText()));
            ps.setInt(4, Integer.parseInt(txtStokMin.getText()));
            ps.setDate(5, Date.valueOf(txtKadaluarsa.getText()));
            ps.setString(6, (String) cbBentuk.getSelectedItem());
            ps.setInt(7, Integer.parseInt((String) cbPerluResep.getSelectedItem()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Obat ditambahkan");
            loadData();
        }catch(Exception ex){ex.printStackTrace();}
    }

    private void editObat(){
        int row = table.getSelectedRow();
        if(row<0) return;
        int id = (int)model.getValueAt(row,0);
        String sql = "UPDATE obat SET nama_obat=?, harga=?, stok=?, stok_minimal=?, tgl_kadaluarsa=?, bentuk_obat=?, perlu_resep=? WHERE id_obat=?";
        try(PreparedStatement ps = Koneksi.getConnection().prepareStatement(sql)){
            ps.setString(1, txtNama.getText());
            ps.setBigDecimal(2, new BigDecimal(txtHarga.getText()));
            ps.setInt(3, Integer.parseInt(txtStok.getText()));
            ps.setInt(4, Integer.parseInt(txtStokMin.getText()));
            ps.setDate(5, Date.valueOf(txtKadaluarsa.getText()));
            ps.setString(6, (String) cbBentuk.getSelectedItem());
            ps.setInt(7, Integer.parseInt((String) cbPerluResep.getSelectedItem()));
            ps.setInt(8, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Obat diupdate");
            loadData();
        }catch(Exception ex){ex.printStackTrace();}
    }

    private void hapusObat(){
        int row = table.getSelectedRow();
        if(row<0) return;
        int id = (int)model.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Hapus?")==JOptionPane.YES_OPTION){
            try(PreparedStatement ps = Koneksi.getConnection().prepareStatement("DELETE FROM obat WHERE id_obat=?")){
                ps.setInt(1,id);
                ps.executeUpdate();
                loadData();
            }catch(SQLException e){e.printStackTrace();}
        }
    }
}
