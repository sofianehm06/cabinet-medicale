package com.cabinet.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Constantes de theme et fonctions utilitaires pour l'interface graphique.
 */
public final class UIUtils {

    // Palette de couleurs (theme medical bleu/vert)
    public static final Color PRIMARY = new Color(0x00897B);      // teal
    public static final Color PRIMARY_DARK = new Color(0x00695C);
    public static final Color SIDEBAR = new Color(0x1E2A38);      // bleu nuit
    public static final Color SIDEBAR_HOVER = new Color(0x2C3E50);
    public static final Color BACKGROUND = new Color(0xF4F6F8);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(0x2C3E50);
    public static final Color TEXT_LIGHT = new Color(0xECF0F1);
    public static final Color DANGER = new Color(0xE53935);
    public static final Color SUCCESS = new Color(0x43A047);
    public static final Color WARNING = new Color(0xFB8C00);

    public static final Font FONT_TITRE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SOUS_TITRE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_PETIT = new Font("Segoe UI", Font.PLAIN, 12);

    private UIUtils() {
    }

    /** Cree un bouton plat colore pret a l'emploi. */
    public static JButton creerBouton(String texte, Color fond) {
        JButton b = new JButton(texte);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(fond);
        b.setFont(FONT_NORMAL);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    /** Cree un modele de table en lecture seule (cellules non editables). */
    public static DefaultTableModel modeleLectureSeule(String... colonnes) {
        return new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Applique un style propre et lisible a un tableau. */
    public static void styliserTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(FONT_NORMAL);
        table.setGridColor(new Color(0xE0E0E0));
        table.setSelectionBackground(new Color(0xB2DFDB));
        table.setSelectionForeground(TEXT);
        table.getTableHeader().setFont(FONT_SOUS_TITRE);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowVerticalLines(false);
    }

    /** Ajoute une bordure interne (padding) a un composant. */
    public static void padding(JComponent c, int haut, int gauche, int bas, int droite) {
        c.setBorder(BorderFactory.createEmptyBorder(haut, gauche, bas, droite));
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void erreur(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmer(Component parent, String message) {
        int r = JOptionPane.showConfirmDialog(parent, message, "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }
}
