package biblio.dao;
import biblio.model.Livre;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Livredao {
    public List<Livre> liste = new ArrayList<>();

    void ajouter(Livre livre){
        for(Livre existing : liste){
            if(Objects.equals(existing.getISBN(), livre.getISBN())){
                throw new IllegalArgumentException("livre existe déjà");
            }else{
                liste.add(livre);
                System.out.println("Produit ajouté : " + liste.get(0).getTitre());
            }
        }
    }

    public void modifier(int ISBN , Livre livre ){
        liste.set(ISBN, livre);

    }

    public void supprimer(int ISBN){
        liste.remove(ISBN);
    }

    public Livre chercher(String titre){
        for (Livre user : liste) {
            if (user.getTitre().equals(titre)) {
                return user;
            }
        }
        return null;
    }

}
