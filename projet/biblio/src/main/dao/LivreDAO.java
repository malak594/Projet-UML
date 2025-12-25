package biblio.dao;
import biblio.model.Livre;
import java.util.List;

public interface LivreDAO {
    void ajouter(Livre livre);
    void modifier(Livre livre);
    void supprimer(String isbn);
    Livre trouverParIsbn(String isbn);
    List<Livre> listerTous();
    List<Livre> rechercherParTitre(String titre);
    List<Livre> rechercherParAuteur(String auteur);
    boolean existeIsbn(String isbn);
}