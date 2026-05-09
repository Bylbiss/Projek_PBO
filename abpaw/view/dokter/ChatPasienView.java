/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view.dokter;

import abpaw.controller.ChatController;
import abpaw.controller.PemesananController;
import abpaw.model.entity.Chat;
import abpaw.model.entity.Dokter;
import abpaw.model.entity.Pemilik;
import abpaw.model.entity.PemesananOnline;
import abpaw.view.components.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javax.swing.text.html.HTMLDocument;

public class ChatPasienView extends JPanel {
    private Dokter dokter;
    private ChatController chatController;
    private PemesananController pemesananController;
    private JList<String> pasienList;
    private DefaultListModel<String> listModel;
    private JTextPane chatPane;
    private HTMLDocument doc;
    private JTextField messageField;
    private JButton sendButton;
    private Pemilik selectedPemilik;
    private Timer refreshTimer;
    private JLabel lblUnread;

    public ChatPasienView(Dokter dokter) {
        this.dokter = dokter;
        this.chatController = new ChatController();
        this.pemesananController = new PemesananController();
        initComponents();
        loadPasienList();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel kiri: daftar pasien
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Pasien yang pernah chat"));

        listModel = new DefaultListModel<>();
        pasienList = new JList<>(listModel);
        pasienList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pasienList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadChatHistory();
            }
        });
        JScrollPane scrollPasien = new JScrollPane(pasienList);
        leftPanel.add(scrollPasien, BorderLayout.CENTER);

        // Panel kanan: area chat
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Percakapan"));

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setContentType("text/html");
        doc = (HTMLDocument) chatPane.getDocument();
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        String style = "<style>body { font-family: Arial; margin: 10px; } .bubble { max-width: 70%; padding: 8px 12px; border-radius: 18px; margin: 5px; display: inline-block; } .left { background-color: #e4e6eb; text-align: left; } .right { background-color: #dcf8c5; text-align: right; } .name { font-weight: bold; font-size: 12px; } .time { font-size: 10px; color: gray; }</style>";
        doc.setInnerHTML(doc.getRootElement(), style + "<body></body>");
        JScrollPane scrollChat = new JScrollPane(chatPane);
        rightPanel.add(scrollChat, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        messageField = new JTextField();
        sendButton = new JButton("Kirim");
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Badge unread
        lblUnread = new JLabel("🔔");
        leftPanel.add(lblUnread, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void loadPasienList() {
        listModel.clear();
        // Ambil semua pemilik yang pernah melakukan pemesanan online dengan dokter ini
        List<PemesananOnline> pemesanan = pemesananController.getPemesananOnlineByDokter(dokter.getIdDokter());
        List<Integer> idPemilikUnik = pemesanan.stream()
                .map(PemesananOnline::getIdPemilik)
                .distinct()
                .collect(Collectors.toList());

        for (Integer id : idPemilikUnik) {
            // Di sini idealnya ambil nama pemilik dari database, untuk sederhana kita pakai ID
            listModel.addElement("Pemilik ID " + id);
        }
        if (listModel.isEmpty()) {
            listModel.addElement("Belum ada pasien");
        }
        // Hitung unread
        int unread = chatController.getUnreadCountForDokter(dokter.getIdDokter());
        lblUnread.setText(unread > 0 ? "🔔 " + unread + " pesan baru" : "🔔 Tidak ada pesan baru");
    }

    private void loadChatHistory() {
        String selected = pasienList.getSelectedValue();
        if (selected == null || selected.equals("Belum ada pasien")) {
            chatPane.setText("");
            selectedPemilik = null;
            return;
        }
        int idPemilik = Integer.parseInt(selected.replaceAll("\\D+", ""));
        // Untuk sementara kita buat objek Pemilik sederhana, nanti bisa diambil dari DB
        selectedPemilik = new Pemilik();
        selectedPemilik.setIdPemilik(idPemilik);

        List<Chat> chats = chatController.getChatBetween(dokter.getIdDokter(), idPemilik);
        try {
            doc.setInnerHTML(doc.getRootElement(), "<body></body>");
        } catch (Exception e) {}
        for (Chat c : chats) {
            String sender;
            if (c.getIdDokter() != null && c.getIdPemilik() == null) sender = "Anda";
            else if (c.getIdPemilik() != null && c.getIdDokter() == null) sender = "Pasien";
            else continue;
            appendChatMessage(sender, c.getPesan(), c.getWaktu());
        }
        // Tandai semua pesan dari pasien sebagai read
        for (Chat c : chats) {
            if (c.getIdPemilik() != null && c.getStatusBaca().equals("unread")) {
                chatController.markAsRead(c.getIdChat());
            }
        }
        chatPane.setCaretPosition(chatPane.getDocument().getLength());
        // Update unread count
        int unread = chatController.getUnreadCountForDokter(dokter.getIdDokter());
        lblUnread.setText(unread > 0 ? "🔔 " + unread + " pesan baru" : "🔔 Tidak ada pesan baru");
    }
    
    private void appendChatMessage(String sender, String message, Timestamp time) {
        try {
            String side = sender.equals("Anda") ? "right" : "left";
            String name = sender.equals("Anda") ? "Anda" : "Pasien";
            String html = "<div style='text-align: " + side + ";'>" +
                          "<div class='bubble " + side + "'>" +
                          "<div class='name'>" + name + "</div>" +
                          "<div>" + message.replace("\n", "<br>") + "</div>" +
                          "<div class='time'>" + time.toString() + "</div>" +
                          "</div></div>";
            doc.insertAfterEnd(doc.getElement(doc.getRootElement(), "body"), html);
            chatPane.setCaretPosition(doc.getLength());
        } catch (Exception e) { e.printStackTrace(); }
    }    

    private void sendMessage() {
        String pesan = messageField.getText().trim();
        if (pesan.isEmpty() || selectedPemilik == null) return;
        boolean success = chatController.sendMessageFromDokter(dokter.getIdDokter(), selectedPemilik.getIdPemilik(), pesan);
        if (success) {
            messageField.setText("");
            appendChatMessage("Anda", pesan, new Timestamp(System.currentTimeMillis()));
            // tidak perlu reload semua history, tapi tetap refresh timer akan sinkronkan
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengirim pesan.");
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    loadPasienList();
                    if (selectedPemilik != null) {
                        loadChatHistory();
                    }
                });
            }
        }, 3000, 3000);
    }

    @Override
    public void removeNotify() {
        if (refreshTimer != null) refreshTimer.cancel();
        super.removeNotify();
    }
}