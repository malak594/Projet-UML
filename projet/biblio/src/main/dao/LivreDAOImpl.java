package biblio.dao;
import biblio.model.Livre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAOImpl implements LivreDAO {
    private Connection connection;

    public LivreDAOImpl(Connection connection) {
        this.connection = connection;
        creerTableSiAbsente();
    }

    private void creerTableSiAbsente() {
        String sql = "CREATE TABLE IF NOT EXISTS livres (" +
                "isbn TEXT PRIMARY KEY," +
                "titre TEXT NOT NULL," +
                "auteur TEXT NOT NULL," +
                "categorie TEXT," +
                "exemplaires_totaux INTEGER DEFAULT 1," +
                "exemplaires_disponibles INTEGER DEFAULT 1" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Livre livre) {
        String sql = "INSERT INTO livres(isbn, titre, auteur, categorie, exemplaires_totaux, exemplaires_disponibles) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, livre.getIsbn());
            pstmt.setString(2, livre.getTitre());
            pstmt.setString(3, livre.getAuteur());
            pstmt.setString(4, livre.getCategorie());
            pstmt.setInt(5, livre.getExemplairesTotaux());
            pstmt.setInt(6, livre.getExemplairesDisponibles());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Livre livre) {
        String sql = "UPDATE livres SET titre = ?, auteur = ?, categorie = ?, " +
                    "exemplaires_totaux = ?, exemplaires_disponibles = ? WHERE isbn = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getCategorie());
            pstmt.setInt(4, livre.getExemplairesTotaux());
            pstmt.setInt(5, livre.getExemplairesDisponibles());
            pstmt.setString(6, livre.getIsbn());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(String isbn) {
        String sql = "DELETE FROM livres WHERE isbn = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Livre trouverParIsbn(String isbn) {
        String sql = "SELECT * FROM livres WHERE isbn = ?";
        Livre livre = null;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                livre = new Livre();
                livre.setIsbn(rs.getString("isbn"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setCategorie(rs.getString("categorie"));
                livre.setExemplairesTotaux(rs.getInt("exemplaires_totaux"));
                livre.setExemplairesDisponibles(rs.getInt("exemplaires_disponibles"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return livre;
    }

    @Override
    public List<Livre> listerTous() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres ORDER BY titre";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Livre livre = new Livre();
                livre.setIsbn(rs.getString("isbn"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setCategorie(rs.getString("categorie"));
                livre.setExemplairesTotaux(rs.getInt("exemplaires_totaux"));
                livre.setExemplairesDisponibles(rs.getInt("exemplaires_disponibles"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return livres;
    }

    @Override
    public List<Livre> rechercherParTitre(String titre) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE titre LIKE ? ORDER BY titre";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + titre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Livre livre = new Livre();
                livre.setIsbn(rs.getString("isbn"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setCategorie(rs.getString("categorie"));
                livre.setExemplairesTotaux(rs.getInt("exemplaires_totaux"));
                livre.setExemplairesDisponibles(rs.getInt("exemplaires_disponibles"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return livres;
    }

    @Override
    public List<Livre> rechercherParAuteur(String auteur) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE auteur LIKE ? ORDER BY auteur";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + auteur + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Livre livre = new Livre();
                livre.setIsbn(rs.getString("isbn"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setCategorie(rs.getString("categorie"));
                livre.setExemplairesTotaux(rs.getInt("exemplaires_totaux"));
                livre.setExemplairesDisponibles(rs.getInt("exemplaires_disponibles"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return livres;
    }

    @Override
    public boolean existeIsbn(String isbn) {
        String sql = "SELECT COUNT(*) FROM livres WHERE isbn = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}