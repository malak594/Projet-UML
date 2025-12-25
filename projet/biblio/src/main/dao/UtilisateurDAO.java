package biblio.dao;

import biblio.model.Utilisateur;
import java.util.List;

public interface UtilisateurDAO {
    void ajouter(Utilisateur utilisateur);
    void modifier(Utilisateur utilisateur);
    void supprimer(String login);
    Utilisateur trouverParLogin(String login);
    List<Utilisateur> listerTous();
    boolean verifierMotDePasse(String login, String motDePasse);
    void desactiverUtilisateur(String login);
    void activerUtilisateur(String login);
}