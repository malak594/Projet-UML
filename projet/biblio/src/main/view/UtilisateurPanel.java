package biblio.view;

import biblio.controller.MainController;
import biblio.model.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UtilisateurPanel extends JPanel {
    private MainController mainController;
    private JTable utilisateurTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public UtilisateurPanel(MainController mainController) {
        this.mainController = mainController;
        initUI();
        chargerUtilisateurs();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Rechercher:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> rechercherUtilisateurs());
        searchPanel.add(searchButton);
        
        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(e -> {
            searchField.setText("");
            chargerUtilisateurs();
        });
        searchPanel.add(resetButton);

        add(searchPanel, BorderLayout.NORTH);

        // Table des utilisateurs
        String[] columns = {"ID", "Login", "Rôle", "Adhérent", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        utilisateurTable = new JTable(tableModel);
        utilisateurTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(utilisateurTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addButton = new JButton("Ajouter utilisateur");
        addButton.addActionListener(e -> ouvrirAjouterUtilisateurDialog());
        buttonPanel.add(addButton);
        
        JButton editButton = new JButton("Modifier rôle");
        editButton.addActionListener(e -> ouvrirModifierRoleDialog());
        buttonPanel.add(editButton);
        
        JButton toggleButton = new JButton("Activer/Désactiver");
        toggleButton.addActionListener(e -> activerDesactiverUtilisateur());
        buttonPanel.add(toggleButton);
        
        JButton resetPasswordButton = new JButton("Réinitialiser mot de passe");
        resetPasswordButton.addActionListener(e -> reinitialiserMotDePasse());
        buttonPanel.add(resetPasswordButton);
        
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> supprimerUtilisateur());
        buttonPanel.add(deleteButton);
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> chargerUtilisateurs());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerUtilisateurs() {
        tableModel.setRowCount(0);
        List<Utilisateur> utilisateurs = mainController.getUtilisateurController().listerTousLesUtilisateurs();
        
        for (Utilisateur utilisateur : utilisateurs) {
            String adherentInfo = utilisateur.getAdherent() != null ? 
                utilisateur.getAdherent().getNom() + " " + utilisateur.getAdherent().getPrenom() : "N/A";
            
            String statut = utilisateur.isActif() ? "✅ Actif" : "❌ Inactif";
            
            Object[] row = {
                utilisateur.getId(),
                utilisateur.getLogin(),
                utilisateur.getRole(),
                adherentInfo,
                statut
            };
            tableModel.addRow(row);
        }
    }

    private void rechercherUtilisateurs() {
        String recherche = searchField.getText().trim();
        if (recherche.isEmpty()) {
            chargerUtilisateurs();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Utilisateur> tous = mainController.getUtilisateurController().listerTousLesUtilisateurs();
        String rechercheLower = recherche.toLowerCase();
        
        for (Utilisateur utilisateur : tous) {
            if (utilisateur.getLogin().toLowerCase().contains(rechercheLower) ||
                utilisateur.getRole().toLowerCase().contains(rechercheLower) ||
                (utilisateur.getAdherent() != null && 
                 (utilisateur.getAdherent().getNom().toLowerCase().contains(rechercheLower) ||
                  utilisateur.getAdherent().getPrenom().toLowerCase().contains(rechercheLower)))) {
                
                String adherentInfo = utilisateur.getAdherent() != null ? 
                    utilisateur.getAdherent().getNom() + " " + utilisateur.getAdherent().getPrenom() : "N/A";
                
                String statut = utilisateur.isActif() ? "✅ Actif" : "❌ Inactif";
                
                Object[] row = {
                    utilisateur.getId(),
                    utilisateur.getLogin(),
                    utilisateur.getRole(),
                    adherentInfo,
                    statut
                };
                tableModel.addRow(row);
            }
        }
    }

    private void ouvrirAjouterUtilisateurDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Ajouter un utilisateur", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField loginField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});
        JCheckBox actifCheckBox = new JCheckBox("Actif", true);
        
        panel.add(new JLabel("Login*:"));
        panel.add(loginField);
        panel.add(new JLabel("Mot de passe*:"));
        panel.add(passwordField);
        panel.add(new JLabel("Rôle*:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Statut:"));
        panel.add(actifCheckBox);
        
        JButton validerButton = new JButton("Valider");
        JButton annulerButton = new JButton("Annuler");
        
        validerButton.addActionListener(e -> {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Veuillez remplir tous les champs obligatoires (*)", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String role = (String) roleCombo.getSelectedItem();
            
            if (mainController.getUtilisateurController().creerUtilisateur(login, password, role)) {
                // Désactiver si la checkbox n'est pas cochée
                if (!actifCheckBox.isSelected()) {
                    mainController.getUtilisateurController().changerStatutUtilisateur(login, false);
                }
                
                JOptionPane.showMessageDialog(dialog, 
                    "Utilisateur créé avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerUtilisateurs();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Erreur lors de la création. Le login peut-être déjà utilisé.", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        annulerButton.addActionListener(e -> dialog.dispose());
        
        panel.add(validerButton);
        panel.add(annulerButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void ouvrirModifierRoleDialog() {
        int selectedRow = utilisateurTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un utilisateur à modifier", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String login = (String) tableModel.getValueAt(selectedRow, 1);
        String roleActuel = (String) tableModel.getValueAt(selectedRow, 2);
        Utilisateur utilisateur = mainController.getUtilisateurController().rechercherParLogin(login);
        
        if (utilisateur == null) {
            JOptionPane.showMessageDialog(this, "Utilisateur non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Ne pas permettre de modifier l'admin principal
        if ("admin".equals(login)) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de modifier le rôle de l'administrateur principal", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Modifier le rôle", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Utilisateur:"));
        panel.add(new JLabel(login));
        
        panel.add(new JLabel("Nouveau rôle:"));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleCombo.setSelectedItem(roleActuel);
        panel.add(roleCombo);
        
        JButton validerButton = new JButton("Valider");
        JButton annulerButton = new JButton("Annuler");
        
        validerButton.addActionListener(e -> {
            String nouveauRole = (String) roleCombo.getSelectedItem();
            utilisateur.setRole(nouveauRole);
            
            if (mainController.getUtilisateurController().modifierUtilisateur(utilisateur)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Rôle modifié avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerUtilisateurs();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Erreur lors de la modification", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        annulerButton.addActionListener(e -> dialog.dispose());
        
        panel.add(validerButton);
        panel.add(annulerButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void activerDesactiverUtilisateur() {
        int selectedRow = utilisateurTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un utilisateur", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String login = (String) tableModel.getValueAt(selectedRow, 1);
        String statut = (String) tableModel.getValueAt(selectedRow, 4);
        boolean estActif = statut.contains("✅");
        
        // Ne pas permettre de désactiver l'admin principal
        if ("admin".equals(login) && estActif) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de désactiver l'administrateur principal", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String action = estActif ? "désactiver" : "activer";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir " + action + " l'utilisateur " + login + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainController.getUtilisateurController().changerStatutUtilisateur(login, !estActif)) {
                JOptionPane.showMessageDialog(this, 
                    "Utilisateur " + action + " avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerUtilisateurs();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'opération", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reinitialiserMotDePasse() {
        int selectedRow = utilisateurTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un utilisateur", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String login = (String) tableModel.getValueAt(selectedRow, 1);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Réinitialiser le mot de passe", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Utilisateur:"));
        panel.add(new JLabel(login));
        
        panel.add(new JLabel("Nouveau mot de passe*:"));
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);
        
        panel.add(new JLabel("Confirmation*:"));
        JPasswordField confirmField = new JPasswordField();
        panel.add(confirmField);
        
        JButton validerButton = new JButton("Valider");
        JButton annulerButton = new JButton("Annuler");
        JButton genererButton = new JButton("Générer");
        
        genererButton.addActionListener(e -> {
            String motDePasseGenere = biblio.util.PasswordHasher.generateRandomPassword(10);
            passwordField.setText(motDePasseGenere);
            confirmField.setText(motDePasseGenere);
        });
        
        validerButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            
            if (password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Veuillez remplir tous les champs", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Les mots de passe ne correspondent pas", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (mainController.getUtilisateurController().changerMotDePasse(login, password)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Mot de passe réinitialisé avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Erreur lors de la réinitialisation", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        annulerButton.addActionListener(e -> dialog.dispose());
        
        panel.add(genererButton);
        panel.add(new JLabel()); // Espace vide
        panel.add(validerButton);
        panel.add(annulerButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void supprimerUtilisateur() {
        int selectedRow = utilisateurTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un utilisateur à supprimer", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String login = (String) tableModel.getValueAt(selectedRow, 1);
 
        if ("admin".equals(login)) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de supprimer l'administrateur principal", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer l'utilisateur:\n" + login + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainController.getUtilisateurController().supprimerUtilisateur(login)) {
                JOptionPane.showMessageDialog(this, 
                    "Utilisateur supprimé avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerUtilisateurs();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}