package com.cabinet.dao;

import com.cabinet.db.Database;
import com.cabinet.model.Consultation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acces aux donnees des consultations (CRUD + historique patient).
 */
public class ConsultationDAO {

    private static final String SELECT_BASE =
            "SELECT c.*, p.nom AS p_nom, p.prenom AS p_prenom, m.nom AS m_nom, m.prenom AS m_prenom "
            + "FROM consultation c "
            + "JOIN patient p ON p.id = c.patient_id "
            + "JOIN medecin m ON m.id = c.medecin_id ";

    public List<Consultation> listerTous() {
        return executer(SELECT_BASE + "ORDER BY c.date_consultation DESC", 0);
    }

    /** Historique des consultations d'un patient (de la plus recente a la plus ancienne). */
    public List<Consultation> listerParPatient(int patientId) {
        return executer(SELECT_BASE + "WHERE c.patient_id = ? ORDER BY c.date_consultation DESC", patientId);
    }

    private List<Consultation> executer(String sql, int patientId) {
        List<Consultation> liste = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            if (patientId > 0) {
                ps.setInt(1, patientId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des consultations : " + e.getMessage(), e);
        }
        return liste;
    }

    public void ajouter(Consultation c) {
        String sql = "INSERT INTO consultation(patient_id, medecin_id, rendez_vous_id, date_consultation, "
                + "motif, diagnostic, traitement, observations, montant) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplir(ps, c);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la consultation : " + e.getMessage(), e);
        }
    }

    public void modifier(Consultation c) {
        String sql = "UPDATE consultation SET patient_id=?, medecin_id=?, rendez_vous_id=?, date_consultation=?, "
                + "motif=?, diagnostic=?, traitement=?, observations=?, montant=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            remplir(ps, c);
            ps.setInt(10, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la consultation : " + e.getMessage(), e);
        }
    }

    public void supprimer(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM consultation WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la consultation : " + e.getMessage(), e);
        }
    }

    public int compter() {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT COUNT(*) FROM consultation");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    /** Total des montants des consultations (chiffre d'affaires). */
    public double totalMontants() {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT COALESCE(SUM(montant),0) FROM consultation");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private void remplir(PreparedStatement ps, Consultation c) throws SQLException {
        ps.setInt(1, c.getPatientId());
        ps.setInt(2, c.getMedecinId());
        if (c.getRendezVousId() != null) {
            ps.setInt(3, c.getRendezVousId());
        } else {
            ps.setNull(3, java.sql.Types.INTEGER);
        }
        ps.setString(4, c.getDateConsultation() != null ? c.getDateConsultation().toString() : null);
        ps.setString(5, c.getMotif());
        ps.setString(6, c.getDiagnostic());
        ps.setString(7, c.getTraitement());
        ps.setString(8, c.getObservations());
        ps.setDouble(9, c.getMontant());
    }

    private Consultation mapper(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setId(rs.getInt("id"));
        c.setPatientId(rs.getInt("patient_id"));
        c.setMedecinId(rs.getInt("medecin_id"));
        int rvId = rs.getInt("rendez_vous_id");
        c.setRendezVousId(rs.wasNull() ? null : rvId);
        c.setDateConsultation(PatientDAO.parseDateTime(rs.getString("date_consultation")));
        c.setMotif(rs.getString("motif"));
        c.setDiagnostic(rs.getString("diagnostic"));
        c.setTraitement(rs.getString("traitement"));
        c.setObservations(rs.getString("observations"));
        c.setMontant(rs.getDouble("montant"));
        c.setPatientNom(rs.getString("p_nom") + " " + rs.getString("p_prenom"));
        c.setMedecinNom("Dr " + rs.getString("m_nom") + " " + rs.getString("m_prenom"));
        return c;
    }
}
