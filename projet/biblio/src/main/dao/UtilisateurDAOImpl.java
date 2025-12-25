package biblio.dao;


import biblio.model.Utilisateur;
import biblio.util.PasswordHasher;
import biblio.model.Adherent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAOImpl implements UtilisateurDAO {
    private Connection connection;
    private AdherentDAO adherentDAO;

    public UtilisateurDAOImpl(Connection connection, AdherentDAO adherentDAO) {
        this.connection = connection;
        this.adherentDAO = adherentDAO;
        creerTableSiAbsente();
        creerAdminParDefaut();
    }

    private void creerTableSiAbsente() {
        String sql = "CREATE TABLE IF NOT EXISTS utilisateurs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "actif BOOLEAN DEFAULT 1," +
                "adherent_id INTEGER," +
                "FOREIGN KEY(adherent_id) REFERENCES adherents(id)" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void creerAdminParDefaut() {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE role = 'ADMIN'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                String hash = PasswordHasher.hash("admin123");
                String insertSql = "INSERT INTO utilisateurs(login, password_hash, role) VALUES('admin', ?, 'ADMIN')";
                
                try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                    pstmt.setString(1, hash);
                    pstmt.executeUpdate();
                    System.out.println("Admin créé avec hash BCrypt");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void ajouter(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateurs(login, password_hash, role, actif, adherent_id) " +
                    "VALUES(?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getLogin());
            pstmt.setString(2, utilisateur.getPasswordHash());
            pstmt.setString(3, utilisateur.getRole());
            pstmt.setBoolean(4, utilisateur.isActif());
            
            if (utilisateur.getAdherent() != null) {
                pstmt.setInt(5, utilisateur.getAdherent().getId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateurs SET password_hash = ?, role = ?, actif = ?, adherent_id = ? WHERE login = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getPasswordHash());
            pstmt.setString(2, utilisateur.getRole());
            pstmt.setBoolean(3, utilisateur.isActif());
            
            if (utilisateur.getAdherent() != null) {
                pstmt.setInt(4, utilisateur.getAdherent().getId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            pstmt.setString(5, utilisateur.getLogin());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(String login) {
        String sql = "DELETE FROM utilisateurs WHERE login = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Utilisateur trouverParLogin(String login) {
        String sql = "SELECT * FROM utilisateurs WHERE login = ?";
        Utilisateur utilisateur = null;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("id"));
                utilisateur.setLogin(rs.getString("login"));
                utilisateur.setPasswordHash(rs.getString("password_hash"));
                utilisateur.setRole(rs.getString("role"));
                utilisateur.setActif(rs.getBoolean("actif"));
                
                // Récupérer l'adhérent si présent
                int adherentId = rs.getInt("adherent_id");
                if (adherentId > 0) {
                    Adherent adherent = adherentDAO.trouverParId(adherentId);
                    utilisateur.setAdherent(adherent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return utilisateur;
    }

    @Override
    public List<Utilisateur> listerTous() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY role, login";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("id"));
                utilisateur.setLogin(rs.getString("login"));
                utilisateur.setPasswordHash(rs.getString("password_hash"));
                utilisateur.setRole(rs.getString("role"));
                utilisateur.setActif(rs.getBoolean("actif"));
                
                int adherentId = rs.getInt("adherent_id");
                if (adherentId > 0) {
                    Adherent adherent = adherentDAO.trouverParId(adherentId);
                    utilisateur.setAdherent(adherent);
                }
                
                utilisateurs.add(utilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return utilisateurs;
    }

    @Override
    public boolean verifierMotDePasse(String login, String motDePasse) {
        Utilisateur utilisateur = trouverParLogin(login);
        if (utilisateur != null && utilisateur.isActif()) {
            // Utiliser PasswordHasher pour vérifier
            return PasswordHasher.verify(motDePasse, utilisateur.getPasswordHash());
        }
        return false;
    }

    @Override
    public void desactiverUtilisateur(String login) {
        String sql = "UPDATE utilisateurs SET actif = 0 WHERE login = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activerUtilisateur(String login) {
        String sql = "UPDATE utilisateurs SET actif = 1 WHERE login = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}