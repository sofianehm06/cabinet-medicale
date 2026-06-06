package com.cabinet.ui.panels;

import com.cabinet.dao.PatientDAO;
import com.cabinet.model.Patient;
import com.cabinet.ui.dialogs.PatientDialog;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Gestion des patients : liste, recherche et operations CRUD.
 */
public class PatientPanel extends JPanel {

    private final PatientDAO dao = new PatientDAO();
    private final JTextField champRecherche = new JTextField(22);
    private final DefaultTableModel modele = UIUtils.modeleLectureSeule(
            "ID", "Nom", "Prénom", "Âge", "Sexe", "Téléphone", "Groupe sanguin");
    private final JTable table = new JTable(modele);
    private List<Patient> patients;

    public PatientPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(construireBarreOutils(), BorderLayout.NORTH);

        UIUtils.styliserTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modifier();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        add(scroll, BorderLayout.CENTER);

        rafraichir();
    }

    private JPanel construireBarreOutils() {
        JPanel barre = new JPanel(new BorderLayout());
        barre.setOpaque(false);

        JPanel recherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        recherche.setOpaque(false);
        JLabel lbl = new JLabel("Rechercher :");
        lbl.setFont(UIUtils.FONT_NORMAL);
        champRecherche.setFont(UIUtils.FONT_NORMAL);
        champRecherche.setPreferredSize(new Dimension(260, 34));
        champRecherche.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recharger();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recharger();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recharger();
            }
        });
        recherche.add(lbl);
        recherche.add(champRecherche);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton ajouter = UIUtils.creerBouton("+ Ajouter", UIUtils.SUCCESS);
        JButton modifier = UIUtils.creerBouton("Modifier", UIUtils.PRIMARY);
        JButton supprimer = UIUtils.creerBouton("Supprimer", UIUtils.DANGER);
        ajouter.addActionListener(e -> ajouter());
        modifier.addActionListener(e -> modifier());
        supprimer.addActionListener(e -> supprimer());
        actions.add(ajouter);
        actions.add(modifier);
        actions.add(supprimer);

        barre.add(recherche, BorderLayout.WEST);
        barre.add(actions, BorderLayout.EAST);
        return barre;
    }

    /** Rechargement complet (appele a l'affichage du panneau). */
    public void rafraichir() {
        recharger();
    }

    private void recharger() {
        patients = dao.rechercher(champRecherche.getText());
        modele.setRowCount(0);
        for (Patient p : patients) {
            String age = p.getAge() >= 0 ? p.getAge() + " ans" : "-";
            modele.addRow(new Object[]{
                p.getId(), p.getNom(), p.getPrenom(), age, p.getSexe(),
                p.getTelephone(), p.getGroupeSanguin()
            });
        }
    }

    private Patient selection() {
        int ligne = table.getSelectedRow();
        if (ligne < 0 || patients == null || ligne >= patients.size()) {
            return null;
        }
        return patients.get(ligne);
    }

    private void ajouter() {
        ouvrirDialog(null);
    }

    private void modifier() {
        Patient p = selection();
        if (p == null) {
            UIUtils.erreur(this, "Veuillez sélectionner un patient.");
            return;
        }
        ouvrirDialog(p);
    }

    private void supprimer() {
        Patient p = selection();
        if (p == null) {
            UIUtils.erreur(this, "Veuillez sélectionner un patient.");
            return;
        }
        if (UIUtils.confirmer(this, "Supprimer le patient « " + p.getNomComplet()
                + " » ?\nCette action supprimera aussi ses rendez-vous et consultations.")) {
            dao.supprimer(p.getId());
            recharger();
        }
    }

    private void ouvrirDialog(Patient patient) {
        PatientDialog dialog = new PatientDialog(SwingUtilities.getWindowAncestor(this), patient);
        dialog.setVisible(true);
        if (dialog.isValide()) {
            recharger();
        }
    }
}
