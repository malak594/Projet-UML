package biblio.view;
import biblio.controller.MainController;
import biblio.model.Emprunt;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MesEmpruntsPanel extends JPanel {
    private MainController mainController;
    private JTable empruntTable;
    private DefaultTableModel tableModel;

    public MesEmpruntsPanel(MainController mainController) {
        this.mainController = mainController;
        initUI();
        chargerMesEmprunts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table des emprunts
        String[] columns = {"Livre", "Auteur", "Date emprunt", "Retour prévu", "Statut", "Retard (j)"};
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

        // Panel d'information
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Mes emprunts actuels"));
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> chargerMesEmprunts());
        infoPanel.add(refreshButton);
        
        add(infoPanel, BorderLayout.NORTH);
    }

    private void chargerMesEmprunts() {
        tableModel.setRowCount(0);
        
        // Récupérer l'adhérent connecté
        int adherentId = mainController.getAuthController().getUtilisateurConnecte().getAdherent().getId();
        List<Emprunt> emprunts = mainController.getEmpruntController().listerEmpruntsParAdherent(adherentId);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Emprunt emprunt : emprunts) {
            int retard = emprunt.calculerRetard();
            String statut;
            
            if (emprunt.getDateRetourReelle() != null) {
                statut = "✅ Retourné";
            } else if (retard > 0) {
                statut = "⚠️ Retard";
            } else {
                statut = "📖 En cours";
            }
            
            Object[] row = {
                emprunt.getLivre().getTitre(),
                emprunt.getLivre().getAuteur(),
                emprunt.getDateEmprunt().format(formatter),
                emprunt.getDateRetourPrevue().format(formatter),
                statut,
                retard
            };
            tableModel.addRow(row);
        }
    }
}
