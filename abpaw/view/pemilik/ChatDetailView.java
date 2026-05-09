/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abpaw.view.pemilik;

import abpaw.controller.ChatController;
import abpaw.model.entity.Chat;
import abpaw.model.entity.Dokter;
import abpaw.model.entity.Pemilik;
import abpaw.view.components.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

public class ChatDetailView extends JFrame {
    private Pemilik pemilik;
    private Dokter dokter;
    private ChatController chatController;
    private JTextPane chatPane;
    private HTMLDocument doc;
    private JTextField messageField;
    private JButton sendButton;
    private Timer refreshTimer;

    public ChatDetailView(Pemilik pemilik, Dokter dokter) throws BadLocationException, IOException {
        this.pemilik = pemilik;
        this.dokter = dokter;
        this.chatController = new ChatController();
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
        loadChatHistory();
        startAutoRefresh();
    }

    private void initComponents() throws BadLocationException, IOException {
        setTitle("Chat dengan Dr. " + dokter.getNamaLengkap());
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel nameLabel = new JLabel("Dr. " + dokter.getNamaLengkap());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        topPanel.add(nameLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setContentType("text/html");
        doc = (HTMLDocument) chatPane.getDocument();  
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        // Set style dasar
        String style = "<style>body { font-family: Arial; margin: 10px; } .bubble { max-width: 70%; padding: 8px 12px; border-radius: 18px; margin: 5px; display: inline-block; } .left { background-color: #e4e6eb; text-align: left; } .right { background-color: #dcf8c5; text-align: right; } .name { font-weight: bold; font-size: 12px; } .time { font-size: 10px; color: gray; }</style>";
        doc.setInnerHTML(doc.getDefaultRootElement(), style + "<body></body>");

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        messageField = new JTextField();
        sendButton = new JButton("Kirim");
        sendButton.setBackground(new Color(0, 102, 204));
        sendButton.setForeground(Color.WHITE);
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(this::sendMessage);
        messageField.addActionListener(this::sendMessage);
    }

    private void loadChatHistory() {
        List<Chat> chats = chatController.getChatBetween(dokter.getIdDokter(), pemilik.getIdPemilik());
        try {
            doc.setInnerHTML(doc.getDefaultRootElement(), "<body></body>"); // reset
        } catch (Exception e) {}
        for (Chat c : chats) {
            String sender;
            if (c.getIdDokter() != null && c.getIdPemilik() == null) sender = "Dokter";
            else if (c.getIdPemilik() != null && c.getIdDokter() == null) sender = "Anda";
            else continue;
            appendChatMessage(sender, c.getPesan(), c.getWaktu());
        }
    }

    private void sendMessage(ActionEvent e) {
        String pesan = messageField.getText().trim();
        if (pesan.isEmpty()) return;
        boolean success = chatController.sendMessageFromPemilik(pemilik.getIdPemilik(), dokter.getIdDokter(), pesan);
        if (success) {
            messageField.setText("");
            appendChatMessage("Anda", pesan, new Timestamp(System.currentTimeMillis()));
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengirim pesan.");
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> loadChatHistory());
            }
        }, 3000, 3000);
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) refreshTimer.cancel();
        super.dispose();
    }
    
    private void appendChatMessage(String sender, String message, Timestamp time) {
        try {
            String side = sender.equals("Anda") ? "right" : "left";
            String name = sender.equals("Anda") ? "Anda" : "Dokter";
            String html = "<div style='text-align: " + side + ";'>" +
                          "<div class='bubble " + side + "'>" +
                          "<div class='name'>" + name + "</div>" +
                          "<div>" + message.replace("\n", "<br>") + "</div>" +
                          "<div class='time'>" + time.toString() + "</div>" +
                          "</div></div>";
            doc.insertAfterEnd(doc.getElement(doc.getDefaultRootElement(), "body"), html);
            chatPane.setCaretPosition(doc.getLength());
        } catch (Exception e) { e.printStackTrace(); }
    }
}