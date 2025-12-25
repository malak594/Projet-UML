package biblio.util;

import java.io.*;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;
    
    static {
        properties = new Properties();
        loadConfig();
    }
    

    private static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
                setDefaultProperties();
            }
        } else {
            setDefaultProperties();
            saveConfig();
        }
    }
    
    private static void setDefaultProperties() {
        properties.setProperty("database.url", "jdbc:sqlite:bibliotheque.db");
        properties.setProperty("app.name", "Gestion Bibliothèque Universitaire");
        properties.setProperty("app.version", "1.0");
        properties.setProperty("app.default_language", "fr");
        properties.setProperty("loan.duration_days", "14");
        properties.setProperty("loan.max_per_user", "3");
        properties.setProperty("penalty.delay_days", "10");
        properties.setProperty("ui.theme", "system");
        properties.setProperty("backup.automatic", "false");
        properties.setProperty("backup.directory", "backups");
        properties.setProperty("log.level", "INFO");
    }
    

    private static void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Configuration de l'application");
            Logger.log("Configuration sauvegardée", "SYSTEM");
        } catch (IOException e) {
            Logger.logError("Erreur lors de la sauvegarde de la configuration", "SYSTEM", e);
        }
    }
    

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
  
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
        Logger.log("Configuration modifiée: " + key + " = " + value, "SYSTEM");
    }
 
    public static int getIntProperty(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    

    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    
 
    public static void removeProperty(String key) {
        properties.remove(key);
        saveConfig();
    }
 
    public static String listAllProperties() {
        StringBuilder sb = new StringBuilder("Configuration actuelle:\n");
        for (String key : properties.stringPropertyNames()) {
            sb.append(key).append(" = ").append(properties.getProperty(key)).append("\n");
        }
        return sb.toString();
    }
}