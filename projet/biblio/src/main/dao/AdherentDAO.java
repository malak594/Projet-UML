package biblio.dao;


import biblio.model.Adherent;
import java.util.List;

public interface AdherentDAO {
    void ajouter(Adherent adherent);
    void modifier(Adherent adherent);
    void supprimer(int id);
    Adherent trouverParId(int id);
    Adherent trouverParEmail(String email);
    List<Adherent> listerTous();
    boolean existeEmail(String email);
    void bloquerAdherent(int id, boolean bloque);
    void incrementerEmprunts(int id);
    void decrementerEmprunts(int id);
}