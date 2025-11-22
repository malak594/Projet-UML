package com.biblio.operation;
import com.biblio.Entite.Livre;
import java.util.List;

public interface LivreOp {
    void ajouter(Livre livre);
    void modifier(Livre livre);
    void supprimer(String ISBN);
    Livre chercher(String ISBN);
    List<Livre> lister();
}
