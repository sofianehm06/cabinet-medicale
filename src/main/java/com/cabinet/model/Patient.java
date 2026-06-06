package com.cabinet.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Patient enregistre au cabinet (fiche administrative).
 */
public class Patient {

    private int id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe; // "M" ou "F"
    private String telephone;
    private String email;
    private String adresse;
    private String groupeSanguin;
    private String numeroSecuriteSociale;
    private LocalDateTime dateCreation;

    public Patient() {
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

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public String getNumeroSecuriteSociale() {
        return numeroSecuriteSociale;
    }

    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getNomComplet() {
        return nom + " " + prenom;
    }

    /** Age calcule a partir de la date de naissance (ou -1 si inconnue). */
    public int getAge() {
        if (dateNaissance == null) {
            return -1;
        }
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    /** Affichage dans les listes deroulantes (JComboBox). */
    @Override
    public String toString() {
        return getNomComplet();
    }
}
