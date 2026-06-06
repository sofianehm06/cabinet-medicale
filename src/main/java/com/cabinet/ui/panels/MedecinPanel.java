package com.cabinet.ui.panels;

import com.cabinet.dao.MedecinDAO;
import com.cabinet.model.Medecin;
import com.cabinet.ui.dialogs.MedecinDialog;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * Gestion des medecins : liste et operations CRUD.
 */
public class MedecinPanel extends JPanel {

    private final MedecinDAO dao = new MedecinDAO();
    private final DefaultTableModel modele = UIUtils.modeleLectureSeule(
            "ID", "Nom", "Prénom", "Spécialité", "Téléphone", "Email");
    private final JTable table = new JTable(modele);
    private List<Medecin> medecins;

    public MedecinPanel() {
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
        JButton ajouter = UIUtils.creerBouton("+ Ajouter", UIUtils.SUCCESS);
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
        medecins = dao.listerTous();
        modele.setRowCount(0);
        for (Medecin m : medecins) {
            modele.addRow(new Object[]{
                m.getId(), m.getNom(), m.getPrenom(), m.getSpecialite(),
                m.getTelephone(), m.getEmail()
            });
        }
    }

    private Medecin selection() {
        int ligne = table.getSelectedRow();
        if (ligne < 0 || medecins == null || ligne >= medecins.size()) {
            return null;
        }
        return medecins.get(ligne);
    }

    private void ajouter() {
        ouvrirDialog(null);
    }

    private void modifier() {
        Medecin m = selection();
        if (m == null) {
            UIUtils.erreur(this, "Veuillez sélectionner un médecin.");
            return;
        }
        ouvrirDialog(m);
    }

    private void supprimer() {
        Medecin m = selection();
        if (m == null) {
            UIUtils.erreur(this, "Veuillez sélectionner un médecin.");
            return;
        }
        if (UIUtils.confirmer(this, "Supprimer le médecin « " + m.getNomComplet()
                + " » ?\nCette action supprimera aussi ses rendez-vous et consultations.")) {
            dao.supprimer(m.getId());
            rafraichir();
        }
    }

    private void ouvrirDialog(Medecin medecin) {
        MedecinDialog dialog = new MedecinDialog(SwingUtilities.getWindowAncestor(this), medecin);
        dialog.setVisible(true);
        if (dialog.isValide()) {
            rafraichir();
        }
    }
}
