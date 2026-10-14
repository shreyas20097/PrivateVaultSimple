import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class MainFrame extends JFrame {

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton viewAllButton;
    private JButton verifyButton;
    private JButton updateButton;
    private JButton logoutButton;
    private JTextPane outputArea;

    private UserManager userManager;
    private RecordManager recordManager;
    private int uid;

    public MainFrame(UserManager userManager, RecordManager recordManager, int uid) {

        this.userManager = userManager;
        this.recordManager = recordManager;
        this.uid = uid;

        setTitle("PrivateVaultSimple - Vault");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(520, 420));
        setSize(700, 600);

        // BorderLayout resizes CENTER automatically when window grows.
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(Theme.BACKGROUND);
        Theme.addPadding(main, 12);
        setContentPane(main);

        // ---- Top: input form ----
        JPanel top = new JPanel(new BorderLayout(5, 8));
        top.setBackground(Theme.BACKGROUND);

        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setBackground(Theme.BACKGROUND);
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(Theme.TEXT);
        titleLabel.setPreferredSize(new Dimension(60, 25));
        titleField = new JTextField();
        titleRow.add(titleLabel, BorderLayout.WEST);
        titleRow.add(titleField, BorderLayout.CENTER);

        JPanel contentRow = new JPanel(new BorderLayout(8, 0));
        contentRow.setBackground(Theme.BACKGROUND);
        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setForeground(Theme.TEXT);
        contentLabel.setPreferredSize(new Dimension(60, 25));
        contentArea = new JTextArea(5, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setPreferredSize(new Dimension(400, 100));
        contentRow.add(contentLabel, BorderLayout.WEST);
        contentRow.add(contentScroll, BorderLayout.CENTER);

        top.add(titleRow, BorderLayout.NORTH);
        top.add(contentRow, BorderLayout.CENTER);
        main.add(top, BorderLayout.NORTH);

        // ---- Middle: buttons in a row ----
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonBar.setBackground(Theme.BACKGROUND);

        saveButton = new JButton("Save Record");
        Theme.styleButton(saveButton, Theme.SUCCESS);

        viewAllButton = new JButton("View All");
        Theme.styleButton(viewAllButton, Theme.PRIMARY);

        verifyButton = new JButton("Verify All");
        Theme.styleButton(verifyButton, Theme.WARNING);

        updateButton = new JButton("Update Record");
        Theme.styleButton(updateButton, Theme.TEAL);

        logoutButton = new JButton("Log Out");
        Theme.styleButton(logoutButton, Theme.DANGER);

        buttonBar.add(saveButton);
        buttonBar.add(viewAllButton);
        buttonBar.add(verifyButton);
        buttonBar.add(updateButton);
        buttonBar.add(logoutButton);

        // Button bar + output stacked in center so both stretch.
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(Theme.BACKGROUND);
        center.add(buttonBar, BorderLayout.NORTH);

        outputArea = new JTextPane();
        outputArea.setEditable(false);
        outputArea.setBackground(Theme.CARD);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        center.add(outputScroll, BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);

        saveButton.addActionListener(e -> saveRecord());
        viewAllButton.addActionListener(e -> viewAll());
        verifyButton.addActionListener(e -> verifyAll());
        updateButton.addActionListener(e -> openUpdate());
        logoutButton.addActionListener(e -> doLogout());
    }

    private void doLogout() {
        LoginFrame login = new LoginFrame(userManager, recordManager);
        login.setVisible(true);
        dispose();
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
                Theme.append(outputArea, "No records yet.\n", Theme.MUTED);
                return;
            }

            for (Record r : records) {
                Theme.append(outputArea, "ID      : " + r.getRecordId() + "\n", Theme.PRIMARY, true);
                Theme.append(outputArea, "Title   : " + r.getTitle() + "\n", Theme.TEXT);
                Theme.append(outputArea, "Content : " + r.getContent() + "\n", Theme.TEXT);
                Theme.append(outputArea, "Created : " + r.getCreatedAt() + "\n", Theme.MUTED);
                Theme.append(outputArea, "Hash    : " + r.getHash() + "\n", Theme.MUTED);
                Theme.append(outputArea, "-------------------------------------\n", Theme.MUTED);
            }

        } catch (Exception e) {
            outputArea.setText("");
            Theme.append(outputArea, "Error: " + e.getMessage() + "\n", Theme.DANGER, true);
        }
    }

    private void verifyAll() {

        try {
            ArrayList<Record> records = recordManager.getAllRecords(uid);

            outputArea.setText("");

            if (records.isEmpty()) {
                Theme.append(outputArea, "No records yet.\n", Theme.MUTED);
                return;
            }

            for (Record r : records) {
                boolean ok = recordManager.verifyRecord(r);

                Theme.append(outputArea, "Record ID: " + r.getRecordId() + " (" + r.getTitle() + ")\n", Theme.PRIMARY, true);
                Theme.append(outputArea, "  Stored Hash     : " + r.getHash() + "\n", Theme.MUTED);
                Theme.append(outputArea, "  Calculated Hash : " + HashUtil.generateHash(r.getContent()) + "\n", Theme.MUTED);
                if (ok) {
                    Theme.append(outputArea, "  Result          : VALID\n", Theme.SUCCESS, true);
                } else {
                    Theme.append(outputArea, "  Result          : TAMPERED\n", Theme.DANGER, true);
                }
                Theme.append(outputArea, "-------------------------------------\n", Theme.MUTED);
            }

        } catch (Exception e) {
            outputArea.setText("");
            Theme.append(outputArea, "Error: " + e.getMessage() + "\n", Theme.DANGER, true);
        }
    }
}
