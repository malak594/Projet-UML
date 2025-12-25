package biblio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:bibliotheque.db";
    private static Connection connection = null;
    
    public static void initialize() {
        if (connection == null) {
            try {
                // Charger le driver SQLite
                Class.forName("org.sqlite.JDBC");
                
                // Établir la connexion
                connection = DriverManager.getConnection(URL);
                connection.setAutoCommit(true);
                
                System.out.println("Connexion à la base de données établie.");
                Logger.log("Connexion à la base de données établie", "SYSTEM");
                
                // Créer les tables si elles n'existent pas
                createTables();
                
            } catch (ClassNotFoundException e) {
                Logger.logError("Driver SQLite non trouvé", "SYSTEM", e);
                System.err.println("ERREUR: Driver SQLite non trouvé. Ajoutez sqlite-jdbc à votre classpath.");
            } catch (SQLException e) {
                Logger.logError("Erreur de connexion à la base de données", "SYSTEM", e);
                System.err.println("ERREUR: Impossible de se connecter à la base de données: " + e.getMessage());
            }
        }
    }
    
    public static Connection getConnection() {
        if (connection == null) {
            initialize();
        }
        return connection;
    }
    
    private static void createTables() {
        String[] createTablesSQL = {
            // Table des livres
            "CREATE TABLE IF NOT EXISTS livres (" +
            "   isbn TEXT PRIMARY KEY," +
            "   titre TEXT NOT NULL," +
            "   auteur TEXT NOT NULL," +
            "   categorie TEXT," +
            "   exemplaires_totaux INTEGER DEFAULT 1," +
            "   exemplaires_disponibles INTEGER DEFAULT 1" +
            ")",
            
            // Table des adhérents
            "CREATE TABLE IF NOT EXISTS adherents (" +
            "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   numero_unique TEXT UNIQUE NOT NULL," +
            "   nom TEXT NOT NULL," +
            "   prenom TEXT NOT NULL," +
            "   email TEXT UNIQUE NOT NULL," +
            "   telephone TEXT," +
            "   date_inscription DATE NOT NULL," +
            "   bloque BOOLEAN DEFAULT 0," +
            "   nb_emprunts_actuels INTEGER DEFAULT 0" +
            ")",
            
            // Table des emprunts
            "CREATE TABLE IF NOT EXISTS emprunts (" +
            "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   livre_isbn TEXT NOT NULL," +
            "   adherent_id INTEGER NOT NULL," +
            "   date_emprunt DATE NOT NULL," +
            "   date_retour_prevue DATE NOT NULL," +
            "   date_retour_reelle DATE," +
            "   statut TEXT DEFAULT 'ACTIF'," +
            "   FOREIGN KEY(livre_isbn) REFERENCES livres(isbn) ON DELETE CASCADE," +
            "   FOREIGN KEY(adherent_id) REFERENCES adherents(id) ON DELETE CASCADE" +
            ")",
            
            // Table des utilisateurs
            "CREATE TABLE IF NOT EXISTS utilisateurs (" +
            "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   login TEXT UNIQUE NOT NULL," +
            "   password_hash TEXT NOT NULL," +
            "   role TEXT NOT NULL," +
            "   actif BOOLEAN DEFAULT 1," +
            "   adherent_id INTEGER," +
            "   FOREIGN KEY(adherent_id) REFERENCES adherents(id) ON DELETE SET NULL" +
            ")"
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String sql : createTablesSQL) {
                stmt.execute(sql);
            }
            System.out.println("Tables créées ou déjà existantes.");
            Logger.log("Structure de la base de données vérifiée/créée", "SYSTEM");
            
        } catch (SQLException e) {
            Logger.logError("Erreur lors de la création des tables", "SYSTEM", e);
            System.err.println("ERREUR lors de la création des tables: " + e.getMessage());
        }
    }
    
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connexion à la base de données fermée.");
                Logger.log("Connexion à la base de données fermée", "SYSTEM");
            } catch (SQLException e) {
                Logger.logError("Erreur lors de la fermeture de la connexion", "SYSTEM", e);
                System.err.println("ERREUR lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
    

    public static void backupDatabase(String backupPath) {
        try {
            Connection backupConn = DriverManager.getConnection("jdbc:sqlite:" + backupPath);
            
            try (Statement stmt = connection.createStatement();
                 Statement backupStmt = backupConn.createStatement()) {
                
                // Copier les données
                String[] tables = {"livres", "adherents", "emprunts", "utilisateurs"};
                
                for (String table : tables) {
                    backupStmt.execute("DROP TABLE IF EXISTS " + table);
                }
                
                createTables();
                
                Logger.log("Sauvegarde de la base de données créée: " + backupPath, "SYSTEM");
                
            } finally {
                backupConn.close();
            }
            
        } catch (SQLException e) {
            Logger.logError("Erreur lors de la sauvegarde de la base de données", "SYSTEM", e);
        }
    }
    
    public static void executeRawSQL(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            Logger.log("Requête SQL exécutée: " + sql, "SYSTEM");
        } catch (SQLException e) {
            Logger.logError("Erreur lors de l'exécution de la requête SQL", "SYSTEM", e);
        }
    }
}