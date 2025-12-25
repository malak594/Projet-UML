package biblio.model;
import java.time.LocalDate;

public class Adherent {
    private int id;
    private String numeroUnique;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateInscription;
    private boolean bloque;
    private int nbEmpruntsActuels;

    // Constructeur par défaut
    public Adherent() {
        this.dateInscription = LocalDate.now();
        this.bloque = false;
        this.nbEmpruntsActuels = 0;
    }

    // Constructeur avec paramètres
    public Adherent(String numeroUnique, String nom, String prenom, String email, String telephone) {
        this();
        this.numeroUnique = numeroUnique;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroUnique() {
        return numeroUnique;
    }

    public void setNumeroUnique(String numeroUnique) {
        this.numeroUnique = numeroUnique;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public boolean isBloque() {
        return bloque;
    }

    public void setBloque(boolean bloque) {
        this.bloque = bloque;
    }

    public int getNbEmpruntsActuels() {
        return nbEmpruntsActuels;
    }

    public void setNbEmpruntsActuels(int nbEmpruntsActuels) {
        this.nbEmpruntsActuels = nbEmpruntsActuels;
    }

    public void incrementerEmprunts() {
        this.nbEmpruntsActuels++;
    }

    public void decrementerEmprunts() {
        if (this.nbEmpruntsActuels > 0) {
            this.nbEmpruntsActuels--;
        }
    }

}