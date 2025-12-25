package biblio.dao;
import biblio.model.Emprunt;
import biblio.model.Livre;
import biblio.model.Adherent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAOImpl implements EmpruntDAO {
    private Connection connection;
    private LivreDAO livreDAO;
    private AdherentDAO adherentDAO;

    public EmpruntDAOImpl(Connection connection, LivreDAO livreDAO, AdherentDAO adherentDAO) {
        this.connection = connection;
        this.livreDAO = livreDAO;
        this.adherentDAO = adherentDAO;
        creerTableSiAbsente();
    }

    private void creerTableSiAbsente() {
        String sql = "CREATE TABLE IF NOT EXISTS emprunts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "livre_isbn TEXT NOT NULL," +
                "adherent_id INTEGER NOT NULL," +
                "date_emprunt DATE NOT NULL," +
                "date_retour_prevue DATE NOT NULL," +
                "date_retour_reelle DATE," +
                "statut TEXT DEFAULT 'ACTIF'," +
                "FOREIGN KEY(livre_isbn) REFERENCES livres(isbn)," +
                "FOREIGN KEY(adherent_id) REFERENCES adherents(id)" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Emprunt emprunt) {
        String sql = "INSERT INTO emprunts(livre_isbn, adherent_id, date_emprunt, date_retour_prevue, date_retour_reelle, statut) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emprunt.getLivre().getIsbn());
            pstmt.setInt(2, emprunt.getAdherent().getId());
            pstmt.setString(3, emprunt.getDateEmprunt().toString());
            pstmt.setString(4, emprunt.getDateRetourPrevue().toString());
            
            if (emprunt.getDateRetourReelle() != null) {
                pstmt.setString(5, emprunt.getDateRetourReelle().toString());
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            
            pstmt.setString(6, emprunt.getStatut());
            pstmt.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                emprunt.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void retourner(int id) {
        String sql = "UPDATE emprunts SET date_retour_reelle = ?, statut = 'TERMINE' WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Emprunt trouverParId(int id) {
        String sql = "SELECT * FROM emprunts WHERE id = ?";
        Emprunt emprunt = null;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                emprunt = creerEmpruntDepuisResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return emprunt;
    }

    @Override
    public List<Emprunt> listerTous() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts ORDER BY date_emprunt DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Emprunt emprunt = creerEmpruntDepuisResultSet(rs);
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return emprunts;
    }

    @Override
    public List<Emprunt> listerEmpruntsActifs() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE statut = 'ACTIF' ORDER BY date_retour_prevue";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Emprunt emprunt = creerEmpruntDepuisResultSet(rs);
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return emprunts;
    }

    @Override
    public List<Emprunt> listerEmpruntsParAdherent(int adherentId) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE adherent_id = ? ORDER BY date_emprunt DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, adherentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Emprunt emprunt = creerEmpruntDepuisResultSet(rs);
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return emprunts;
    }

    @Override
    public List<Emprunt> listerRetards() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE statut = 'ACTIF' AND date_retour_prevue < ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Emprunt emprunt = creerEmpruntDepuisResultSet(rs);
                emprunt.setStatut("RETARD");
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return emprunts;
    }

    @Override
    public boolean adherentAEmprunteLivre(int adherentId, String isbn) {
        String sql = "SELECT COUNT(*) FROM emprunts WHERE adherent_id = ? AND livre_isbn = ? AND statut = 'ACTIF'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, adherentId);
            pstmt.setString(2, isbn);
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
    public int compterEmpruntsActifsParAdherent(int adherentId) {
        String sql = "SELECT COUNT(*) FROM emprunts WHERE adherent_id = ? AND statut = 'ACTIF'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, adherentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    private Emprunt creerEmpruntDepuisResultSet(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(rs.getInt("id"));
        
        // Récupérer le livre
        String isbn = rs.getString("livre_isbn");
        Livre livre = livreDAO.trouverParIsbn(isbn);
        emprunt.setLivre(livre);
        
        // Récupérer l'adhérent
        int adherentId = rs.getInt("adherent_id");
        Adherent adherent = adherentDAO.trouverParId(adherentId);
        emprunt.setAdherent(adherent);
        
        emprunt.setDateEmprunt(LocalDate.parse(rs.getString("date_emprunt")));
        emprunt.setDateRetourPrevue(LocalDate.parse(rs.getString("date_retour_prevue")));
        
        String dateRetourReelle = rs.getString("date_retour_reelle");
        if (dateRetourReelle != null) {
            emprunt.setDateRetourReelle(LocalDate.parse(dateRetourReelle));
        }
        
        emprunt.setStatut(rs.getString("statut"));
        return emprunt;
    }
}