package com.cabinet.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hachage securise des mots de passe avec PBKDF2-HMAC-SHA256.
 * Uniquement base sur la bibliotheque standard de Java (aucune dependance).
 */
public final class PasswordUtil {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // octets
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /** Genere un sel aleatoire encode en Base64. */
    public static String genererSel() {
        byte[] sel = new byte[SALT_LENGTH];
        RANDOM.nextBytes(sel);
        return Base64.getEncoder().encodeToString(sel);
    }

    /** Calcule le hachage PBKDF2 d'un mot de passe avec le sel fourni. */
    public static String hacher(String motDePasse, String selBase64) {
        try {
            byte[] sel = Base64.getDecoder().decode(selBase64);
            KeySpec spec = new PBEKeySpec(motDePasse.toCharArray(), sel, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors du hachage du mot de passe", e);
        }
    }

    /** Verifie un mot de passe en clair contre un hachage + sel stockes. */
    public static boolean verifier(String motDePasseEnClair, String hashAttendu, String selBase64) {
        if (motDePasseEnClair == null || hashAttendu == null || selBase64 == null) {
            return false;
        }
        String hashCalcule = hacher(motDePasseEnClair, selBase64);
        return constantTimeEquals(hashCalcule, hashAttendu);
    }

    /** Comparaison a temps constant pour eviter les attaques temporelles. */
    private static boolean constantTimeEquals(String a, String b) {
        byte[] ba = a.getBytes();
        byte[] bb = b.getBytes();
        if (ba.length != bb.length) {
            return false;
        }
        int resultat = 0;
        for (int i = 0; i < ba.length; i++) {
            resultat |= ba[i] ^ bb[i];
        }
        return resultat == 0;
    }
}
