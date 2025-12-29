package biblio.controller;

import biblio.dao.AdherentDAO;

import biblio.dao.EmpruntDAO;
import biblio.dao.UtilisateurDAO;
import biblio.model.Adherent;
import biblio.model.Utilisateur;
import biblio.util.PasswordHasher;
import java.util.List;
public class AdherentController {
    private AdherentDAO adherentDAO;
    private EmpruntDAO empruntDAO;
    private UtilisateurDAO utilisateurDAO;

    public AdherentController(AdherentDAO adherentDAO, EmpruntDAO empruntDAO, UtilisateurDAO utilisateurDAO) {
        this.adherentDAO = adherentDAO;
        this.empruntDAO = empruntDAO;
        this.utilisateurDAO = utilisateurDAO;
    }

    public boolean ajouterAdherent(Adherent adherent) {
        if (adherent == null || adherent.getEmail() == null || adherent.getEmail().trim().isEmpty()) {
            return false;
        }

     
        if (adherentDAO.existeEmail(adherent.getEmail())) {
            return false;
        }

        adherentDAO.ajouter(adherent);
        

        creerCompteUtilisateurPourAdherent(adherent);
        
        
        return true;
    }
  
  
    private void creerCompteUtilisateurPourAdherent(Adherent adherent) {
      
        String login = adherent.getEmail();
        String motDePasse = "password123"; 
        String hash = PasswordHasher.hash(motDePasse);
        
        Utilisateur utilisateur = new Utilisateur(login, hash, adherent);
        utilisateurDAO.ajouter(utilisateur);
    }

    /**
     * Modifie un adhérent existant
     */
    public boolean modifierAdherent(Adherent adherent) {
        if (adherent == null || adherent.getId() <= 0) {
            return false;
        }

        Adherent existant = adherentDAO.trouverParId(adherent.getId());
        if (existant == null) {
            return false;
        }

        
        if (!existant.getEmail().equals(adherent.getEmail()) && 
            adherentDAO.existeEmail(adherent.getEmail())) {
            return false;
        }

        adherentDAO.modifier(adherent);
        return true;
    }

    public boolean supprimerAdherent(int id) {
        Adherent adherent = adherentDAO.trouverParId(id);
        if (adherent == null) {
            return false;
        }

      
        if (adherent.getNbEmpruntsActuels() > 0) {
            return false;
        }

        
        Utilisateur utilisateur = utilisateurDAO.trouverParLogin(adherent.getEmail());
        if (utilisateur != null) {
            utilisateurDAO.supprimer(utilisateur.getLogin());
        }

        adherentDAO.supprimer(id);
        return true;
    }

    public Adherent rechercherParId(int id) {
        return adherentDAO.trouverParId(id);
    }

    public Adherent rechercherParEmail(String email) {
        return adherentDAO.trouverParEmail(email);
    }

    public List<Adherent> listerTousLesAdherents() {
        return adherentDAO.listerTous();
    }

    public boolean peutEmprunter(int adherentId) {
        Adherent adherent = adherentDAO.trouverParId(adherentId);
        if (adherent == null || adherent.isBloque()) {
            return false;
        }
        return adherent.getNbEmpruntsActuels() < 3;
    }

  
    public boolean aRetardImportant(int adherentId) {
       
        Adherent adherent = adherentDAO.trouverParId(adherentId);
        return adherent != null && adherent.isBloque();
    }

    public boolean bloquerAdherent(int id, boolean bloquer) {
        Adherent adherent = adherentDAO.trouverParId(id);
        if (adherent == null) {
            return false;
        }

        adherentDAO.bloquerAdherent(id, bloquer);
        String action = bloquer ? "Bloqué" : "Débloqué";
        return true;
    }

    public void incrementerEmprunts(int adherentId) {
        adherentDAO.incrementerEmprunts(adherentId);
    }

    public void decrementerEmprunts(int adherentId) {
        adherentDAO.decrementerEmprunts(adherentId);
    }

    public boolean estAdmin() {
        return true;
    }
    public List<Adherent> rechercherParNom(String nom) {
        List<Adherent> tous = adherentDAO.listerTous();
        if (nom == null || nom.trim().isEmpty()) {
            return tous;
        }
        
        String recherche = nom.trim().toLowerCase();
        return tous.stream()
                .filter(a -> a.getNom().toLowerCase().contains(recherche) || 
                           a.getPrenom().toLowerCase().contains(recherche))
                .toList();
    }
}
