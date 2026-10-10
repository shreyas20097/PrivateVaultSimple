import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainFrame extends JFrame {

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton viewAllButton;
    private JButton verifyButton;
    private JButton updateButton;
    private JTextArea outputArea;

    private RecordManager recordManager;
    private int uid;

    public MainFrame(RecordManager recordManager, int uid) {

        this.recordManager = recordManager;
        this.uid = uid;

        setTitle("PrivateVaultSimple - Vault");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(20, 15, 60, 25);
        add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(90, 15, 530, 25);
        add(titleField);

        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setBounds(20, 50, 60, 25);
        add(contentLabel);

        contentArea = new JTextArea();
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBounds(90, 50, 530, 100);
        add(contentScroll);

        saveButton = new JButton("Save Record");
        saveButton.setBounds(20, 160, 120, 30);
        add(saveButton);

        viewAllButton = new JButton("View All");
        viewAllButton.setBounds(150, 160, 120, 30);
        add(viewAllButton);

        verifyButton = new JButton("Verify All");
        verifyButton.setBounds(280, 160, 120, 30);
        add(verifyButton);

        updateButton = new JButton("Update Record");
        updateButton.setBounds(410, 160, 130, 30);
        add(updateButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBounds(20, 205, 600, 300);
        add(outputScroll);

        saveButton.addActionListener(e -> saveRecord());
        viewAllButton.addActionListener(e -> viewAll());
        verifyButton.addActionListener(e -> verifyAll());
        updateButton.addActionListener(e -> openUpdate());
    }

    private void openUpdate() {

        UpdateFrame updateFrame = new UpdateFrame(recordManager, uid);
        updateFrame.setVisible(true);
    }

    private void saveRecord() {

        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and content cannot be empty.");
            return;
        }

        try {
            Record record = recordManager.addRecord(uid, title, content);
            JOptionPane.showMessageDialog(this, "Saved! Record ID: " + record.getRecordId());
            titleField.setText("");
            contentArea.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void viewAll() {

        try {
            ArrayList<Record> records = recordManager.getAllRecords(uid);

            outputArea.setText("");

            if (records.isEmpty()) {
                outputArea.setText("No records yet.");
                return;
            }

            for (Record r : records) {
                outputArea.append("ID      : " + r.getRecordId() + "\n");
                outputArea.append("Title   : " + r.getTitle() + "\n");
                outputArea.append("Content : " + r.getContent() + "\n");
                outputArea.append("Created : " + r.getCreatedAt() + "\n");
                outputArea.append("Hash    : " + r.getHash() + "\n");
                outputArea.append("-------------------------------------\n");
            }

        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void verifyAll() {

        try {
            ArrayList<Record> records = recordManager.getAllRecords(uid);

            outputArea.setText("");

            if (records.isEmpty()) {
                outputArea.setText("No records yet.");
                return;
            }

            for (Record r : records) {
                boolean ok = recordManager.verifyRecord(r);

                outputArea.append("Record ID: " + r.getRecordId() + " (" + r.getTitle() + ")\n");
                outputArea.append("  Stored Hash     : " + r.getHash() + "\n");
                outputArea.append("  Calculated Hash : " + HashUtil.generateHash(r.getContent()) + "\n");
                outputArea.append("  Result          : " + (ok ? "VALID" : "TAMPERED") + "\n");
                outputArea.append("-------------------------------------\n");
            }

        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }
}
