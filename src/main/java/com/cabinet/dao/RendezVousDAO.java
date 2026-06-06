package com.cabinet.dao;

import com.cabinet.db.Database;
import com.cabinet.model.RendezVous;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acces aux donnees des rendez-vous (CRUD + filtres).
 */
public class RendezVousDAO {

    private static final String SELECT_BASE =
            "SELECT rv.*, p.nom AS p_nom, p.prenom AS p_prenom, m.nom AS m_nom, m.prenom AS m_prenom "
            + "FROM rendez_vous rv "
            + "JOIN patient p ON p.id = rv.patient_id "
            + "JOIN medecin m ON m.id = rv.medecin_id ";

    public List<RendezVous> listerTous() {
        return executer(SELECT_BASE + "ORDER BY rv.date_heure DESC", null);
    }

    /** Rendez-vous d'une journee donnee, tries par heure croissante. */
    public List<RendezVous> listerParJour(LocalDate jour) {
        return executer(SELECT_BASE + "WHERE rv.date_heure LIKE ? ORDER BY rv.date_heure ASC",
                jour.toString() + "%");
    }

    private List<RendezVous> executer(String sql, String parametreLike) {
        List<RendezVous> liste = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            if (parametreLike != null) {
                ps.setString(1, parametreLike);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des rendez-vous : " + e.getMessage(), e);
        }
        return liste;
    }

    public void ajouter(RendezVous rv) {
        String sql = "INSERT INTO rendez_vous(patient_id, medecin_id, date_heure, motif, statut, notes) "
                + "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplir(ps, rv);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    rv.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du rendez-vous : " + e.getMessage(), e);
        }
    }

    public void modifier(RendezVous rv) {
        String sql = "UPDATE rendez_vous SET patient_id=?, medecin_id=?, date_heure=?, motif=?, statut=?, notes=? "
                + "WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            remplir(ps, rv);
            ps.setInt(7, rv.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du rendez-vous : " + e.getMessage(), e);
        }
    }

    public void changerStatut(int id, String statut) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "UPDATE rendez_vous SET statut=? WHERE id=?")) {
            ps.setString(1, statut);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du changement de statut : " + e.getMessage(), e);
        }
    }

    public void supprimer(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM rendez_vous WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du rendez-vous : " + e.getMessage(), e);
        }
    }

    /** Nombre de rendez-vous planifies pour une journee donnee. */
    public int compterParJour(LocalDate jour) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM rendez_vous WHERE date_heure LIKE ?")) {
            ps.setString(1, jour.toString() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    private void remplir(PreparedStatement ps, RendezVous rv) throws SQLException {
        ps.setInt(1, rv.getPatientId());
        ps.setInt(2, rv.getMedecinId());
        ps.setString(3, rv.getDateHeure() != null ? rv.getDateHeure().toString() : null);
        ps.setString(4, rv.getMotif());
        ps.setString(5, rv.getStatut());
        ps.setString(6, rv.getNotes());
    }

    private RendezVous mapper(ResultSet rs) throws SQLException {
        RendezVous rv = new RendezVous();
        rv.setId(rs.getInt("id"));
        rv.setPatientId(rs.getInt("patient_id"));
        rv.setMedecinId(rs.getInt("medecin_id"));
        rv.setDateHeure(PatientDAO.parseDateTime(rs.getString("date_heure")));
        rv.setMotif(rs.getString("motif"));
        rv.setStatut(rs.getString("statut"));
        rv.setNotes(rs.getString("notes"));
        rv.setPatientNom(rs.getString("p_nom") + " " + rs.getString("p_prenom"));
        rv.setMedecinNom("Dr " + rs.getString("m_nom") + " " + rs.getString("m_prenom"));
        return rv;
    }
}
