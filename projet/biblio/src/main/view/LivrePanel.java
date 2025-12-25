package biblio.view;

import biblio.controller.MainController;
import biblio.model.Livre;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LivrePanel extends JPanel {
    private MainController mainController;
    private JTable livreTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public LivrePanel(MainController mainController) {
        this.mainController = mainController;
        initUI();
        chargerLivres();
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
        searchButton.addActionListener(e -> rechercherLivres());
        searchPanel.add(searchButton);
        
        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(e -> {
            searchField.setText("");
            chargerLivres();
        });
        searchPanel.add(resetButton);

        add(searchPanel, BorderLayout.NORTH);

        // Table des livres
        String[] columns = {"ISBN", "Titre", "Auteur", "Catégorie", "Exemplaires", "Disponibles"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table non éditable
            }
        };
        
        livreTable = new JTable(tableModel);
        livreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(livreTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons (seulement pour admin)
        if (mainController.getAuthController().estAdmin()) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton addButton = new JButton("Ajouter");
            addButton.addActionListener(e -> ouvrirAjouterLivreDialog());
            buttonPanel.add(addButton);
            
            JButton editButton = new JButton("Modifier");
            editButton.addActionListener(e -> ouvrirModifierLivreDialog());
            buttonPanel.add(editButton);
            
            JButton deleteButton = new JButton("Supprimer");
            deleteButton.addActionListener(e -> supprimerLivre());
            buttonPanel.add(deleteButton);
            
            JButton refreshButton = new JButton("Actualiser");
            refreshButton.addActionListener(e -> chargerLivres());
            buttonPanel.add(refreshButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void chargerLivres() {
        tableModel.setRowCount(0);
        List<Livre> livres = mainController.getLivreController().listerTousLesLivres();
        
        for (Livre livre : livres) {
            Object[] row = {
                livre.getIsbn(),
                livre.getTitre(),
                livre.getAuteur(),
                livre.getCategorie(),
                livre.getExemplairesTotaux(),
                livre.getExemplairesDisponibles()
            };
            tableModel.addRow(row);
        }
    }

    private void rechercherLivres() {
        String recherche = searchField.getText().trim();
        if (recherche.isEmpty()) {
            chargerLivres();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Livre> livres = mainController.getLivreController().rechercherParTitre(recherche);
        
        if (livres.isEmpty()) {
            livres = mainController.getLivreController().rechercherParAuteur(recherche);
        }
        
        for (Livre livre : livres) {
            Object[] row = {
                livre.getIsbn(),
                livre.getTitre(),
                livre.getAuteur(),
                livre.getCategorie(),
                livre.getExemplairesTotaux(),
                livre.getExemplairesDisponibles()
            };
            tableModel.addRow(row);
        }
    }

    private void ouvrirAjouterLivreDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un livre", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField isbnField = new JTextField();
        JTextField titreField = new JTextField();
        JTextField auteurField = new JTextField();
        JTextField categorieField = new JTextField();
        JSpinner exemplairesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        
        panel.add(new JLabel("ISBN*:"));
        panel.add(isbnField);
        panel.add(new JLabel("Titre*:"));
        panel.add(titreField);
        panel.add(new JLabel("Auteur*:"));
        panel.add(auteurField);
        panel.add(new JLabel("Catégorie:"));
        panel.add(categorieField);
        panel.add(new JLabel("Nombre d'exemplaires*:"));
        panel.add(exemplairesSpinner);
        
        JButton validerButton = new JButton("Valider");
        JButton annulerButton = new JButton("Annuler");
        
        validerButton.addActionListener(e -> {
            if (isbnField.getText().trim().isEmpty() || 
                titreField.getText().trim().isEmpty() || 
                auteurField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir les champs obligatoires (*)", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Livre livre = new Livre(
                isbnField.getText().trim(),
                titreField.getText().trim(),
                auteurField.getText().trim(),
                categorieField.getText().trim(),
                (int) exemplairesSpinner.getValue()
            );
            
            if (mainController.getLivreController().ajouterLivre(livre)) {
                JOptionPane.showMessageDialog(dialog, "Livre ajouté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerLivres();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout. ISBN peut-être déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        annulerButton.addActionListener(e -> dialog.dispose());
        
        panel.add(validerButton);
        panel.add(annulerButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void ouvrirModifierLivreDialog() {
        int selectedRow = livreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à modifier", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        Livre livre = mainController.getLivreController().rechercherParIsbn(isbn);
        
        if (livre == null) {
            JOptionPane.showMessageDialog(this, "Livre non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier le livre", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField isbnField = new JTextField(livre.getIsbn());
        isbnField.setEditable(false);
        JTextField titreField = new JTextField(livre.getTitre());
        JTextField auteurField = new JTextField(livre.getAuteur());
        JTextField categorieField = new JTextField(livre.getCategorie());
        JSpinner exemplairesSpinner = new JSpinner(new SpinnerNumberModel(livre.getExemplairesTotaux(), 1, 100, 1));
        
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Titre*:"));
        panel.add(titreField);
        panel.add(new JLabel("Auteur*:"));
        panel.add(auteurField);
        panel.add(new JLabel("Catégorie:"));
        panel.add(categorieField);
        panel.add(new JLabel("Nombre d'exemplaires*:"));
        panel.add(exemplairesSpinner);
        
        JButton validerButton = new JButton("Valider");
        JButton annulerButton = new JButton("Annuler");
        
        validerButton.addActionListener(e -> {
            if (titreField.getText().trim().isEmpty() || auteurField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir les champs obligatoires (*)", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            livre.setTitre(titreField.getText().trim());
            livre.setAuteur(auteurField.getText().trim());
            livre.setCategorie(categorieField.getText().trim());
            livre.setExemplairesTotaux((int) exemplairesSpinner.getValue());
            
            if (mainController.getLivreController().modifierLivre(livre)) {
                JOptionPane.showMessageDialog(dialog, "Livre modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerLivres();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        annulerButton.addActionListener(e -> dialog.dispose());
        
        panel.add(validerButton);
        panel.add(annulerButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void supprimerLivre() {
        int selectedRow = livreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à supprimer", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        String titre = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer le livre:\n" + titre + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainController.getLivreController().supprimerLivre(isbn)) {
                JOptionPane.showMessageDialog(this, "Livre supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerLivres();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de supprimer le livre.\nIl peut avoir des exemplaires en prêt.",
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}