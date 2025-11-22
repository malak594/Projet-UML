package com.biblio.operation;
import com.biblio.Entite.Utilisateur;

public interface UtilisateurOp {
    Utilisateur chercher(String login);
    void ajouter(Utilisateur user);
}
