package com.biblio.operation;
import com.biblio.Entite.Adherent;
import java.util.List;

public interface AdherentOp {
    void ajouter(Adherent adh);
    void modifier(Adherent adh);
    void supprimer(int IDAdherent);
    Adherent chercher(int IDAdherent);
    List<Adherent> lister();
}
