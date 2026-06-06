package com.cabinet.ui.dialogs;

import com.cabinet.dao.ConsultationDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.dao.PatientDAO;
import com.cabinet.model.Consultation;
import com.cabinet.model.Medecin;
import com.cabinet.model.Patient;
import com.cabinet.util.DateUtil;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDateTime;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

/**
 * Boite de dialogue de creation / modification d'une consultation
 * (diagnostic, ordonnance et montant).
 */
public class ConsultationDialog extends JDialog {

    private final ConsultationDAO dao = new ConsultationDAO();
    private final Consultation consultation; // null => creation
    private boolean valide = false;

    private final JComboBox<Patient> comboPatient = new JComboBox<>();
    private final JComboBox<Medecin> comboMedecin = new JComboBox<>();
    private final JSpinner dateConsultation = new JSpinner(new SpinnerDateModel());
    private final JTextField motif = new JTextField(20);
    private final JTextArea diagnostic = new JTextArea(3, 20);
    private final JTextArea traitement = new JTextArea(4, 20);
    private final JTextArea observations = new JTextArea(2, 20);
    private final JSpinner montant = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000.0, 5.0));

    public ConsultationDialog(Window parent, Consultation consultation) {
        super(parent, consultation == null ? "Nouvelle consultation" : "Modifier la consultation",
                ModalityType.APPLICATION_MODAL);
        this.consultation = consultation;

        chargerCombos();
        dateConsultation.setEditor(new JSpinner.DateEditor(dateConsultation, "yyyy-MM-dd HH:mm"));
        dateConsultation.setValue(DateUtil.versDate(LocalDateTime.now().withSecond(0).withNano(0)));
        for (JTextArea zone : new JTextArea[]{diagnostic, traitement, observations}) {
            zone.setLineWrap(true);
            zone.setWrapStyleWord(true);
        }

        setContentPane(construire());
        if (consultation != null) {
            remplirChamps();
        }
        pack();
        setMinimumSize(new Dimension(480, getHeight()));
        setLocationRelativeTo(parent);
    }

    public boolean isValide() {
        return valide;
    }

    private void chargerCombos() {
        for (Patient p : new PatientDAO().listerTous()) {
            comboPatient.addItem(p);
        }
        for (Medecin m : new MedecinDAO().listerTous()) {
            comboMedecin.addItem(m);
        }
    }

    private JPanel construire() {
        JPanel racine = new JPanel(new BorderLayout());
        racine.setBackground(UIUtils.CARD);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.gridy = 0;

        ligne(form, g, "Patient * :", comboPatient);
        ligne(form, g, "Médecin * :", comboMedecin);
        ligne(form, g, "Date :", dateConsultation);
        ligne(form, g, "Motif :", motif);
        ligne(form, g, "Diagnostic :", new JScrollPane(diagnostic));
        ligne(form, g, "Traitement (ordonnance) :", new JScrollPane(traitement));
        ligne(form, g, "Observations :", new JScrollPane(observations));
        ligne(form, g, "Montant (€) :", montant);

        racine.add(form, BorderLayout.CENTER);
        racine.add(construireBoutons(), BorderLayout.SOUTH);
        return racine;
    }

    private JPanel construireBoutons() {
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        boutons.setBackground(UIUtils.CARD);
        boutons.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        JButton annuler = UIUtils.creerBouton("Annuler", new java.awt.Color(0x90A4AE));
        JButton enregistrer = UIUtils.creerBouton("Enregistrer", UIUtils.PRIMARY);
        annuler.addActionListener(e -> dispose());
        enregistrer.addActionListener(e -> enregistrer());
        boutons.add(annuler);
        boutons.add(enregistrer);
        return boutons;
    }

    private void ligne(JPanel p, GridBagConstraints g, String label, Component champ) {
        g.gridx = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.FIRST_LINE_END;
        JLabel l = new JLabel(label);
        l.setFont(UIUtils.FONT_NORMAL);
        p.add(l, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.LINE_START;
        p.add(champ, g);
        g.gridy++;
    }

    private void remplirChamps() {
        selectionnerPatient(consultation.getPatientId());
        selectionnerMedecin(consultation.getMedecinId());
        if (consultation.getDateConsultation() != null) {
            dateConsultation.setValue(DateUtil.versDate(consultation.getDateConsultation()));
        }
        motif.setText(consultation.getMotif());
        diagnostic.setText(consultation.getDiagnostic());
        traitement.setText(consultation.getTraitement());
        observations.setText(consultation.getObservations());
        montant.setValue(consultation.getMontant());
    }

    private void selectionnerPatient(int id) {
        for (int i = 0; i < comboPatient.getItemCount(); i++) {
            if (comboPatient.getItemAt(i).getId() == id) {
                comboPatient.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectionnerMedecin(int id) {
        for (int i = 0; i < comboMedecin.getItemCount(); i++) {
            if (comboMedecin.getItemAt(i).getId() == id) {
                comboMedecin.setSelectedIndex(i);
                return;
            }
        }
    }

    private void enregistrer() {
        Patient p = (Patient) comboPatient.getSelectedItem();
        Medecin m = (Medecin) comboMedecin.getSelectedItem();
        if (p == null || m == null) {
            UIUtils.erreur(this, "Vous devez avoir au moins un patient et un médecin enregistrés.");
            return;
        }

        Consultation c = consultation != null ? consultation : new Consultation();
        c.setPatientId(p.getId());
        c.setMedecinId(m.getId());
        c.setDateConsultation(DateUtil.versLocalDateTime((Date) dateConsultation.getValue()).withSecond(0).withNano(0));
        c.setMotif(motif.getText().trim());
        c.setDiagnostic(diagnostic.getText().trim());
        c.setTraitement(traitement.getText().trim());
        c.setObservations(observations.getText().trim());
        c.setMontant(((Number) montant.getValue()).doubleValue());

        try {
            if (consultation == null) {
                dao.ajouter(c);
            } else {
                dao.modifier(c);
            }
            valide = true;
            dispose();
        } catch (RuntimeException ex) {
            UIUtils.erreur(this, ex.getMessage());
        }
    }
}
