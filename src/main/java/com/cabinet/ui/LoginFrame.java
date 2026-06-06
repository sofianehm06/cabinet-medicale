package com.cabinet.ui;

import com.cabinet.dao.UtilisateurDAO;
import com.cabinet.model.Utilisateur;
import com.cabinet.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Fenetre de connexion : authentifie l'utilisateur avant d'ouvrir l'application.
 */
public class LoginFrame extends JFrame {

    private final JTextField champUtilisateur = new JTextField("admin", 18);
    private final JPasswordField champMotDePasse = new JPasswordField(18);
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private JButton boutonConnexion;

    public LoginFrame() {
        setTitle("Cabinet Médical - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        setContentPane(construireContenu());
        getRootPane().setDefaultButton(boutonConnexion);
    }

    private JPanel construireContenu() {
        JPanel racine = new JPanel(new BorderLayout());
        racine.setBackground(UIUtils.CARD);

        // En-tete colore
        JPanel entete = new JPanel();
        entete.setLayout(new javax.swing.BoxLayout(entete, javax.swing.BoxLayout.Y_AXIS));
        entete.setBackground(UIUtils.PRIMARY);
        entete.setBorder(BorderFactory.createEmptyBorder(36, 20, 36, 20));

        JLabel logo = new JLabel("⚕", SwingConstants.CENTER); // symbole medical
        logo.setFont(logo.getFont().deriveFont(56f));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titre = new JLabel("Cabinet Médical");
        titre.setFont(UIUtils.FONT_TITRE);
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sousTitre = new JLabel("Gestion des patients et consultations");
        sousTitre.setForeground(new Color(0xE0F2F1));
        sousTitre.setFont(UIUtils.FONT_PETIT);
        sousTitre.setAlignmentX(CENTER_ALIGNMENT);

        entete.add(logo);
        entete.add(javax.swing.Box.createVerticalStrut(8));
        entete.add(titre);
        entete.add(javax.swing.Box.createVerticalStrut(4));
        entete.add(sousTitre);

        // Formulaire
        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBackground(UIUtils.CARD);
        formulaire.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 4, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        champUtilisateur.setFont(UIUtils.FONT_NORMAL);
        champMotDePasse.setFont(UIUtils.FONT_NORMAL);
        champUtilisateur.setPreferredSize(new Dimension(0, 36));
        champMotDePasse.setPreferredSize(new Dimension(0, 36));

        formulaire.add(label("Identifiant"), gbc);
        gbc.gridy++;
        formulaire.add(champUtilisateur, gbc);
        gbc.gridy++;
        formulaire.add(label("Mot de passe"), gbc);
        gbc.gridy++;
        formulaire.add(champMotDePasse, gbc);

        boutonConnexion = UIUtils.creerBouton("Se connecter", UIUtils.PRIMARY);
        boutonConnexion.setPreferredSize(new Dimension(0, 42));
        boutonConnexion.addActionListener(e -> seConnecter());
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 4, 0);
        formulaire.add(boutonConnexion, gbc);

        JLabel aide = new JLabel("Compte par défaut : admin / admin123", SwingConstants.CENTER);
        aide.setFont(UIUtils.FONT_PETIT);
        aide.setForeground(new Color(0x9E9E9E));
        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 0, 0);
        formulaire.add(aide, gbc);

        racine.add(entete, BorderLayout.NORTH);
        racine.add(formulaire, BorderLayout.CENTER);

        JPanel pied = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pied.setBackground(UIUtils.CARD);
        JLabel copyright = new JLabel("© 2026 - Cabinet Médical v1.0");
        copyright.setFont(UIUtils.FONT_PETIT);
        copyright.setForeground(new Color(0xBDBDBD));
        pied.add(copyright);
        racine.add(pied, BorderLayout.SOUTH);

        return racine;
    }

    private JLabel label(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(UIUtils.FONT_SOUS_TITRE);
        l.setForeground(UIUtils.TEXT);
        return l;
    }

    private void seConnecter() {
        String identifiant = champUtilisateur.getText().trim();
        String motDePasse = new String(champMotDePasse.getPassword());

        if (identifiant.isEmpty() || motDePasse.isEmpty()) {
            UIUtils.erreur(this, "Veuillez saisir votre identifiant et votre mot de passe.");
            return;
        }

        Utilisateur utilisateur = utilisateurDAO.authentifier(identifiant, motDePasse);
        if (utilisateur == null) {
            UIUtils.erreur(this, "Identifiant ou mot de passe incorrect.");
            champMotDePasse.setText("");
            return;
        }

        dispose();
        new MainFrame(utilisateur).setVisible(true);
    }
}
