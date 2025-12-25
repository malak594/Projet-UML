package biblio.controller;

import biblio.dao.*;
import biblio.util.DatabaseConnection;

public class MainController {
    private AuthController authController;
    private LivreController livreController;
    private AdherentController adherentController;
    private EmpruntController empruntController;
    private UtilisateurController utilisateurController;

    public MainController() {
        System.out.println("=== DÉMARRAGE APPLICATION ===");
        
        // Initialiser la connexion à la base de données
        DatabaseConnection.initialize();
        System.out.println("✓ Connexion DB établie");
        
        // Initialiser les DAO
        LivreDAO livreDAO = new LivreDAOImpl(DatabaseConnection.getConnection());
        System.out.println("✓ LivreDAO créé");
        
        AdherentDAO adherentDAO = new AdherentDAOImpl(DatabaseConnection.getConnection());
        System.out.println("✓ AdherentDAO créé");
        
        EmpruntDAO empruntDAO = new EmpruntDAOImpl(
            DatabaseConnection.getConnection(), 
            livreDAO, 
            adherentDAO
        );
        System.out.println("✓ EmpruntDAO créé");
        
        System.out.println("--- Création UtilisateurDAO (va créer admin si nécessaire) ---");
        UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl(
            DatabaseConnection.getConnection(), 
            adherentDAO
        );
        System.out.println("✓ UtilisateurDAO créé");
        
        // Initialiser les contrôleurs
        this.authController = new AuthController(utilisateurDAO);
        this.livreController = new LivreController(livreDAO, empruntDAO);
        this.adherentController = new AdherentController(adherentDAO, empruntDAO, utilisateurDAO);
        this.utilisateurController = new UtilisateurController(utilisateurDAO);
        this.empruntController = new EmpruntController(
            empruntDAO, livreDAO, adherentDAO,
            livreController, adherentController
        );
        
        System.out.println("✓ Tous les contrôleurs initialisés");
        System.out.println("=== APPLICATION PRÊTE ===");
    }

    // Getters pour accéder aux contrôleurs depuis l'interface utilisateur
    public AuthController getAuthController() {
        return authController;
    }

    public LivreController getLivreController() {
        return livreController;
    }

    public AdherentController getAdherentController() {
        return adherentController;
    }

    public EmpruntController getEmpruntController() {
        return empruntController;
    }

    public UtilisateurController getUtilisateurController() {
        return utilisateurController;
    }

    public String formaterDate(java.time.LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        return date.toString();
    }

    public void fermerApplication() {
        DatabaseConnection.close();
        biblio.util.Logger.log("Application fermée", "SYSTEM");
    }
}