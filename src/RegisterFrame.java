import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JButton registerButton;
    private JLabel statusLabel;

    private UserManager userManager;

    public RegisterFrame(UserManager userManager) {

        this.userManager = userManager;

        setTitle("PrivateVaultSimple - Register");
        setSize(360, 260);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 15, 90, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(130, 15, 200, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 50, 90, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(130, 50, 200, 25);
        add(passwordField);

        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setBounds(20, 85, 90, 25);
        add(confirmLabel);

        confirmField = new JPasswordField();
        confirmField.setBounds(130, 85, 200, 25);
        add(confirmField);

        registerButton = new JButton("Register");
        registerButton.setBounds(130, 120, 120, 30);
        add(registerButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(20, 165, 320, 40);
        add(statusLabel);

        registerButton.addActionListener(e -> tryRegister());
    }

    private void tryRegister() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty.");
            return;
        }

        if (!password.equals(confirm)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        try {
            boolean created = userManager.register(username, password);

            if (created) {
                statusLabel.setText("Registered! You can now login.");
                javax.swing.JOptionPane.showMessageDialog(this, "Account created. The fallback admin has been removed.");
                dispose();
            } else {
                statusLabel.setText("That username is already taken.");
            }

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}
