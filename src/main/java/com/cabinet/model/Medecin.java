package com.cabinet.model;

/**
 * Medecin exercant dans le cabinet.
 */
public class Medecin {

    private int id;
    private String nom;
    private String prenom;
    private String specialite;
    private String telephone;
    private String email;

    public Medecin() {
    }

    public Medecin(int id, String nom, String prenom, String specialite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomComplet() {
        return "Dr " + nom + " " + prenom;
    }

    /** Affichage dans les listes deroulantes (JComboBox). */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getNomComplet());
        if (specialite != null && !specialite.isBlank()) {
            sb.append(" (").append(specialite).append(")");
        }
        return sb.toString();
    }
}
