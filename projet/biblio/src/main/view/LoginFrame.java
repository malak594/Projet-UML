package biblio.view;

import biblio.controller.MainController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private MainController mainController;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame(MainController mainController) {
        this.mainController = mainController;
        initUI();
    }

    private void initUI() {
        setTitle("Connexion - Gestion Bibliothèque");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Centre la fenêtre
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel du titre
        JLabel titleLabel = new JLabel("Gestion Bibliothèque Universitaire", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel du formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Identifiants de connexion"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        loginField = new JTextField(15);
        formPanel.add(loginField, gbc);

        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Bouton de connexion
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Se connecter");
        loginButton.addActionListener(new LoginListener());
        formPanel.add(loginButton, gbc);

        // Informations par défaut
        gbc.gridy = 3;
        JLabel infoLabel = new JLabel("Admin: admin / admin123");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        formPanel.add(infoLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Raccourci clavier Entrée pour connexion
        getRootPane().setDefaultButton(loginButton);
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Veuillez remplir tous les champs",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (mainController.getAuthController().connecter(login, password)) {
                // Connexion réussie
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Connexion réussie!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Ouvrir la fenêtre principale
                dispose(); // Fermer la fenêtre de login
                new MainFrame(mainController).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Login ou mot de passe incorrect",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }
}