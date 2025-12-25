package biblio.util;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "bibliotheque.log";
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
 
    public static void log(String action, String user) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String logEntry = String.format("[%s] %s - %s\n", timestamp, user, action);
        
        // Écrire dans le fichier
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(logEntry);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le log: " + e.getMessage());
        }
        
        // Afficher aussi dans la console (optionnel, pour le débogage)
        System.out.print(logEntry);
    }
    
 
    public static void logError(String errorMessage, String user, Exception exception) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String logEntry = String.format("[%s] ERREUR - %s - %s", timestamp, user, errorMessage);
        
        if (exception != null) {
            logEntry += " - Exception: " + exception.getClass().getName() + ": " + exception.getMessage();
        }
        logEntry += "\n";
        
        // Écrire dans le fichier
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(logEntry);
            
            // Si une exception est fournie, ajouter la stack trace
            if (exception != null) {
                exception.printStackTrace(out);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le log: " + e.getMessage());
        }
        
        // Afficher dans la console
        System.err.print(logEntry);
        if (exception != null) {
            exception.printStackTrace();
        }
    }
    
 
    public static String readLog() {
        StringBuilder content = new StringBuilder();
        
        try (FileReader fr = new FileReader(LOG_FILE);
             BufferedReader br = new BufferedReader(fr)) {
            
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            
        } catch (FileNotFoundException e) {
            return "Le fichier de log n'existe pas encore.";
        } catch (IOException e) {
            return "Erreur lors de la lecture du log: " + e.getMessage();
        }
        
        return content.toString();
    }
    
   
    public static void clearLog() {
        try (PrintWriter writer = new PrintWriter(LOG_FILE)) {
            writer.print("");
            log("Fichier de log vidé", "SYSTEM");
        } catch (FileNotFoundException e) {
            System.err.println("Erreur lors du vidage du log: " + e.getMessage());
        }
    }
    
 
    public static void backupLog() {
        String backupFile = "bibliotheque_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log";
        
        File original = new File(LOG_FILE);
        if (!original.exists()) {
            return;
        }
        
        try (InputStream in = new FileInputStream(original);
             OutputStream out = new FileOutputStream(backupFile)) {
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            
            log("Sauvegarde du log créée: " + backupFile, "SYSTEM");
            
        } catch (IOException e) {
            logError("Erreur lors de la sauvegarde du log", "SYSTEM", e);
        }
    }
}