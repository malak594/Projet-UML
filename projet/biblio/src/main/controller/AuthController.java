package biblio.controller;

import biblio.dao.UtilisateurDAO;
import biblio.model.Utilisateur;
import biblio.util.PasswordHasher;

public class AuthController {
    private UtilisateurDAO utilisateurDAO;
    private Utilisateur utilisateurConnecte;

    public AuthController(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    public boolean connecter(String login, String motDePasse) {
        Utilisateur utilisateur = utilisateurDAO.trouverParLogin(login);
        System.out.println("Tentative de connexion: " + login);
        System.out.println("Utilisateur trouvé: " + (utilisateur != null));
        
        if (utilisateur != null && utilisateur.isActif()) {
            System.out.println("Hash en base: " + utilisateur.getPasswordHash());
            boolean verifie = PasswordHasher.verify(motDePasse, utilisateur.getPasswordHash());
            System.out.println("Vérification: " + verifie);
            
            if (verifie) {
                utilisateurConnecte = utilisateur;
                return true;
            }
        }
        return false;
    }

    public void deconnecter() {
        utilisateurConnecte = null;
    }

    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    public boolean estAdmin() {
        return estConnecte() && "ADMIN".equals(utilisateurConnecte.getRole());
    }

    public boolean estUtilisateur() {
        return estConnecte() && "USER".equals(utilisateurConnecte.getRole());
    }

 
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public boolean changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse) {
        if (!estConnecte()) {
            return false;
        }

        if (PasswordHasher.verify(ancienMotDePasse, utilisateurConnecte.getPasswordHash())) {
            String nouveauHash = PasswordHasher.hash(nouveauMotDePasse);
            utilisateurConnecte.setPasswordHash(nouveauHash);
            utilisateurDAO.modifier(utilisateurConnecte);
            return true;
        }
        return false;
    }
}