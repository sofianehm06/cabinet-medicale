package com.cabinet.model;

import java.time.LocalDateTime;

/**
 * Rendez-vous entre un patient et un medecin.
 */
public class RendezVous {

    public static final String STATUT_PLANIFIE = "PLANIFIE";
    public static final String STATUT_CONFIRME = "CONFIRME";
    public static final String STATUT_TERMINE = "TERMINE";
    public static final String STATUT_ANNULE = "ANNULE";

    public static final String[] STATUTS = {
        STATUT_PLANIFIE, STATUT_CONFIRME, STATUT_TERMINE, STATUT_ANNULE
    };

    private int id;
    private int patientId;
    private int medecinId;
    private LocalDateTime dateHeure;
    private String motif;
    private String statut = STATUT_PLANIFIE;
    private String notes;

    // Champs d'affichage (renseignes via jointures, non persistes directement)
    private String patientNom;
    private String medecinNom;

    public RendezVous() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(int medecinId) {
        this.medecinId = medecinId;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPatientNom() {
        return patientNom;
    }

    public void setPatientNom(String patientNom) {
        this.patientNom = patientNom;
    }

    public String getMedecinNom() {
        return medecinNom;
    }

    public void setMedecinNom(String medecinNom) {
        this.medecinNom = medecinNom;
    }
}
