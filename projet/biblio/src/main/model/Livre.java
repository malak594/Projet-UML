package biblio.model;
public class Livre {
    private String isbn;
    private String titre;
    private String auteur;
    private String categorie;
    private int exemplairesDisponibles;
    private int exemplairesTotaux;

    // Constructeur par défaut
    public Livre() {}

    // Constructeur avec paramètres
    public Livre(String isbn, String titre, String auteur, String categorie, int exemplairesTotaux) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.exemplairesTotaux = exemplairesTotaux;
        this.exemplairesDisponibles = exemplairesTotaux; // Initialement tous disponibles
    }

    // Getters et Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getExemplairesDisponibles() {
        return exemplairesDisponibles;
    }

    public void setExemplairesDisponibles(int exemplairesDisponibles) {
        this.exemplairesDisponibles = exemplairesDisponibles;
    }

    public int getExemplairesTotaux() {
        return exemplairesTotaux;
    }

    public void setExemplairesTotaux(int exemplairesTotaux) {
        this.exemplairesTotaux = exemplairesTotaux;
    }

 
}