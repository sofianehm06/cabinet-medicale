package com.cabinet.dao;

import com.cabinet.db.Database;
import com.cabinet.model.Utilisateur;
import com.cabinet.util.PasswordUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acces aux donnees des comptes utilisateurs (authentification).
 */
public class UtilisateurDAO {

    /**
     * Authentifie un utilisateur a partir de son identifiant et mot de passe.
     *
     * @return l'utilisateur si les identifiants sont valides et le compte actif, sinon {@code null}.
     */
    public Utilisateur authentifier(String nomUtilisateur, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE nom_utilisateur = ? AND actif = 1";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, nomUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = mapper(rs);
                    if (PasswordUtil.verifier(motDePasse, u.getMotDePasse(), u.getSel())) {
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'authentification : " + e.getMessage(), e);
        }
        return null;
    }

    private Utilisateur mapper(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNomUtilisateur(rs.getString("nom_utilisateur"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setSel(rs.getString("sel"));
        u.setNomComplet(rs.getString("nom_complet"));
        u.setRole(rs.getString("role"));
        u.setActif(rs.getInt("actif") == 1);
        return u;
    }
}
