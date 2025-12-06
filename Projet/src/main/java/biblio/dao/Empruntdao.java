package biblio.dao;
import java.util.ArrayList;
import java.util.List;


import biblio.model.Emprunt;


public class Empruntdao {
    List<Emprunt> liste =new ArrayList<>();

    void ajouter(Emprunt emprunt){
        for(Emprunt existing : liste){
            if(existing.getID()==emprunt.getID()){
                throw new IllegalArgumentException("emprunt existe déjà");
            }else{
                liste.add(emprunt);
                System.out.println("Produit ajouté : " + liste.get(0).getID());
            }
        }
    }

    void modifier(int ID ,Emprunt emprunt){
        liste.add(ID,emprunt);
    }

    void supprimer(int ID){
        liste.remove(ID);
    }

    Emprunt chercher(int ID){
        return liste.stream().filter(emprunt -> emprunt.getID()==ID).findFirst().orElse(null);
    }

}
