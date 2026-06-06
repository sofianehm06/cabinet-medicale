package com.cabinet.dao;

import com.cabinet.db.Database;
import com.cabinet.model.Medecin;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acces aux donnees des medecins (CRUD).
 */
public class MedecinDAO {

    public List<Medecin> listerTous() {
        List<Medecin> liste = new ArrayList<>();
        String sql = "SELECT * FROM medecin ORDER BY nom, prenom";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des medecins : " + e.getMessage(), e);
        }
        return liste;
    }

    public Medecin trouverParId(int id) {
        String sql = "SELECT * FROM medecin WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du medecin : " + e.getMessage(), e);
        }
        return null;
    }

    public void ajouter(Medecin m) {
        String sql = "INSERT INTO medecin(nom, prenom, specialite, telephone, email) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplir(ps, m);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du medecin : " + e.getMessage(), e);
        }
    }

    public void modifier(Medecin m) {
        String sql = "UPDATE medecin SET nom=?, prenom=?, specialite=?, telephone=?, email=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            remplir(ps, m);
            ps.setInt(6, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du medecin : " + e.getMessage(), e);
        }
    }

    public void supprimer(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM medecin WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du medecin : " + e.getMessage(), e);
        }
    }

    public int compter() {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT COUNT(*) FROM medecin");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private void remplir(PreparedStatement ps, Medecin m) throws SQLException {
        ps.setString(1, m.getNom());
        ps.setString(2, m.getPrenom());
        ps.setString(3, m.getSpecialite());
        ps.setString(4, m.getTelephone());
        ps.setString(5, m.getEmail());
    }

    private Medecin mapper(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();
        m.setId(rs.getInt("id"));
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setSpecialite(rs.getString("specialite"));
        m.setTelephone(rs.getString("telephone"));
        m.setEmail(rs.getString("email"));
        return m;
    }
}
