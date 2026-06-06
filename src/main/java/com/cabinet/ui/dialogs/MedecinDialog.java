package com.cabinet.ui.dialogs;

import com.cabinet.dao.MedecinDAO;
import com.cabinet.model.Medecin;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Boite de dialogue de creation / modification d'un medecin.
 */
public class MedecinDialog extends JDialog {

    private final MedecinDAO dao = new MedecinDAO();
    private final Medecin medecin; // null => creation
    private boolean valide = false;

    private final JTextField nom = new JTextField(20);
    private final JTextField prenom = new JTextField(20);
    private final JTextField specialite = new JTextField(20);
    private final JTextField telephone = new JTextField(20);
    private final JTextField email = new JTextField(20);

    public MedecinDialog(Window parent, Medecin medecin) {
        super(parent, medecin == null ? "Nouveau médecin" : "Modifier le médecin",
                ModalityType.APPLICATION_MODAL);
        this.medecin = medecin;

        setContentPane(construire());
        if (medecin != null) {
            remplirChamps();
        }
        pack();
        setMinimumSize(new Dimension(420, getHeight()));
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
        ligne(form, g, "Spécialité :", specialite);
        ligne(form, g, "Téléphone :", telephone);
        ligne(form, g, "Email :", email);

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
        nom.setText(medecin.getNom());
        prenom.setText(medecin.getPrenom());
        specialite.setText(medecin.getSpecialite());
        telephone.setText(medecin.getTelephone());
        email.setText(medecin.getEmail());
    }

    private void enregistrer() {
        if (nom.getText().trim().isEmpty() || prenom.getText().trim().isEmpty()) {
            UIUtils.erreur(this, "Le nom et le prénom sont obligatoires.");
            return;
        }

        Medecin m = medecin != null ? medecin : new Medecin();
        m.setNom(nom.getText().trim());
        m.setPrenom(prenom.getText().trim());
        m.setSpecialite(specialite.getText().trim());
        m.setTelephone(telephone.getText().trim());
        m.setEmail(email.getText().trim());

        try {
            if (medecin == null) {
                dao.ajouter(m);
            } else {
                dao.modifier(m);
            }
            valide = true;
            dispose();
        } catch (RuntimeException ex) {
            UIUtils.erreur(this, ex.getMessage());
        }
    }
}
