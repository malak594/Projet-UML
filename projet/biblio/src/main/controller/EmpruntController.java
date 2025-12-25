package biblio.controller;


import biblio.dao.EmpruntDAO;
import biblio.dao.LivreDAO;
import biblio.dao.AdherentDAO;
import biblio.model.Emprunt;
import biblio.model.Livre;
import biblio.model.Adherent;
import java.time.LocalDate;
import java.util.List;

public class EmpruntController {
    private EmpruntDAO empruntDAO;
    private LivreDAO livreDAO;
    private AdherentDAO adherentDAO;
    private LivreController livreController;
    private AdherentController adherentController;

    public EmpruntController(EmpruntDAO empruntDAO, LivreDAO livreDAO, AdherentDAO adherentDAO,
                             LivreController livreController, AdherentController adherentController) {
        this.empruntDAO = empruntDAO;
        this.livreDAO = livreDAO;
        this.adherentDAO = adherentDAO;
        this.livreController = livreController;
        this.adherentController = adherentController;
    }

    public boolean creerEmprunt(int adherentId, String isbn) {
        // Vérifier si l'adhérent existe
        Adherent adherent = adherentDAO.trouverParId(adherentId);
        if (adherent == null) {
            return false;
        }

        // Vérifier si le livre existe
        Livre livre = livreDAO.trouverParIsbn(isbn);
        if (livre == null) {
            return false;
        }

        // Vérifier les contraintes
        if (!peutEmprunter(adherentId, isbn)) {
            return false;
        }

        // Créer l'emprunt
        Emprunt emprunt = new Emprunt(livre, adherent);
        empruntDAO.ajouter(emprunt);

        // Mettre à jour les compteurs
        livreController.decrementerExemplairesDisponibles(isbn);
        adherentController.incrementerEmprunts(adherentId);

        biblio.util.Logger.log("Nouvel emprunt - Adhérent: " + adherentId + ", Livre: " + isbn, 
                       adherentController.estAdmin() ? "ADMIN" : "USER");
        return true;
    }

  
    public boolean peutEmprunter(int adherentId, String isbn) {
        // Vérifier si l'adhérent peut emprunter
        if (!adherentController.peutEmprunter(adherentId)) {
            return false;
        }

        // Vérifier si le livre est disponible
        if (!livreController.estDisponiblePourEmprunt(isbn)) {
            return false;
        }

        // Vérifier si l'adhérent a déjà emprunté ce livre
        if (empruntDAO.adherentAEmprunteLivre(adherentId, isbn)) {
            return false;
        }

        return true;
    }

 
    public boolean retournerLivre(int empruntId) {
        Emprunt emprunt = empruntDAO.trouverParId(empruntId);
        if (emprunt == null || !"ACTIF".equals(emprunt.getStatut())) {
            return false;
        }

        // Marquer comme retourné
        emprunt.setDateRetourReelle(LocalDate.now());
        emprunt.setStatut("TERMINE");
        
        // Mettre à jour la base de données
        empruntDAO.retourner(empruntId);

        // Mettre à jour les compteurs
        livreController.incrementerExemplairesDisponibles(emprunt.getLivre().getIsbn());
        adherentController.decrementerEmprunts(emprunt.getAdherent().getId());

        // Vérifier et gérer les retards
        gererRetard(emprunt);

        biblio.util.Logger.log("Retour livre - Emprunt ID: " + empruntId, 
                       adherentController.estAdmin() ? "ADMIN" : "USER");
        return true;
    }


    private void gererRetard(Emprunt emprunt) {
        int joursRetard = emprunt.calculerRetard();
        if (joursRetard > 10) {
            adherentController.bloquerAdherent(emprunt.getAdherent().getId(), true);
            biblio.util.Logger.log("Adhérent bloqué pour retard - ID: " + emprunt.getAdherent().getId(), "SYSTEM");
        }
    }

    public List<Emprunt> listerTousLesEmprunts() {
        return empruntDAO.listerTous();
    }

 
    public List<Emprunt> listerEmpruntsActifs() {
        return empruntDAO.listerEmpruntsActifs();
    }

 
    public List<Emprunt> listerEmpruntsParAdherent(int adherentId) {
        return empruntDAO.listerEmpruntsParAdherent(adherentId);
    }


    public List<Emprunt> listerRetards() {
        return empruntDAO.listerRetards();
    }

    public Emprunt rechercherEmpruntParId(int id) {
        return empruntDAO.trouverParId(id);
    }

  
    public boolean renouvelerEmprunt(int empruntId) {
        Emprunt emprunt = empruntDAO.trouverParId(empruntId);
        if (emprunt == null || !"ACTIF".equals(emprunt.getStatut())) {
            return false;
        }

        // Vérifier si l'emprunt n'est pas déjà en retard
        if (emprunt.calculerRetard() > 0) {
            return false;
        }

        // Prolonger de 14 jours supplémentaires
        emprunt.setDateRetourPrevue(emprunt.getDateRetourPrevue().plusDays(14));
        
        biblio.util.Logger.log("Renouvellement emprunt ID: " + empruntId, 
                       adherentController.estAdmin() ? "ADMIN" : "USER");
        return true;
    }


    public int compterEmpruntsActifs(int adherentId) {
        return empruntDAO.compterEmpruntsActifsParAdherent(adherentId);
    }


 
}