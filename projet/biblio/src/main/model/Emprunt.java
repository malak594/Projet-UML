package biblio.model;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private Livre livre;
    private Adherent adherent;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourReelle;
    private String statut; // "ACTIF", "RETARD", "TERMINE"

    // Constructeur par défaut
    public Emprunt() {}

    // Constructeur avec paramètres
    public Emprunt(Livre livre, Adherent adherent) {
        this.livre = livre;
        this.adherent = adherent;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = dateEmprunt.plusDays(14); // +14 jours
        this.statut = "ACTIF";
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public LocalDate getDateRetourReelle() {
        return dateRetourReelle;
    }

    public void setDateRetourReelle(LocalDate dateRetourReelle) {
        this.dateRetourReelle = dateRetourReelle;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

  
    public int calculerRetard() {
        if (dateRetourReelle == null) {
            // Pas encore rendu
            LocalDate aujourdhui = LocalDate.now();
            if (aujourdhui.isAfter(dateRetourPrevue)) {
                return (int) dateRetourPrevue.until(aujourdhui).getDays();
            }
            return 0;
        } else {
            // Déjà rendu
            if (dateRetourReelle.isAfter(dateRetourPrevue)) {
                return (int) dateRetourPrevue.until(dateRetourReelle).getDays();
            }
            return 0;
        }
    }

}