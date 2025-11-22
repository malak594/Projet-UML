package com.biblio.operation;
import java.util.List;
import com.biblio.Entite.Emprunt;

public interface EmpruntOp {
    void ajouter(Emprunt emprunt);
    void modifier(Emprunt emprunt);
    void supprimer(int ID);
    Emprunt chercher(int ID);
    List<Emprunt> lister();
}
