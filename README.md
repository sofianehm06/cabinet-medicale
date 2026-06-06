# 🏥 Cabinet Médical — Application de gestion

Application **desktop** de gestion de cabinet médical développée en **Java (Swing)** avec
une base de données **SQLite embarquée** (aucun serveur à installer). Projet **Maven**,
prêt à ouvrir dans **NetBeans**.

---

## ✨ Fonctionnalités

| Module | Description |
|--------|-------------|
| 🔐 **Authentification** | Écran de connexion, mots de passe hachés (PBKDF2-HMAC-SHA256), rôles (Admin / Médecin / Secrétaire) |
| 📊 **Tableau de bord** | Statistiques (patients, médecins, RDV du jour, consultations, chiffre d'affaires) + liste des rendez-vous du jour |
| 👤 **Patients** | Création / modification / suppression, recherche instantanée, calcul automatique de l'âge, groupe sanguin, n° de sécurité sociale |
| 🩺 **Médecins** | Gestion complète avec spécialité et coordonnées |
| 📅 **Rendez-vous** | Planification patient ↔ médecin, date/heure, statut (Planifié / Confirmé / Terminé / Annulé) |
| 📋 **Consultations** | Diagnostic, **ordonnance** (traitement), observations, **facturation** (montant) + impression écran de l'ordonnance |

> Les suppressions sont **en cascade** : supprimer un patient supprime ses rendez-vous et consultations.

---

## 🔑 Identifiants par défaut

```
Utilisateur : admin
Mot de passe : admin123
```

---

## 🚀 Démarrage rapide

### Option A — NetBeans (recommandé)

1. Ouvrir NetBeans → **File ▸ Open Project…**
2. Sélectionner le dossier du projet (celui qui contient `pom.xml`). NetBeans le reconnaît
   comme projet **Maven** et télécharge automatiquement le pilote SQLite.
3. Clic droit sur le projet ▸ **Run** (ou touche **F6**).

> La classe principale est déjà configurée (`com.cabinet.Main`), le bouton **Run** fonctionne directement.

### Option B — Sans Maven (JDK 17 seul)

Double-cliquez sur **`run.bat`** (ou, en PowerShell) :

```powershell
powershell -ExecutionPolicy Bypass -File run.ps1
```

Le script télécharge le pilote (une seule fois dans `lib/`), compile et lance l'application.

### Option C — Ligne de commande Maven

```bash
mvn clean package
java -jar target/cabinet-medical.jar
```

`mvn package` produit un **JAR exécutable autonome** (toutes dépendances incluses) grâce au plugin Shade.

---

## 🧱 Architecture

Architecture en couches classique (`Modèle → DAO → Interface`) :

```
src/main/java/com/cabinet/
├── Main.java                  # Point d'entrée (Look & Feel Nimbus + init BDD)
├── db/
│   └── Database.java          # Connexion SQLite, création du schéma, données initiales
├── model/                     # Objets métier (POJO)
│   ├── Utilisateur.java
│   ├── Patient.java
│   ├── Medecin.java
│   ├── RendezVous.java
│   └── Consultation.java
├── dao/                       # Accès aux données (JDBC)
│   ├── UtilisateurDAO.java
│   ├── PatientDAO.java
│   ├── MedecinDAO.java
│   ├── RendezVousDAO.java
│   └── ConsultationDAO.java
├── ui/
│   ├── LoginFrame.java        # Fenêtre de connexion
│   ├── MainFrame.java         # Fenêtre principale (barre latérale + CardLayout)
│   ├── panels/                # Un panneau par module
│   └── dialogs/               # Boîtes de dialogue d'ajout / modification
└── util/
    ├── PasswordUtil.java      # Hachage PBKDF2
    ├── DateUtil.java          # Conversions java.util.Date ↔ java.time
    └── UIUtils.java           # Thème, couleurs et composants réutilisables
```

---

## 🗄️ Base de données

- Moteur : **SQLite** (fichier unique, créé automatiquement au premier lancement).
- Emplacement : `~/.cabinet-medical/cabinet.db`
  (sous Windows : `C:\Users\<vous>\.cabinet-medical\cabinet.db`).
- **Réinitialiser** : fermez l'application et supprimez ce fichier ; il sera recréé avec
  les données de démonstration.

Tables : `utilisateur`, `patient`, `medecin`, `rendez_vous`, `consultation`.

---

## 🛠️ Prérequis

- **JDK 17** ou supérieur.
- **NetBeans** (avec le support Java/Maven) — ou simplement le JDK pour l'option B.
- Connexion Internet **au premier build** uniquement (téléchargement du pilote SQLite).

---

## ℹ️ Remarques

- Le message `SLF4J: Failed to load class "StaticLoggerBinder"` au démarrage est **normal**
  et sans conséquence : il indique simplement qu'aucun système de log n'est branché sur le pilote.
- Interface codée « à la main » en Swing (sans le générateur de formulaires) : le projet
  reste **portable** et lisible dans n'importe quel IDE.

---

© 2026 — Cabinet Médical v1.0
