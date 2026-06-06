package com.cabinet.ui.panels;

import com.cabinet.dao.RendezVousDAO;
import com.cabinet.model.RendezVous;
import com.cabinet.ui.dialogs.RendezVousDialog;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Gestion des rendez-vous : liste et operations CRUD.
 */
public class RendezVousPanel extends JPanel {

    private final RendezVousDAO dao = new RendezVousDAO();
    private final DefaultTableModel modele = UIUtils.modeleLectureSeule(
            "ID", "Date", "Heure", "Patient", "Médecin", "Motif", "Statut");
    private final JTable table = new JTable(modele);
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter heureFmt = DateTimeFormatter.ofPattern("HH:mm");
    private List<RendezVous> rendezVous;

    public RendezVousPanel() {
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
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton ajouter = UIUtils.creerBouton("+ Nouveau rendez-vous", UIUtils.SUCCESS);
        JButton modifier = UIUtils.creerBouton("Modifier", UIUtils.PRIMARY);
        JButton supprimer = UIUtils.creerBouton("Supprimer", UIUtils.DANGER);
        ajouter.addActionListener(e -> ajouter());
        modifier.addActionListener(e -> modifier());
        supprimer.addActionListener(e -> supprimer());
        actions.add(ajouter);
        actions.add(modifier);
        actions.add(supprimer);

        JPanel barre = new JPanel(new BorderLayout());
        barre.setOpaque(false);
        barre.add(actions, BorderLayout.EAST);
        return barre;
    }

    public void rafraichir() {
        rendezVous = dao.listerTous();
        modele.setRowCount(0);
        for (RendezVous rv : rendezVous) {
            String date = rv.getDateHeure() != null ? rv.getDateHeure().format(dateFmt) : "-";
            String heure = rv.getDateHeure() != null ? rv.getDateHeure().format(heureFmt) : "-";
            modele.addRow(new Object[]{
                rv.getId(), date, heure, rv.getPatientNom(), rv.getMedecinNom(),
                rv.getMotif(), rv.getStatut()
            });
        }
    }

    private RendezVous selection() {
        int ligne = table.getSelectedRow();
        if (ligne < 0 || rendezVous == null || ligne >= rendezVous.size()) {
            return null;
        }
        return rendezVous.get(ligne);
    }

    private void ajouter() {
        ouvrirDialog(null);
    }

    private void modifier() {
        RendezVous rv = selection();
        if (rv == null) {
            UIUtils.erreur(this, "Veuillez sélectionner un rendez-vous.");
            return;
        }
        ouvrirDialog(rv);
    }

    private void supprimer() {
        RendezVous rv = selection();
        if (rv == null) {
            UIUtils.erreur(this, "Veuillez sélectionner un rendez-vous.");
            return;
        }
        if (UIUtils.confirmer(this, "Supprimer ce rendez-vous ?")) {
            dao.supprimer(rv.getId());
            rafraichir();
        }
    }

    private void ouvrirDialog(RendezVous rv) {
        RendezVousDialog dialog = new RendezVousDialog(SwingUtilities.getWindowAncestor(this), rv);
        dialog.setVisible(true);
        if (dialog.isValide()) {
            rafraichir();
        }
    }
}
