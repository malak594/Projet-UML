package biblio.model;

import java.time.LocalDate;

public class Emprunt {
    private int ID ;
    private Adherent adherent ;
    private Livre livre ;
    private LocalDate dateRetour ;
    private LocalDate dateEmprunt;

    public Emprunt(int ID,Adherent adherent ,Livre livre,LocalDate dateRetour ,LocalDate dateEmprunt){
        this.ID=ID;
        this.adherent=adherent;
        this.livre=livre;
        this.dateEmprunt=dateEmprunt;
        this.dateRetour=dateRetour;
    }

    public int getID() {
        return ID;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public Livre getLivre() {
        return livre;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }
}

