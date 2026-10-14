import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(Theme.BACKGROUND);
        Theme.addPadding(main, 15);
        setContentPane(main);

        JLabel heading = new JLabel("Create Account");
        heading.setFont(Theme.TITLE_FONT);
        heading.setForeground(Theme.TEAL);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBackground(Theme.BACKGROUND);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Theme.TEXT);
        usernameField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Theme.TEXT);
        passwordField = new JPasswordField(15);

        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setForeground(Theme.TEXT);
        confirmField = new JPasswordField(15);

        form.add(userLabel);
        form.add(usernameField);
        form.add(passLabel);
        form.add(passwordField);
        form.add(confirmLabel);
        form.add(confirmField);
        // One wrapper in NORTH: heading on top, form below.
        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setBackground(Theme.BACKGROUND);
        top.add(heading, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);
        main.add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBackground(Theme.BACKGROUND);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setBackground(Theme.BACKGROUND);

        registerButton = new JButton("Register");
        Theme.styleButton(registerButton, Theme.SUCCESS);
        buttons.add(registerButton);
        bottom.add(buttons, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Theme.DANGER);
        bottom.add(statusLabel, BorderLayout.SOUTH);

        main.add(bottom, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> tryRegister());

        pack();
        setSize(400, 280);
        setLocationRelativeTo(null);
    }

    private void tryRegister() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            Theme.showStatus(statusLabel, "Username and password cannot be empty.", false);
            return;
        }

        if (!password.equals(confirm)) {
            Theme.showStatus(statusLabel, "Passwords do not match.", false);
            return;
        }

        try {
            // Check BEFORE registering: only the very first user removes the fallback.
            boolean hadFallback = false;
            try {
                hadFallback = userManager.userExists("admin");
            } catch (Exception ignored) {
            }

            boolean created = userManager.register(username, password);

            if (created) {
                String message = "Account '" + username + "' created! Please login.";
                if (hadFallback) {
                    message += "\nFallback admin removed for safety.";
                }
                Theme.showStatus(statusLabel, "Registered! You can now login.", true);
                javax.swing.JOptionPane.showMessageDialog(this, message);
                dispose();
            } else {
                Theme.showStatus(statusLabel, "That username is already taken.", false);
            }

        } catch (Exception e) {
            Theme.showStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }
}
