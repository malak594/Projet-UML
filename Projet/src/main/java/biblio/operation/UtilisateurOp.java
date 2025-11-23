package biblio.operation;
import biblio.Entite.Utilisateur;

public interface UtilisateurOp {
    Utilisateur chercher(String login);
    void ajouter(Utilisateur user);
}
