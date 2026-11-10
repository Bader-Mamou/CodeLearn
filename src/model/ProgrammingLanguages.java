package model;

import java.util.List;

/**
 * Catalogue of the programming languages that can be taught on the platform.
 *
 * <p>The administrator assigns these languages to teachers. Students can then
 * only pick among the languages actually taught by at least one teacher.</p>
 */
public final class ProgrammingLanguages {

    private static final List<LanguageInfo> ALL = List.of(
            new LanguageInfo("java", "Java"),
            new LanguageInfo("python", "Python"),
            new LanguageInfo("javascript", "JavaScript"),
            new LanguageInfo("cpp", "C++"));

    private ProgrammingLanguages() {
    }

    /** Every programming language the administrator may assign. */
    public static List<LanguageInfo> all() {
        return ALL;
    }

    /** Display name of a language, falling back to Java for unknown codes. */
    public static String label(String code) {
        for (LanguageInfo language : ALL) {
            if (language.code().equals(code)) {
                return language.label();
            }
        }
        return "Java";
    }

    /** Language descriptor for a code, or {@code null} when unknown. */
    public static LanguageInfo find(String code) {
        for (LanguageInfo language : ALL) {
            if (language.code().equals(code)) {
                return language;
            }
        }
        return null;
    }
}
