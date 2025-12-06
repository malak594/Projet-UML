package biblio.dao;
import biblio.model.Adherent;

import java.util.ArrayList;
import java.util.List;

public class Adherentdao {
    List<Adherent> liste=new ArrayList<>();

    void ajouter(Adherent adh){
        for(Adherent existing : liste){
            if(existing.getIDAdherent()== adh.getIDAdherent()){
                throw new IllegalArgumentException("adherent existe déjà");
            }else{
                liste.add(adh);
                System.out.println("Produit ajouté : " + liste.get(0).getNom());
            }
        }
    }
    void modifier(int IDAdherent,Adherent adh){
        liste.set(IDAdherent , adh);
    }

    void supprimer(int IDAdherent){
        liste.remove(IDAdherent);
    }

    Adherent chercher(String nom) {
        for (Adherent adh : liste) {
            if (adh.getNom().equals(nom)) {
                return adh;
            }
        }
        return null;
    }

}
