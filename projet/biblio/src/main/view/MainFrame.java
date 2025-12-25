package biblio.view;
import biblio.controller.MainController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private MainController mainController;
    private JTabbedPane tabbedPane;

    public MainFrame(MainController mainController) {
        this.mainController = mainController;
        initUI();
    }

    private void initUI() {
        setTitle("Gestion Bibliothèque - " + 
                (mainController.getAuthController().estAdmin() ? "Administrateur" : "Utilisateur"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Fichier
        JMenu fichierMenu = new JMenu("Fichier");
        JMenuItem quitterItem = new JMenuItem("Quitter");
        quitterItem.addActionListener(e -> System.exit(0));
        fichierMenu.add(quitterItem);
        menuBar.add(fichierMenu);

        // Menu Aide
        JMenu aideMenu = new JMenu("Aide");
        JMenuItem aproposItem = new JMenuItem("À propos");
        aproposItem.addActionListener(e -> 
            JOptionPane.showMessageDialog(this,
                "Gestion Bibliothèque Universitaire\nVersion 1.0\n© EST Meknes 2025",
                "À propos",
                JOptionPane.INFORMATION_MESSAGE));
        aideMenu.add(aproposItem);
        menuBar.add(aideMenu);

        setJMenuBar(menuBar);

        // TabbedPane pour les différentes fonctionnalités
        tabbedPane = new JTabbedPane();

        // Onglets pour administrateur
        if (mainController.getAuthController().estAdmin()) {
            tabbedPane.addTab("📚 Livres", new LivrePanel(mainController));
            tabbedPane.addTab("👥 Adhérents", new AdherentPanel(mainController));
            tabbedPane.addTab("📖 Emprunts", new EmpruntPanel(mainController));
            tabbedPane.addTab("👤 Utilisateurs", new UtilisateurPanel(mainController));
        } else {
            // Onglets pour utilisateur normal
            tabbedPane.addTab("📚 Catalogue", new LivrePanel(mainController));
            tabbedPane.addTab("📖 Mes emprunts", new MesEmpruntsPanel(mainController));
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Barre d'état
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        String userInfo = mainController.getAuthController().getUtilisateurConnecte().getLogin();
        JLabel statusLabel = new JLabel("Connecté en tant que: " + userInfo);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.addActionListener(e -> {
            mainController.getAuthController().deconnecter();
            dispose();
            new LoginFrame(mainController).setVisible(true);
        });
        statusPanel.add(logoutButton, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
}