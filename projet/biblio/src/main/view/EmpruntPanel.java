package biblio.view;


import biblio.controller.MainController;
import biblio.model.Emprunt;
import biblio.model.Livre;
import biblio.model.Adherent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmpruntPanel extends JPanel {
    private MainController mainController;
    private JTable empruntTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> adherentCombo;
    private JComboBox<String> livreCombo;

    public EmpruntPanel(MainController mainController) {
        this.mainController = mainController;
        initUI();
        chargerEmprunts();
        chargerCombos();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel supérieur pour nouvel emprunt
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Nouvel emprunt"));
        
        topPanel.add(new JLabel("Adhérent:"));
        adherentCombo = new JComboBox<>();
        topPanel.add(adherentCombo);
        
        topPanel.add(new JLabel("Livre:"));
        livreCombo = new JComboBox<>();
        topPanel.add(livreCombo);
        
        JButton emprunterButton = new JButton("Créer emprunt");
        emprunterButton.addActionListener(e -> creerEmprunt());
        topPanel.add(emprunterButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Table des emprunts
        String[] columns = {"ID", "Livre", "Adhérent", "Date emprunt", "Retour prévu", "Retour effectif", "Statut", "Retard (j)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        empruntTable = new JTable(tableModel);
        empruntTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(empruntTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton retourButton = new JButton("Marquer comme retourné");
        retourButton.addActionListener(e -> retournerLivre());
        buttonPanel.add(retourButton);
        
        JButton retardsButton = new JButton("Voir les retards");
        retardsButton.addActionListener(e -> afficherRetards());
        buttonPanel.add(retardsButton);
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> {
            chargerEmprunts();
            chargerCombos();
        });
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerEmprunts() {
        tableModel.setRowCount(0);
        List<Emprunt> emprunts = mainController.getEmpruntController().listerEmpruntsActifs();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Emprunt emprunt : emprunts) {
            String retourEffectif = emprunt.getDateRetourReelle() != null ? 
                emprunt.getDateRetourReelle().format(formatter) : "En cours";
            
            int retard = emprunt.calculerRetard();
            String statut = retard > 0 ? "⚠️ Retard" : "✅ Actif";
            
            Object[] row = {
                emprunt.getId(),
                emprunt.getLivre().getTitre(),
                emprunt.getAdherent().getNom() + " " + emprunt.getAdherent().getPrenom(),
                emprunt.getDateEmprunt().format(formatter),
                emprunt.getDateRetourPrevue().format(formatter),
                retourEffectif,
                statut,
                retard
            };
            tableModel.addRow(row);
        }
    }

    private void chargerCombos() {
        adherentCombo.removeAllItems();
        livreCombo.removeAllItems();
        
        // Charger les adhérents non bloqués
        List<Adherent> adherents = mainController.getAdherentController().listerTousLesAdherents();
        for (Adherent adherent : adherents) {
            if (!adherent.isBloque() && adherent.getNbEmpruntsActuels() < 3) {
                adherentCombo.addItem(adherent.getId() + " - " + adherent.getNom() + " " + adherent.getPrenom());
            }
        }
        
        // Charger les livres disponibles
        List<Livre> livres = mainController.getLivreController().listerTousLesLivres();
        for (Livre livre : livres) {
            if (livre.getExemplairesDisponibles() > 0) {
                livreCombo.addItem(livre.getIsbn() + " - " + livre.getTitre());
            }
        }
    }

    private void creerEmprunt() {
        String adherentSelection = (String) adherentCombo.getSelectedItem();
        String livreSelection = (String) livreCombo.getSelectedItem();
        
        if (adherentSelection == null || livreSelection == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un adhérent et un livre", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extraire l'ID de l'adhérent et l'ISBN du livre
        int adherentId = Integer.parseInt(adherentSelection.split(" - ")[0]);
        String isbn = livreSelection.split(" - ")[0];
        
        if (mainController.getEmpruntController().creerEmprunt(adherentId, isbn)) {
            JOptionPane.showMessageDialog(this, "Emprunt créé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerEmprunts();
            chargerCombos();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Impossible de créer l'emprunt.\n" +
                "Vérifiez que l'adhérent peut emprunter et que le livre est disponible.",
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void retournerLivre() {
        int selectedRow = empruntTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à retourner", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int empruntId = (int) tableModel.getValueAt(selectedRow, 0);
        String livreTitre = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir marquer comme retourné:\n" + livreTitre + " ?",
                "Confirmation de retour",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainController.getEmpruntController().retournerLivre(empruntId)) {
                JOptionPane.showMessageDialog(this, "Livre retourné avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerEmprunts();
                chargerCombos();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors du retour", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void afficherRetards() {
        List<Emprunt> retards = mainController.getEmpruntController().listerRetards();
        
        if (retards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun retard à signaler", "Retards", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder("Emprunts en retard:\n\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Emprunt emprunt : retards) {
            int retard = emprunt.calculerRetard();
            message.append(String.format("- %s emprunté par %s %s\n  Retard: %d jours (retour prévu le %s)\n\n",
                emprunt.getLivre().getTitre(),
                emprunt.getAdherent().getNom(),
                emprunt.getAdherent().getPrenom(),
                retard,
                emprunt.getDateRetourPrevue().format(formatter)));
        }
        
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Emprunts en retard", JOptionPane.WARNING_MESSAGE);
    }
}