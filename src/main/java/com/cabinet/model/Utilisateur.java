package com.cabinet.model;

/**
 * Compte utilisateur de l'application (personnel du cabinet).
 * Le mot de passe n'est jamais stocke en clair : on conserve un hachage
 * PBKDF2 ({@code motDePasse}) accompagne de son sel ({@code sel}).
 */
public class Utilisateur {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MEDECIN = "MEDECIN";
    public static final String ROLE_SECRETAIRE = "SECRETAIRE";

    private int id;
    private String nomUtilisateur;
    private String motDePasse; // hachage PBKDF2 (Base64)
    private String sel;        // sel cryptographique (Base64)
    private String nomComplet;
    private String role = ROLE_SECRETAIRE;
    private boolean actif = true;

    public Utilisateur() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getSel() {
        return sel;
    }

    public void setSel(String sel) {
        this.sel = sel;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return nomComplet != null && !nomComplet.isBlank() ? nomComplet : nomUtilisateur;
    }
}
