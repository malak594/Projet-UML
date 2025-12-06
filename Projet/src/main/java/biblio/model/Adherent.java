package biblio.model;

public class Adherent {
    private int IDAdherent ;
    private String nom ;
    private String email ;

    public Adherent(int IDAdherent,String nom , String email){
        this.email=email;
        this.IDAdherent=IDAdherent;
        this.nom=nom;
    }

    public String getNom() {
        return nom;
    }

    public int getIDAdherent() {
        return IDAdherent;
    }

    public String getEmail() {
        return email;
    }
}
