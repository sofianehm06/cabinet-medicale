package com.cabinet.model;

import java.time.LocalDateTime;

/**
 * Consultation medicale : diagnostic, traitement (ordonnance) et montant.
 * Peut etre rattachee a un rendez-vous (facultatif).
 */
public class Consultation {

    private int id;
    private int patientId;
    private int medecinId;
    private Integer rendezVousId; // facultatif (peut etre null)
    private LocalDateTime dateConsultation;
    private String motif;
    private String diagnostic;
    private String traitement;   // ordonnance / prescription
    private String observations;
    private double montant;

    // Champs d'affichage (renseignes via jointures)
    private String patientNom;
    private String medecinNom;

    public Consultation() {
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

    public Integer getRendezVousId() {
        return rendezVousId;
    }

    public void setRendezVousId(Integer rendezVousId) {
        this.rendezVousId = rendezVousId;
    }

    public LocalDateTime getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDateTime dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
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
