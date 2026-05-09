/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view.pemilik;

import abpaw.controller.ObatController;
import abpaw.controller.ResepController;
import abpaw.model.entity.Obat;
import abpaw.model.entity.Resep;
import abpaw.model.entity.Pemilik;
import abpaw.view.components.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ObatView extends JPanel {
    private Pemilik pemilik;
    private ObatController obatController;
    private ResepController resepController;
    private JTable tableObat;
    private DefaultTableModel tableModel;
    private JTable tableResep;
    private DefaultTableModel resepModel;
    
    public ObatView(Pemilik pemilik) {
        this.pemilik = pemilik;
        this.obatController = new ObatController();
        this.resepController = new ResepController();
        initComponents();
        loadObat();
        loadResep();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab Beli Obat
        JPanel beliPanel = new JPanel(new BorderLayout(5, 5));
        String[] cols = {"ID", "Nama Obat", "Bentuk", "Harga", "Stok", "Resep?"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableObat = new JTable(tableModel);
        JScrollPane spObat = new JScrollPane(tableObat);
        JButton btnBeli = new JButton("Beli Obat Terpilih");
        btnBeli.addActionListener(e -> beliObat());
        beliPanel.add(spObat, BorderLayout.CENTER);
        beliPanel.add(btnBeli, BorderLayout.SOUTH);
        tabbedPane.addTab("Beli Obat", beliPanel);
        
        // Tab Ambil Resep
        JPanel resepPanel = new JPanel(new BorderLayout(5, 5));
        String[] colsResep = {"ID Resep", "Dokter", "Tgl Resep", "Status"};
        resepModel = new DefaultTableModel(colsResep, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableResep = new JTable(resepModel);
        JScrollPane spResep = new JScrollPane(tableResep);
        JButton btnDetail = new JButton("Lihat Detail / Ambil Obat");
        btnDetail.addActionListener(e -> detailResep());
        resepPanel.add(spResep, BorderLayout.CENTER);
        resepPanel.add(btnDetail, BorderLayout.SOUTH);
        tabbedPane.addTab("Resep Saya", resepPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void loadObat() {
        tableModel.setRowCount(0);
        List<Obat> obatList = obatController.getAllObat();
        for (Obat o : obatList) {
            tableModel.addRow(new Object[]{
                o.getIdObat(),
                o.getNamaObat(),
                o.getBentukObat(),
                "Rp " + o.getHarga(),
                o.getStok(),
                o.isPerluResep() ? "Ya" : "Tidak"
            });
        }
    }
    
    private void beliObat() {
        int row = tableObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih obat terlebih dahulu.");
            return;
        }
        int idObat = (int) tableModel.getValueAt(row, 0);
        String namaObat = (String) tableModel.getValueAt(row, 1);
        boolean perluResep = tableModel.getValueAt(row, 5).equals("Ya");
        if (perluResep) {
            JOptionPane.showMessageDialog(this, "Obat ini memerlukan resep dokter. Silakan ambil di tab Resep.");
            return;
        }
        String jumlahStr = JOptionPane.showInputDialog(this, "Masukkan jumlah yang akan dibeli:", "1");
        if (jumlahStr == null) return;
        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah tidak valid.");
            return;
        }
        boolean success = obatController.beliObat(idObat, jumlah, pemilik.getIdPemilik());
        if (success) {
            JOptionPane.showMessageDialog(this, "Pembelian berhasil! Stok telah dikurangi.");
            loadObat();
        } else {
            JOptionPane.showMessageDialog(this, "Stok tidak mencukupi atau gagal.");
        }
    }
    
    private void loadResep() {
        resepModel.setRowCount(0);
        List<Resep> resepList = resepController.getResepByPemilik(pemilik.getIdPemilik());
        for (Resep r : resepList) {
            resepModel.addRow(new Object[]{
                r.getIdResep(),
                "Dokter ID " + r.getIdDokter(),
                r.getTanggalResep(),
                r.getStatus()
            });
        }
    }
    
    private void detailResep() {
        int row = tableResep.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih resep terlebih dahulu.");
            return;
        }
        int idResep = (int) resepModel.getValueAt(row, 0);
        // Buka dialog detail resep (belum dibuat, bisa sederhana)
        // Untuk sementara, tampilkan informasi
        JOptionPane.showMessageDialog(this, "Detail resep ID " + idResep + "\nFitur pengambilan obat akan segera hadir.");
    }
}