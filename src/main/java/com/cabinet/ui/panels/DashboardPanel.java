package com.cabinet.ui.panels;

import com.cabinet.dao.ConsultationDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.dao.PatientDAO;
import com.cabinet.dao.RendezVousDAO;
import com.cabinet.model.RendezVous;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Tableau de bord : statistiques cles et rendez-vous du jour.
 */
public class DashboardPanel extends JPanel {

    private final PatientDAO patientDAO = new PatientDAO();
    private final MedecinDAO medecinDAO = new MedecinDAO();
    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private final ConsultationDAO consultationDAO = new ConsultationDAO();

    private final JLabel valPatients = grandLabel(UIUtils.PRIMARY);
    private final JLabel valMedecins = grandLabel(new Color(0x3949AB));
    private final JLabel valRdvJour = grandLabel(UIUtils.WARNING);
    private final JLabel valConsultations = grandLabel(new Color(0x8E24AA));
    private final JLabel valChiffre = grandLabel(UIUtils.SUCCESS);

    private final DefaultTableModel modeleRdv = UIUtils.modeleLectureSeule(
            "Heure", "Patient", "Médecin", "Motif", "Statut");
    private final DateTimeFormatter heureFmt = DateTimeFormatter.ofPattern("HH:mm");

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        // Cartes statistiques
        JPanel cartes = new JPanel(new GridLayout(1, 5, 16, 0));
        cartes.setOpaque(false);
        cartes.add(carte("PATIENTS", valPatients, UIUtils.PRIMARY));
        cartes.add(carte("MÉDECINS", valMedecins, new Color(0x3949AB)));
        cartes.add(carte("RDV AUJOURD'HUI", valRdvJour, UIUtils.WARNING));
        cartes.add(carte("CONSULTATIONS", valConsultations, new Color(0x8E24AA)));
        cartes.add(carte("CHIFFRE D'AFFAIRES", valChiffre, UIUtils.SUCCESS));
        add(cartes, BorderLayout.NORTH);

        // Rendez-vous du jour
        JPanel bloc = new JPanel(new BorderLayout(0, 10));
        bloc.setBackground(UIUtils.CARD);
        bloc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titre = new JLabel("Rendez-vous du jour");
        titre.setFont(UIUtils.FONT_SOUS_TITRE);
        titre.setForeground(UIUtils.TEXT);
        bloc.add(titre, BorderLayout.NORTH);

        JTable table = new JTable(modeleRdv);
        UIUtils.styliserTable(table);
        bloc.add(new JScrollPane(table), BorderLayout.CENTER);

        add(bloc, BorderLayout.CENTER);

        rafraichir();
    }

    /** Recharge les compteurs et le tableau des rendez-vous du jour. */
    public void rafraichir() {
        valPatients.setText(String.valueOf(patientDAO.compter()));
        valMedecins.setText(String.valueOf(medecinDAO.compter()));
        valRdvJour.setText(String.valueOf(rendezVousDAO.compterParJour(LocalDate.now())));
        valConsultations.setText(String.valueOf(consultationDAO.compter()));
        valChiffre.setText(String.format("%.0f €", consultationDAO.totalMontants()));

        modeleRdv.setRowCount(0);
        for (RendezVous rv : rendezVousDAO.listerParJour(LocalDate.now())) {
            String heure = rv.getDateHeure() != null ? rv.getDateHeure().format(heureFmt) : "-";
            modeleRdv.addRow(new Object[]{
                heure, rv.getPatientNom(), rv.getMedecinNom(), rv.getMotif(), rv.getStatut()
            });
        }
    }

    private JPanel carte(String titre, JLabel valeur, Color couleur) {
        JPanel carte = new JPanel(new BorderLayout(0, 8));
        carte.setBackground(UIUtils.CARD);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, couleur),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        carte.setPreferredSize(new Dimension(0, 110));

        JLabel l = new JLabel(titre);
        l.setFont(UIUtils.FONT_PETIT);
        l.setForeground(new Color(0x9E9E9E));

        carte.add(l, BorderLayout.NORTH);
        carte.add(valeur, BorderLayout.CENTER);
        return carte;
    }

    private JLabel grandLabel(Color couleur) {
        JLabel l = new JLabel("0");
        l.setFont(l.getFont().deriveFont(java.awt.Font.BOLD, 30f));
        l.setForeground(couleur);
        return l;
    }
}
