package biblio.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    
    public static void initializeAdminUser() {
        Connection conn = DatabaseConnection.getConnection();
        
        try {
            // Vérifier si l'admin existe déjà
            String checkSql = "SELECT COUNT(*) FROM utilisateurs WHERE login = 'admin'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSql)) {
                
                rs.next();
                int count = rs.getInt(1);
                
                if (count == 0) {
                    // Créer l'admin par défaut
                    String insertSql = "INSERT INTO utilisateurs (login, password_hash, role, actif) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        
                        // Mot de passe: "admin123" haché avec BCrypt
                        // Vous pouvez générer un nouveau hash avec : PasswordHasher.hash("admin123")
                        String adminHash = PasswordHasher.hash("admin123");
                        
                        pstmt.setString(1, "admin");
                        pstmt.setString(2, adminHash);
                        pstmt.setString(3, "ADMIN");
                        pstmt.setBoolean(4, true);
                        
                        pstmt.executeUpdate();
                        
                        System.out.println("✓ Administrateur créé avec succès");
                        System.out.println("  Login: admin");
                        System.out.println("  Mot de passe: admin123");
                        
                        Logger.log("Administrateur par défaut créé", "SYSTEM");
                    }
                } else {
                    System.out.println("✓ Administrateur existe déjà");
                }
            }
            
        } catch (SQLException e) {
            Logger.logError("Erreur lors de l'initialisation de l'administrateur", "SYSTEM", e);
            System.err.println("ERREUR: Impossible de créer l'administrateur: " + e.getMessage());
        }
    }
    
    public static void initializeDatabase() {
        // Cette méthode peut être utilisée pour initialiser d'autres données par défaut
        initializeAdminUser();
    }
}