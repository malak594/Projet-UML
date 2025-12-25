package biblio.model;
public class Utilisateur {
    private int id;
    private String login;
    private String passwordHash;
    private String role; // "ADMIN", "USER"
    private boolean actif;
    private Adherent adherent; 
    // Constructeur par défaut
    public Utilisateur() {
        this.actif = true;
    }

    // Constructeur avec paramètres
    public Utilisateur(String login, String passwordHash, String role) {
        this();
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Constructeur pour USER avec adhérent
    public Utilisateur(String login, String passwordHash, Adherent adherent) {
        this(login, passwordHash, "USER");
        this.adherent = adherent;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    // Méthode pour vérifier si c'est un administrateur
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", actif=" + actif +
                ", adherent=" + (adherent != null ? adherent.getNom() : "null") +
                '}';
    }
}