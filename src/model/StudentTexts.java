package model;

/**
 * Localized interface strings for the student workspace.
 *
 * <p>Kept in the model layer so the view stays free of translation logic.</p>
 */
public record StudentTexts(
        String roleLabel,
        String navHome, String navCourses, String navExercises, String navTests,
        String navProgress, String navProfile, String logout,
        String levelCurrent, String progression, String global,
        String sectionTitle, String seeAll,
        String heroStep, String startNow,
        String resultsTitle, String activitiesTitle,
        String openCourse, String locked,
        String l1Tag, String l1Name, String l1Desc, String l1Status,
        String l2Tag, String l2Name, String l2Desc, String l2Status,
        String l3Tag, String l3Name, String l3Desc, String l3Status,
        String brandTagline, String daysUnit, String recordLabel,
        String yesterday, String dayBefore) {

    public static StudentTexts forLanguage(String language) {
        return switch (language == null ? "" : language) {
            case "en" -> new StudentTexts(
                    "Student",
                    "Home", "My courses", "Exercises", "Tests",
                    "My progress", "My profile", "Log out",
                    "Current level", "Progress", "Overall",
                    "Learning path", "See all levels  \u2192",
                    "step by step !", "Start now",
                    "My Results", "Recent Activity",
                    "Open course  \u2192", "Locked",
                    "Level 1", "Beginner",
                    "Discover the {0} basics: variables, loops, conditions...",
                    "Unlocked",
                    "Level 2", "Intermediate",
                    "Deepen your knowledge: classes, objects, inheritance...",
                    "Locked",
                    "Level 3", "Advanced",
                    "Master advanced concepts: threads, collections, design patterns...",
                    "Locked",
                    "Learn. Practice. Progress.", "days", "Record",
                    "Yesterday", "Day before");
            default -> new StudentTexts(
                    "Apprenant",
                    "Accueil", "Mes cours", "Exercices", "Tests",
                    "Ma progression", "Mon profil", "D\u00e9connexion",
                    "Niveau Actuel", "Progression", "Globale",
                    "Parcours d'apprentissage", "Voir tous les niveaux  \u2192",
                    "pas \u00e0 pas !", "Commencer maintenant",
                    "Mes R\u00e9sultats", "Derni\u00e8res Activit\u00e9s",
                    "Acc\u00e9der au cours  \u2192", "Verrouill\u00e9",
                    "Niveau 1", "D\u00e9butant",
                    "D\u00e9couvrez les bases de {0} : variables, boucles, conditions...",
                    "D\u00e9verrouill\u00e9",
                    "Niveau 2", "Interm\u00e9diaire",
                    "Approfondissez vos connaissances : classes, objets, h\u00e9ritage...",
                    "Verrouill\u00e9",
                    "Niveau 3", "Avanc\u00e9",
                    "Ma\u00eetrisez les concepts avanc\u00e9s : threads, collections, design patterns...",
                    "Verrouill\u00e9",
                    "Apprendre. Pratiquez. Progresser.", "jours", "Record",
                    "Hier", "Avant-hier");
        };
    }
}
