import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UpdateFrame extends JFrame {

    private JTextField idField;
    private JButton loadButton;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton updateButton;
    private JButton closeButton;
    private JLabel statusLabel;

    private RecordManager recordManager;
    private int uid;

    public UpdateFrame(RecordManager recordManager, int uid) {

        this.recordManager = recordManager;
        this.uid = uid;

        setTitle("PrivateVaultSimple - Update Record");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(480, 400));
        setSize(600, 450);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(Theme.BACKGROUND);
        Theme.addPadding(main, 12);
        setContentPane(main);

        JLabel heading = new JLabel("Update Record");
        heading.setFont(Theme.TITLE_FONT);
        heading.setForeground(Theme.TEAL);
        main.add(heading, BorderLayout.NORTH);

        // Center holds the form and stretches with the window.
        JPanel form = new JPanel(new BorderLayout(5, 8));
        form.setBackground(Theme.BACKGROUND);

        JPanel idRow = new JPanel(new BorderLayout(8, 0));
        idRow.setBackground(Theme.BACKGROUND);
        JLabel idLabel = new JLabel("Record ID:");
        idLabel.setForeground(Theme.TEXT);
        idLabel.setPreferredSize(new Dimension(70, 25));
        idField = new JTextField(8);
        loadButton = new JButton("Load");
        Theme.styleButton(loadButton, Theme.PRIMARY);
        idRow.add(idLabel, BorderLayout.WEST);
        idRow.add(idField, BorderLayout.CENTER);
        idRow.add(loadButton, BorderLayout.EAST);

        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setBackground(Theme.BACKGROUND);
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(Theme.TEXT);
        titleLabel.setPreferredSize(new Dimension(70, 25));
        titleField = new JTextField(20);
        titleRow.add(titleLabel, BorderLayout.WEST);
        titleRow.add(titleField, BorderLayout.CENTER);

        JPanel contentRow = new JPanel(new BorderLayout(8, 0));
        contentRow.setBackground(Theme.BACKGROUND);
        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setForeground(Theme.TEXT);
        contentLabel.setPreferredSize(new Dimension(70, 25));
        contentArea = new JTextArea(8, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(contentArea);
        contentRow.add(contentLabel, BorderLayout.WEST);
        contentRow.add(scroll, BorderLayout.CENTER);

        // Top rows stay compact, content gets all extra space.
        JPanel topRows = new JPanel(new java.awt.GridLayout(2, 1, 0, 8));
        topRows.setBackground(Theme.BACKGROUND);
        topRows.add(idRow);
        topRows.add(titleRow);

        form.add(topRows, BorderLayout.NORTH);
        form.add(contentRow, BorderLayout.CENTER);

        main.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBackground(Theme.BACKGROUND);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setBackground(Theme.BACKGROUND);

        updateButton = new JButton("Update Record");
        Theme.styleButton(updateButton, Theme.SUCCESS);

        closeButton = new JButton("Close");
        Theme.styleButton(closeButton, Theme.MUTED);

        buttons.add(updateButton);
        buttons.add(closeButton);
        bottom.add(buttons, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Theme.DANGER);
        bottom.add(statusLabel, BorderLayout.SOUTH);

        main.add(bottom, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadRecord());
        updateButton.addActionListener(e -> updateRecord());
        closeButton.addActionListener(e -> dispose());
    }

    private void loadRecord() {

        try {
            int recordId = Integer.parseInt(idField.getText().trim());

            Record record = recordManager.getRecord(uid, recordId);

            if (record == null) {
                Theme.showStatus(statusLabel, "No record found with ID " + recordId + ".", false);
                return;
            }

            titleField.setText(record.getTitle());
            contentArea.setText(record.getContent());
            Theme.showStatus(statusLabel, "Loaded record " + recordId + ". Edit and click Update.", true);

        } catch (NumberFormatException e) {
            Theme.showStatus(statusLabel, "Please enter a valid numeric ID.", false);
        } catch (Exception e) {
            Theme.showStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }

    private void updateRecord() {

        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            Theme.showStatus(statusLabel, "Load a record first.", false);
            return;
        }

        try {
            int recordId = Integer.parseInt(idText);
            String newTitle = titleField.getText();
            String newContent = contentArea.getText();

            boolean updated = recordManager.updateRecord(uid, recordId, newTitle, newContent);

            if (updated) {
                Record fresh = recordManager.getRecord(uid, recordId);

                String message;
                if (fresh != null && recordManager.verifyRecord(fresh)) {
                    message = "Record " + recordId + " updated. New hash generated. Integrity: VALID.";
                } else {
                    message = "Record " + recordId + " updated. Warning: integrity check failed.";
                }

                JOptionPane.showMessageDialog(this, message);
                dispose();
            } else {
                Theme.showStatus(statusLabel, "Update failed. Check the ID and that fields are not empty.", false);
            }

        } catch (NumberFormatException e) {
            Theme.showStatus(statusLabel, "Please enter a valid numeric ID.", false);
        } catch (Exception e) {
            Theme.showStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }
}
