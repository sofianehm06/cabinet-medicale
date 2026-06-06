package com.cabinet.ui;

import com.cabinet.model.Utilisateur;
import com.cabinet.ui.panels.ConsultationPanel;
import com.cabinet.ui.panels.DashboardPanel;
import com.cabinet.ui.panels.MedecinPanel;
import com.cabinet.ui.panels.PatientPanel;
import com.cabinet.ui.panels.RendezVousPanel;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Fenetre principale : barre laterale de navigation + zone de contenu (CardLayout).
 */
public class MainFrame extends JFrame {

    private final Utilisateur utilisateur;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contenu = new JPanel(cardLayout);
    private final JLabel titreSection = new JLabel("Tableau de bord");
    private final List<JButton> boutonsNav = new ArrayList<>();

    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final PatientPanel patientPanel = new PatientPanel();
    private final MedecinPanel medecinPanel = new MedecinPanel();
    private final RendezVousPanel rendezVousPanel = new RendezVousPanel();
    private final ConsultationPanel consultationPanel = new ConsultationPanel();

    public MainFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        setTitle("Cabinet Médical - Gestion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(construireSidebar(), BorderLayout.WEST);
        add(construireZonePrincipale(), BorderLayout.CENTER);

        contenu.add(dashboardPanel, "dashboard");
        contenu.add(patientPanel, "patients");
        contenu.add(medecinPanel, "medecins");
        contenu.add(rendezVousPanel, "rendezvous");
        contenu.add(consultationPanel, "consultations");

        // Selection initiale
        if (!boutonsNav.isEmpty()) {
            selectionner(boutonsNav.get(0), "Tableau de bord", "dashboard", dashboardPanel::rafraichir);
        }
    }

    private JPanel construireSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtils.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Logo / titre
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 18));
        logo.setBackground(UIUtils.SIDEBAR);
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel symbole = new JLabel("⚕");
        symbole.setFont(symbole.getFont().deriveFont(30f));
        symbole.setForeground(UIUtils.PRIMARY);
        JLabel nomApp = new JLabel("Cabinet Médical");
        nomApp.setFont(UIUtils.FONT_SOUS_TITRE);
        nomApp.setForeground(Color.WHITE);
        logo.add(symbole);
        logo.add(nomApp);
        sidebar.add(logo);
        sidebar.add(separateur());

        // Boutons de navigation
        sidebar.add(creerNav("Tableau de bord", "dashboard", dashboardPanel::rafraichir));
        sidebar.add(creerNav("Patients", "patients", patientPanel::rafraichir));
        sidebar.add(creerNav("Médecins", "medecins", medecinPanel::rafraichir));
        sidebar.add(creerNav("Rendez-vous", "rendezvous", rendezVousPanel::rafraichir));
        sidebar.add(creerNav("Consultations", "consultations", consultationPanel::rafraichir));

        sidebar.add(Box.createVerticalGlue());

        // Bloc utilisateur + deconnexion
        sidebar.add(separateur());
        JPanel infoUser = new JPanel();
        infoUser.setLayout(new BoxLayout(infoUser, BoxLayout.Y_AXIS));
        infoUser.setBackground(UIUtils.SIDEBAR);
        infoUser.setBorder(BorderFactory.createEmptyBorder(10, 18, 6, 18));
        infoUser.setAlignmentX(LEFT_ALIGNMENT);
        JLabel nomUser = new JLabel(utilisateur.toString());
        nomUser.setForeground(Color.WHITE);
        nomUser.setFont(UIUtils.FONT_NORMAL);
        JLabel roleUser = new JLabel(utilisateur.getRole());
        roleUser.setForeground(new Color(0x90A4AE));
        roleUser.setFont(UIUtils.FONT_PETIT);
        infoUser.add(nomUser);
        infoUser.add(roleUser);
        sidebar.add(infoUser);

        JButton deconnexion = new JButton("Déconnexion");
        deconnexion.setAlignmentX(LEFT_ALIGNMENT);
        deconnexion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        deconnexion.setFocusPainted(false);
        deconnexion.setForeground(Color.WHITE);
        deconnexion.setBackground(UIUtils.SIDEBAR);
        deconnexion.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        deconnexion.setHorizontalAlignment(SwingConstants.LEFT);
        deconnexion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deconnexion.addActionListener(e -> seDeconnecter());
        deconnexion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                deconnexion.setBackground(UIUtils.DANGER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                deconnexion.setBackground(UIUtils.SIDEBAR);
            }
        });
        sidebar.add(deconnexion);

        return sidebar;
    }

    private JPanel construireZonePrincipale() {
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(UIUtils.BACKGROUND);

        // Barre de titre superieure
        JPanel barre = new JPanel(new BorderLayout());
        barre.setBackground(Color.WHITE);
        barre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        titreSection.setFont(UIUtils.FONT_TITRE);
        titreSection.setForeground(UIUtils.TEXT);
        barre.add(titreSection, BorderLayout.WEST);

        JLabel bienvenue = new JLabel("Connecté en tant que " + utilisateur.toString());
        bienvenue.setFont(UIUtils.FONT_PETIT);
        bienvenue.setForeground(new Color(0x757575));
        barre.add(bienvenue, BorderLayout.EAST);

        principal.add(barre, BorderLayout.NORTH);
        principal.add(contenu, BorderLayout.CENTER);
        return principal;
    }

    private JButton creerNav(String texte, String cle, Runnable rafraichir) {
        JButton bouton = new JButton(texte);
        bouton.setAlignmentX(LEFT_ALIGNMENT);
        bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        bouton.setHorizontalAlignment(SwingConstants.LEFT);
        bouton.setFocusPainted(false);
        bouton.setForeground(UIUtils.TEXT_LIGHT);
        bouton.setBackground(UIUtils.SIDEBAR);
        bouton.setFont(UIUtils.FONT_NORMAL);
        bouton.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bouton.addActionListener(e -> selectionner(bouton, texte, cle, rafraichir));
        bouton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(bouton.getClientProperty("actif"))) {
                    bouton.setBackground(UIUtils.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(bouton.getClientProperty("actif"))) {
                    bouton.setBackground(UIUtils.SIDEBAR);
                }
            }
        });
        boutonsNav.add(bouton);
        return bouton;
    }

    private void selectionner(JButton bouton, String titre, String cle, Runnable rafraichir) {
        for (JButton b : boutonsNav) {
            b.putClientProperty("actif", Boolean.FALSE);
            b.setBackground(UIUtils.SIDEBAR);
            b.setForeground(UIUtils.TEXT_LIGHT);
        }
        bouton.putClientProperty("actif", Boolean.TRUE);
        bouton.setBackground(UIUtils.PRIMARY);
        bouton.setForeground(Color.WHITE);

        titreSection.setText(titre);
        if (rafraichir != null) {
            rafraichir.run();
        }
        cardLayout.show(contenu, cle);
    }

    private Component separateur() {
        JPanel sep = new JPanel();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setBackground(new Color(0x2C3E50));
        return sep;
    }

    private void seDeconnecter() {
        if (UIUtils.confirmer(this, "Voulez-vous vraiment vous déconnecter ?")) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
