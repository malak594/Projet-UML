package biblio.controller;

import biblio.dao.UtilisateurDAO;
import biblio.model.Utilisateur;
import biblio.util.PasswordHasher;
import java.util.List;

public class UtilisateurController {
    private UtilisateurDAO utilisateurDAO;

    public UtilisateurController(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    /**
     * Crée un nouvel utilisateur
     */
    public boolean creerUtilisateur(String login, String motDePasse, String role) {
        if (login == null || login.trim().isEmpty() || motDePasse == null || motDePasse.isEmpty()) {
            return false;
        }

        // Vérifier si le login existe déjà
        if (utilisateurDAO.trouverParLogin(login) != null) {
            return false;
        }

        // Valider le rôle
        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            return false;
        }

        String hash = PasswordHasher.hash(motDePasse);
        Utilisateur utilisateur = new Utilisateur(login, hash, role);
        utilisateurDAO.ajouter(utilisateur);

        biblio.util.Logger.log("Création utilisateur: " + login, "ADMIN");
        return true;
    }

  
    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getLogin() == null) {
            return false;
        }

        Utilisateur existant = utilisateurDAO.trouverParLogin(utilisateur.getLogin());
        if (existant == null) {
            return false;
        }

        utilisateurDAO.modifier(utilisateur);
        biblio.util.Logger.log("Modification utilisateur: " + utilisateur.getLogin(), "ADMIN");
        return true;
    }


    public boolean supprimerUtilisateur(String login) {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }

        // Ne pas permettre la suppression de l'admin principal
        if ("admin".equals(login)) {
            return false;
        }

        utilisateurDAO.supprimer(login);
        biblio.util.Logger.log("Suppression utilisateur: " + login, "ADMIN");
        return true;
    }


    public List<Utilisateur> listerTousLesUtilisateurs() {
        return utilisateurDAO.listerTous();
    }


    public Utilisateur rechercherParLogin(String login) {
        return utilisateurDAO.trouverParLogin(login);
    }

    public boolean changerStatutUtilisateur(String login, boolean actif) {
        Utilisateur utilisateur = utilisateurDAO.trouverParLogin(login);
        if (utilisateur == null) {
            return false;
        }

        if (actif) {
            utilisateurDAO.activerUtilisateur(login);
        } else {
            utilisateurDAO.desactiverUtilisateur(login);
        }

        String action = actif ? "Activé" : "Désactivé";
        biblio.util.Logger.log(action + " utilisateur: " + login, "ADMIN");
        return true;
    }


    public boolean changerMotDePasse(String login, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurDAO.trouverParLogin(login);
        if (utilisateur == null) {
            return false;
        }

        String hash = PasswordHasher.hash(nouveauMotDePasse);
        utilisateur.setPasswordHash(hash);
        utilisateurDAO.modifier(utilisateur);

        biblio.util.Logger.log("Changement mot de passe: " + login, "ADMIN");
        return true;
    }

    public boolean verifierConnexion(String login, String motDePasse) {
        return utilisateurDAO.verifierMotDePasse(login, motDePasse);
    }

    public boolean estUtilisateurActif(String login) {
        Utilisateur utilisateur = utilisateurDAO.trouverParLogin(login);
        return utilisateur != null && utilisateur.isActif();
    }

  
}