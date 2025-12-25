package biblio.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
  
    public static String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public static boolean verify(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            // En cas d'erreur (hash invalide, etc.)
            System.err.println("Erreur lors de la vérification du mot de passe: " + e.getMessage());
            return false;
        }
    }
    
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8; // Longueur minimale recommandée
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
    
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return 1; // Trop court
        }
        
        int score = 0;
        

        if (password.length() >= 12) score++;
        
        // Majuscules
        if (password.matches(".*[A-Z].*")) score++;
        
        // Minuscules
        if (password.matches(".*[a-z].*")) score++;
        
        // Chiffres
        if (password.matches(".*[0-9].*")) score++;
        
        // Caractères spéciaux
        if (password.matches(".*[!@#$%^&*()].*")) score++;
        
        // Évaluer le score
        if (score <= 2) return 1; // Faible
        if (score <= 4) return 2; // Moyen
        return 3; // Fort
    }
}