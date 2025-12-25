package biblio.controller;

import biblio.dao.EmpruntDAO;
import biblio.model.Livre;
import java.util.List;
import biblio.dao.LivreDAO;
public class LivreController {
    private LivreDAO livreDAO;
    private EmpruntDAO empruntDAO;

    public LivreController(LivreDAO livreDAO , EmpruntDAO empruntDAO) {
        this.livreDAO = livreDAO;
        this.empruntDAO=empruntDAO;
        
    }
    public LivreController() {};

    
    public boolean ajouterLivre(Livre livre) {
        if (livre == null || livre.getIsbn() == null || livre.getIsbn().trim().isEmpty()) {
            return false;
        }

        // Vérifier si l'ISBN existe déjà
        if (livreDAO.existeIsbn(livre.getIsbn())) {
            return false;
        }

        livreDAO.ajouter(livre);
        biblio.util.Logger.log("Ajout livre: " + livre.getIsbn(), "ADMIN");
        return true;
    }

    public boolean modifierLivre(Livre livre) {
        if (livre == null || livre.getIsbn() == null) {
            return false;
        }

        Livre existant = livreDAO.trouverParIsbn(livre.getIsbn());
        if (existant == null) {
            return false;
        }

        livreDAO.modifier(livre);
        biblio.util.Logger.log("Modification livre: " + livre.getIsbn(), "ADMIN");
        return true;
    }

    public boolean supprimerLivre(String isbn) {
        Livre livre = livreDAO.trouverParIsbn(isbn);
        if (livre == null) {
            return false;
        }

        // Vérifier si le livre a des exemplaires en prêt
        if (livre.getExemplairesDisponibles() < livre.getExemplairesTotaux()) {
            return false; // Des exemplaires sont en prêt
        }

        livreDAO.supprimer(isbn);
        biblio.util.Logger.log("Suppression livre: " + isbn, "ADMIN");
        return true;
    }

    public Livre rechercherParIsbn(String isbn) {
        return livreDAO.trouverParIsbn(isbn);
    }

    public List<Livre> listerTousLesLivres() {
        return livreDAO.listerTous();
    }

    public List<Livre> rechercherParTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            return livreDAO.listerTous();
        }
        return livreDAO.rechercherParTitre(titre.trim());
    }

    public List<Livre> rechercherParAuteur(String auteur) {
        if (auteur == null || auteur.trim().isEmpty()) {
            return livreDAO.listerTous();
        }
        return livreDAO.rechercherParAuteur(auteur.trim());
    }

    public boolean estDisponiblePourEmprunt(String isbn) {
        Livre livre = livreDAO.trouverParIsbn(isbn);
        if (livre == null) {
            return false;
        }
        return livre.getExemplairesDisponibles() > 0;
    }

 
    public void decrementerExemplairesDisponibles(String isbn) {
        Livre livre = livreDAO.trouverParIsbn(isbn);
        if (livre != null && livre.getExemplairesDisponibles() > 0) {
            livre.setExemplairesDisponibles(livre.getExemplairesDisponibles() - 1);
            livreDAO.modifier(livre);
        }
    }

   
    public void incrementerExemplairesDisponibles(String isbn) {
        Livre livre = livreDAO.trouverParIsbn(isbn);
        if (livre != null && livre.getExemplairesDisponibles() < livre.getExemplairesTotaux()) {
            livre.setExemplairesDisponibles(livre.getExemplairesDisponibles() + 1);
            livreDAO.modifier(livre);
        }
    }

  
    public int importerLivresCSV(List<Livre> livres) {
        int compteur = 0;
        for (Livre livre : livres) {
            if (!livreDAO.existeIsbn(livre.getIsbn())) {
                livreDAO.ajouter(livre);
                compteur++;
            }
        }
        biblio.util.Logger.log("Import CSV: " + compteur + " livres importés", "ADMIN");
        return compteur;
    }
}