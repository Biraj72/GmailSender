package GmailSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendMailGUI extends JFrame {
    private final JTextField toField = new JTextField();
    private final JTextField ccField = new JTextField();
    private final JTextField bccField = new JTextField();
    private final JTextField subjectField = new JTextField();
    private final JTextArea bodyArea = new JTextArea(14, 50);

    private final DefaultListModel<File> attachmentsModel = new DefaultListModel<>();
    private final JList<File> attachmentsList = new JList<>(attachmentsModel);

    private final JButton sendButton = new JButton("Send");
    private final JButton attachButton = new JButton("\uD83D\uDCCE Attach");
    private final JButton removeAttachButton = new JButton("🗑 Remove");

    private final JLabel statusLabel = new JLabel(" ");

    public SendMailGUI() {
        super("New Message");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initLayout();
        pack();
        setLocationRelativeTo(null);

        attachButton.addActionListener(this::doAttach);
        removeAttachButton.addActionListener(this::doRemoveAttachment);
        sendButton.addActionListener(this::doSend);
    }

    private void initLayout() {
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        fieldsPanel.add(new JLabel("To:"));
        fieldsPanel.add(toField);
        fieldsPanel.add(new JLabel("Cc:"));
        fieldsPanel.add(ccField);
        fieldsPanel.add(new JLabel("Bcc:"));
        fieldsPanel.add(bccField);
        fieldsPanel.add(new JLabel("Subject:"));
        fieldsPanel.add(subjectField);

        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);

        attachmentsList.setVisibleRowCount(3);
        JScrollPane attachScroll = new JScrollPane(attachmentsList);
        attachScroll.setPreferredSize(new Dimension(200, 80));
        attachScroll.setBorder(BorderFactory.createTitledBorder("Attachments"));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        sendButton.setBackground(new Color(66, 133, 244));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD));

        leftButtons.add(sendButton);
        leftButtons.add(attachButton);
        leftButtons.add(removeAttachButton);

        bottomPanel.add(leftButtons, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(bodyScroll, BorderLayout.CENTER);
        mainPanel.add(attachScroll, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void doAttach(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            for (File f : chooser.getSelectedFiles()) {
                attachmentsModel.addElement(f);
            }
        }
    }

    private void doRemoveAttachment(ActionEvent e) {
        int idx = attachmentsList.getSelectedIndex();
        if (idx >= 0) attachmentsModel.remove(idx);
    }

    private void doSend(ActionEvent e) {
        String to = toField.getText().trim();
        String cc = ccField.getText().trim();
        String bcc = bccField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText();

        if (to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipient (To) is required");
            return;
        }

        List<File> attachments = new ArrayList<>();
        for (int i = 0; i < attachmentsModel.size(); i++) {
            attachments.add(attachmentsModel.get(i));
        }

        try {
            EmailSender sender = new EmailSender();
            statusLabel.setText("Sending...");
            sender.sendEmail(to, cc, bcc, subject, body, attachments);
            statusLabel.setText("Email sent successfully!");
            JOptionPane.showMessageDialog(this, "Email sent successfully!");
        } catch (Exception ex) {
            statusLabel.setText("Failed to send email.");
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Failed",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new SendMailGUI().setVisible(true));
    }
}
