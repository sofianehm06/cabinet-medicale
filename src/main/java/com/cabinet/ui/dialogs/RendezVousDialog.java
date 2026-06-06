package com.cabinet.ui.dialogs;

import com.cabinet.dao.MedecinDAO;
import com.cabinet.dao.PatientDAO;
import com.cabinet.dao.RendezVousDAO;
import com.cabinet.model.Medecin;
import com.cabinet.model.Patient;
import com.cabinet.model.RendezVous;
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

/**
 * Boite de dialogue de creation / modification d'un rendez-vous.
 */
public class RendezVousDialog extends JDialog {

    private final RendezVousDAO dao = new RendezVousDAO();
    private final RendezVous rendezVous; // null => creation
    private boolean valide = false;

    private final JComboBox<Patient> comboPatient = new JComboBox<>();
    private final JComboBox<Medecin> comboMedecin = new JComboBox<>();
    private final JSpinner dateHeure = new JSpinner(new SpinnerDateModel());
    private final JTextField motif = new JTextField(20);
    private final JComboBox<String> statut = new JComboBox<>(RendezVous.STATUTS);
    private final JTextArea notes = new JTextArea(3, 20);

    public RendezVousDialog(Window parent, RendezVous rendezVous) {
        super(parent, rendezVous == null ? "Nouveau rendez-vous" : "Modifier le rendez-vous",
                ModalityType.APPLICATION_MODAL);
        this.rendezVous = rendezVous;

        chargerCombos();
        dateHeure.setEditor(new JSpinner.DateEditor(dateHeure, "yyyy-MM-dd HH:mm"));
        dateHeure.setValue(DateUtil.versDate(LocalDateTime.now().withSecond(0).withNano(0)));
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);

        setContentPane(construire());
        if (rendezVous != null) {
            remplirChamps();
        }
        pack();
        setMinimumSize(new Dimension(460, getHeight()));
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
        ligne(form, g, "Date et heure :", dateHeure);
        ligne(form, g, "Motif :", motif);
        ligne(form, g, "Statut :", statut);
        JScrollPane scrollNotes = new JScrollPane(notes);
        ligne(form, g, "Notes :", scrollNotes);

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
        selectionner(comboPatient, rendezVous.getPatientId());
        selectionnerMedecin(rendezVous.getMedecinId());
        if (rendezVous.getDateHeure() != null) {
            dateHeure.setValue(DateUtil.versDate(rendezVous.getDateHeure()));
        }
        motif.setText(rendezVous.getMotif());
        statut.setSelectedItem(rendezVous.getStatut());
        notes.setText(rendezVous.getNotes());
    }

    private void selectionner(JComboBox<Patient> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
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

        RendezVous rv = rendezVous != null ? rendezVous : new RendezVous();
        rv.setPatientId(p.getId());
        rv.setMedecinId(m.getId());
        rv.setDateHeure(DateUtil.versLocalDateTime((Date) dateHeure.getValue()).withSecond(0).withNano(0));
        rv.setMotif(motif.getText().trim());
        rv.setStatut((String) statut.getSelectedItem());
        rv.setNotes(notes.getText().trim());

        try {
            if (rendezVous == null) {
                dao.ajouter(rv);
            } else {
                dao.modifier(rv);
            }
            valide = true;
            dispose();
        } catch (RuntimeException ex) {
            UIUtils.erreur(this, ex.getMessage());
        }
    }
}
