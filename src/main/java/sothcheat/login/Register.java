package sothcheat.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import sothcheat.manager.FormsManager;
import sothcheat.services.RegistrationResult;
import sothcheat.services.UserService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Register extends JPanel {
    public Register() {
        init();
    }

    private JLabel userNameLabel;
    private JTextField userNameField;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel confirmPassLabel;
    private JPasswordField confirmPassField;
    private JButton registerButton;
    private JCheckBox agreementCheckBox;

    private void registerButtonClicked() {
        String userName = userNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!agreementCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "You must agree to the terms.", "Agreement required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Password mismatch",
                    JOptionPane.ERROR_MESSAGE);
        }

        UserService us = new UserService();
        RegistrationResult r = us.registerUser(userName, email, password);

        if (r.success) {
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            LoginContainer container = LoginContainer.getInstance();
            if (container != null) {
                container.showForm(new Login());
            } else {
                FormsManager.getInstance().showForm(new Login());
            }
        } else {
            JOptionPane.showMessageDialog(this, r.message, "Registration failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void init() {
        setBackground(Color.WHITE);
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        userNameLabel = new JLabel("Username:");
        userNameField = new JTextField();
        emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        confirmPassLabel = new JLabel("Confirm password:");
        confirmPassField = new JPasswordField();
        registerButton = new JButton("REGISTER");
        agreementCheckBox = new JCheckBox("By registering your details, you agree to save your information with us.");

        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[fill, 420]"));
        panel.setBackground(Color.WHITE);
        panel.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:0;");

        JLabel titleLabel = new JLabel("Create an account");
        JLabel descriptionLabel = new JLabel("Join us and take control of your spending!");
        titleLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "font:bold +23;" +
                "foreground:#3D3828");
        descriptionLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "[light]foreground:lighten(@foreground,30%)");

        userNameLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "foreground:#3D3828");
        userNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Enter your username");
        emailLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "foreground:#3D3828");
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Enter your email");
        passwordLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "foreground:#3D3828");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Enter your password");
        passwordField.putClientProperty(FlatClientProperties.STYLE,"" +
                "showRevealButton:true");
        confirmPassLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "foreground:#3D3828");
        confirmPassField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Re-enter your password");
        confirmPassField.putClientProperty(FlatClientProperties.STYLE,"" +
                "showRevealButton:true");

        registerButton.putClientProperty(FlatClientProperties.STYLE,"" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        registerButton.putClientProperty("JComponent.minimumHeight", 36);
        agreementCheckBox.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:-2");

        registerButton.addActionListener(e -> registerButtonClicked());

        panel.add(titleLabel);
        panel.add(descriptionLabel);

        panel.add(userNameLabel, "gapy 24");
        panel.add(userNameField, "wrap, h 36!");
        panel.add(emailLabel, "gapy 8");
        panel.add(emailField, "wrap, h 36!");

        JPanel passwordPanel = new JPanel(new MigLayout("insets 0", "[fill][fill]", "[][]"));
        passwordPanel.setOpaque(false);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(confirmPassLabel, "wrap");
        passwordPanel.add(passwordField,"w 230!, h 36!");
        passwordPanel.add(confirmPassField, "w 230!, h 36!");
        panel.add(passwordPanel, "gapy 8");

        panel.add(agreementCheckBox, "gapy 8");

        panel.add(registerButton, "gapy 20");
        panel.add(createSignInLabel(), "gapy 10");

        add(panel);
    }

    private Component createSignInLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));

        Color normal = new Color(0x3D3828);
        Color hover = new Color(0x4876FD);

        panel.putClientProperty(FlatClientProperties.STYLE,"" +
                "background:null");
        JButton signInButton = new JButton("Sign in"); //<html><a href="#">Register Now!</a></html>
        signInButton.setContentAreaFilled(false);
        signInButton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        signInButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        signInButton.setForeground(normal);

        signInButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signInButton.setForeground(hover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signInButton.setForeground(normal);
            }
        });

        signInButton.addActionListener(actionEvent -> {
            LoginContainer container = LoginContainer.getInstance();
            if (container != null) {
                container.showForm(new Login());
            } else {
                FormsManager.getInstance().showForm(new Login());
            }
        });
        JLabel label = new JLabel("Already have an account?");
        label.putClientProperty(FlatClientProperties.STYLE,"" +
                "[light]foreground:lighten(@foreground,30%)");
        panel.add(label);
        panel.add(signInButton);

        return panel;
    }
}
