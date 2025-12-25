package biblio.dao;

import biblio.model.Emprunt;
import java.util.List;

public interface EmpruntDAO {
    void ajouter(Emprunt emprunt);
    void retourner(int id);
    Emprunt trouverParId(int id);
    List<Emprunt> listerTous();
    List<Emprunt> listerEmpruntsActifs();
    List<Emprunt> listerEmpruntsParAdherent(int adherentId);
    List<Emprunt> listerRetards();
    boolean adherentAEmprunteLivre(int adherentId, String isbn);
    int compterEmpruntsActifsParAdherent(int adherentId);
}