package biblio.view;
import biblio.controller.MainController;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
      
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        SwingUtilities.invokeLater(() -> {
            MainController mainController = new MainController();
            LoginFrame loginFrame = new LoginFrame(mainController);
            loginFrame.setVisible(true);
        });
    }
}