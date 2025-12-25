package biblio.view;

import biblio.controller.MainController;
import biblio.model.Adherent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdherentPanel extends JPanel {
    private MainController mainController;
    private JTable adherentTable;
    private DefaultTableModel tableModel;

    public AdherentPanel(MainController mainController) {
        this.mainController = mainController;
        initUI();
        chargerAdherents();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table des adhérents
        String[] columns = {"ID", "Numéro", "Nom", "Prénom", "Email", "Téléphone", "Inscription", "Emprunts", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        adherentTable = new JTable(tableModel);
        adherentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(adherentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> ouvrirAjouterAdherentDialog());
        buttonPanel.add(addButton);
        
        JButton editButton = new JButton("Modifier");
        editButton.addActionListener(e -> ouvrirModifierAdherentDialog());
        buttonPanel.add(editButton);
        
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> supprimerAdherent());
        buttonPanel.add(deleteButton);
        
        JButton blockButton = new JButton("Bloquer/Débloquer");
        blockButton.addActionListener(e -> bloquerDebloquerAdherent());
        buttonPanel.add(blockButton);
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> chargerAdherents());
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerAdherents() {
        tableModel.setRowCount(0);
        List<Adherent> adherents = mainController.getAdherentController().listerTousLesAdherents();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Adherent adherent : adherents) {
            String statut = adherent.isBloque() ? "❌ Bloqué" : "✅ Actif";
            Object[] row = {
                adherent.getId(),
                adherent.getNumeroUnique(),
                adherent.getNom(),
                adherent.getPrenom(),
                adherent.getEmail(),
                adherent.getTelephone(),
                adherent.getDateInscription().format(formatter),
                adherent.getNbEmpruntsActuels(),
                statut
            };
            tableModel.addRow(row);
        }
    }

    private void ouvrirAjouterAdherentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un adhérent", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField numeroField = new JTextField();
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telephoneField = new JTextField();
        
        panel.add(new JLabel("Numéro unique*:"));
        panel.add(numeroField);
        panel.add(new JLabel("Nom*:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom*:"));
        panel.add(prenomField);
        panel.add(new JLabel("Email*:"));
        panel.add(emailField);
        panel.add(new JLabel("Téléphone:"));
        panel.add(telephoneField);
        
        JButton validerButton = new JButton("Valider");
        JButton annulerButton = new JButton("Annuler");
        
        validerButton.addActionListener(e -> {
            if (numeroField.getText().trim().isEmpty() || 
                nomField.getText().trim().isEmpty() || 
                prenomField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir les champs obligatoires (*)", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Adherent adherent = new Adherent(
                numeroField.getText().trim(),
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                telephoneField.getText().trim()
            );
            
            if (mainController.getAdherentController().ajouterAdherent(adherent)) {
                JOptionPane.showMessageDialog(dialog, "Adhérent ajouté avec succès!\nUn compte utilisateur a été créé avec l'email et le mot de passe 'password123'", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerAdherents();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout. L'email peut-être déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        annulerButton.addActionListener(e -> dialog.dispose());
        
        panel.add(validerButton);
        panel.add(annulerButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void ouvrirModifierAdherentDialog() {
        int selectedRow = adherentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un adhérent à modifier", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Adherent adherent = mainController.getAdherentController().rechercherParId(id);
        
        if (adherent == null) {
            JOptionPane.showMessageDialog(this, "Adhérent non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier l'adhérent", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField numeroField = new JTextField(adherent.getNumeroUnique());
        JTextField nomField = new JTextField(adherent.getNom());
        JTextField prenomField = new JTextField(adherent.getPrenom());
        JTextField emailField = new JTextField(adherent.getEmail());
        JTextField telephoneField = new JTextField(adherent.getTelephone());
        
        panel.add(new JLabel("Numéro unique*:"));
        panel.add(numeroField);
        panel.add(new JLabel("Nom*:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom*:"));
        panel.add(prenomField);
        panel.add(new JLabel("Email*:"));
        panel.add(emailField);
        panel.add(new JLabel("Téléphone:"));
        panel.add(telephoneField);
        
        JButton validerButton = new JButton("Valider");
        JButton annulerButton = new JButton("Annuler");
        
        validerButton.addActionListener(e -> {
            if (numeroField.getText().trim().isEmpty() || 
                nomField.getText().trim().isEmpty() || 
                prenomField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir les champs obligatoires (*)", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            adherent.setNumeroUnique(numeroField.getText().trim());
            adherent.setNom(nomField.getText().trim());
            adherent.setPrenom(prenomField.getText().trim());
            adherent.setEmail(emailField.getText().trim());
            adherent.setTelephone(telephoneField.getText().trim());
            
            if (mainController.getAdherentController().modifierAdherent(adherent)) {
                JOptionPane.showMessageDialog(dialog, "Adhérent modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerAdherents();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification. L'email peut-être déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        annulerButton.addActionListener(e -> dialog.dispose());
        
        panel.add(validerButton);
        panel.add(annulerButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void supprimerAdherent() {
        int selectedRow = adherentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un adhérent à supprimer", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 2);
        String prenom = (String) tableModel.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer l'adhérent:\n" + nom + " " + prenom + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainController.getAdherentController().supprimerAdherent(id)) {
                JOptionPane.showMessageDialog(this, "Adhérent supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerAdherents();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de supprimer l'adhérent.\nIl peut avoir des emprunts en cours.",
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bloquerDebloquerAdherent() {
        int selectedRow = adherentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un adhérent", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String statut = (String) tableModel.getValueAt(selectedRow, 8);
        boolean estBloque = statut.contains("❌");
        
        String action = estBloque ? "débloquer" : "bloquer";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir " + action + " cet adhérent ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainController.getAdherentController().bloquerAdherent(id, !estBloque)) {
                JOptionPane.showMessageDialog(this, "Adhérent " + action + " avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerAdherents();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'opération", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}