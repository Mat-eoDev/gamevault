# Document de justification UX/UI – GameVault

# Document de justification UX/UI – GameVault

> Ce document explique **pourquoi** les choix de conception ont été faits
> 

---

## 1. Organisation de l'écran principal

L'écran principal repose sur trois zones : une barre latérale à gauche, la liste des jeux au centre, un panneau de détail à droite, et une barre de statut en bas.

**Pourquoi la sidebar à gauche ?**
La navigation principale est placée à gauche car c'est l'endroit où l'œil se pose en premier (lecture de gauche à droite) et c'est la convention de la quasi-totalité des logiciels desktop : l'utilisateur n'a rien à apprendre. Les sections retenues — Collection, Ajouter un jeu, Statistiques, Paramètres — correspondent aux seules vraies destinations de l'application ; on évite de surcharger le menu avec des entrées secondaires. La section active (Collection) est mise en évidence pour que l'utilisateur sache toujours où il se trouve.

**Pourquoi un tableau pour la liste, et pas une grille de cartes ?**
C'est un choix assumé. Une grille de jaquettes est jolie mais peu efficace dès que la collection grossit : on y compare mal les jeux entre eux et on perd les informations textuelles (année, note, statut). RetroSphere gère plusieurs centaines de titres sur de nombreuses plateformes ; l'usage dominant est de scanner, comparer et trier, pas de feuilleter des pochettes. Le tableau (colonnes Titre, Plateforme, Année, Note, Statut) affiche beaucoup d'informations par ligne, se trie colonne par colonne, et reste lisible avec 6 comme avec 600 jeux. Une miniature de jaquette est conservée dans la colonne Titre pour garder le repère visuel sans sacrifier la densité.

**Pourquoi un panneau de détail à droite ?**
Au clic sur une ligne, le détail complet du jeu s'affiche à droite sans changer d'écran. L'utilisateur garde la liste sous les yeux et peut enchaîner la consultation de plusieurs jeux rapidement — moins de navigation, moins de désorientation.

---

## 2. Navigation entre les écrans

La structure est plate : tout part de la Collection. L'ajout et la modification ouvrent un formulaire en fenêtre modale par-dessus la liste, plutôt qu'un nouvel écran ; la modale garde le contexte visible et se ferme d'un bouton, ce qui évite à l'utilisateur de « se perdre » dans l'application. La suppression passe par un dialog de confirmation également en surimpression. Un retour à la liste est donc toujours immédiat, et l'utilisateur n'est jamais à plus d'un clic de son point de départ.

---

## 3. Ajout d'un jeu

Le chemin le plus court est volontairement très direct : un bouton **« + Ajouter un jeu »** est présent en permanence (dans la barre latérale et dans la barre d'outils de la collection). Un clic ouvre directement le formulaire — aucune étape intermédiaire.

Dans le formulaire, les champs sont regroupés par logique : identité (titre, développeur, éditeur), classification (année, plateforme, statut, genre), appréciation (note), puis visuel (jaquette) et description. Les champs obligatoires sont marqués d'un astérisque dès l'ouverture, pour que l'utilisateur sache immédiatement le minimum à renseigner. Le même formulaire sert à l'ajout et à la modification : une seule interface à comprendre.

---

## 4. Prévention des erreurs

La priorité est de prévenir l'erreur plutôt que de la sanctionner après coup :

- Les champs obligatoires sont signalés visuellement (astérisque) avant toute saisie.
- En cas de saisie incorrecte ou incomplète, le champ fautif passe en rouge et un message d'erreur explicite s'affiche juste en dessous (ex. « Le titre est obligatoire. »), au plus près du problème — pas dans une fenêtre séparée. Cet état d'erreur est visible dans la maquette du formulaire.
- Les messages affichés sont exactement ceux renvoyés par la couche métier (`GameService.validate()`), ce qui garantit la cohérence entre l'interface et les règles réelles (titre et plateforme obligatoires, année entre 1950 et 2100, note entre 0 et 10).
- La suppression demande une confirmation explicite (dialog dédié) car l'action est définitive : on protège l'utilisateur d'un clic malheureux.
- Robustesse : une image invalide ou un champ incohérent affiche un message, mais ne provoque jamais l'arrêt de l'application.

---

## 5. Choix de la jaquette

Dans le formulaire, la jaquette occupe une colonne dédiée à gauche, avec une zone d'aperçu et un bouton « Choisir une image » qui ouvre un sélecteur de fichier (png / jpg). L'aperçu s'affiche immédiatement après sélection, ce qui permet à l'utilisateur de vérifier visuellement qu'il a choisi la bonne image avant d'enregistrer. Si aucune jaquette n'est fournie ou si l'image est invalide, une image par défaut est utilisée : aucune fiche ni aucune ligne du tableau n'apparaît « cassée ».

---

## 6. Recherche et filtres

Pour retrouver un jeu dans une grande collection, trois mécanismes complémentaires sont réunis dans une seule barre d'outils en haut de la liste :

- une recherche par titre (et plateforme / développeur), avec résultats mis à jour à la frappe ;
- des filtres par plateforme et par statut, les deux critères les plus discriminants pour cette collection multi-plateformes ;
- un tri par titre, année ou note.

La barre de statut en bas (« X jeux affichés ») confirme en permanence combien de résultats correspondent, ce qui rassure l'utilisateur quand il filtre. Réunir recherche, filtres et tri au même endroit crée une zone unique de « pilotage » de la collection, plutôt que des fonctions dispersées à chercher.

---

## 7. Choix des couleurs et du thème

Le thème retenu est clair, neutre avec des éléments arrondis. Ce choix est motivé par l'usage : une application de consultation et de saisie de données, utilisée potentiellement longtemps, gagne à être reposante et très lisible. Un fond clair maximise le contraste du texte (lisibilité des titres, années, notes) et laisse les jaquettes et les badges de statut être les seuls éléments colorés — donc les seuls à attirer l'œil, ce qui est exactement le but.

La couleur n'est utilisée que de façon fonctionnelle, pour porter du sens : les statuts sont codés par couleur (Terminé = vert, En cours = orange, À faire = bleu, Abandonné = gris) et l'action destructrice Supprimer est en rouge. Les coins arrondis et les surfaces blanches sur fond gris créent une hiérarchie douce entre les zones sans recourir à des bordures lourdes. Le résultat reste sobre, cohérent et accessible.

---

## Lien Figma :

**https://www.figma.com/design/97oEW9HmVjYaFCPD2Qtinc/GAMEVAULT**
