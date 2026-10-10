import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    private UserManager userManager;
    private RecordManager recordManager;

    public LoginFrame(UserManager userManager, RecordManager recordManager) {

        this.userManager = userManager;
        this.recordManager = recordManager;

        setTitle("PrivateVaultSimple - Login");
        setSize(360, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(110, 20, 220, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 55, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(110, 55, 220, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(110, 90, 100, 30);
        add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(220, 90, 100, 30);
        add(registerButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(20, 130, 300, 40);
        add(statusLabel);

        loginButton.addActionListener(e -> tryLogin());
        registerButton.addActionListener(e -> openRegister());
    }

    private void openRegister() {

        RegisterFrame registerFrame = new RegisterFrame(userManager);
        registerFrame.setVisible(true);
    }

    private void tryLogin() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = userManager.login(username, password);

            if (user != null) {
                MainFrame mainFrame = new MainFrame(recordManager, user.getUid());
                mainFrame.setVisible(true);
                dispose();
            } else {
                statusLabel.setText("Wrong username or password.");
            }

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}
