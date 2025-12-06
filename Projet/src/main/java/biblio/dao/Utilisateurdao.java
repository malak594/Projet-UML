package biblio.dao;

import biblio.model.Utilisateur;
import java.util.ArrayList;
import java.util.List;

public class Utilisateurdao {
    private List<Utilisateur> liste = new ArrayList<>();

    public Utilisateur chercher(String login) {
        for (Utilisateur user : liste) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    public void ajouter(Utilisateur user) {
        for (Utilisateur existing : liste) {
            if (existing.getIDUser() == user.getIDUser()) {
                throw new IllegalArgumentException("Utilisateur existe déjà !");
            }
        }
        liste.add(user);
        System.out.println("Utilisateur ajouté : " + user.getIDUser());
    }
}

