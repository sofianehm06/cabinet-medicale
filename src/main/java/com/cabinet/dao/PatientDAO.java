package com.cabinet.dao;

import com.cabinet.db.Database;
import com.cabinet.model.Patient;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acces aux donnees des patients (CRUD + recherche).
 */
public class PatientDAO {

    public List<Patient> listerTous() {
        return executerListe("SELECT * FROM patient ORDER BY nom, prenom", null);
    }

    /** Recherche par nom, prenom, telephone ou numero de securite sociale. */
    public List<Patient> rechercher(String terme) {
        if (terme == null || terme.isBlank()) {
            return listerTous();
        }
        String sql = "SELECT * FROM patient WHERE nom LIKE ? OR prenom LIKE ? OR telephone LIKE ? "
                + "OR numero_securite_sociale LIKE ? ORDER BY nom, prenom";
        return executerListe(sql, "%" + terme.trim() + "%");
    }

    private List<Patient> executerListe(String sql, String parametreLike) {
        List<Patient> liste = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            if (parametreLike != null) {
                for (int i = 1; i <= 4; i++) {
                    ps.setString(i, parametreLike);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des patients : " + e.getMessage(), e);
        }
        return liste;
    }

    public Patient trouverParId(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM patient WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du patient : " + e.getMessage(), e);
        }
        return null;
    }

    public void ajouter(Patient p) {
        String sql = "INSERT INTO patient(nom, prenom, date_naissance, sexe, telephone, email, adresse, "
                + "groupe_sanguin, numero_securite_sociale, date_creation) VALUES (?,?,?,?,?,?,?,?,?,?)";
        if (p.getDateCreation() == null) {
            p.setDateCreation(LocalDateTime.now());
        }
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplir(ps, p);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du patient : " + e.getMessage(), e);
        }
    }

    public void modifier(Patient p) {
        String sql = "UPDATE patient SET nom=?, prenom=?, date_naissance=?, sexe=?, telephone=?, email=?, "
                + "adresse=?, groupe_sanguin=?, numero_securite_sociale=?, date_creation=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            remplir(ps, p);
            ps.setInt(11, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du patient : " + e.getMessage(), e);
        }
    }

    public void supprimer(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM patient WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du patient : " + e.getMessage(), e);
        }
    }

    public int compter() {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT COUNT(*) FROM patient");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private void remplir(PreparedStatement ps, Patient p) throws SQLException {
        ps.setString(1, p.getNom());
        ps.setString(2, p.getPrenom());
        ps.setString(3, p.getDateNaissance() != null ? p.getDateNaissance().toString() : null);
        ps.setString(4, p.getSexe());
        ps.setString(5, p.getTelephone());
        ps.setString(6, p.getEmail());
        ps.setString(7, p.getAdresse());
        ps.setString(8, p.getGroupeSanguin());
        ps.setString(9, p.getNumeroSecuriteSociale());
        ps.setString(10, p.getDateCreation() != null ? p.getDateCreation().toString() : null);
    }

    private Patient mapper(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setDateNaissance(parseDate(rs.getString("date_naissance")));
        p.setSexe(rs.getString("sexe"));
        p.setTelephone(rs.getString("telephone"));
        p.setEmail(rs.getString("email"));
        p.setAdresse(rs.getString("adresse"));
        p.setGroupeSanguin(rs.getString("groupe_sanguin"));
        p.setNumeroSecuriteSociale(rs.getString("numero_securite_sociale"));
        p.setDateCreation(parseDateTime(rs.getString("date_creation")));
        return p;
    }

    static LocalDate parseDate(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(valeur);
        } catch (Exception e) {
            return null;
        }
    }

    static LocalDateTime parseDateTime(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(valeur);
        } catch (Exception e) {
            return null;
        }
    }
}
