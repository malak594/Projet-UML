package biblio.util;
import biblio.model.Livre;
import biblio.model.Adherent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class CSVHandler {
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
 
    public static boolean exportLivresToCSV(List<Livre> livres, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-tête
            writer.println("ISBN,Titre,Auteur,Categorie,ExemplairesTotaux,ExemplairesDisponibles");
            
            // Données
            for (Livre livre : livres) {
                writer.println(String.format("%s,%s,%s,%s,%d,%d",
                    escapeCSV(livre.getIsbn()),
                    escapeCSV(livre.getTitre()),
                    escapeCSV(livre.getAuteur()),
                    escapeCSV(livre.getCategorie()),
                    livre.getExemplairesTotaux(),
                    livre.getExemplairesDisponibles()));
            }
            
            Logger.log("Export CSV des livres: " + livres.size() + " livres exportés vers " + filePath, "SYSTEM");
            return true;
            
        } catch (IOException e) {
            Logger.logError("Erreur lors de l'export CSV des livres", "SYSTEM", e);
            return false;
        }
    }
    
 
    public static List<Livre> importLivresFromCSV(String filePath) {
        List<Livre> livres = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Ignorer la première ligne (en-tête)
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                // Ignorer les lignes vides
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parser la ligne CSV
                String[] parts = parseCSVLine(line);
                
                if (parts.length >= 5) {
                    try {
                        Livre livre = new Livre(
                            parts[0].trim(), // ISBN
                            parts[1].trim(), // Titre
                            parts[2].trim(), // Auteur
                            parts[3].trim(), // Catégorie
                            Integer.parseInt(parts[4].trim()) // Exemplaires totaux
                        );
                        
                        // Si un 6ème champ existe pour les exemplaires disponibles
                        if (parts.length >= 6) {
                            livre.setExemplairesDisponibles(Integer.parseInt(parts[5].trim()));
                        }
                        
                        livres.add(livre);
                        
                    } catch (NumberFormatException e) {
                        Logger.logError("Erreur de format numérique dans le CSV à la ligne: " + line, 
                                      "SYSTEM", e);
                    }
                }
            }
            
            Logger.log("Import CSV des livres: " + livres.size() + " livres importés depuis " + filePath, 
                      "SYSTEM");
            
        } catch (FileNotFoundException e) {
            Logger.logError("Fichier CSV non trouvé: " + filePath, "SYSTEM", e);
        } catch (IOException e) {
            Logger.logError("Erreur de lecture du fichier CSV: " + filePath, "SYSTEM", e);
        }
        
        return livres;
    }
    
  
    public static boolean exportAdherentsToCSV(List<Adherent> adherents, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-tête
            writer.println("NumeroUnique,Nom,Prenom,Email,Telephone,DateInscription,Bloque,NbEmpruntsActuels");
            
            // Données
            for (Adherent adherent : adherents) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s,%b,%d",
                    escapeCSV(adherent.getNumeroUnique()),
                    escapeCSV(adherent.getNom()),
                    escapeCSV(adherent.getPrenom()),
                    escapeCSV(adherent.getEmail()),
                    escapeCSV(adherent.getTelephone()),
                    adherent.getDateInscription().format(DATE_FORMATTER),
                    adherent.isBloque(),
                    adherent.getNbEmpruntsActuels()));
            }
            
            Logger.log("Export CSV des adhérents: " + adherents.size() + " adhérents exportés vers " + filePath, 
                      "SYSTEM");
            return true;
            
        } catch (IOException e) {
            Logger.logError("Erreur lors de l'export CSV des adhérents", "SYSTEM", e);
            return false;
        }
    }
    
 
    public static List<Adherent> importAdherentsFromCSV(String filePath) {
        List<Adherent> adherents = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Ignorer la première ligne (en-tête)
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                // Ignorer les lignes vides
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parser la ligne CSV
                String[] parts = parseCSVLine(line);
                
                if (parts.length >= 5) {
                    try {
                        Adherent adherent = new Adherent(
                            parts[0].trim(), // Numéro unique
                            parts[1].trim(), // Nom
                            parts[2].trim(), // Prénom
                            parts[3].trim(), // Email
                            parts[4].trim()  // Téléphone
                        );
                        
                        // Date d'inscription (si fournie)
                        if (parts.length >= 6 && !parts[5].trim().isEmpty()) {
                            try {
                                LocalDate date = LocalDate.parse(parts[5].trim(), DATE_FORMATTER);
                                adherent.setDateInscription(date);
                            } catch (DateTimeParseException e) {
                                // Utiliser la date actuelle par défaut
                            }
                        }
                        
                        // Statut bloqué (si fourni)
                        if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
                            adherent.setBloque(Boolean.parseBoolean(parts[6].trim()));
                        }
                        
                        // Nombre d'emprunts actuels (si fourni)
                        if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                            adherent.setNbEmpruntsActuels(Integer.parseInt(parts[7].trim()));
                        }
                        
                        adherents.add(adherent);
                        
                    } catch (NumberFormatException e) {
                        Logger.logError("Erreur de format numérique dans le CSV à la ligne: " + line, 
                                      "SYSTEM", e);
                    }
                }
            }
            
            Logger.log("Import CSV des adhérents: " + adherents.size() + " adhérents importés depuis " + filePath, 
                      "SYSTEM");
            
        } catch (FileNotFoundException e) {
            Logger.logError("Fichier CSV non trouvé: " + filePath, "SYSTEM", e);
        } catch (IOException e) {
            Logger.logError("Erreur de lecture du fichier CSV: " + filePath, "SYSTEM", e);
        }
        
        return adherents;
    }
    
 
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        // Si la valeur contient des virgules, des guillemets ou des sauts de ligne
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Remplacer les guillemets par des doubles guillemets
            value = value.replace("\"", "\"\"");
            // Encadrer de guillemets
            value = "\"" + value + "\"";
        }
        
        return value;
    }
    

    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Guillemet double à l'intérieur des guillemets
                    current.append('"');
                    i++; // Passer le prochain guillemet
                } else {
                    // Début ou fin de guillemets
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Fin d'un champ
                result.add(current.toString());
                current.setLength(0); // Réinitialiser
            } else {
                current.append(c);
            }
        }
        
        // Ajouter le dernier champ
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
}