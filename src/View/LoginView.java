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
 * Login screen component containing username/password inputs and auth actions.
 */
public class LoginView extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton createAccountButton;
    private final JButton guestLoginButton;
    private final JLabel statusLabel;

    /**
     * Constructs and lays out the login view.
     */
    public LoginView() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("My Game Library", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(18);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(18);
        gbc.gridx = 1;
        add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(loginButton, gbc);

        createAccountButton = new JButton("Create Account");
        gbc.gridx = 1;
        add(createAccountButton, gbc);

        guestLoginButton = new JButton("Continue as Guest");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(guestLoginButton, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(statusLabel, gbc);
    }

    /**
     * Returns the username entered by the user.
     *
     * @return trimmed username text
     */
    public String getUsernameInput() {
        return usernameField.getText().trim();
    }

    /**
     * Returns the password entered by the user.
     *
     * @return password text
     */
    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    /**
     * Clears only the password field.
     */
    public void clearPassword() {
        passwordField.setText("");
    }

    /**
     * Clears username, password, and status message.
     */
    public void clearAllInputs() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }

    /**
     * Sets a status message with color based on error state.
     *
     * @param message status text to display
     * @param isError true to use error color, false for success color
     */
    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message == null || message.isBlank() ? " " : message);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 128, 0));
    }

    /**
     * Registers the login button listener.
     *
     * @param listener action listener for login
     */
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    /**
     * Registers the create-account button listener.
     *
     * @param listener action listener for account creation
     */
    public void addCreateAccountListener(ActionListener listener) {
        createAccountButton.addActionListener(listener);
    }

    /**
     * Registers the guest-login button listener.
     *
     * @param listener action listener for guest login
     */
    public void addGuestLoginListener(ActionListener listener) {
        guestLoginButton.addActionListener(listener);
    }
}
