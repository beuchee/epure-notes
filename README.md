# Épure Notes

Application Android de prise de notes minimaliste, entièrement hors ligne : écrire sans distraction, sans réseau, sans collecte de données.

Projet en cours de construction (jalon 0). Pas encore utilisable.

## Confidentialité

- Les notes restent sur le téléphone, dans le stockage privé de l'app.
- Aucune permission Android : l'app ne peut pas accéder au réseau (aucune permission `INTERNET` dans le manifeste fusionné).
- Aucune statistique, aucun journal d'usage, aucune sauvegarde automatique par le système (ni dans le cloud, ni par transfert d'appareil à appareil).
- Sauvegarde optionnelle : tu peux choisir un dossier (par exemple `Documents/Épure Notes`) où l'app recopie tes notes en fichiers `.md`. Si ce dossier est synchronisé par une app de cloud, les fichiers quittent le téléphone par cette app, à ton initiative.
- Sans sauvegarde activée, tes notes sont enregistrées seulement sur ce téléphone. Elles seront perdues si tu désinstalles l'app ou si tu perds le téléphone.

## Licence

Épure Notes est un logiciel libre distribué sous licence GNU GPL v3 ; voir [LICENSE](LICENSE).
