package com.cabinet.ui.dialogs;

import com.cabinet.dao.PatientDAO;
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
import java.time.LocalDate;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

/**
 * Boite de dialogue de creation / modification d'un patient.
 */
public class PatientDialog extends JDialog {

    private static final String[] GROUPES = {"", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    private final PatientDAO dao = new PatientDAO();
    private final Patient patient; // null => creation
    private boolean valide = false;

    private final JTextField nom = new JTextField(20);
    private final JTextField prenom = new JTextField(20);
    private final JSpinner dateNaissance = new JSpinner(new SpinnerDateModel());
    private final JComboBox<String> sexe = new JComboBox<>(new String[]{"M", "F"});
    private final JTextField telephone = new JTextField(20);
    private final JTextField email = new JTextField(20);
    private final JTextField adresse = new JTextField(20);
    private final JComboBox<String> groupeSanguin = new JComboBox<>(GROUPES);
    private final JTextField numeroSS = new JTextField(20);

    public PatientDialog(Window parent, Patient patient) {
        super(parent, patient == null ? "Nouveau patient" : "Modifier le patient",
                ModalityType.APPLICATION_MODAL);
        this.patient = patient;

        dateNaissance.setEditor(new JSpinner.DateEditor(dateNaissance, "yyyy-MM-dd"));
        dateNaissance.setValue(DateUtil.versDate(LocalDate.of(1990, 1, 1)));

        setContentPane(construire());
        if (patient != null) {
            remplirChamps();
        }
        pack();
        setMinimumSize(new Dimension(440, getHeight()));
        setLocationRelativeTo(parent);
    }

    public boolean isValide() {
        return valide;
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

        ligne(form, g, "Nom * :", nom);
        ligne(form, g, "Prénom * :", prenom);
        ligne(form, g, "Date de naissance :", dateNaissance);
        ligne(form, g, "Sexe :", sexe);
        ligne(form, g, "Téléphone :", telephone);
        ligne(form, g, "Email :", email);
        ligne(form, g, "Adresse :", adresse);
        ligne(form, g, "Groupe sanguin :", groupeSanguin);
        ligne(form, g, "N° sécurité sociale :", numeroSS);

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
        g.anchor = GridBagConstraints.LINE_END;
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
        nom.setText(patient.getNom());
        prenom.setText(patient.getPrenom());
        if (patient.getDateNaissance() != null) {
            dateNaissance.setValue(DateUtil.versDate(patient.getDateNaissance()));
        }
        sexe.setSelectedItem(patient.getSexe());
        telephone.setText(patient.getTelephone());
        email.setText(patient.getEmail());
        adresse.setText(patient.getAdresse());
        groupeSanguin.setSelectedItem(patient.getGroupeSanguin() != null ? patient.getGroupeSanguin() : "");
        numeroSS.setText(patient.getNumeroSecuriteSociale());
    }

    private void enregistrer() {
        if (nom.getText().trim().isEmpty() || prenom.getText().trim().isEmpty()) {
            UIUtils.erreur(this, "Le nom et le prénom sont obligatoires.");
            return;
        }

        Patient p = patient != null ? patient : new Patient();
        p.setNom(nom.getText().trim());
        p.setPrenom(prenom.getText().trim());
        p.setDateNaissance(DateUtil.versLocalDate((Date) dateNaissance.getValue()));
        p.setSexe((String) sexe.getSelectedItem());
        p.setTelephone(telephone.getText().trim());
        p.setEmail(email.getText().trim());
        p.setAdresse(adresse.getText().trim());
        String groupe = (String) groupeSanguin.getSelectedItem();
        p.setGroupeSanguin(groupe == null || groupe.isBlank() ? null : groupe);
        p.setNumeroSecuriteSociale(numeroSS.getText().trim());

        try {
            if (patient == null) {
                dao.ajouter(p);
            } else {
                dao.modifier(p);
            }
            valide = true;
            dispose();
        } catch (RuntimeException ex) {
            UIUtils.erreur(this, ex.getMessage());
        }
    }
}
