package biblio.util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;


public class DateUtils {
    
    public static final DateTimeFormatter DISPLAY_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATABASE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
 
    public static String formatForDisplay(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        return date.format(DISPLAY_FORMATTER);
    }
  
    public static String formatForDatabase(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATABASE_FORMATTER);
    }
    
  
    public static LocalDate parseFromDisplay(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateString.trim(), DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
  
    public static LocalDate parseFromDatabase(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateString.trim(), DATABASE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
 
    public static long daysBetween(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(date1, date2);
    }
   
    public static boolean isFutureDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }
    
  
    public static boolean isPastDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDate.now());
    }
    
   
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isEqual(LocalDate.now());
    }
    
   
    public static LocalDate addWorkingDays(LocalDate startDate, int workingDays) {
        if (startDate == null) {
            return null;
        }
        
        LocalDate result = startDate;
        int addedDays = 0;
        
        while (addedDays < workingDays) {
            result = result.plusDays(1);
            // Si ce n'est pas samedi (6) ni dimanche (7)
            if (result.getDayOfWeek().getValue() < 6) {
                addedDays++;
            }
        }
        
        return result;
    }
    
 
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Samedi ou dimanche
    }
}