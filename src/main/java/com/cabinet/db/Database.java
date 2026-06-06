package com.cabinet.db;

import com.cabinet.util.PasswordUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestion de la base de donnees SQLite embarquee :
 * connexion partagee, creation du schema et insertion des donnees initiales.
 *
 * Le fichier de base est cree dans le dossier personnel de l'utilisateur :
 * {@code ~/.cabinet-medical/cabinet.db}
 */
public final class Database {

    private static Connection connection;

    private Database() {
    }

    /** Retourne la connexion partagee (l'ouvre au premier appel). */
    public static synchronized Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("La base n'est pas initialisee. Appelez Database.initialiser() au demarrage.");
        }
        return connection;
    }

    /**
     * Initialise la base : ouvre la connexion, active les cles etrangeres,
     * cree les tables si necessaire et insere les donnees de demonstration.
     */
    public static synchronized void initialiser() {
        try {
            Path dossier = Paths.get(System.getProperty("user.home"), ".cabinet-medical");
            Files.createDirectories(dossier);
            Path fichier = dossier.resolve("cabinet.db");

            String url = "jdbc:sqlite:" + fichier.toAbsolutePath();
            connection = DriverManager.getConnection(url);

            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }

            creerSchema();
            insererDonneesInitiales();
        } catch (Exception e) {
            throw new RuntimeException("Impossible d'initialiser la base de donnees : " + e.getMessage(), e);
        }
    }

    private static void creerSchema() throws SQLException {
        String[] tables = {
            """
            CREATE TABLE IF NOT EXISTS utilisateur (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom_utilisateur TEXT NOT NULL UNIQUE,
                mot_de_passe TEXT NOT NULL,
                sel TEXT NOT NULL,
                nom_complet TEXT,
                role TEXT NOT NULL DEFAULT 'SECRETAIRE',
                actif INTEGER NOT NULL DEFAULT 1
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS medecin (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                specialite TEXT,
                telephone TEXT,
                email TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS patient (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                date_naissance TEXT,
                sexe TEXT,
                telephone TEXT,
                email TEXT,
                adresse TEXT,
                groupe_sanguin TEXT,
                numero_securite_sociale TEXT,
                date_creation TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS rendez_vous (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id INTEGER NOT NULL REFERENCES patient(id) ON DELETE CASCADE,
                medecin_id INTEGER NOT NULL REFERENCES medecin(id) ON DELETE CASCADE,
                date_heure TEXT NOT NULL,
                motif TEXT,
                statut TEXT NOT NULL DEFAULT 'PLANIFIE',
                notes TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS consultation (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id INTEGER NOT NULL REFERENCES patient(id) ON DELETE CASCADE,
                medecin_id INTEGER NOT NULL REFERENCES medecin(id) ON DELETE CASCADE,
                rendez_vous_id INTEGER REFERENCES rendez_vous(id) ON DELETE SET NULL,
                date_consultation TEXT NOT NULL,
                motif TEXT,
                diagnostic TEXT,
                traitement TEXT,
                observations TEXT,
                montant REAL NOT NULL DEFAULT 0
            )
            """
        };

        try (Statement st = connection.createStatement()) {
            for (String sql : tables) {
                st.execute(sql);
            }
        }
    }

    /** Insere un compte administrateur et quelques donnees de demonstration. */
    private static void insererDonneesInitiales() throws SQLException {
        if (compter("utilisateur") == 0) {
            String sel = PasswordUtil.genererSel();
            String hash = PasswordUtil.hacher("admin123", sel);
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO utilisateur(nom_utilisateur, mot_de_passe, sel, nom_complet, role, actif) "
                    + "VALUES (?,?,?,?,?,1)")) {
                ps.setString(1, "admin");
                ps.setString(2, hash);
                ps.setString(3, sel);
                ps.setString(4, "Administrateur");
                ps.setString(5, "ADMIN");
                ps.executeUpdate();
            }
        }

        if (compter("medecin") == 0) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO medecin(nom, prenom, specialite, telephone, email) VALUES (?,?,?,?,?)")) {
                inserer(ps, "Bernard", "Sophie", "Medecine generale", "0612345678", "s.bernard@cabinet.fr");
                inserer(ps, "Martin", "Lucas", "Cardiologie", "0623456789", "l.martin@cabinet.fr");
                inserer(ps, "Dubois", "Amel", "Pediatrie", "0634567890", "a.dubois@cabinet.fr");
            }
        }

        if (compter("patient") == 0) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO patient(nom, prenom, date_naissance, sexe, telephone, email, adresse, "
                    + "groupe_sanguin, numero_securite_sociale, date_creation) VALUES (?,?,?,?,?,?,?,?,?,?)")) {
                insererPatient(ps, "Petit", "Karim", "1985-03-12", "M", "0701020304",
                        "karim.petit@email.fr", "12 rue des Lilas, Lyon", "O+", "185031234567890");
                insererPatient(ps, "Moreau", "Ines", "1992-07-25", "F", "0705060708",
                        "ines.moreau@email.fr", "5 avenue Jean Jaures, Lyon", "A+", "292071234567890");
                insererPatient(ps, "Garcia", "Hugo", "2015-11-03", "M", "0709101112",
                        "contact.garcia@email.fr", "8 place Bellecour, Lyon", "B+", "115111234567890");
            }
        }
    }

    private static void inserer(PreparedStatement ps, String nom, String prenom,
            String specialite, String tel, String email) throws SQLException {
        ps.setString(1, nom);
        ps.setString(2, prenom);
        ps.setString(3, specialite);
        ps.setString(4, tel);
        ps.setString(5, email);
        ps.executeUpdate();
    }

    private static void insererPatient(PreparedStatement ps, String nom, String prenom, String naissance,
            String sexe, String tel, String email, String adresse, String groupe, String nss) throws SQLException {
        ps.setString(1, nom);
        ps.setString(2, prenom);
        ps.setString(3, naissance);
        ps.setString(4, sexe);
        ps.setString(5, tel);
        ps.setString(6, email);
        ps.setString(7, adresse);
        ps.setString(8, groupe);
        ps.setString(9, nss);
        ps.setString(10, java.time.LocalDateTime.now().toString());
        ps.executeUpdate();
    }

    private static int compter(String table) throws SQLException {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Ferme proprement la connexion (a l'arret de l'application). */
    public static synchronized void fermer() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
            connection = null;
        }
    }
}
