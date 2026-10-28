# Feuille de route — Épure Notes

> Version 0.2 du 28 octobre 2026.
> Une tâche n'est cochée qu'**après validation sur l'appareil** (Samsung, Android 16 / One UI 8.0).
> On ne travaille que sur le jalon en cours. Chaque jalon produit une app installable et utilisable.
> Avant de clore un jalon : `./gradlew assembleDebug`, `./gradlew testDebugUnitTest`, `./gradlew lint` (aucune nouvelle erreur), vérification de l'absence de permissions dans l'APK, liste de contrôle du jalon passée sur le téléphone, puis commit.
> Notes de test : fictives, conservées **hors du dépôt**.

---

## Jalon 0 — Mise en place

- [x] Trancher DEC-0001 (nom, `applicationId`, dépôt) — décidée le 28/10/2026
- [x] Créer le projet Android Studio (Kotlin, Compose, `minSdk` 31, `targetSdk` / `compileSdk` 36)
- [x] `app_name` = « Épure Notes » dans `strings.xml`
- [x] Thème : `Color.kt` (3 jetons), `Theme.kt` (schéma sombre seul pour l'instant), `Type.kt`, `themes.xml` (fond noir) repris d'Épure PDF
- [x] Supprimer la couleur dynamique et le thème clair générés par le modèle
- [x] Paquets vides `ui/`, `data/`, `system/`, `domain/` ; classe `AppContainer`
- [x] Sauvegarde système fermée (DEC-0008) : `allowBackup="false"` + `res/xml/data_extraction_rules.xml` excluant tout pour `<cloud-backup>` et `<device-transfer>`
- [x] `docs/` : `SPEC.md`, `ROADMAP.md`, `DECISIONS.md`, `CHARTE.md` (Partie A copiée telle quelle, Partie B à rédiger pour Épure Notes)
- [x] `CLAUDE.md` à la racine
- [x] `LICENSE` (texte officiel GPL v3), `.gitignore` Android vérifié, `README.md` minimal (objet, confidentialité, licence)
- [x] Premier build sur le téléphone : écran noir avec « Épure Notes »
- [x] Vérifier : aucune permission dans l'APK
- [x] Premier commit (adresse « noreply »), création du dépôt GitHub, premier envoi

**Contrôle sur l'appareil** : l'app s'installe, démarre sans flash blanc, affiche un fond noir.

---

## Jalon 1 — Écrire et retrouver sa note (prototype)

Décision à trancher avant de coder : DEC-0010 (police, ou police système provisoire). Stockage : DEC-0007, décidée.

- [ ] `domain/` : calcul du titre (F-4.1) + tests unitaires
- [ ] `data/` : dépôt des notes (créer, lire, écrire, lister), écriture atomique (`AtomicFile`)
- [ ] Éditeur plein écran, texte brut, marges 24 dp (F-2.1 à F-2.3)
- [ ] Gestion du clavier et des barres système (`WindowInsets`), défilement qui suit le curseur (F-2.2)
- [ ] Enregistrement automatique après pause de frappe et en `onStop` (F-3.1, F-3.2)
- [ ] Ouverture sur la dernière note, curseur à la fin (F-1.1, F-1.2)
- [ ] Texte indicatif sur note vide (F-1.3)
- [ ] Note vide jamais conservée (F-3.4)
- [ ] Barre de commandes toujours visible avec « Nouvelle » (F-5, F-7.3)
- [ ] Retour depuis l'éditeur : quitte l'app (T-8)
- [ ] Vérifier : toujours aucune permission dans l'APK

**Contrôle sur l'appareil**

- [ ] Lancer → écrire une phrase → Accueil → relancer : la phrase est là, curseur à la fin
- [ ] Écrire puis fermer l'app depuis les apps récentes → texte conservé
- [ ] Arrêt forcé (Paramètres > Applications > Épure Notes > « Forcer l'arrêt ») → texte conservé
- [ ] Texte de 3 écrans : le curseur n'est jamais caché par le clavier
- [ ] Rotation de l'écran : ni perte de texte, ni saut du curseur
- [ ] « Nouvelle » sans rien écrire, puis relance : aucune note vide créée
- [ ] Taille de police système agrandie au maximum : texte et barre restent utilisables

---

## Jalon 2 — Plusieurs notes, sauvegarde, partage, réception

Décisions à trancher : F-4.3 (longueur du titre), F-6.4 (suppression / archivage), F-10 (réception de texte), F-9.2 (partage en fichier), aucune pour la sauvegarde (DEC-0019 entièrement décidée).

- [ ] Écran « Notes » : liste triée, titre + date (F-6.1, F-6.2) ; format de date dans `domain/` + tests
- [ ] Appui court : ouvrir ; appui long : menu « Partager », « Supprimer » (F-6.3)
- [ ] Suppression avec confirmation ; cas de la note ouverte (F-6.4, F-6.5)
- [ ] Liste vide : « Aucune note » (F-6.6)
- [ ] Partage du texte par la feuille de partage (F-9.1)
- [ ] Réception de texte partagé depuis une autre app → nouvelle note (F-10.1), si retenue
- [ ] « À propos » (F-15), avec l'avertissement de perte (F-18.2)
- [ ] Commande « Réglages » ; réglage « Sauvegarde » avec avertissement en italique (F-8.6, F-18.2, F-18.3)
- [ ] Recopie des notes dans le dossier choisi, à l'activation puis à chaque enregistrement (F-18.4, F-18.5, F-18.6)
- [ ] « Restaurer depuis un dossier » sans écrasement (F-18.7) ; règle d'import dans `domain/` + tests
- [ ] Dossier inaccessible et arrêt de la sauvegarde (F-18.8, F-18.9)
- [ ] Retour : Notes → éditeur (T-8, `BackHandler`)

**Contrôle sur l'appareil**

- [ ] 30 notes fictives : ordre correct, défilement fluide, titres longs lisibles
- [ ] Modifier une ancienne note → elle remonte en tête
- [ ] Supprimer avec « Annuler » puis « Supprimer » → comportement attendu, fichier effacé
- [ ] Sauvegarde active : supprimer une note → message de confirmation adapté, copie effacée aussi du dossier
- [ ] Partager vers Gmail ou Samsung Notes → texte et sujet corrects
- [ ] Depuis Chrome, partager un texte sélectionné vers Épure Notes → nouvelle note (si F-10 retenue)
- [ ] Geste Retour prédictif : aperçu correct, pas de sortie involontaire de l'app depuis « Notes »
- [ ] Sauvegarde désactivée : l'avertissement est visible dans les réglages et dans « À propos »
- [ ] Activer la sauvegarde dans `Documents/Épure Notes` → fichiers visibles dans « Mes fichiers » ; modifier une note → copie à jour
- [ ] Désinstaller, réinstaller, « Restaurer depuis un dossier » → toutes les notes reviennent, sans doublon
- [ ] Renommer le dossier dans « Mes fichiers » → message « Dossier de sauvegarde introuvable. », notes intactes
- [ ] Sauvegarde désactivée : une sauvegarde Google ou un transfert Smart Switch ne contient aucune note **[À VÉRIFIER]**
- [ ] Smart Switch vers un autre téléphone recopie-t-il `Documents/Épure Notes` ? **[À VÉRIFIER]**, résultat consigné dans DEC-0019

---

## Jalon 3 — Confort d'écriture

Décisions à trancher : couleurs exactes du thème Jour (DEC-0009, principe décidé), DEC-0015 (effacement), F-7.4 (masquage de la barre), F-8.1 (taille par défaut), forme des réglages (§ 3), F-2.7 (immersif), F-16, F-17.

- [ ] Réglages enregistrés dans DataStore
- [ ] Taille du texte 16 → 24 sp, par paliers (F-8.1)
- [ ] Thème Nuit / Jour (F-8.2) ; jetons du thème clair dans `Color.kt`
- [ ] Effacement oui / non (F-8.3) ; règle dans `domain/` + tests (retour arrière, couper, coller sur sélection, correction automatique)
- [ ] Police retenue embarquée, licence copiée dans le dépôt et citée dans « À propos » (DEC-0010)
- [ ] Masquage automatique de la barre (F-7.4)
- [ ] Mode immersif selon décision (F-2.7)
- [ ] Écran allumé (F-16) et compteur de mots (F-17), seulement si retenus

**Contrôle sur l'appareil**

- [ ] Chaque réglage s'applique tout de suite et survit à un redémarrage du téléphone
- [ ] « Effacer : non » avec le clavier Samsung : touche Retour arrière, maintien de Retour arrière, « Couper », correction automatique, balayage pour effacer un mot → aucun caractère retiré
- [ ] « Effacer : non » avec Gboard (si installé) : mêmes tests
- [ ] Thème Jour : aucun flash, contraste lisible en plein soleil
- [ ] Barre : n'apparaît jamais pendant la frappe, toujours accessible en moins d'un geste

---

## Jalon 4 — Recherche

Décision à trancher : F-11.5.

- [ ] Fonction de recherche insensible à la casse et aux accents dans `domain/` + tests (F-11.2)
- [ ] Champ « Rechercher » sur l'écran « Notes » (F-11.1)
- [ ] Ouverture d'un résultat avec sélection de la première occurrence (F-11.3)
- [ ] « Aucun résultat » (F-11.4)

**Contrôle sur l'appareil**

- [ ] « eleve » trouve « Élève » ; « ECOS » trouve « ecos »
- [ ] 100 notes fictives : résultat sans délai perceptible
- [ ] Retour : vide la recherche, puis revient à l'éditeur

---

## Jalon 5 — Liste de tâches et ligne → tâche

Décisions à trancher : DEC-0014 (format), F-12.3, F-13.5, F-10.2. Mode d'ajout : DEC-0013, décidée (action « → Tâche »).

- [ ] Lecture / écriture du format des tâches dans `domain/` + tests (F-12.7)
- [ ] Écran « Tâches » : ajout, fait / à faire, modifier, supprimer, « Effacer les tâches faites » (F-12)
- [ ] Commande « Tâches » dans la barre
- [ ] Action « → Tâche » dans la barre : ajout de la ligne du curseur (F-13, DEC-0013) ; nettoyage de la ligne dans `domain/` + tests
- [ ] Si la sauvegarde est active : `taches.md` recopié aussi (F-18.4)
- [ ] Retour visuel de l'ajout (F-13.5)

**Contrôle sur l'appareil**

- [ ] Ligne « - appeler le labo » → tâche « appeler le labo »
- [ ] Ligne vide → rien n'est ajouté
- [ ] La note n'est pas modifiée par l'ajout
- [ ] Tâches conservées après arrêt forcé
- [ ] Sélection, copier et coller dans l'éditeur fonctionnent comme avant

---

## Jalon 6 — Tâches sur l'écran d'accueil (conditionnel)

Décision à trancher : DEC-0016 (widget ou raccourci, bibliothèque).

- [ ] **[À VÉRIFIER]** Le launcher Épure peut-il afficher un widget ?
- [ ] Selon la décision : widget des tâches à faire (F-14) ou raccourci d'app « Tâches »
- [ ] Vérifier : la bibliothèque ajoutée n'injecte aucune permission

**Contrôle sur l'appareil**

- [ ] Widget mis à jour après ajout ou fin d'une tâche dans l'app
- [ ] Toucher le widget → écran « Tâches »
- [ ] Aucune permission dans l'APK

---

## Avant une éventuelle publication (hors jalons)

- [ ] Recherche d'homonymes, INPI / EUIPO (classe 9)
- [ ] Clé de signature créée et sauvegardée hors du dépôt
- [ ] Politique de confidentialité (exigée par le Play Store, même sans collecte)
- [ ] Fiche du store sans nom de marque tierce
