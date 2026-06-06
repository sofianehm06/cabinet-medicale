package com.cabinet.ui.panels;

import com.cabinet.dao.ConsultationDAO;
import com.cabinet.model.Consultation;
import com.cabinet.ui.dialogs.ConsultationDialog;
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
 * Gestion des consultations : liste, CRUD et consultation de l'ordonnance.
 */
public class ConsultationPanel extends JPanel {

    private final ConsultationDAO dao = new ConsultationDAO();
    private final DefaultTableModel modele = UIUtils.modeleLectureSeule(
            "ID", "Date", "Patient", "Médecin", "Motif", "Diagnostic", "Montant");
    private final JTable table = new JTable(modele);
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private List<Consultation> consultations;

    public ConsultationPanel() {
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
        JButton ajouter = UIUtils.creerBouton("+ Nouvelle consultation", UIUtils.SUCCESS);
        JButton modifier = UIUtils.creerBouton("Modifier", UIUtils.PRIMARY);
        JButton ordonnance = UIUtils.creerBouton("Ordonnance", new Color(0x3949AB));
        JButton supprimer = UIUtils.creerBouton("Supprimer", UIUtils.DANGER);
        ajouter.addActionListener(e -> ajouter());
        modifier.addActionListener(e -> modifier());
        ordonnance.addActionListener(e -> afficherOrdonnance());
        supprimer.addActionListener(e -> supprimer());
        actions.add(ajouter);
        actions.add(modifier);
        actions.add(ordonnance);
        actions.add(supprimer);

        JPanel barre = new JPanel(new BorderLayout());
        barre.setOpaque(false);
        barre.add(actions, BorderLayout.EAST);
        return barre;
    }

    public void rafraichir() {
        consultations = dao.listerTous();
        modele.setRowCount(0);
        for (Consultation c : consultations) {
            String date = c.getDateConsultation() != null ? c.getDateConsultation().format(dateFmt) : "-";
            modele.addRow(new Object[]{
                c.getId(), date, c.getPatientNom(), c.getMedecinNom(),
                c.getMotif(), c.getDiagnostic(), String.format("%.2f €", c.getMontant())
            });
        }
    }

    private Consultation selection() {
        int ligne = table.getSelectedRow();
        if (ligne < 0 || consultations == null || ligne >= consultations.size()) {
            return null;
        }
        return consultations.get(ligne);
    }

    private void ajouter() {
        ouvrirDialog(null);
    }

    private void modifier() {
        Consultation c = selection();
        if (c == null) {
            UIUtils.erreur(this, "Veuillez sélectionner une consultation.");
            return;
        }
        ouvrirDialog(c);
    }

    private void afficherOrdonnance() {
        Consultation c = selection();
        if (c == null) {
            UIUtils.erreur(this, "Veuillez sélectionner une consultation.");
            return;
        }
        String traitement = c.getTraitement() == null || c.getTraitement().isBlank()
                ? "(Aucun traitement prescrit)" : c.getTraitement();
        String texte = "ORDONNANCE\n"
                + "───────────────────────────────\n"
                + "Patient  : " + c.getPatientNom() + "\n"
                + "Médecin  : " + c.getMedecinNom() + "\n"
                + "Date     : " + (c.getDateConsultation() != null ? c.getDateConsultation().format(dateFmt) : "-") + "\n"
                + "Diagnostic : " + (c.getDiagnostic() != null ? c.getDiagnostic() : "-") + "\n"
                + "───────────────────────────────\n\n"
                + traitement;
        javax.swing.JTextArea zone = new javax.swing.JTextArea(texte, 16, 40);
        zone.setEditable(false);
        zone.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        zone.setLineWrap(true);
        zone.setWrapStyleWord(true);
        javax.swing.JOptionPane.showMessageDialog(this, new JScrollPane(zone),
                "Ordonnance", javax.swing.JOptionPane.PLAIN_MESSAGE);
    }

    private void supprimer() {
        Consultation c = selection();
        if (c == null) {
            UIUtils.erreur(this, "Veuillez sélectionner une consultation.");
            return;
        }
        if (UIUtils.confirmer(this, "Supprimer cette consultation ?")) {
            dao.supprimer(c.getId());
            rafraichir();
        }
    }

    private void ouvrirDialog(Consultation c) {
        ConsultationDialog dialog = new ConsultationDialog(SwingUtilities.getWindowAncestor(this), c);
        dialog.setVisible(true);
        if (dialog.isValide()) {
            rafraichir();
        }
    }
}
