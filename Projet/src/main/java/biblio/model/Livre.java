package biblio.model;

public class Livre {
    private int ISBN;
    private String titre ;
    private String categorie;
    private int nbExemplairesDisp;

    public Livre(int ISBN,String titre,String categorie,int nbExemplairesDisp){
        this.categorie=categorie;
        this.ISBN=ISBN;
        this.titre=titre;
        this.nbExemplairesDisp=nbExemplairesDisp;
    }

    public String getTitre() {
        return titre;
    }

    public int getNbExemplairesDisp() {
        return nbExemplairesDisp;
    }

    public String getCategorie() {
        return categorie;
    }

    public int getISBN() {
        return ISBN;
    }
}



