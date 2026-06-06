package com.cabinet;

import com.cabinet.db.Database;
import com.cabinet.ui.LoginFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Point d'entree de l'application de gestion de cabinet medical.
 *
 * <p>Identifiants par defaut : <b>admin</b> / <b>admin123</b></p>
 */
public class Main {

    public static void main(String[] args) {
        // Apparence moderne (Nimbus, fournie avec le JDK)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // On garde l'apparence par defaut en cas d'echec
        }

        // Initialisation de la base SQLite (creation du schema au premier lancement)
        try {
            Database.initialiser();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur au demarrage de la base de donnees :\n" + e.getMessage(),
                    "Erreur fatale", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fermeture propre de la connexion a l'arret
        Runtime.getRuntime().addShutdownHook(new Thread(Database::fermer));

        // Lancement de l'interface sur l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
