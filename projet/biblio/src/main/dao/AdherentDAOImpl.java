package biblio.dao;

import biblio.model.Adherent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdherentDAOImpl implements AdherentDAO {
    private Connection connection;
    public AdherentDAOImpl(Connection connection) {
        this.connection = connection;
       
    }

      

    @Override
    public void ajouter(Adherent adherent) {
        String sql = "INSERT INTO adherents(numero_unique, nom, prenom, email, telephone, date_inscription, bloque, nb_emprunts_actuels) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, adherent.getNumeroUnique());
            pstmt.setString(2, adherent.getNom());
            pstmt.setString(3, adherent.getPrenom());
            pstmt.setString(4, adherent.getEmail());
            pstmt.setString(5, adherent.getTelephone());
            pstmt.setString(6, adherent.getDateInscription().toString());
            pstmt.setBoolean(7, adherent.isBloque());
            pstmt.setInt(8, adherent.getNbEmpruntsActuels());
            pstmt.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                adherent.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Adherent adherent) {
        String sql = "UPDATE adherents SET numero_unique = ?, nom = ?, prenom = ?, email = ?, " +
                    "telephone = ?, bloque = ?, nb_emprunts_actuels = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, adherent.getNumeroUnique());
            pstmt.setString(2, adherent.getNom());
            pstmt.setString(3, adherent.getPrenom());
            pstmt.setString(4, adherent.getEmail());
            pstmt.setString(5, adherent.getTelephone());
            pstmt.setBoolean(6, adherent.isBloque());
            pstmt.setInt(7, adherent.getNbEmpruntsActuels());
            pstmt.setInt(8, adherent.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM adherents WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Adherent trouverParId(int id) {
        String sql = "SELECT * FROM adherents WHERE id = ?";
        Adherent adherent = null;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                adherent = new Adherent();
                adherent.setId(rs.getInt("id"));
                adherent.setNumeroUnique(rs.getString("numero_unique"));
                adherent.setNom(rs.getString("nom"));
                adherent.setPrenom(rs.getString("prenom"));
                adherent.setEmail(rs.getString("email"));
                adherent.setTelephone(rs.getString("telephone"));
                adherent.setDateInscription(LocalDate.parse(rs.getString("date_inscription")));
                adherent.setBloque(rs.getBoolean("bloque"));
                adherent.setNbEmpruntsActuels(rs.getInt("nb_emprunts_actuels"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return adherent;
    }

    @Override
    public Adherent trouverParEmail(String email) {
        String sql = "SELECT * FROM adherents WHERE email = ?";
        Adherent adherent = null;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                adherent = new Adherent();
                adherent.setId(rs.getInt("id"));
                adherent.setNumeroUnique(rs.getString("numero_unique"));
                adherent.setNom(rs.getString("nom"));
                adherent.setPrenom(rs.getString("prenom"));
                adherent.setEmail(rs.getString("email"));
                adherent.setTelephone(rs.getString("telephone"));
                adherent.setDateInscription(LocalDate.parse(rs.getString("date_inscription")));
                adherent.setBloque(rs.getBoolean("bloque"));
                adherent.setNbEmpruntsActuels(rs.getInt("nb_emprunts_actuels"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return adherent;
    }

    @Override
    public List<Adherent> listerTous() {
        List<Adherent> adherents = new ArrayList<>();
        String sql = "SELECT * FROM adherents ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Adherent adherent = new Adherent();
                adherent.setId(rs.getInt("id"));
                adherent.setNumeroUnique(rs.getString("numero_unique"));
                adherent.setNom(rs.getString("nom"));
                adherent.setPrenom(rs.getString("prenom"));
                adherent.setEmail(rs.getString("email"));
                adherent.setTelephone(rs.getString("telephone"));
                adherent.setDateInscription(LocalDate.parse(rs.getString("date_inscription")));
                adherent.setBloque(rs.getBoolean("bloque"));
                adherent.setNbEmpruntsActuels(rs.getInt("nb_emprunts_actuels"));
                adherents.add(adherent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return adherents;
    }

    @Override
    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM adherents WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public void bloquerAdherent(int id, boolean bloque) {
        String sql = "UPDATE adherents SET bloque = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, bloque);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void incrementerEmprunts(int id) {
        String sql = "UPDATE adherents SET nb_emprunts_actuels = nb_emprunts_actuels + 1 WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decrementerEmprunts(int id) {
        String sql = "UPDATE adherents SET nb_emprunts_actuels = nb_emprunts_actuels - 1 WHERE id = ? AND nb_emprunts_actuels > 0";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}