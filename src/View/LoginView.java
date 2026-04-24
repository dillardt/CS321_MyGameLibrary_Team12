package View;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

/**
 * Login screen. Contains username/password inputs and three action buttons.
 * All layout matches the original; status label provides user feedback.
 */
public class LoginView extends JPanel {

    private final JTextField     usernameField;
    private final JPasswordField passwordField;
    private final JButton        loginButton;
    private final JButton        createAccountButton;
    private final JButton        guestLoginButton;
    private final JLabel         statusLabel;

    public LoginView() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("My Game Library", SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username row
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(18);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password row
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(18);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Login + Create Account buttons
        loginButton = new JButton("Login");
        gbc.gridy = 3; gbc.gridx = 0;
        add(loginButton, gbc);
        createAccountButton = new JButton("Create Account");
        gbc.gridx = 1;
        add(createAccountButton, gbc);

        // Guest button
        guestLoginButton = new JButton("Continue as Guest");
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(guestLoginButton, gbc);

        // Status message label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        gbc.gridy = 5;
        add(statusLabel, gbc);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** Returns the trimmed username text. */
    public String getUsernameInput() {
        return usernameField.getText().trim();
    }

    /** Returns the entered password. */
    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    /** Clears only the password field. */
    public void clearPassword() {
        passwordField.setText("");
    }

    /** Clears both fields and the status label. */
    public void clearAllInputs() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }

    /**
     * Displays a status message.
     *
     * @param message the text to show
     * @param isError true = red, false = green
     */
    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message == null || message.isBlank() ? " " : message);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 128, 0));
    }

    // ── Listener registration ─────────────────────────────────────────────────

    public void addLoginListener(ActionListener l)         { loginButton.addActionListener(l); }
    public void addCreateAccountListener(ActionListener l) { createAccountButton.addActionListener(l); }
    public void addGuestLoginListener(ActionListener l)    { guestLoginButton.addActionListener(l); }
}