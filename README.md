# CodeLearn Swing — Prototype fonctionnel (architecture MVC)

Prototype Java Swing pour une plateforme d'apprentissage d'un langage de programmation par niveaux.

## Périmètre
- Application de bureau complète : connexion, espaces apprenant, enseignant et administrateur.
- Toutes les actions de l'interface sont fonctionnelles (pas de messages « zone de démonstration »).
- Les données (comptes, cours, questions, progression) sont conservées **en mémoire** pendant
  l'exécution, puis réinitialisées au redémarrage.
- Aucune API externe, base de données ni stockage persistant.
- Les mots de passe sont stockés en clair : il s'agit d'un prototype, pas d'un système sécurisé.

## Prérequis
- JDK 17 ou supérieur.

## Exécution

### Méthode recommandée : double-cliquer sur `run.bat`

`run.bat` supprime l'ancienne compilation, recompile toutes les sources dans
`out/`, puis lance l'application depuis le dossier du projet. C'est la méthode
la plus fiable pour voir la nouvelle interface.

### En ligne de commande

Depuis le dossier du projet :

```powershell
if (Test-Path out) { Remove-Item -Recurse -Force out }
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
java -cp out Main
```

### Comptes de démonstration

Le rôle sélectionné sur l'écran de connexion doit correspondre au rôle du compte.

| Rôle | Identifiant | Mot de passe | Parcours / langages |
| --- | --- | --- | --- |
| Apprenant | `etudiant` | `1234` | Java |
| Apprenant | `amine01` | `1234` | Python |
| Enseignant | `sara02` | `1234` | Java, Python |
| Enseignant | `karim03` | `1234` | JavaScript |
| Administrateur | `admin` | `1234` | — |

## Parcours apprenant

1. Se connecter avec un compte **Apprenant**.
2. Choisir la langue de l'interface (**Français** ou **English**).
3. Choisir le langage de programmation à apprendre parmi ceux enseignés par au moins
   un enseignant (Java, Python, JavaScript, C++, selon les affectations).
4. Le tableau de bord affiche la progression réelle : score moyen, tests réussis,
   temps total, niveau et activités.

Pages disponibles :

- **Mes cours** : liste des cours du parcours. **Ouvrir** affiche le détail et
  **Terminer ce cours** enregistre la completion et met à jour la progression.
- **Exercices** : éditeur de code, validation par mot-clé attendu
  (`System.out.println`, `print`, `console.log` ou `std::cout` selon le langage),
  page de résultat et possibilité de réessayer.
- **Tests** : QCM du parcours, validation de la réponse, score enregistré et page
  de résultat permettant de refaire le test.
- **Ma progression** : pourcentage, niveau et nombre de cours terminés.
- **Mon profil** : modification du nom, de la langue de l'interface et du langage
  de programmation. L'espace de travail est rechargé avec les nouveaux choix.

## Parcours enseignant

- **Tableau de bord** : statistiques calculées à partir des apprenants de ses
  langages (apprenants, cours, langages, taux de réussite), activités récentes,
  progression des apprenants, actions rapides, aperçu des cours et langages
  attribués.
- **Gestion des cours** : ajout, modification et suppression de cours, limités aux
  langages attribués à l'enseignant.
- **Niveaux** : niveaux disponibles pour chaque langage enseigné.
- **Évaluations** : consultation des QCM et modification de la bonne réponse.
- **Suivi des apprenants** : tableau de progression, parcours et dernière activité.

## Parcours administrateur

- **Tableau de bord** : nombre d'utilisateurs, enseignants, apprenants et
  administrateurs, derniers inscrits avec rôle et statut, état de la plateforme,
  langages enseignés et actions rapides.
- **Utilisateurs** : ajout, modification et suppression réels des comptes
  (nom, identifiant, mot de passe, rôle, statut, parcours, langages enseignés).
  Le dernier administrateur ne peut pas être supprimé.
- **Enseignants** : création, modification et suppression des enseignants avec
  affectation des langages enseignés. Un enseignant créé reçoit le mot de passe
  initial `1234`. Les langages proposés aux apprenants sont recalculés
  automatiquement : un langage qu'aucun enseignant n'enseigne n'est plus proposé.
- **Rôles et droits** et **Supervision** : synthèse des droits et état de la
  plateforme (comptes actifs, cours, langages enseignés).

## Architecture MVC

L'application est organisée selon le patron Modèle–Vue–Contrôleur.

```
ApplicationController             (session, fenêtre principale, navigation)
├── LoginController               (LoginModel + LoginView)
├── StudentController             (StudentModel + StudentView)
├── TeacherController             (TeacherModel + TeacherView)
└── AdminController               (AdminModel + AdminView)
```

- **Model** : état applicatif et règles métier (`AppState`, `UserAccount`,
  `StudentProgress`, `StudentModel`, `TeacherModel`, `AdminModel`, `CourseItem`,
  `TestQuestion`, `Session`, `Role`, etc.). Aucune dépendance à Swing. Les
  couleurs sont exprimées sémantiquement via `Tone`.
- **View** : composants Swing. Ils affichent les données fournies par le
  contrôleur et exposent une interface `Listener` pour signaler les actions de
  l'utilisateur. Aucune logique métier.
- **Controller** : implémente le `Listener` de sa vue, interroge le modèle,
  valide les saisies et fournit les données à afficher. `ApplicationController`
  coordonne la connexion, la session et la navigation entre les tableaux de bord.

### Structure des sources
- `src/Main.java` : point d'entrée, lance `ApplicationController`.
- `src/controller/ApplicationController.java` : contrôleur racine, session et
  navigation.
- `src/controller/{Login,Student,Teacher,Admin}Controller.java` : contrôleurs
  des écrans.
- `src/view/` : vues (`MainFrame`, `LoginView`, `LanguageView`, `StudentView`,
  `TeacherView`, `AdminView`).
- `src/view/components/` : thème visuel (`Theme`), barre latérale réutilisable
  (`Sidebar`), icônes vectorielles (`Icons`) et chargement des images (`Assets`).
- `src/model/` : état en mémoire, modèles, records et énumérations.
- `run.bat` : compilation propre et lancement de l'application.
- `pc.png` : illustration affichée dans la barre latérale.

### Dans un IDE

Importer le dossier comme projet Java, puis exécuter `src/Main.java`. Si une
ancienne interface apparaît, supprimer les fichiers `.class` générés dans
`src/`, reconstruire le projet (`Build > Rebuild Project`) et relancer. Le
dossier `src/` ne doit contenir que des fichiers `.java` ; les classes compilées
vont exclusivement dans `out/`.

Les icônes de l'interface sont dessinées en vectoriel (`Icons.java`) et
l'illustration provient de `pc.png`. Aucune police emoji n'est nécessaire.
