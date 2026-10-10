import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

    public UpdateFrame(RecordManager recordManager) {

        this.recordManager = recordManager;

        setTitle("PrivateVaultSimple - Update Record");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel idLabel = new JLabel("Record ID:");
        idLabel.setBounds(20, 15, 70, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(100, 15, 120, 25);
        add(idField);

        loadButton = new JButton("Load");
        loadButton.setBounds(230, 15, 90, 25);
        add(loadButton);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(20, 55, 70, 25);
        add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(100, 55, 470, 25);
        add(titleField);

        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setBounds(20, 95, 70, 25);
        add(contentLabel);

        contentArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBounds(100, 95, 470, 180);
        add(scroll);

        updateButton = new JButton("Update Record");
        updateButton.setBounds(100, 290, 140, 30);
        add(updateButton);

        closeButton = new JButton("Close");
        closeButton.setBounds(250, 290, 100, 30);
        add(closeButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(20, 335, 540, 25);
        add(statusLabel);

        loadButton.addActionListener(e -> loadRecord());
        updateButton.addActionListener(e -> updateRecord());
        closeButton.addActionListener(e -> dispose());
    }

    private void loadRecord() {

        try {
            int recordId = Integer.parseInt(idField.getText().trim());

            Record record = recordManager.getRecord(recordId);

            if (record == null) {
                statusLabel.setText("No record found with ID " + recordId + ".");
                return;
            }

            titleField.setText(record.getTitle());
            contentArea.setText(record.getContent());
            statusLabel.setText("Loaded record " + recordId + ". Edit and click Update.");

        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid numeric ID.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void updateRecord() {

        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            statusLabel.setText("Load a record first.");
            return;
        }

        try {
            int recordId = Integer.parseInt(idText);
            String newTitle = titleField.getText();
            String newContent = contentArea.getText();

            boolean updated = recordManager.updateRecord(recordId, newTitle, newContent);

            if (updated) {
                Record fresh = recordManager.getRecord(recordId);

                String message;
                if (fresh != null && recordManager.verifyRecord(fresh)) {
                    message = "Record " + recordId + " updated. New hash generated. Integrity: VALID.";
                } else {
                    message = "Record " + recordId + " updated. Warning: integrity check failed.";
                }

                JOptionPane.showMessageDialog(this, message);
                dispose();
            } else {
                statusLabel.setText("Update failed. Check the ID and that fields are not empty.");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid numeric ID.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}
